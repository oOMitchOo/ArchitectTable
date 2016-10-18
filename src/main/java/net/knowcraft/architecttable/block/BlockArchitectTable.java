package net.knowcraft.architecttable.block;

import net.knowcraft.architecttable.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Created by oOMitchOo on 14.10.2016.
 */

// TODO: TileEntities für den Tisch bauen. Sonst funktionieren die vielen BlockStates nicht.
public class BlockArchitectTable extends BlockBase {
    public static final PropertyEnum<BlockArchitectTable.EnumArcTablePart> PART = PropertyEnum.create("part", BlockArchitectTable.EnumArcTablePart.class);
    public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);
    private static final AxisAlignedBB TABLE_COLLIDING_BB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.8125D, 1.0D);
    private static final AxisAlignedBB TABLE_SELECTING_BB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D);
    // Bounding boxes for: pinboard_left south, west, north, east then pinboard_right south, west, north east.
    private static final AxisAlignedBB[] PINBOARD_COLLIDING_BB = new AxisAlignedBB[] {new AxisAlignedBB(0.03125D, 0.03125D, 0.0D, 1.0D, 0.96875D, 0.0625D), new AxisAlignedBB(0.9375D, 0.03125D, 0.03125D, 1.0D, 0.96875D, 1.0D), new AxisAlignedBB(0.0D, 0.03125D, 0.9375D, 0.96875D, 0.96875D, 1.0D), new AxisAlignedBB(0.0D, 0.03125D, 0.0D, 0.0625D, 0.96875D, 0.96875D), new AxisAlignedBB(0.0D, 0.03125D, 0.0D, 0.96875D, 0.96875D, 0.0625D), new AxisAlignedBB(0.9375D, 0.03125D, 0.0D, 1.0D, 0.96875D, 0.96875D), new AxisAlignedBB(0.03125D, 0.03125D, 0.9375D, 1.0D, 0.96875D, 1.0D), new AxisAlignedBB(0.0D, 0.03125D, 0.03125D, 0.0625D, 0.96875D, 1.0D)};
    private static final AxisAlignedBB[] PINBOARD_SELECTING_BB = new AxisAlignedBB[] {new AxisAlignedBB(0.03125D, 0.03125D, 0.0D, 1.96875D, 0.96875D, 0.0625D), new AxisAlignedBB(0.9375D, 0.03125D, 0.03125D, 1.0D, 0.96875D, 1.96875D), new AxisAlignedBB(0.03125D, 0.03125D, 0.9375D, 1.96875D, 0.96875D, 1.0D), new AxisAlignedBB(0.0D, 0.03125D, 0.03125D, 0.0625D, 0.96875D, 1.96875D)};

    public BlockArchitectTable(String unlName, String regName) {
        super(Material.WOOD, unlName, regName);
        this.setSoundType(SoundType.WOOD);
        this.setHardness(2.0F);
        this.setResistance(5.0F);

        this.setDefaultState(this.blockState.getBaseState().withProperty(PART, EnumArcTablePart.TABLE_LEFT).withProperty(FACING, EnumFacing.SOUTH));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return false;
    }

    // WARNUNG: Könnte bald veraltet sein.
    @Override
    public int damageDropped(IBlockState state)
    {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        list.add(new ItemStack(itemIn, 1, 0));
    }

    // Gibt ja nach BlockState die BoundingBox aus. Die Pinboardhälften sind keine ganzen Blöcke.
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        state = this.getActualState(state, source, pos);

        if (this.getMetaFromState(state) == 0 || this.getMetaFromState(state) == 1) {
            return TABLE_COLLIDING_BB;
        } else if (this.getMetaFromState(state) == 2) {
            // if lookin south
            if (state.getValue(FACING) == EnumFacing.SOUTH) {
                return PINBOARD_COLLIDING_BB[0];
            } else if (state.getValue(FACING) == EnumFacing.WEST) {
                return PINBOARD_COLLIDING_BB[1];
            } else if (state.getValue(FACING) == EnumFacing.NORTH) {
                return PINBOARD_COLLIDING_BB[2];
            } else {
                // Didn't check for facing east, because up and down should never appear.
                return PINBOARD_COLLIDING_BB[3];
            }
        } else if (this.getMetaFromState(state) == 3) {
            if (state.getValue(FACING) == EnumFacing.SOUTH) {
                return PINBOARD_COLLIDING_BB[4];
            } else if (state.getValue(FACING) == EnumFacing.WEST) {
                return PINBOARD_COLLIDING_BB[5];
            } else if (state.getValue(FACING) == EnumFacing.NORTH) {
                return PINBOARD_COLLIDING_BB[6];
            } else {
                // Didn't check for facing east, because up and down should never appear.
                return PINBOARD_COLLIDING_BB[7];
            }
        } else {
            // Should not be possible.
            return FULL_BLOCK_AABB;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    {
        state = this.getActualState(state, worldIn, pos);
        // If the block is table_left or table_right the selectBoundingBox should be a bit smaller (in y) than the collisionBoundinBox.
        if (this.getMetaFromState(state) == 0 || this.getMetaFromState(state) == 1) {
            // Those are table_left and table_right
            return TABLE_SELECTING_BB.offset(pos);
        } else if (this.getMetaFromState(state) == 2) {
            // No more split selectBoundingBox for the pinboard_left.
            if (state.getValue(FACING) == EnumFacing.SOUTH) {
                return PINBOARD_SELECTING_BB[0].offset(pos);
            } else if (state.getValue(FACING) == EnumFacing.WEST) {
                return PINBOARD_SELECTING_BB[1].offset(pos);
            } else if (state.getValue(FACING) == EnumFacing.NORTH) {
                return PINBOARD_SELECTING_BB[2].offset(pos.west());
            } else {
                // Ist facing east.
                return PINBOARD_SELECTING_BB[3].offset(pos.north());
            }
        } else if (this.getMetaFromState(state) == 3) {
            // No more split selectBoundingBox for the pinboard_right.
            if (state.getValue(FACING) == EnumFacing.SOUTH) {
                return PINBOARD_SELECTING_BB[0].offset(pos.west());
            } else if (state.getValue(FACING) == EnumFacing.WEST) {
                return PINBOARD_SELECTING_BB[1].offset(pos.north());
            } else if (state.getValue(FACING) == EnumFacing.NORTH) {
                return PINBOARD_SELECTING_BB[2].offset(pos);
            } else {
                // Ist facing east.
                return PINBOARD_SELECTING_BB[3].offset(pos);
            }
        } else {
            // should not be possible
            return state.getBoundingBox(worldIn, pos).offset(pos);
        }
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        //IProperty[] properties = new IProperty[2];
        //properties[0] = PART;
        //properties[1] = FACING;
        return new BlockStateContainer(this, new IProperty[] {PART, FACING});
    }

    // Wird direkt aufgerufen, um die genaue BlockState zu erfassen, wenn der Block das BlockItems (mit placeBlockAt()) gesetzt wird.
    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        // Wir wollen, dass die Block-Richtung entgegen der horizontalen Spieler-Guck-Richtung ist.
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    // Vielleicht überflüssig, da onBlockPlaced die Methode nicht aufruft, sondern gleich den Multiblock baut.
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        // return this.getDefaultState();
        if (meta == 0) {
            return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
        } else if (meta == 1) {
            return this.getDefaultState().withProperty(PART, EnumArcTablePart.TABLE_RIGHT).withProperty(FACING, EnumFacing.SOUTH);
        } else if (meta == 2) {
            return this.getDefaultState().withProperty(PART, EnumArcTablePart.PINBOARD_LEFT).withProperty(FACING, EnumFacing.SOUTH);
        } else if (meta == 3) {
            return this.getDefaultState().withProperty(PART, EnumArcTablePart.PINBOARD_RIGHT).withProperty(FACING, EnumFacing.SOUTH);
        } else throw new IllegalArgumentException("The ArchitectTable meta: " + meta + " has none of the four EnumArcTablePart values.");
    }

    // Was wollen wir hier? Man soll im Spiel ja eigentlich nur den LEFT_TABLE handeln. Alle anderen sollen nicht als Item vorliegen. Aber schon als Block.
    // Wir testen mal für Tisch-Teil einen anderen Meta-Value.
    /**Convert the BlockState into the correct metadata value*/
    @Override
    public int getMetaFromState(IBlockState state)
    {
        if(state.getValue(PART) == EnumArcTablePart.TABLE_LEFT) {
            return 0;
        } else if (state.getValue(PART) == EnumArcTablePart.TABLE_RIGHT) {
            return 1;
        } else if (state.getValue(PART) == EnumArcTablePart.PINBOARD_LEFT) {
            return 2;
        } else if (state.getValue(PART) == EnumArcTablePart.PINBOARD_RIGHT) {
            return 3;
        } else {
            throw new IllegalArgumentException("The ArchitectTable state: " + state + " has none of the four EnumArcTablePart values.");
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
         // So wird in BlockBed nach der State gecheckt:
         // state.getValue(PART) == BlockBed.EnumPartType.FOOT
         // Ersetze ich mal mit dem Vorigen MetaDatenVergleich:
         // int metaFromState = state.getBlock().damageDropped(state);
         // Block.dropBlockAsItem

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

     // Muss definiert sein, da der Block in den Welt-Daten nur mit 16 bit Informationen abgespeichert wird.
     // D. h. nach jedem Neustart würde der Multiblock in Richtung Süden schauen.
     @Override
     public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
     {
         int partMeta = this.getMetaFromState(state);

         if (partMeta == 0) {
             // partMeta 0 means this is table_left.
             // Vorher hatte ich worldIn.getBlockState(pos.DIRECT()) == ModBlocks.ARCHITECT_TABLE <- Warum immer false?
             // worldIn.getBlockState(pos.DIRECT()) instanceof BlockArchitectTable <- Ist auch immer false. Warum?
             if (worldIn.getBlockState(pos.north()).getBlock() instanceof BlockArchitectTable) {
                 return this.getStateFromMeta(partMeta).withProperty(FACING, EnumFacing.EAST);
             } else if (worldIn.getBlockState(pos.east()).getBlock() instanceof BlockArchitectTable) {
                 return this.getStateFromMeta(partMeta).withProperty(FACING, EnumFacing.SOUTH);
             } else if (worldIn.getBlockState(pos.south()).getBlock() instanceof BlockArchitectTable) {
                 return this.getStateFromMeta(partMeta).withProperty(FACING, EnumFacing.WEST);
             } else if (worldIn.getBlockState(pos.west()).getBlock() instanceof BlockArchitectTable) {
                 return this.getStateFromMeta(partMeta).withProperty(FACING, EnumFacing.NORTH);
             } else {
                 // Should not be possible.
                 return this.getDefaultState();
         }
         } else if (partMeta == 1) {
             // partMeta 1 means this is table_right.
             if (worldIn.getBlockState(pos.north()).getBlock() instanceof BlockArchitectTable) {
                 return this.getStateFromMeta(partMeta).withProperty(FACING, EnumFacing.WEST);
             } else if (worldIn.getBlockState(pos.east()).getBlock() instanceof BlockArchitectTable) {
                 return this.getStateFromMeta(partMeta).withProperty(FACING, EnumFacing.NORTH);
             } else if (worldIn.getBlockState(pos.south()).getBlock() instanceof BlockArchitectTable) {
                 return this.getStateFromMeta(partMeta).withProperty(FACING, EnumFacing.EAST);
             } else if (worldIn.getBlockState(pos.west()).getBlock() instanceof BlockArchitectTable) {
                 return this.getStateFromMeta(partMeta).withProperty(FACING, EnumFacing.SOUTH);
             } else {
                 // Should not be possible.
                 return this.getStateFromMeta(partMeta);
             }
         } else if (partMeta == 2) {
             // partMeta 2 means this is pinboard_left.
             if (worldIn.getBlockState(pos.north()).getBlock() instanceof BlockArchitectTable) {
                 return this.getStateFromMeta(partMeta).withProperty(FACING, EnumFacing.EAST);
             } else if (worldIn.getBlockState(pos.east()).getBlock() instanceof BlockArchitectTable) {
                 return this.getStateFromMeta(partMeta).withProperty(FACING, EnumFacing.SOUTH);
             } else if (worldIn.getBlockState(pos.south()).getBlock() instanceof BlockArchitectTable) {
                 return this.getStateFromMeta(partMeta).withProperty(FACING, EnumFacing.WEST);
             } else if (worldIn.getBlockState(pos.west()).getBlock() instanceof BlockArchitectTable) {
                 return this.getStateFromMeta(partMeta).withProperty(FACING, EnumFacing.NORTH);
             } else {
                 // Should not be possible.
                 return this.getStateFromMeta(partMeta);
             }
         } else if (partMeta == 3) {
             // partMeta 3 means this is pinboard_right.
             if (worldIn.getBlockState(pos.north()).getBlock() instanceof BlockArchitectTable) {
                 return this.getStateFromMeta(partMeta).withProperty(FACING, EnumFacing.WEST);
             } else if (worldIn.getBlockState(pos.east()).getBlock() instanceof BlockArchitectTable) {
                 return this.getStateFromMeta(partMeta).withProperty(FACING, EnumFacing.NORTH);
             } else if (worldIn.getBlockState(pos.south()).getBlock() instanceof BlockArchitectTable) {
                 return this.getStateFromMeta(partMeta).withProperty(FACING, EnumFacing.EAST);
             } else if (worldIn.getBlockState(pos.west()).getBlock() instanceof BlockArchitectTable) {
                 return this.getStateFromMeta(partMeta).withProperty(FACING, EnumFacing.SOUTH);
             } else {
                 // Should not be possible.
                 return this.getStateFromMeta(partMeta);
             }
         } else {
             // Should not be possible.
             return this.getDefaultState();
         }
     }

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

    public enum EnumArcTablePart implements IStringSerializable {
        TABLE_LEFT("table_left", 0),
        TABLE_RIGHT("table_right", 1),
        PINBOARD_LEFT("pinboard_left", 2),
        PINBOARD_RIGHT("pinboard_right", 3);

        private final String name;
        private final int meta;

        EnumArcTablePart(String name, int meta)
        {
            this.name = name;
            this.meta = meta;
        }

        public int getMetadata(){return this.meta;}

        public String toString(){return this.name;}

        public String getName(){return this.name;}
    }
}
