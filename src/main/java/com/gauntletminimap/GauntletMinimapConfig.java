package com.gauntletminimap;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("gauntletMinimap")
public interface GauntletMinimapConfig extends Config {

	@ConfigSection(
			name = "Resources",
			description = "Resources section.",
			position = 0
	)
	String resourcesSection = "resources";

	@ConfigItem(
			keyName = "oreDeposit",
			name = "Ore Deposits",
			description = "Show the Ore Deposits (Mining) on the minimap.",
			section = "resources",
			position = 0
	)
	default boolean oreDeposit() {
		return true;
	}

	@ConfigItem(
			keyName = "phrenRoots",
			name = "Phren Roots",
			description = "Show the Phren Roots (Woodcutting) on the minimap.",
			section = "resources",
			position = 1
	)
	default boolean phrenRoots() {
		return true;
	}

	@ConfigItem(
			keyName = "linumTirinum",
			name = "Linum Tirinums",
			description = "Show the Linum Tirinums (Farming) on the minimap.",
			section = "resources",
			position = 2
	)
	default boolean linumTirinum() {
		return true;
	}

	@ConfigItem(
			keyName = "grymRoot",
			name = "Grym Roots",
			description = "Show the Grym Roots (Herblore) on the minimap.",
			section = "resources",
			position = 3
	)
	default boolean grymRoot() {
		return true;
	}

	@ConfigItem(
			keyName = "fishingSpot",
			name = "Fishing Spots",
			description = "Show the Fishing Spots (Fishing) on the minimap.",
			section = "resources",
			position = 4
	)
	default boolean fishingSpot() {
		return true;
	}

	@ConfigSection(
			name = "Demi-bosses",
			description = "Demi-bosses section.",
			position = 1
	)
	String demiBossesSection = "demiBosses";

	@ConfigItem(
			keyName = "bear",
			name = "Bear",
			description = "Show the Fishing Spots (Fishing) on the minimap.",
			section = "demiBosses",
			position = 0
	)
	default boolean bear() {
		return true;
	}

	@ConfigItem(
			keyName = "dragon",
			name = "Dragon",
			description = "Show the Fishing Spots (Fishing) on the minimap.",
			section = "demiBosses",
			position = 1
	)
	default boolean dragon() {
		return true;
	}

	@ConfigItem(
			keyName = "darkBeast",
			name = "Dark Beast",
			description = "Show the Fishing Spots (Fishing) on the minimap.",
			section = "demiBosses",
			position = 2
	)
	default boolean darkBeast() {
		return true;
	}
}
