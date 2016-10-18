package net.knowcraft.architecttable.proxy;

import net.knowcraft.architecttable.init.ModBlocks;
import net.knowcraft.architecttable.init.ModItems;

/**
 * Created by oOMitchOo on 14.10.2016.
 */
public class ClientProxy extends CommonProxy{

    @Override
    public void registerItemModels() {
        ModItems.registerModels();
    }

    @Override
    public void registerItemBlockModels() { ModBlocks.registerItemBlockModels(); }
}
