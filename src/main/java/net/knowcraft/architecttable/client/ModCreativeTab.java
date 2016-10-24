package net.knowcraft.architecttable.client;

import net.knowcraft.architecttable.init.ModItems;
import net.knowcraft.architecttable.reference.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * Created by oOMitchOo on 14.10.2016.
 */
public class ModCreativeTab extends CreativeTabs{

    public ModCreativeTab(){
        super(Reference.MOD_ID);
    }

    @Override
    public Item getTabIconItem() {
        return ModItems.DRAWING_TOOLS;
    }

    @Override
    public boolean hasSearchBar() {
        return false;
    }
}
