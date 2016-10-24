package net.knowcraft.architecttable.init;

import net.knowcraft.architecttable.block.*;
import net.knowcraft.architecttable.item.ItemBlockTableLeft;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by oOMitchOo on 14.10.2016.
 */
public class ModBlocks {
    // Blocks
    public static Block TABLE_LEFT;
    public static Block TABLE_RIGHT;
    public static Block PINBOARD_LEFT;
    public static Block PINBOARD_RIGHT;

    // ItemBlocks
    public static ItemBlock ITEMBLOCK_TABLE_LEFT;

    public static void init() {
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
        for (BlockPlanks.EnumType blockplanks$enumtype : BlockPlanks.EnumType.values())
        {
            ModelLoader.setCustomModelResourceLocation(ITEMBLOCK_TABLE_LEFT, blockplanks$enumtype.getMetadata(), new ModelResourceLocation(ITEMBLOCK_TABLE_LEFT.getRegistryName()+"_"+blockplanks$enumtype.getName(), "inventory"));
        }
    }
}
