package net.knowcraft.architecttable.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

/**
 * Created by oOMitchOo on 14.10.2016.
 */
public class BlockArchitectTable extends BlockBase {
    private static final PropertyEnum<BlockArchitectTable.EnumArcTablePart> PART = PropertyEnum.create("part", BlockArchitectTable.EnumArcTablePart.class);
    private static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);

    public BlockArchitectTable(String unlName, String regName) {
        super(Material.WOOD, unlName, regName);
        this.setSoundType(SoundType.WOOD);

        this.setDefaultState(this.blockState.getBaseState().withProperty(PART, EnumArcTablePart.TABLE_LEFT).withProperty(FACING, EnumFacing.SOUTH));
    }

    // WARNUNG: Könnte bald veraltet sein.
    @Override
    public int damageDropped(IBlockState state)
    {
        return ((EnumArcTablePart)state.getValue(PART)).getMetadata();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        for (EnumArcTablePart enumtablepart : EnumArcTablePart.values())
        {
            list.add(new ItemStack(itemIn, 1, enumtablepart.getMetadata()));
        }
    }

     @Override
     public Item getItemDropped(IBlockState state, Random rand, int fortune)
     {
         // So wird in BlockBed nach der State gecheckt:
         // state.getValue(PART) == BlockBed.EnumPartType.FOOT
         // Ersetze ich mal mit dem Vorigen MetaDatenVergleich:
         // int metaFromState = state.getBlock().damageDropped(state);

         if (state.getValue(PART) == EnumArcTablePart.TABLE_LEFT) {
             return Item.getItemFromBlock(Blocks.PLANKS);
         } else if (state.getValue(PART) == EnumArcTablePart.TABLE_RIGHT) {
             return Item.getItemFromBlock(Blocks.CHEST);
         } else if (state.getValue(PART) == EnumArcTablePart.PINBOARD_LEFT || state.getValue(PART) == EnumArcTablePart.PINBOARD_RIGHT) {
             return Items.SIGN;
         } else {
             return null;
         }
     }

     // Muss ich eventuell nicht überschreiben, da die Blöcke ja nicht per Hand gesetzt werden (sich also nicht nach einem Block-Update anpassen müssen)
     // Die Blöcke werden vielmehr vom Item-Event ein mal gesetzt und danach höchstens wieder zerstört - nicht geändert.
     /*
     @Override
     public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
     {
         // This is from the Fence-Block:
         // state.withProperty(NORTH, Boolean.valueOf(this.canConnectTo(worldIn, pos.north())))
         // .withProperty(EAST, Boolean.valueOf(this.canConnectTo(worldIn, pos.east())))
         // .withProperty(SOUTH, Boolean.valueOf(this.canConnectTo(worldIn, pos.south())))
         // .withProperty(WEST, Boolean.valueOf(this.canConnectTo(worldIn, pos.west())));
         return state;
     }
     */

     @Override
     public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
     {
         // TODO: Hier die TileEntity für die Truhe zerstören, sobald diese implementiert ist.
         /*
         if (hasTileEntity(state) && !(this instanceof BlockContainer))
         {
             worldIn.removeTileEntity(pos);
         }
         */

         int partMeta = state.getValue(PART).getMetadata();
         // S-W-N-E ist 0-1-2-3
         int facingIndex = state.getValue(FACING).getHorizontalIndex();
         int yOffset = 1;
         int sideOffset = 1;


         // Part und Facing bestimmt wie zerstört wird.
         // Für oben muss unten zerstört werden (& vice versa) // Für links muss rechts zerstört werden (& vice versa).
         //
         //     #---#---#
         //     | 2 | 3 |
         //     #---#---#   Für yOffset = 1 und sideOffset = 1 werden {1,2,3} zerstört.
         //     | 0 | 1 |
         //     #---#---#
         //
         // x- und y-Offset verschiebt und/oder spiegelt die 3 Zerstör-Positionen
         if (partMeta == 0) {
             yOffset = 1;
             sideOffset = 1;
             // Vielleicht falsch, da destroyBlock wohl dropBlockAsItem macht.
             // worldIn.destroyBlock(pos.east(),true);
             // worldIn.destroyBlock(pos.up(), true);
         } else if (partMeta == 1) {
             yOffset = 1;
             sideOffset = -1;
         } else if (partMeta == 2) {
             yOffset = -1;
             sideOffset = 1;
         } else if (partMeta == 3) {
             yOffset = -1;
             sideOffset = -1;
         }

         if (facingIndex == 0) {
             // Facing South
             worldIn.destroyBlock(pos.add(0, yOffset, 0), true);
             worldIn.destroyBlock(pos.add(sideOffset, 0, 0), true);
             worldIn.destroyBlock(pos.add(sideOffset, yOffset, 0), true);
         } else if (facingIndex == 1) {
             worldIn.destroyBlock(pos.add(0, yOffset, 0), true);
             worldIn.destroyBlock(pos.add(0, 0, sideOffset), true);
             worldIn.destroyBlock(pos.add(0, yOffset, sideOffset), true);
         } else if (facingIndex == 2) {
             worldIn.destroyBlock(pos.add(0, yOffset, 0), true);
             worldIn.destroyBlock(pos.add(-sideOffset, 0, 0), true);
             worldIn.destroyBlock(pos.add(-sideOffset, yOffset, 0), true);
         } else if (facingIndex == 3) {
             worldIn.destroyBlock(pos.add(0, yOffset, 0), true);
             worldIn.destroyBlock(pos.add(0, 0, -sideOffset), true);
             worldIn.destroyBlock(pos.add(0, yOffset, -sideOffset), true);
         }
     }

    public static enum EnumArcTablePart implements IStringSerializable {
        TABLE_LEFT("tableLeft", 0),
        TABLE_RIGHT("tableRight", 1),
        PINBOARD_LEFT("pinBoardLeft", 2),
        PINBOARD_RIGHT("pinBoardRight", 3);

        private final String name;
        private final int meta;

        private EnumArcTablePart(String name, int meta)
        {
            this.name = name;
            this.meta = meta;
        }

        public int getMetadata(){return this.meta;}

        public String toString(){return this.name;}

        public String getName(){return this.name;}
    }
}
