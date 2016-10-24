package net.knowcraft.architecttable.init;

import net.knowcraft.architecttable.block.*;
import net.knowcraft.architecttable.helper.LogHelper;
import net.knowcraft.architecttable.item.ItemBlockArchitectTable;
import net.knowcraft.architecttable.item.ItemBlockTableLeft;
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
    //public static Block ARCHITECT_TABLE;
    //public static ItemBlock ITEMBLOCK_ARCHITECT_TABLE;

    // Blocks
    public static Block TABLE_LEFT;
    public static Block TABLE_RIGHT;
    public static Block PINBOARD_LEFT;
    public static Block PINBOARD_RIGHT;

    // ItemBlocks
    public static ItemBlock ITEMBLOCK_TABLE_LEFT;

    public static void init() {
        //ARCHITECT_TABLE = new BlockArchitectTable("architectTable", "architect_table");
        //ITEMBLOCK_ARCHITECT_TABLE = new ItemBlockArchitectTable(ARCHITECT_TABLE, "architectTable", "architect_table");

        // Blocks
        TABLE_LEFT = new BlockTableLeft("tableLeft", "table_left");
        TABLE_RIGHT = new BlockTableRight("tableRight", "table_right");
        PINBOARD_LEFT = new BlockPinboardLeft("pinboardLeft", "pinboard_left");
        PINBOARD_RIGHT = new BlockPinboardRight("pinboardRight", "pinboard_right");

        // ItemBlocks
        ITEMBLOCK_TABLE_LEFT = new ItemBlockTableLeft(TABLE_LEFT, "tableLeft", "table_left");
    }

    public static void register()
    {
        //GameRegistry.register(ARCHITECT_TABLE);
        //GameRegistry.register(ITEMBLOCK_ARCHITECT_TABLE);

        // Blocks
        GameRegistry.register(TABLE_LEFT);
        GameRegistry.register(TABLE_RIGHT);
        GameRegistry.register(PINBOARD_LEFT);
        GameRegistry.register(PINBOARD_RIGHT);

        // ItemBlocks
        GameRegistry.register(ITEMBLOCK_TABLE_LEFT);
    }

    public static void registerItemBlockModels()
    {
        // getRegistryName() returns: "architecttable:architect_table"
        //ModelLoader.setCustomModelResourceLocation(ITEMBLOCK_ARCHITECT_TABLE, 0, new ModelResourceLocation(ITEMBLOCK_ARCHITECT_TABLE.getRegistryName(), "inventory"));

        // TODO: Schicke For-Schleife bauen.
        ModelLoader.setCustomModelResourceLocation(ITEMBLOCK_TABLE_LEFT, 0, new ModelResourceLocation(ITEMBLOCK_TABLE_LEFT.getRegistryName()+"_oak", "inventory")); // Oak
        ModelLoader.setCustomModelResourceLocation(ITEMBLOCK_TABLE_LEFT, 1, new ModelResourceLocation(ITEMBLOCK_TABLE_LEFT.getRegistryName()+"_spruce", "inventory")); // Spruce
        ModelLoader.setCustomModelResourceLocation(ITEMBLOCK_TABLE_LEFT, 2, new ModelResourceLocation(ITEMBLOCK_TABLE_LEFT.getRegistryName()+"_birch", "inventory")); // Birch
        ModelLoader.setCustomModelResourceLocation(ITEMBLOCK_TABLE_LEFT, 3, new ModelResourceLocation(ITEMBLOCK_TABLE_LEFT.getRegistryName()+"_jungle", "inventory")); // Jungle
        ModelLoader.setCustomModelResourceLocation(ITEMBLOCK_TABLE_LEFT, 4, new ModelResourceLocation(ITEMBLOCK_TABLE_LEFT.getRegistryName()+"_acacia", "inventory")); // Acacia
        ModelLoader.setCustomModelResourceLocation(ITEMBLOCK_TABLE_LEFT, 5, new ModelResourceLocation(ITEMBLOCK_TABLE_LEFT.getRegistryName()+"_dark_oak", "inventory")); // Dark Oak
    }
}
