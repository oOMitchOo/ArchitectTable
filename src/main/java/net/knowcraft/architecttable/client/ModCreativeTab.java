package net.knowcraft.architecttable.client;

import net.knowcraft.architecttable.reference.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
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
        // TODO: Mod-Item als Tab-Icon setzen.
        return Items.BRICK;
    }

    @Override
    public boolean hasSearchBar() {
        return false;
    }
}
