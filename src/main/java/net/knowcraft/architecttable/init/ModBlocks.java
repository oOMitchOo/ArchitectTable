package net.knowcraft.architecttable.init;

import net.knowcraft.architecttable.block.BlockArchitectTable;
import net.knowcraft.architecttable.item.ItemBlockArchitectTable;
import net.knowcraft.architecttable.reference.Reference;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by oOMitchOo on 14.10.2016.
 */
public class ModBlocks {
    public static Block ARCHITECT_TABLE;
    public static ItemBlock ITEMBLOCK_ARCHITECT_TABLE;

    public static void init() {
        ARCHITECT_TABLE = new BlockArchitectTable("architectTable", "architect_table");
        ITEMBLOCK_ARCHITECT_TABLE = new ItemBlockArchitectTable(ARCHITECT_TABLE, "architectTable", "architect_table");
    }

    public static void register()
    {
        GameRegistry.register(ARCHITECT_TABLE);
        GameRegistry.register(ITEMBLOCK_ARCHITECT_TABLE);
    }

    public static void registerItemBlockModels()
    {
        // getRegistryName() returns: "architecttable:architect_table"
        ModelLoader.setCustomModelResourceLocation(ITEMBLOCK_ARCHITECT_TABLE, 0, new ModelResourceLocation(ITEMBLOCK_ARCHITECT_TABLE.getRegistryName(), "inventory"));
    }
}
