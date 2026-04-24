package net.wots;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.wots.block.ModBlocks;
import net.wots.client.PlushieSoundKeyHandler;
import net.wots.inventory.PlushieInventory;
import net.wots.item.ModItemGroups;
import net.wots.item.ModItems;
import net.wots.network.*;
import net.wots.screen.StevePlushScreenHandler;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.wots.unlock.UnlockSyncHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wots implements ModInitializer {

	public static final String MOD_ID = "wots";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static MenuType<StevePlushScreenHandler> STEVE_PLUSH_HANDLER;

	// ── Loot table keys ───────────────────────────────────────────────────────
	private static final ResourceKey<LootTable> SIMPLE_DUNGEON_LOOT =
			ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("chests/simple_dungeon"));
	private static final ResourceKey<LootTable> WOODLAND_MANSION_LOOT =
			ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("chests/woodland_mansion"));
	private static final ResourceKey<LootTable> IGLOO_CHEST_LOOT =
			ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("chests/igloo_chest"));

	@Override
	public void onInitialize() {
		net.wots.sound.ModSounds.registerModSounds();
		net.wots.item.ModItems.registerModItems();
		ModBlocks.registerModBlock();
		ModItemGroups.registerItemGroups();
		net.wots.particle.ModParticles.registerParticles();

		// ── Plushie inventory attachment (force class-load to register) ───────
		@SuppressWarnings("unused")
		var unused = PlushieInventory.ATTACHMENT;

		// ── Packets ───────────────────────────────────────────────────────────
		SetVariantPayload.registerServer();
		RequestUnlockSyncPayload.registerServer();
		SetStevePlushNamePayload.registerServer();

		// ── Variant unlock system ─────────────────────────────────────────────
		SyncVariantUnlocksPayload.register();
		VariantChangeParticlePayload.register();
		SubtitlePayload.register();

		// ── Plushie slot sync (S2C) ──────────────────────────────────────────
		PayloadTypeRegistry.clientboundPlay().register(S2CPlushieSyncPayload.TYPE, S2CPlushieSyncPayload.CODEC);

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			UnlockSyncHelper.onPlayerJoin(handler.getPlayer());
			broadcastPlushieSync(handler.getPlayer());
		});

		EntityTrackingEvents.START_TRACKING.register((trackedEntity, player) -> {
			if (trackedEntity instanceof ServerPlayer trackedPlayer) {
				PlushieInventory inv = PlushieInventory.get(trackedPlayer);
				ServerPlayNetworking.send(player, new S2CPlushieSyncPayload(
						trackedPlayer.getId(),
						inv.getStack(PlushieInventory.HAT_SLOT),
						inv.getStack(PlushieInventory.BACK_SLOT)
				));
			}
		});

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			broadcastPlushieSync(newPlayer);
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				PlushieInventory inv = PlushieInventory.get(player);
				if (inv.isDirty()) {
					broadcastPlushieSync(player);
				}
			}
		});

		// ── Screen handlers ───────────────────────────────────────────────────
		STEVE_PLUSH_HANDLER = Registry.register(
				BuiltInRegistries.MENU,
				Identifier.fromNamespaceAndPath("wots", "steve_plush"),
				new ExtendedMenuType<>(StevePlushScreenHandler::new, BlockPos.STREAM_CODEC)
		);

		// ── Resource packs ────────────────────────────────────────────────────
		ResourceManagerHelper.registerBuiltinResourcePack(
				Identifier.fromNamespaceAndPath("wots", "luminite-shaders"),
				FabricLoader.getInstance().getModContainer("wots").orElseThrow(),
				ResourcePackActivationType.ALWAYS_ENABLED
		);
		PayloadTypeRegistry.serverboundPlay().register(
				PlushieSoundPayloads.HatPayload.TYPE,
				PlushieSoundPayloads.HatPayload.CODEC
		);
		PayloadTypeRegistry.serverboundPlay().register(
				PlushieSoundPayloads.BackPayload.TYPE,
				PlushieSoundPayloads.BackPayload.CODEC
		);
		PlushieSoundKeyHandler.registerServerReceiver();

		// ── Flammable blocks ─────────────────────────────────────────────────
		net.fabricmc.fabric.api.registry.FlammableBlockRegistry.getDefaultInstance()
				.add(ModBlocks.RGB_WOOL, 30, 60);

		// ── Plushie gifting ──────────────────────────────────────────────────
		net.wots.interaction.PlushieGiftHandler.register();

		// ── Loot table injections ─────────────────────────────────────────────
		registerLootInjections();

		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			ServerLifecycleEvents.SERVER_STARTED.register(server -> {
				server.setUsesAuthentication(false);
			});
		}
	}

	public static void broadcastPlushieSync(ServerPlayer player) {
		PlushieInventory inv = PlushieInventory.get(player);
		S2CPlushieSyncPayload payload = new S2CPlushieSyncPayload(
				player.getId(),
				inv.getStack(PlushieInventory.HAT_SLOT),
				inv.getStack(PlushieInventory.BACK_SLOT)
		);
		ServerPlayNetworking.send(player, payload);
		for (ServerPlayer tracker : PlayerLookup.tracking(player)) {
			ServerPlayNetworking.send(tracker, payload);
		}
		inv.clearDirty();
	}

	private void registerLootInjections() {
		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {

			if (key.equals(SIMPLE_DUNGEON_LOOT)) {
				tableBuilder.pool(
						LootPool.lootPool()
								.setRolls(ConstantValue.exactly(1))
								.add(LootItem.lootTableItem(ModBlocks.UZI_PLUSH.asItem())
										.apply(SetItemCountFunction.setCount(
												ConstantValue.exactly(1)))
										.when(LootItemRandomChanceCondition.randomChance(0.08f)))
								.build()
				);
			} else if (key.equals(WOODLAND_MANSION_LOOT)) {
				tableBuilder.pool(
						LootPool.lootPool()
								.setRolls(ConstantValue.exactly(1))
								.add(LootItem.lootTableItem(ModBlocks.N_PLUSH.asItem())
										.apply(SetItemCountFunction.setCount(
												ConstantValue.exactly(1)))
										.when(LootItemRandomChanceCondition.randomChance(0.10f)))
								.build()
				);
			} else if (key.equals(IGLOO_CHEST_LOOT)) {
				tableBuilder.pool(
						LootPool.lootPool()
								.setRolls(ConstantValue.exactly(1))
								.add(LootItem.lootTableItem(ModBlocks.CYN_PLUSH.asItem())
										.apply(SetItemCountFunction.setCount(
												ConstantValue.exactly(1)))
										.when(LootItemRandomChanceCondition.randomChance(0.12f)))
								.build()
				);
			}
		});
	}
}
