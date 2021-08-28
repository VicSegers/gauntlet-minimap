package com.gauntletminimap.resourcenode;

import net.runelite.api.GameObject;
import net.runelite.api.Skill;

public class OreDeposit extends ResourceNode {

    public OreDeposit(GameObject gameObject) {
        super(gameObject, Skill.MINING);
    }

}
