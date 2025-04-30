package com.r3signed.ac.regions.internal.events;

import com.r3signed.ac.regions.ArdaRegions;
import net.fabricmc.fabric.api.event.Event;

import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class EventHandlerRegistry {
    private static final Set<Method> registeredMethods = Collections.newSetFromMap(new WeakHashMap<>());

    /**
     * Registers an object's methods to the relevant event handlers.
     *
     * @param object The object to register
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void register(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (registeredMethods.contains(method)) {
                return;
            }

            if (method.isAnnotationPresent(SubscribeEvent.class)) {
                Events event = method.getAnnotation(SubscribeEvent.class).value();
                try {
                    method.setAccessible(true);
                } catch (InaccessibleObjectException | SecurityException e) {
                    ArdaRegions.LOGGER.warn(
                            "Could not register event handler for method \"{}\" in class \"{}\"",
                            method.getName(),
                            method.getDeclaringClass().getSimpleName(),
                            e
                    );
                    return;
                }

                Class<?>[] expectedTypes = event.getParameterTypes();
                Class<?>[] actualTypes = method.getParameterTypes();
                if (!Arrays.equals(expectedTypes, actualTypes)) {
                    ArdaRegions.LOGGER.warn(
                            "Could not register event handler for method \"{}\" in class \"{}\". Expected parameter types: {}, but got: {}",
                            method.getName(),
                            method.getDeclaringClass().getSimpleName(),
                            Arrays.toString(expectedTypes),
                            Arrays.toString(actualTypes)
                    );
                    return;
                }

                Event<?> eventHandler = event.getHandler();
                Class<?> eventListener = event.getListener();
                ListenerInvocationHandler handler = new ListenerInvocationHandler(object, method);

                Object proxy = Proxy.newProxyInstance(
                        eventListener.getClassLoader(),
                        new Class<?>[]{eventListener},
                        handler
                );

                ((Event) eventHandler).register(proxy);
                registeredMethods.add(method);
            }
        }
    }
}
