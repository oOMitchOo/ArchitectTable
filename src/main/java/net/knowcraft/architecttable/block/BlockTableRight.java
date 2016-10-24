package net.knowcraft.architecttable.block;

import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
public class BlockTableRight extends BlockArchitectTableNEW {
    public BlockTableRight(String unlName, String regName) {
        super(unlName, regName);

        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.OAK).withProperty(FACING, EnumFacing.SOUTH));
    }

    /** The boundingBoxes determine the colliding with the blocks. */
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) { return TABLE_COLLIDING_BB; }

    /** The selectedBoundingBox is the black lines when looking at a block.
     * They must all be offset by the BlockPos of the block which is been looked at. (rendering stuff) */
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) { return TABLE_SELECTING_BB; }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) { return Item.getItemFromBlock(Blocks.CHEST); }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        // The direction in which BlockTableLeft lies determines which facing direction the multiBlock (hence this block) will have.
        // Example: If BlockTableLeft lies north from BlockTableRight the blocks are facing west.
        if (worldIn.getBlockState(pos.north()).getBlock() instanceof BlockTableLeft) {
            return state.withProperty(FACING, EnumFacing.WEST);
        } else if (worldIn.getBlockState(pos.south()).getBlock() instanceof BlockTableLeft) {
            return state.withProperty(FACING, EnumFacing.EAST);
        } else if (worldIn.getBlockState(pos.west()).getBlock() instanceof BlockTableLeft) {
            return state.withProperty(FACING, EnumFacing.SOUTH);
        } else return state.withProperty(FACING, EnumFacing.NORTH);
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
            case NORTH: // facing north means BlockTableLeft must be east of it.
                worldIn.destroyBlock(pos.up(), dropItems);
                worldIn.destroyBlock(pos.east(), dropItems);
                worldIn.destroyBlock(pos.east().up(), dropItems);
                break;
            case SOUTH: // facing south means BlockTableLeft must be west of it.
                worldIn.destroyBlock(pos.up(), dropItems);
                worldIn.destroyBlock(pos.west(), dropItems);
                worldIn.destroyBlock(pos.west().up(), dropItems);
                break;
            case WEST: // facing west means BlockTableLeft must be north of it.
                worldIn.destroyBlock(pos.up(), dropItems);
                worldIn.destroyBlock(pos.north(), dropItems);
                worldIn.destroyBlock(pos.north().up(), dropItems);
                break;
            case EAST: // facing east means BlockTableLeft must be south of it.
                worldIn.destroyBlock(pos.up(), dropItems);
                worldIn.destroyBlock(pos.south(), dropItems);
                worldIn.destroyBlock(pos.south().up(), dropItems);
                break;
            default: break;
        }
    }

    /** Helping method */
    private boolean isPartOfFullMultiBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing blockFacing = this.getActualState(state, worldIn, pos).getValue(FACING);

        switch (blockFacing) {
            case NORTH: // facing north means BlockTableLeft must be east of it.
                return (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockPinboardRight && worldIn.getBlockState(pos.east()).getBlock() instanceof BlockTableLeft && worldIn.getBlockState(pos.east().up()).getBlock() instanceof BlockPinboardLeft);
            case SOUTH: // facing south means BlockTableLeft must be west of it.
                return (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockPinboardRight && worldIn.getBlockState(pos.west()).getBlock() instanceof BlockTableLeft && worldIn.getBlockState(pos.west().up()).getBlock() instanceof BlockPinboardLeft);
            case WEST: // facing west means BlockTableLeft must be north of it.
                return (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockPinboardRight && worldIn.getBlockState(pos.north()).getBlock() instanceof BlockTableLeft && worldIn.getBlockState(pos.north().up()).getBlock() instanceof BlockPinboardLeft);
            case EAST: // facing east means BlockTableLeft must be south of it.
                return (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockPinboardRight && worldIn.getBlockState(pos.south()).getBlock() instanceof BlockTableLeft && worldIn.getBlockState(pos.south().up()).getBlock() instanceof BlockPinboardLeft);
            default: return false;
        }
    }
}
