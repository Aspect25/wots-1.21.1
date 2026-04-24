package net.wots;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class WotsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		// TODO: MC 26.1 completely changed the datagen API.
		// All providers need to be updated to use FabricPackOutput instead of FabricDataOutput.
		// Stubbed for now -- datagen will be re-implemented after compilation succeeds.
	}
}
