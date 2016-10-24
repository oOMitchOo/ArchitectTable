package net.knowcraft.architecttable.init;

import net.knowcraft.architecttable.item.ItemBase;
import net.knowcraft.architecttable.item.ItemDrawingTools;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by oOMitchOo on 14.10.2016.
 */
public class ModItems {
    public static ItemBase DRAWING_TOOLS;

    public static void init() {
        DRAWING_TOOLS = new ItemDrawingTools("drawingTools", "drawing_tools");
    }

    public static void register() {
        GameRegistry.register(DRAWING_TOOLS);
    }

    public static void registerRecipes() {
        GameRegistry.addShapedRecipe(new ItemStack(DRAWING_TOOLS),
                "AA ",
                "AAB",
                " BB",
                'A', new ItemStack(Items.PAPER), 'B', new ItemStack(Items.IRON_INGOT));
    }

    public static void registerModels() {
        // LogHelper.error("Item's unlocalizedName:"+DRAWING_TOOLS.getUnlocalizedName()); returns item.drawingTools
        // ... getRegistryName() returns architecttable:drawing_tools
        ModelLoader.setCustomModelResourceLocation(DRAWING_TOOLS, 0, new ModelResourceLocation(DRAWING_TOOLS.getRegistryName(), "inventory"));
    }
}
