package com.gauntletminimap;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("gauntletMinimap")
public interface GauntletMinimapConfig extends Config {

	@ConfigItem(
			keyName = "oreDeposit",
			name = "Ore Deposits",
			description = "Show the Ore Deposits (Mining) on the minimap.",
			position = 0
	)
	default boolean oreDeposit() {
		return true;
	}

	@ConfigItem(
			keyName = "phrenRoots",
			name = "Phren Roots",
			description = "Show the Phren Roots (Woodcutting) on the minimap.",
			position = 1
	)
	default boolean phrenRoots() {
		return true;
	}

	@ConfigItem(
			keyName = "linumTirinum",
			name = "Linum Tirinums",
			description = "Show the Linum Tirinums (Farming) on the minimap.",
			position = 2
	)
	default boolean linumTirinum() {
		return true;
	}

	@ConfigItem(
			keyName = "grymRoot",
			name = "Grym Roots",
			description = "Show the Grym Roots (Herblore) on the minimap.",
			position = 3
	)
	default boolean grymRoot() {
		return true;
	}

	@ConfigItem(
			keyName = "fishingSpot",
			name = "Fishing Spots",
			description = "Show the Fishing Spots (Fishing) on the minimap.",
			position = 4
	)
	default boolean fishingSpot() {
		return true;
	}
}
