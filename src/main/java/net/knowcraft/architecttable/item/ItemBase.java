package net.knowcraft.architecttable.item;

import net.knowcraft.architecttable.reference.Reference;
import net.minecraft.item.Item;

/**
 * Created by oOMitchOo on 14.10.2016.
 */
public class ItemBase extends Item {

    public ItemBase(String unlName, String regName) {
        super();
        this.setCreativeTab(Reference.creativeTab);

        this.setUnlocalizedName(unlName);
        this.setRegistryName(Reference.MOD_ID, regName);
    }
}
