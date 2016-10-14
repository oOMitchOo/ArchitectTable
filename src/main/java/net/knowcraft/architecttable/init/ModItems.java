package net.knowcraft.architecttable.init;

import net.knowcraft.architecttable.helper.LogHelper;
import net.knowcraft.architecttable.item.ItemBase;
import net.knowcraft.architecttable.item.ItemDrawingTools;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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

    public static void registerModels() {
        // TODO: ModelResourceLocation trouble shooten.
        LogHelper.error(DRAWING_TOOLS.getUnlocalizedName());
        ModelLoader.setCustomModelResourceLocation(DRAWING_TOOLS, 0, new ModelResourceLocation(DRAWING_TOOLS.getUnlocalizedName(), "inventory"));
    }
}
