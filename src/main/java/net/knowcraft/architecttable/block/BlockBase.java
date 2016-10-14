package net.knowcraft.architecttable.block;

import net.knowcraft.architecttable.reference.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

/**
 * Created by oOMitchOo on 14.10.2016.
 */
public class BlockBase extends Block{

    public BlockBase(Material materialIn, String unlName, String regName) {
        super(materialIn);
        this.setCreativeTab(Reference.creativeTab);

        this.setUnlocalizedName(unlName);
        this.setRegistryName(Reference.MOD_ID, regName);
    }

    // INFO: Muss ich mir erklären lassen...
    @Override
    public BlockBase setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(tab);
        return this;
    }
}
