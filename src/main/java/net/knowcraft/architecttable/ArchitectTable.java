package net.knowcraft.architecttable;

import net.knowcraft.architecttable.block.BlockArchitectTable;
import net.knowcraft.architecttable.client.ModCreativeTab;
import net.knowcraft.architecttable.helper.LogHelper;
import net.knowcraft.architecttable.init.ModBlocks;
import net.knowcraft.architecttable.init.ModItems;
import net.knowcraft.architecttable.proxy.IProxy;
import net.knowcraft.architecttable.reference.Reference;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by oOMitchOo on 14.10.2016.
 */

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION)
public class ArchitectTable {

    @Mod.Instance(Reference.MOD_ID)
    public static ArchitectTable instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ModItems.init();
        ModItems.register();
        ModItems.registerRecipes();
        proxy.registerItemModels();

        ModBlocks.init();
        ModBlocks.register();
        proxy.registerItemBlockModels();

        // Gibt aus: PropertyEnum{name=part, clazz=class net.knowcraft.architecttable.block.BlockArchitectTable$EnumArcTablePart, values=[table_left, table_right, pinboard_left, pinboard_right]}
        // LogHelper.error(BlockArchitectTable.PART.toString);
        // Gibt aus: [PropertyEnum{name=facing, clazz=class net.minecraft.util.EnumFacing, values=[down, up, north, south, west, east]}, PropertyEnum{name=part, clazz=class net.knowcraft.architecttable.block.BlockArchitectTable$EnumArcTablePart, values=[table_left, table_right, pinboard_left, pinboard_right]}]
        // LogHelper.error(ModBlocks.ARCHITECT_TABLE.getBlockState().getProperties());

        LogHelper.info("Pre Initialization Complete!");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {

        LogHelper.info("Initialization Complete!");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {

        LogHelper.info("Post Initialization Complete!");
    }
}
