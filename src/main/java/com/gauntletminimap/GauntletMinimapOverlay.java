package com.gauntletminimap;

import com.gauntletminimap.resourcenode.ResourceNode;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

public class GauntletMinimapOverlay extends Overlay {

    private final GauntletMinimapPlugin plugin;
    private final GauntletMinimapConfig config;

    @Inject
    private GauntletMinimapOverlay(GauntletMinimapPlugin plugin, GauntletMinimapConfig config) {
        this.plugin = plugin;
        this.config = config;

        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        for (ResourceNode resourceNode : plugin.getResourceNodes()) {

            if (displayResource(resourceNode)) {
                Point minimapLocation = resourceNode.getMinimapLocation();

                if (minimapLocation != null)
                    OverlayUtil.renderImageLocation(graphics, resourceNode.getMinimapLocation(), resourceNode.getImage());
            }
        }

        return null;
    }

    private boolean displayResource(ResourceNode resourceNode) {
        switch (resourceNode.getSkill()) {
            case MINING:
                return config.oreDeposit();
            case WOODCUTTING:
                return config.phrenRoots();
            case FARMING:
                return config.linumTirinum();
            case HERBLORE:
                return config.grymRoot();
            case FISHING:
                return config.fishingSpot();
            default:
                return false;
        }
    }

}
