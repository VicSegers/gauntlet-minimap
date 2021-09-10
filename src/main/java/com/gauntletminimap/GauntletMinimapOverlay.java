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
        plugin.getResourceNodes().forEach(node -> renderImageOnMinimap(graphics, node));
        plugin.getDemiBosses().forEach(demiBoss -> renderImageOnMinimap(graphics, demiBoss));

        return null;
    }

    private void renderImageOnMinimap(Graphics2D graphics, MinimapRenderable minimapRenderable) {
        if (!GauntletMinimapPlugin.displayableItems.contains(minimapRenderable.getClass().getSimpleName()))
            return;

        Point minimapLocation = minimapRenderable.getMinimapLocation();

        if (minimapLocation != null)
            OverlayUtil.renderImageLocation(graphics, minimapLocation, minimapRenderable.getImage());
    }

}
