package net.knowcraft.architecttable.init;

import net.knowcraft.architecttable.block.BlockArchitectTable;
import net.knowcraft.architecttable.block.BlockBase;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by oOMitchOo on 14.10.2016.
 */
public class ModBlocks {
    public static BlockBase ARCHITECT_TABLE;
    ItemBlock ITEMBLOCK_ARCHITECT_TABLE;

    public static void init() {
        ARCHITECT_TABLE = new BlockArchitectTable("architectTable", "architect_table");

    }

    public static void register() {
        GameRegistry.register(ARCHITECT_TABLE);
    }
}
