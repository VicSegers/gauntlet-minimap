package com.gauntletminimap.demiboss;

import com.gauntletminimap.MinimapRenderable;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.client.game.SkillIconManager;

import java.awt.image.BufferedImage;

public abstract class DemiBoss implements MinimapRenderable {

    private final NPC npc;
    private final BufferedImage image;

    protected DemiBoss(NPC npc, Skill skill) {
        this.npc = npc;
        image = new SkillIconManager().getSkillImage(skill, true);
    }

    public NPC getNpc() {
        return npc;
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }

    @Override
    public Point getMinimapLocation() {
        Point point = npc.getMinimapLocation();

        if (point == null)
            return null;

        return new Point(point.getX() - image.getHeight() / 2, point.getY() - image.getWidth() / 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        return npc.equals(((DemiBoss) o).getNpc());
    }

    @Override
    public int hashCode() {
        return npc.hashCode();
    }

}
