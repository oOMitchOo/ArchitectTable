package net.knowcraft.architecttable.block;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * Created by oOMitchOo on 23.10.2016.
 */
public class BlockPinboardLeft extends BlockArchitectTableNEW {
    public BlockPinboardLeft(String unlName, String regName) {
        super(unlName, regName);

        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.SOUTH));
    }

    /** This must be overriden for every block with properties. */ // No different Wood-Types for the pinboard for now.
    @Override
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, new IProperty[] { FACING }); }

    /** Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate */
    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    { return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()); }

    /** This is used together with getMetaFromState when saving the world / reloading the data from a saved world into BlockStates. (the direction is set in getActualState dynamically) */
    @Override
    public IBlockState getStateFromMeta(int meta) { return this.getDefaultState(); }

    /** Convert the BlockState into the correct metadata value */
    @Override
    public int getMetaFromState(IBlockState state) { return 0; }

    /** The boundingBoxes determine the colliding with the blocks. */
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        // blockFacing with getActualState to get the proper facing before proceeding.
        EnumFacing facing = this.getActualState(state, source, pos).getValue(FACING);

        switch (facing) {
            case NORTH:
                return PINBOARD_COLLIDING_BB[0];
            case SOUTH:
                return PINBOARD_COLLIDING_BB[1];
            case WEST:
                return PINBOARD_COLLIDING_BB[2];
            case EAST:
                return PINBOARD_COLLIDING_BB[3];
            default: return Block.FULL_BLOCK_AABB; // Should not be possible. (Up and Down)
        }
    }

    /** The selectedBoundingBox is the black lines when looking at a block.
     * They must all be offset by the BlockPos of the block which is been looked at. (rendering stuff) */
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    {
        // blockFacing with getActualState to get the proper facing before proceeding.
        EnumFacing facing = this.getActualState(state, worldIn, pos).getValue(FACING);

        switch (facing) {
            case NORTH:
                return PINBOARD_SELECTING_BB[0].offset(pos.west());
            case SOUTH:
                return PINBOARD_SELECTING_BB[1].offset(pos);
            case WEST:
                return PINBOARD_SELECTING_BB[2].offset(pos);
            case EAST:
                return PINBOARD_SELECTING_BB[3].offset(pos.north());
            default: return Block.FULL_BLOCK_AABB; // Should not be possible. (Up and Down)
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) { return Items.ITEM_FRAME; }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        // The direction in which BlockPinboardRight lies determines which facing direction the multiBlock (hence this block) will have.
        // Example: If BlockPinboardRight lies north from BlockPinboardLeft the blocks are facing east.
        if (worldIn.getBlockState(pos.north()).getBlock() instanceof BlockPinboardRight) {
            return state.withProperty(FACING, EnumFacing.EAST);
        } else if (worldIn.getBlockState(pos.south()).getBlock() instanceof BlockPinboardRight) {
            return state.withProperty(FACING, EnumFacing.WEST);
        } else if (worldIn.getBlockState(pos.west()).getBlock() instanceof BlockPinboardRight) {
            return state.withProperty(FACING, EnumFacing.NORTH);
        } else return state.withProperty(FACING, EnumFacing.SOUTH);
    }

    /** I've put in some effort so that this method only gets executed if a full multiBlock is present. (else non-multiBlock-blocks could get destroyed in the process) */
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        boolean isFullMultiBlock = isPartOfFullMultiBlock(worldIn, pos, state);
        boolean dropItems = true;

        if (!worldIn.isRemote && isFullMultiBlock)
        {
            EntityPlayer destroyingPlayer = worldIn.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 7.0D, false); // false looks for not-spectators.
            if (destroyingPlayer != null) if (destroyingPlayer.isCreative()) dropItems = false;

            breakOtherThreeBlocks(worldIn, pos, state, dropItems);
        }
    }

    /** Helping method */
    private void breakOtherThreeBlocks(World worldIn, BlockPos pos, IBlockState state, boolean dropItems) {
        EnumFacing blockFacing = this.getActualState(state, worldIn, pos).getValue(FACING);

        switch (blockFacing) {
            case NORTH: // facing north means BlockPinboardRight must be west of it.
                worldIn.destroyBlock(pos.down(), dropItems);
                worldIn.destroyBlock(pos.west(), dropItems);
                worldIn.destroyBlock(pos.west().down(), dropItems);
                break;
            case SOUTH: // facing south means BlockPinboardRight must be east of it.
                worldIn.destroyBlock(pos.down(), dropItems);
                worldIn.destroyBlock(pos.east(), dropItems);
                worldIn.destroyBlock(pos.east().down(), dropItems);
                break;
            case WEST: // facing west means BlockPinboardRight must be south of it.
                worldIn.destroyBlock(pos.down(), dropItems);
                worldIn.destroyBlock(pos.south(), dropItems);
                worldIn.destroyBlock(pos.south().down(), dropItems);
                break;
            case EAST: // facing east means BlockPinboardRight must be north of it.
                worldIn.destroyBlock(pos.down(), dropItems);
                worldIn.destroyBlock(pos.north(), dropItems);
                worldIn.destroyBlock(pos.north().down(), dropItems);
                break;
            default: break;
        }
    }

    /** Helping method */
    private boolean isPartOfFullMultiBlock(World worldIn, BlockPos pos, IBlockState state) {
        EnumFacing blockFacing = this.getActualState(state, worldIn, pos).getValue(FACING);

        switch (blockFacing) {
            case NORTH: // facing north means BlockPinboardRight must be west of it.
                return (worldIn.getBlockState(pos.down()).getBlock() instanceof BlockTableLeft && worldIn.getBlockState(pos.west()).getBlock() instanceof BlockPinboardRight && worldIn.getBlockState(pos.west().down()).getBlock() instanceof BlockTableRight);
            case SOUTH: // facing south means BlockPinboardRight must be east of it.
                return (worldIn.getBlockState(pos.down()).getBlock() instanceof BlockTableLeft && worldIn.getBlockState(pos.east()).getBlock() instanceof BlockPinboardRight && worldIn.getBlockState(pos.east().down()).getBlock() instanceof BlockTableRight);
            case WEST: // facing west means BlockPinboardRight must be south of it.
                return (worldIn.getBlockState(pos.down()).getBlock() instanceof BlockTableLeft && worldIn.getBlockState(pos.south()).getBlock() instanceof BlockPinboardRight && worldIn.getBlockState(pos.south().down()).getBlock() instanceof BlockTableRight);
            case EAST: // facing east means BlockPinboardRight must be north of it.
                return (worldIn.getBlockState(pos.down()).getBlock() instanceof BlockTableLeft && worldIn.getBlockState(pos.north()).getBlock() instanceof BlockPinboardRight && worldIn.getBlockState(pos.north().down()).getBlock() instanceof BlockTableRight);
            default: return false;
        }
    }
}
