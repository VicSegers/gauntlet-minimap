package com.gauntletminimap;

import com.gauntletminimap.demiboss.*;
import com.gauntletminimap.resourcenode.*;
import com.google.common.collect.ImmutableMap;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

	private final String DEPOSIT_ORE_CLASS_NAME = "OreDeposit";
	private final String PHREN_ROOT_CLASS_NAME = "PhrenRoot";
	private final String LINUM_TIRINUM_CLASS_NAME = "LinumTirinum";
	private static final String GRYM_ROOT_CLASS_NAME = "GrymRoot";
	private static final String FISHING_SPOT_CLASS_NAME = "FishingSpot";

	private static final String BEAR_CLASS_NAME = "Bear";
	private static final String DRAGON_CLASS_NAME = "Dragon";
	private static final String DARK_BEAST_CLASS_NAME = "DarkBeast";

	private static final String MINING_MESSAGE = "You manage to mine some ore.";
	private static final String WOODCUTTING_MESSAGE = "You get some bark.";
	private static final String FARMING_MESSAGE = "You pick some fibre from the plant.";
	private static final String HERBLORE_MESSAGE = "You pick a herb from the roots.";
	private static final String FISHING_MESSAGE = "You manage to catch a fish.";

	protected final Set<ResourceNode> resourceNodes = new HashSet<>();
	protected final Set<DemiBoss> demiBosses = new HashSet<>();

	protected Map<String, Integer> collectedResources;
	protected Map<String, Integer> maxResources;
	protected boolean trackResources;

	protected Set<String> displayableItems = new HashSet<>();

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

		collectedResources = new HashMap<String, Integer>() {{
			put(DEPOSIT_ORE_CLASS_NAME, 0);
			put(PHREN_ROOT_CLASS_NAME, 0);
			put(LINUM_TIRINUM_CLASS_NAME, 0);
			put(GRYM_ROOT_CLASS_NAME, 0);
			put(FISHING_SPOT_CLASS_NAME, 0);
		}};

		setConfigs();

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

	@Subscribe
	private void onChatMessage(ChatMessage event) {
		if (event.getType() == ChatMessageType.SPAM) {
			switch (event.getMessage()) {
				case MINING_MESSAGE:
					collectedResources.merge(DEPOSIT_ORE_CLASS_NAME, 1, Integer::sum);
					break;
				case WOODCUTTING_MESSAGE:
					collectedResources.merge(PHREN_ROOT_CLASS_NAME, 1, Integer::sum);
					break;
				case FARMING_MESSAGE:
					collectedResources.merge(LINUM_TIRINUM_CLASS_NAME, 1, Integer::sum);
					break;
				case HERBLORE_MESSAGE:
					collectedResources.merge(GRYM_ROOT_CLASS_NAME, 1, Integer::sum);
					break;
				case FISHING_MESSAGE:
					collectedResources.merge(FISHING_SPOT_CLASS_NAME, 1, Integer::sum);
					break;
				default:
					break;
			}
		}
	}

	@Provides
	GauntletMinimapConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(GauntletMinimapConfig.class);
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
		updateDisplayableItems(config.oreDeposit(), DEPOSIT_ORE_CLASS_NAME);
		updateDisplayableItems(config.phrenRoots(), PHREN_ROOT_CLASS_NAME);
		updateDisplayableItems(config.linumTirinum(), LINUM_TIRINUM_CLASS_NAME);
		updateDisplayableItems(config.grymRoot(), GRYM_ROOT_CLASS_NAME);
		updateDisplayableItems(config.fishingSpot(), FISHING_SPOT_CLASS_NAME);

		updateDisplayableItems(config.bear(), BEAR_CLASS_NAME);
		updateDisplayableItems(config.dragon(), DRAGON_CLASS_NAME);
		updateDisplayableItems(config.darkBeast(), DARK_BEAST_CLASS_NAME);

		trackResources = config.trackResources();

		maxResources = ImmutableMap.of(
				DEPOSIT_ORE_CLASS_NAME, config.ore(),
				PHREN_ROOT_CLASS_NAME, config.bark(),
				LINUM_TIRINUM_CLASS_NAME, config.fibre(),
				GRYM_ROOT_CLASS_NAME, config.herb(),
				FISHING_SPOT_CLASS_NAME, config.fish()
		);
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
