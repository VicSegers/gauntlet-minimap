package com.gauntletminimap;

import net.runelite.api.Point;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

public class GauntletMinimapOverlay extends Overlay {

    private final GauntletMinimapPlugin plugin;

    @Inject
    private GauntletMinimapOverlay(GauntletMinimapPlugin plugin) {
        this.plugin = plugin;

        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        plugin.resourceNodes.forEach(node -> renderImageOnMinimap(graphics, node));
        plugin.demiBosses.forEach(demiBoss -> renderImageOnMinimap(graphics, demiBoss));

        return null;
    }

    private void renderImageOnMinimap(Graphics2D graphics, MinimapRenderable minimapRenderable) {
        final String className = minimapRenderable.getClass().getSimpleName();

        if (!plugin.displayableItems.contains(className) || plugin.trackResources && !plugin.isDemiboss(className)
                && plugin.collectedResources.get(className) >= plugin.maxResources.get(className))
            return;

        Point minimapLocation = minimapRenderable.getMinimapLocation();

        if (minimapLocation != null)
            OverlayUtil.renderImageLocation(graphics, minimapLocation, minimapRenderable.getImage());
    }

}
