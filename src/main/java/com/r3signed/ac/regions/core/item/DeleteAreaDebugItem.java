package com.r3signed.ac.regions.core.item;

import net.minecraft.item.Item;

public class DeleteAreaDebugItem extends Item {
    public DeleteAreaDebugItem() {
        super(new Settings().maxCount(1));
    }

    //TODO: add existing area's point removal when areas can be identified by unique UUIDs on both server and client side
}
