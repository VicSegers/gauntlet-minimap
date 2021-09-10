package com.gauntletminimap;

import net.runelite.api.Point;

import java.awt.image.BufferedImage;

public interface MinimapRenderable {

    Point getMinimapLocation();

    BufferedImage getImage();

}
