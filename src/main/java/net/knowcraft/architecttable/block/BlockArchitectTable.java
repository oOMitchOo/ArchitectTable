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
import net.minecraft.entity.player.EntityPlayer;
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
    // Bounding boxes for: pinboard_left N-S-W-E then pinboard_right N-S-W-E.
    private static final AxisAlignedBB[] PINBOARD_COLLIDING_BB = new AxisAlignedBB[] {new AxisAlignedBB(0.0D, 0.03125D, 0.9375D, 0.96875D, 0.96875D, 1.0D), new AxisAlignedBB(0.03125D, 0.03125D, 0.0D, 1.0D, 0.96875D, 0.0625D), new AxisAlignedBB(0.9375D, 0.03125D, 0.03125D, 1.0D, 0.96875D, 1.0D), new AxisAlignedBB(0.0D, 0.03125D, 0.0D, 0.0625D, 0.96875D, 0.96875D), new AxisAlignedBB(0.03125D, 0.03125D, 0.9375D, 1.0D, 0.96875D, 1.0D), new AxisAlignedBB(0.0D, 0.03125D, 0.0D, 0.96875D, 0.96875D, 0.0625D), new AxisAlignedBB(0.9375D, 0.03125D, 0.0D, 1.0D, 0.96875D, 0.96875D), new AxisAlignedBB(0.0D, 0.03125D, 0.03125D, 0.0625D, 0.96875D, 1.0D)};
    private static final AxisAlignedBB[] PINBOARD_SELECTING_BB = new AxisAlignedBB[] {new AxisAlignedBB(0.03125D, 0.03125D, 0.9375D, 1.96875D, 0.96875D, 1.0D), new AxisAlignedBB(0.03125D, 0.03125D, 0.0D, 1.96875D, 0.96875D, 0.0625D), new AxisAlignedBB(0.9375D, 0.03125D, 0.03125D, 1.0D, 0.96875D, 1.96875D), new AxisAlignedBB(0.0D, 0.03125D, 0.03125D, 0.0625D, 0.96875D, 1.96875D)};

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
                        return PINBOARD_COLLIDING_BB[0];
                    case SOUTH:
                        return PINBOARD_COLLIDING_BB[1];
                    case WEST:
                        return PINBOARD_COLLIDING_BB[2];
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
                        return PINBOARD_COLLIDING_BB[4];
                    case SOUTH:
                        return PINBOARD_COLLIDING_BB[5];
                    case WEST:
                        return PINBOARD_COLLIDING_BB[6];
                    case EAST:
                        return PINBOARD_COLLIDING_BB[7];
                }
            default: return TABLE_COLLIDING_BB; // table_left (meta 0) and table_right (meta 1)
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
                        return PINBOARD_SELECTING_BB[0].offset(pos.west());
                    case SOUTH:
                        return PINBOARD_SELECTING_BB[1].offset(pos);
                    case WEST:
                        return PINBOARD_SELECTING_BB[2].offset(pos);
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
                        return PINBOARD_SELECTING_BB[0].offset(pos);
                    case SOUTH:
                        return PINBOARD_SELECTING_BB[1].offset(pos.west());
                    case WEST:
                        return PINBOARD_SELECTING_BB[2].offset(pos.north());
                    case EAST:
                        return PINBOARD_SELECTING_BB[3].offset(pos);
                }
            default: return TABLE_SELECTING_BB.offset(pos); // table_left (meta 0) and table_right (meta 1) - smaller selectingBox (in y) than collidingBox.
        }
    }

    /** This must be overriden for every block with properties. */
    @Override
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, new IProperty[] {PART, FACING}); }

    /** This is called from the ItemBlock class, when the (multi)block is placed into the world to get the BlockState for the placed block.
    /* We are always placing the multiBlock by placing the table_left. */
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

    /** Convert the BlockState into the correct metadata value */
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

    /** Helping method for finding out in which direction the next architectTableBlock lies. */
    private EnumFacing getDirectionOfNeighborArchTableBlock(IBlockAccess worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos.north()).getBlock() instanceof BlockArchitectTable) {
            return EnumFacing.NORTH;
        } else if (worldIn.getBlockState(pos.south()).getBlock() instanceof BlockArchitectTable) {
            return EnumFacing.SOUTH;
        } else if (worldIn.getBlockState(pos.west()).getBlock() instanceof BlockArchitectTable) {
            return EnumFacing.WEST;
        } else return EnumFacing.EAST; // Every ArchTableBlock must have a neighboring ArchTableBlock. Last possibility is east.
    }

    /** Must be defined because only the PART of the blocks are saved in the world data (in form of the meta value 0/1/2/3).
    /* Without it the blocks would always face south. */
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        int partMeta = this.getMetaFromState(state);
        EnumFacing directionNextArchTable = getDirectionOfNeighborArchTableBlock(worldIn, pos);
        // This is returning the multiBlockFacing for every part of the architect table. Example (bird's eye view):
        //  #----#----#----#----#
        //  |    |    |    |    |        N
        //  #----#----#----#----#        |
        //  |    |  x | rt |    |   W----|----E
        //  #----#----#----#----#        |
        //  |    |    |    |    |        S
        //  #----#----#----#----#
        // Let the block at position pos (x) be left_table (partMeta=0) and the block at position rt be right_table.
        // Then directionNextArchTable is East and left_table should face South (East.rotateY()).
        switch (partMeta) {
            case 0: // table_left
                return this.getStateFromMeta(partMeta).withProperty(FACING, directionNextArchTable.rotateY());
            case 1: // table_right
                return this.getStateFromMeta(partMeta).withProperty(FACING, directionNextArchTable.getOpposite().rotateY());
            case 2: // pinboard_left
                return this.getStateFromMeta(partMeta).withProperty(FACING, directionNextArchTable.rotateY());
            case 3: // pinboard_right
                return this.getStateFromMeta(partMeta).withProperty(FACING, directionNextArchTable.getOpposite().rotateY());
            default: return this.getDefaultState(); // Should not be possible.
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
        int yOffset = 0;
        int sideOffset = 0;
        boolean dropItems = true;

        // Part und Facing bestimmt wie zerstört wird.
        // Für oben muss unten zerstört werden (& vice versa) // Für links muss rechts zerstört werden (& vice versa).
        //
        //     #---#---#
        //     | 2 | 3 |    If 0 is the position of pos, yOffset = 1 and sideOffset = 1,
        //     #---#---#    then the blocks on position 1,2,3 will be destroyed.
        //     | 0 | 1 |
        //     #---#---#
        //
        // yOffset and sideOffset moves which three blocks around pos get destroyed.

        switch (partMeta) {
            case 0: // table_left
                yOffset = 1;
                sideOffset = 1;
                break;
            case 1: // table_right
                yOffset = 1;
                sideOffset = -1;
                break;
            case 2: // pinboard_left
                yOffset = -1;
                sideOffset = 1;
                break;
            case 3: // pinboard_right
                yOffset = -1;
                sideOffset = -1;
                break;
        }

        // Don't get any dropped Items, if the destroyingPlayer (more accurate: the player closest by) is in creative. WARNING: Could be buggy with more than one player nearby both in different playing modes.
        EntityPlayer destroyingPlayer = worldIn.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 7.0D, false); // false looks for not-spectators.
        if (destroyingPlayer != null) if(destroyingPlayer.isCreative()) dropItems = false;

        // TODO: This could use some tidy up.
        switch (state.getValue(FACING)) { // multiBlockFacing
            case DOWN: // Should not be possible.
                break;
            case UP: // Should not be possible.
                break;
            case NORTH:
                worldIn.destroyBlock(pos.add(0, yOffset, 0), dropItems);
                worldIn.destroyBlock(pos.add(-sideOffset, 0, 0), dropItems);
                worldIn.destroyBlock(pos.add(-sideOffset, yOffset, 0), dropItems);
                break;
            case SOUTH:
                worldIn.destroyBlock(pos.add(0, yOffset, 0), dropItems);
                worldIn.destroyBlock(pos.add(sideOffset, 0, 0), dropItems);
                worldIn.destroyBlock(pos.add(sideOffset, yOffset, 0), dropItems);
                break;
            case WEST:
                worldIn.destroyBlock(pos.add(0, yOffset, 0), dropItems);
                worldIn.destroyBlock(pos.add(0, 0, sideOffset), dropItems);
                worldIn.destroyBlock(pos.add(0, yOffset, sideOffset), dropItems);
                break;
            case EAST:
                worldIn.destroyBlock(pos.add(0, yOffset, 0), dropItems);
                worldIn.destroyBlock(pos.add(0, 0, -sideOffset), dropItems);
                worldIn.destroyBlock(pos.add(0, yOffset, -sideOffset), dropItems);
                break;
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
