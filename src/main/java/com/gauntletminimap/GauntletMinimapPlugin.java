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

	private boolean isStarted = false;

	private static final int CRYSTAL_GAUNTLET_REGION_ID = 7512;
	private static final int CORRUPTED_GAUNTLET_REGION_ID = 7768;

	private static final String MINING_MESSAGE = "You manage to mine some ore.";
	private static final String WOODCUTTING_MESSAGE = "You get some bark.";
	private static final String FARMING_MESSAGE = "You pick some fibre from the plant.";
	private static final String HERBLORE_MESSAGE = "You pick a herb from the roots.";
	private static final String FISHING_MESSAGE = "You manage to catch a fish.";

	protected final Set<ResourceNode> resourceNodes = new HashSet<>();
	protected final Set<DemiBoss> demiBosses = new HashSet<>();

	protected boolean trackResources;
	protected Map<String, Integer> maxResources;
	protected Map<String, Integer> collectedResources = new HashMap<String, Integer>() {{
		put(OreDeposit.class.getSimpleName(), 0);
		put(PhrenRoot.class.getSimpleName(), 0);
		put(LinumTirinum.class.getSimpleName(), 0);
		put(GrymRoot.class.getSimpleName(), 0);
		put(FishingSpot.class.getSimpleName(), 0);
	}};

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

		if (!isStarted) {
			isStarted = true;
			setConfigs();

			if (isInGauntlet())
				overlayManager.add(overlay);
		}
	}

	@Override
	protected void shutDown() {
		if (isStarted) {
			isStarted = false;
			overlayManager.remove(overlay);
			resourceNodes.clear();

			collectedResources = new HashMap<String, Integer>() {{
				put(OreDeposit.class.getSimpleName(), 0);
				put(PhrenRoot.class.getSimpleName(), 0);
				put(LinumTirinum.class.getSimpleName(), 0);
				put(GrymRoot.class.getSimpleName(), 0);
				put(FishingSpot.class.getSimpleName(), 0);
			}};
		}
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
					collectedResources.merge(OreDeposit.class.getSimpleName(), 1, Integer::sum);
					break;
				case WOODCUTTING_MESSAGE:
					collectedResources.merge(PhrenRoot.class.getSimpleName(), 1, Integer::sum);
					break;
				case FARMING_MESSAGE:
					collectedResources.merge(LinumTirinum.class.getSimpleName(), 1, Integer::sum);
					break;
				case HERBLORE_MESSAGE:
					collectedResources.merge(GrymRoot.class.getSimpleName(), 1, Integer::sum);
					break;
				case FISHING_MESSAGE:
					collectedResources.merge(FishingSpot.class.getSimpleName(), 1, Integer::sum);
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
		updateDisplayableItems(config.oreDeposit(), OreDeposit.class.getSimpleName());
		updateDisplayableItems(config.phrenRoots(), PhrenRoot.class.getSimpleName());
		updateDisplayableItems(config.linumTirinum(), LinumTirinum.class.getSimpleName());
		updateDisplayableItems(config.grymRoot(), GrymRoot.class.getSimpleName());
		updateDisplayableItems(config.fishingSpot(), FishingSpot.class.getSimpleName());

		updateDisplayableItems(config.bear(), Bear.class.getSimpleName());
		updateDisplayableItems(config.dragon(), Dragon.class.getSimpleName());
		updateDisplayableItems(config.darkBeast(), DarkBeast.class.getSimpleName());

		trackResources = config.trackResources();

		maxResources = ImmutableMap.of(
				OreDeposit.class.getSimpleName(), config.ore(),
				PhrenRoot.class.getSimpleName(), config.bark(),
				LinumTirinum.class.getSimpleName(), config.fibre(),
				GrymRoot.class.getSimpleName(), config.herb(),
				FishingSpot.class.getSimpleName(), config.fish()
		);
	}

	private void updateDisplayableItems(boolean add, String className) {
		if (add)
			displayableItems.add(className);
		else
			displayableItems.remove(className);
	}

	protected boolean isDemiboss(String className) {
		return className.equals(Bear.class.getSimpleName())
				|| className.equals(Dragon.class.getSimpleName())
				|| className.equals(DarkBeast.class.getSimpleName());
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
