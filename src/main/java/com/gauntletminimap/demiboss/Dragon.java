package com.gauntletminimap.demiboss;

import net.runelite.api.NPC;
import net.runelite.api.Skill;

public class Dragon extends DemiBoss {

    public Dragon(NPC npc) {
        super(npc, Skill.MAGIC);
    }

}
