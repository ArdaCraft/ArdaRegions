package com.r3signed.ac.regions.core.events.areas;

import com.r3signed.ac.regions.core.areas.Area;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class AreaTickEvents {
    private AreaTickEvents() {}

    /**
     * Called when an area starts ticking.
     */
    public static final Event<Start> START = EventFactory.createArrayBacked(Start.class, callbacks -> (area) -> {
        for (Start callback : callbacks) {
            callback.onAreaStartTicking(area);
        }
    });

    /**
     * Called when an area stops ticking.
     */
    public static final Event<Stop> STOP = EventFactory.createArrayBacked(Stop.class, callbacks -> (area) -> {
        for (Stop callback : callbacks) {
            callback.onAreaStopTicking(area);
        }
    });

    @FunctionalInterface
    public interface Start {
        void onAreaStartTicking(Area area);
    }

    @FunctionalInterface
    public interface Stop {
        void onAreaStopTicking(Area area);
    }
}
