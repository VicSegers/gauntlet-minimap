package com.gauntletminimap;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class GauntletMinimapPluginTest {
	public static void main(String[] args) throws Exception {
		ExternalPluginManager.loadBuiltin(GauntletMinimapPlugin.class);
		RuneLite.main(args);
	}
}