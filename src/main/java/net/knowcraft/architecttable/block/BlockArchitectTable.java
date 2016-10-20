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

    // Determines with which damage value the items are dropped, when the multiBlock is broken.
    @Override
    public int damageDropped(IBlockState state)
    {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) { list.add(new ItemStack(itemIn, 1, 0)); } // We don't want any SubBlocks besides the left_table (in the creativeTab).

    /** The boundingBoxes determine the colliding with the blocks. */
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        // getActualState to get the proper facing before proceeding.
        state = this.getActualState(state, source, pos);

        switch (this.getMetaFromState(state)) {
            case 2: // pinboard_left
                switch (state.getValue(FACING)) {
                    case DOWN: // Should not be possible.
                        break;
                    case UP: // Should not be possible.
                        break;
                    case NORTH:
                        return PINBOARD_COLLIDING_BB[2];
                    case SOUTH:
                        return PINBOARD_COLLIDING_BB[0];
                    case WEST:
                        return PINBOARD_COLLIDING_BB[1];
                    case EAST:
                        return PINBOARD_COLLIDING_BB[3];
                }
            case 3: // pinboard_right
                switch (state.getValue(FACING)) {
                    case DOWN: // Should not be possible.
                        break;
                    case UP: // Should not be possible.
                        break;
                    case NORTH:
                        return PINBOARD_COLLIDING_BB[6];
                    case SOUTH:
                        return PINBOARD_COLLIDING_BB[4];
                    case WEST:
                        return PINBOARD_COLLIDING_BB[5];
                    case EAST:
                        return PINBOARD_COLLIDING_BB[7];
                }
            default: return TABLE_COLLIDING_BB; // table_left and table_right
        }
    }

    /** The selectedBoundingBox is the black lines when looking at a block.
    /* They must all be offset by the BlockPos of the block which is been looked at. (rendering stuff) */
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    {
        // getActualState to get the proper facing before proceeding.
        state = this.getActualState(state, worldIn, pos);

        switch (this.getMetaFromState(state)) {
            case 2: // pinboard_left - the selectingBB goes over both halfs of the pinboard. (no more dividing the pinboard when looking at it)
                switch (state.getValue(FACING)) {
                    case DOWN: // Should not be possible.
                        break;
                    case UP: // Should not be possible.
                        break;
                    case NORTH:
                        return PINBOARD_SELECTING_BB[2].offset(pos.west());
                    case SOUTH:
                        return PINBOARD_SELECTING_BB[0].offset(pos);
                    case WEST:
                        return PINBOARD_SELECTING_BB[1].offset(pos);
                    case EAST:
                        return PINBOARD_SELECTING_BB[3].offset(pos.north());
                }
            case 3: // pinboard_right - the selectingBB goes over both halfs of the pinboard. (no more dividing the pinboard when looking at it)
                switch (state.getValue(FACING)) {
                    case DOWN: // Should not be possible.
                        break;
                    case UP: // Should not be possible.
                        break;
                    case NORTH:
                        return PINBOARD_SELECTING_BB[2].offset(pos);
                    case SOUTH:
                        return PINBOARD_SELECTING_BB[0].offset(pos.west());
                    case WEST:
                        return PINBOARD_SELECTING_BB[1].offset(pos.north());
                    case EAST:
                        return PINBOARD_SELECTING_BB[3].offset(pos);
                }
            default: return TABLE_SELECTING_BB.offset(pos); // table_left and table_right - smaller selectingBox (in y) than collidingBox.
        }
    }

    /** This must be overriden for every block with properties. */
    @Override
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, new IProperty[] {PART, FACING}); }

    // This is called from the ItemBlock class, when the (multi)block is placed into the world to get the BlockState for the placed block.
    // We are always placing the multiBlock by placing the table_left.
    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        // We want the block(s) facing in the opposite direction of the players facing direction.
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    /** This is used together with getMetaFromState when saving the world / reloading the data from a saved world into BlockStates. (the direction is set in getActualState dynamically) */
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        switch (meta) {
            case 0:
                return this.getDefaultState();
            case 1:
                return this.getDefaultState().withProperty(PART, EnumArcTablePart.TABLE_RIGHT);
            case 2:
                return this.getDefaultState().withProperty(PART, EnumArcTablePart.PINBOARD_LEFT);
            case 3:
                return this.getDefaultState().withProperty(PART, EnumArcTablePart.PINBOARD_RIGHT);
            default: return this.getDefaultState();
        }
    }

    /**Convert the BlockState into the correct metadata value*/
    @Override
    public int getMetaFromState(IBlockState state) { return state.getValue(PART).getMetadata(); }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        switch (state.getValue(PART)) {
            case TABLE_LEFT:
                return Item.getItemFromBlock(Blocks.PLANKS);
            case TABLE_RIGHT:
                return Item.getItemFromBlock(Blocks.CHEST);
            case PINBOARD_LEFT:
                return Items.SIGN;
            case PINBOARD_RIGHT:
                return Items.SIGN;
            default: return null;
        }
    }


    // TODO: AB HIER WEITER! Aufräumen...
    /** Must be defined because only the PART of the blocks are saved in the world data (in form of the meta value 0/1/2/3).
    /* Without it the blocks would always face south. */
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        int partMeta = this.getMetaFromState(state);

        if (partMeta == 0) {
            // partMeta 0 means this is table_left.
            // Die Richtung, in der der table_right davon liegt, bestimmt die Richtung des MultiBlocks.
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

        // We need to getActualState so the facing fits the actual multiBlockFacing.
        state = this.getActualState(state, worldIn, pos);
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
