package net.knowcraft.architecttable.block;

import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

/**
 * Created by oOMitchOo on 23.10.2016.
 */
public class BlockTableLeft extends BlockArchitectTableNEW {
    private String unlName;

    public BlockTableLeft(String unlName, String regName) {
        super(unlName, regName);

        this.unlName = unlName;

        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.OAK).withProperty(FACING, EnumFacing.SOUTH));
    }

    /** This is used together with getMetaFromState when saving the world / reloading the data from a saved world into BlockStates. (the direction is set in getActualState dynamically) */
    @Override
    public IBlockState getStateFromMeta(int meta) { return this.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.byMetadata(meta)); }

    /** Convert the BlockState into the correct metadata value */
    @Override
    public int getMetaFromState(IBlockState state) { return state.getValue(BlockPlanks.VARIANT).getMetadata(); }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        // The direction in which BlockTableRight lies determines which facing direction the multiBlock (hence this block) will have.
        // Example: If BlockTableRight lies north from BlockTableLeft the blocks are facing east.
        if (worldIn.getBlockState(pos.north()).getBlock() instanceof BlockTableRight) {
            return state.withProperty(FACING, EnumFacing.EAST);
        } else if (worldIn.getBlockState(pos.south()).getBlock() instanceof BlockTableRight) {
            return state.withProperty(FACING, EnumFacing.WEST);
        } else if (worldIn.getBlockState(pos.west()).getBlock() instanceof BlockTableRight) {
            return state.withProperty(FACING, EnumFacing.NORTH);
        } else return state.withProperty(FACING, EnumFacing.SOUTH);
    }

    /** The boundingBoxes determine the colliding with the blocks. */
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) { return TABLE_COLLIDING_BB; }

    /** The selectedBoundingBox is the black lines when looking at a block.
     * They must all be offset by the BlockPos of the block which is been looked at. (rendering stuff) */
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) { return TABLE_SELECTING_BB; }

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

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) { return Item.getItemFromBlock(Blocks.PLANKS); }

    @Override
    public int damageDropped(IBlockState state)
    {
        return state.getValue(BlockPlanks.VARIANT).getMetadata();
    }

    /** Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate */
    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.byMetadata(meta)).withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) // We don't want any SubBlocks besides the left_table (in the creativeTab). But this for all wood types.
    {
        for (BlockPlanks.EnumType blockplanks$enumtype : BlockPlanks.EnumType.values())
        {
            list.add(new ItemStack(itemIn, 1, blockplanks$enumtype.getMetadata()));
        }
    }

    /** This must be overriden for every block with properties. */
    @Override
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, new IProperty[] {BlockPlanks.VARIANT, FACING}); }

    /* ======================================== HELPING METHODS =====================================*/

    private boolean isPartOfFullMultiBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing blockFacing = this.getActualState(state, worldIn, pos).getValue(FACING);

        switch (blockFacing) {
            case NORTH: // facing north means BlockTableRight must be west of it.
                return (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockPinboardLeft && worldIn.getBlockState(pos.west()).getBlock() instanceof BlockTableRight && worldIn.getBlockState(pos.west().up()).getBlock() instanceof BlockPinboardRight);
            case SOUTH: // facing south means BlockTableRight must be east of it.
                return (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockPinboardLeft && worldIn.getBlockState(pos.east()).getBlock() instanceof BlockTableRight && worldIn.getBlockState(pos.east().up()).getBlock() instanceof BlockPinboardRight);
            case WEST: // facing west means BlockTableRight must be south of it.
                return (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockPinboardLeft && worldIn.getBlockState(pos.south()).getBlock() instanceof BlockTableRight && worldIn.getBlockState(pos.south().up()).getBlock() instanceof BlockPinboardRight);
            case EAST: // facing east means BlockTableRight must be north of it.
                return (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockPinboardLeft && worldIn.getBlockState(pos.north()).getBlock() instanceof BlockTableRight && worldIn.getBlockState(pos.north().up()).getBlock() instanceof BlockPinboardRight);
            default: return false;
        }
    }

    private void breakOtherThreeBlocks(World worldIn, BlockPos pos, IBlockState state, boolean dropItems) {
        EnumFacing blockFacing = this.getActualState(state, worldIn, pos).getValue(FACING);

        switch (blockFacing) {
            case NORTH: // facing north means BlockTableRight must be west of it.
                worldIn.destroyBlock(pos.up(), dropItems);
                worldIn.destroyBlock(pos.west(), dropItems);
                worldIn.destroyBlock(pos.west().up(), dropItems);
                break;
            case SOUTH: // facing south means BlockTableRight must be east of it.
                worldIn.destroyBlock(pos.up(), dropItems);
                worldIn.destroyBlock(pos.east(), dropItems);
                worldIn.destroyBlock(pos.east().up(), dropItems);
                break;
            case WEST: // facing west means BlockTableRight must be south of it.
                worldIn.destroyBlock(pos.up(), dropItems);
                worldIn.destroyBlock(pos.south(), dropItems);
                worldIn.destroyBlock(pos.south().up(), dropItems);
                break;
            case EAST: // facing east means BlockTableRight must be north of it.
                worldIn.destroyBlock(pos.up(), dropItems);
                worldIn.destroyBlock(pos.north(), dropItems);
                worldIn.destroyBlock(pos.north().up(), dropItems);
                break;
            default: break;
        }
    }
}
