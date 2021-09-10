package com.gauntletminimap.resourcenode;

import com.gauntletminimap.MinimapRenderable;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.client.game.SkillIconManager;

import java.awt.image.BufferedImage;

public abstract class ResourceNode implements MinimapRenderable {

    private final GameObject gameObject;
    private final BufferedImage image;

    protected ResourceNode(GameObject gameObject, Skill skill) {
        this.gameObject = gameObject;
        image = new SkillIconManager().getSkillImage(skill, true);
    }

    public GameObject getGameObject() {
        return gameObject;
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }

    @Override
    public Point getMinimapLocation() {
        Point point = gameObject.getMinimapLocation();

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

        return gameObject.equals(((ResourceNode) o).getGameObject());
    }

    @Override
    public int hashCode() {
        return gameObject.hashCode();
    }

}
