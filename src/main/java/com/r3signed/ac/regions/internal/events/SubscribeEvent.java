package com.r3signed.ac.regions.internal.events;

import com.r3signed.ac.regions.core.Side;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to subscribe to events.
 * <p>
 * This annotation is used to mark methods that should be called when a specific event occurs.
 * The method must have the same parameters as the event listener.
 * <p>
 * Example:
 * <pre>
 * {@code
 * @SubscribeEvent(Events.SERVER_WORLD_LOAD)
 * private void onServerWorldLoad(MinecraftServer server, ServerWorld world) {
 *     // Handle the event
 * }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SubscribeEvent {
    /**
     * The event to subscribe to.
     */
    Events value();

    /**
     * The side to register the handler on. Defaults to
     * {@link Side#BOTH}.
     */
    Side side() default Side.BOTH;
}
