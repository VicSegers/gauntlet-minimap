package com.gauntletminimap.resourcenode;

import net.runelite.api.GameObject;
import net.runelite.api.Skill;

public class FishingSpot extends ResourceNode {

    public FishingSpot(GameObject gameObject) {
        super(gameObject, Skill.FISHING);
    }

}
