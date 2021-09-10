package com.gauntletminimap;

import com.gauntletminimap.demiboss.*;
import com.gauntletminimap.resourcenode.*;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@PluginDescriptor(
		name = "Gauntlet Minimap",
		description = "Displays the resource nodes of the Gauntlet on your minimap",
		tags = {"corrupted","gauntlet","resource","nodes","demi-boss","demiboss","minimap","hunllef","pve","pvm","minigame"}
)
public class GauntletMinimapPlugin extends Plugin {

	@Inject
	private Client client;

	@Inject
	private GauntletMinimapConfig config;

	@Inject
	private GauntletMinimapOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	private static final int CRYSTAL_GAUNTLET_REGION_ID = 7512;
	private static final int CORRUPTED_GAUNTLET_REGION_ID = 7768;

	private final Set<ResourceNode> resourceNodes = new HashSet<>();
	private final Set<DemiBoss> demiBosses = new HashSet<>();

	protected static Set<String> displayableItems = new HashSet<>();

	private static final Set<Integer> RESOURCE_NODE_IDS = ImmutableSet.of(
			ObjectID.CRYSTAL_DEPOSIT,
			ObjectID.CORRUPT_DEPOSIT,
			ObjectID.PHREN_ROOTS,
			ObjectID.PHREN_ROOTS_36066,
			ObjectID.LINUM_TIRINUM,
			ObjectID.LINUM_TIRINUM_36072,
			ObjectID.GRYM_ROOT,
			ObjectID.GRYM_ROOT_36070,
			ObjectID.FISHING_SPOT_36068,
			ObjectID.FISHING_SPOT_35971
	);

	private static final Set<Integer> DEMI_BOSS_IDS = ImmutableSet.of(
			NpcID.CRYSTALLINE_BEAR,
			NpcID.CORRUPTED_BEAR,
			NpcID.CRYSTALLINE_DRAGON,
			NpcID.CORRUPTED_DRAGON,
			NpcID.CRYSTALLINE_DARK_BEAST,
			NpcID.CORRUPTED_DARK_BEAST
	);

	@Override
	protected void startUp() {
		resourceNodes.clear();

		if (isInGauntlet())
			overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() {
		overlayManager.remove(overlay);
		resourceNodes.clear();
	}

	@Subscribe
	private void onGameStateChanged(final GameStateChanged event) {
		switch(event.getGameState()) {
			case LOADING:
				if (isInGauntlet())
					startUp();
				else
					shutDown();
				break;
			case LOGIN_SCREEN:
			case HOPPING:
				shutDown();
				break;
			default:
				break;
		}
	}

	@Subscribe
	private void onGameObjectSpawned(final GameObjectSpawned event) {
		if (!isInGauntlet())
			return;

		final GameObject gameObject = event.getGameObject();

		if (RESOURCE_NODE_IDS.contains(gameObject.getId()))
			resourceNodes.add(gameObjectToResource(gameObject));
	}

	@Subscribe
	private void onGameObjectDespawned(final GameObjectDespawned event) {
		if (!isInGauntlet())
			return;

		final GameObject gameObject = event.getGameObject();

		if (RESOURCE_NODE_IDS.contains(gameObject.getId()))
			resourceNodes.remove(gameObjectToResource(gameObject));
	}

	@Subscribe
	private void onNpcSpawned(final NpcSpawned event) {
		if (!isInGauntlet())
			return;

		final NPC npc = event.getNpc();

		if (DEMI_BOSS_IDS.contains(npc.getId()))
			demiBosses.add(npcToDemiBoss(npc));
	}

	@Subscribe
	private void onNpcDespawned(NpcDespawned event) {
		if (!isInGauntlet())
			return;

		final NPC npc = event.getNpc();

		if (DEMI_BOSS_IDS.contains(npc.getId()))
			demiBosses.remove(npcToDemiBoss(npc));
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event) {
		if (!event.getGroup().equals(GauntletMinimapConfig.CONFIG_GROUP))
			return;

		setConfigs();
	}

	@Provides
	GauntletMinimapConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(GauntletMinimapConfig.class);
	}

	protected Set<ResourceNode> getResourceNodes() {
		return resourceNodes;
	}

	protected Set<DemiBoss> getDemiBosses() {
		return demiBosses;
	}

	private ResourceNode gameObjectToResource(GameObject gameObject) {
		switch (gameObject.getId()) {
			case ObjectID.CRYSTAL_DEPOSIT:
			case ObjectID.CORRUPT_DEPOSIT:
				return new OreDeposit(gameObject);
			case ObjectID.PHREN_ROOTS:
			case ObjectID.PHREN_ROOTS_36066:
				return new PhrenRoot(gameObject);
			case ObjectID.LINUM_TIRINUM:
			case ObjectID.LINUM_TIRINUM_36072:
				return new LinumTirinum(gameObject);
			case ObjectID.GRYM_ROOT:
			case ObjectID.GRYM_ROOT_36070:
				return new GrymRoot(gameObject);
			case ObjectID.FISHING_SPOT_36068:
			case ObjectID.FISHING_SPOT_35971:
				return new FishingSpot(gameObject);
			default:
				return null;
		}
	}

	private DemiBoss npcToDemiBoss(NPC npc) {
		switch (npc.getId()) {
			case NpcID.CRYSTALLINE_BEAR:
			case NpcID.CORRUPTED_BEAR:
				return new Bear(npc);
			case NpcID.CRYSTALLINE_DRAGON:
			case NpcID.CORRUPTED_DRAGON:
				return new Dragon(npc);
			case NpcID.CRYSTALLINE_DARK_BEAST:
			case NpcID.CORRUPTED_DARK_BEAST:
				return new DarkBeast(npc);
			default:
				return null;
		}
	}

	private void setConfigs() {
		updateDisplayableItems(config.oreDeposit(), "OreDeposit");
		updateDisplayableItems(config.phrenRoots(), "PhrenRoot");
		updateDisplayableItems(config.linumTirinum(), "LinumTirinum");
		updateDisplayableItems(config.grymRoot(), "GrymRoot");
		updateDisplayableItems(config.fishingSpot(), "FishingSpot");

		updateDisplayableItems(config.bear(), "Bear");
		updateDisplayableItems(config.dragon(), "Dragon");
		updateDisplayableItems(config.darkBeast(), "DarkBeast");
	}

	private void updateDisplayableItems(boolean add, String className) {
		if (add)
			displayableItems.add(className);
		else
			displayableItems.remove(className);
	}

	public boolean isInNormal() {
		if (client.getLocalPlayer() == null)
			return false;

		return client.getMapRegions()[0] == CRYSTAL_GAUNTLET_REGION_ID;
	}

	public boolean isInCorrupted() {
		if (client.getLocalPlayer() == null)
			return false;

		return client.getMapRegions()[0] == CORRUPTED_GAUNTLET_REGION_ID;
	}

	private boolean isInGauntlet() {
		return isInNormal() || isInCorrupted();
	}
}
