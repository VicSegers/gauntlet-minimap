package com.gauntletminimap;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(GauntletMinimapConfig.CONFIG_GROUP)
public interface GauntletMinimapConfig extends Config {

	String CONFIG_GROUP = "gauntletMinimap";

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

	@ConfigSection(
			name = "Tracker",
			description = "Resource tracker section.",
			position = 2
	)
	String trackerSection = "tracker";

	@ConfigItem(
			keyName = "trackResources",
			name = "Track Resources",
			description = "Hides resource nodes after gathering a certain amount.",
			section = "tracker",
			position = 0
	)
	default boolean trackResources() {
		return false;
	}

	@ConfigItem(
			keyName = "ore",
			name = "Ore",
			description = "The desired amount of ore.",
			section = "tracker",
			position = 1
	)
	default int ore() {
		return 3;
	}

	@ConfigItem(
			keyName = "bark",
			name = "Bark",
			description = "The desired amount of bark.",
			section = "tracker",
			position = 2
	)
	default int bark() {
		return 3;
	}

	@ConfigItem(
			keyName = "fibre",
			name = "Fibre",
			description = "The desired amount of fibre.",
			section = "tracker",
			position = 3
	)
	default int fibre() {
		return 3;
	}

	@ConfigItem(
			keyName = "herb",
			name = "Herb",
			description = "The desired amount of herb.",
			section = "tracker",
			position = 4
	)
	default int herb() {
		return 3;
	}

	@ConfigItem(
			keyName = "fish",
			name = "Fish",
			description = "The desired amount of fish.",
			section = "tracker",
			position = 5
	)
	default int fish() {
		return 20;
	}

}
