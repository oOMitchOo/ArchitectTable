package net.knowcraft.architecttable.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Created by oOMitchOo on 24.10.2016.
 */
public class BlockArchitectTableNEW extends BlockBase {
    public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);
    public static final AxisAlignedBB TABLE_COLLIDING_BB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.8125D, 1.0D);
    public static final AxisAlignedBB TABLE_SELECTING_BB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D);
    public static final AxisAlignedBB[] PINBOARD_COLLIDING_BB = new AxisAlignedBB[] {new AxisAlignedBB(0.0D, 0.03125D, 0.9375D, 0.96875D, 0.96875D, 1.0D), new AxisAlignedBB(0.03125D, 0.03125D, 0.0D, 1.0D, 0.96875D, 0.0625D), new AxisAlignedBB(0.9375D, 0.03125D, 0.03125D, 1.0D, 0.96875D, 1.0D), new AxisAlignedBB(0.0D, 0.03125D, 0.0D, 0.0625D, 0.96875D, 0.96875D), new AxisAlignedBB(0.03125D, 0.03125D, 0.9375D, 1.0D, 0.96875D, 1.0D), new AxisAlignedBB(0.0D, 0.03125D, 0.0D, 0.96875D, 0.96875D, 0.0625D), new AxisAlignedBB(0.9375D, 0.03125D, 0.0D, 1.0D, 0.96875D, 0.96875D), new AxisAlignedBB(0.0D, 0.03125D, 0.03125D, 0.0625D, 0.96875D, 1.0D)};
    public static final AxisAlignedBB[] PINBOARD_SELECTING_BB = new AxisAlignedBB[] {new AxisAlignedBB(0.03125D, 0.03125D, 0.9375D, 1.96875D, 0.96875D, 1.0D), new AxisAlignedBB(0.03125D, 0.03125D, 0.0D, 1.96875D, 0.96875D, 0.0625D), new AxisAlignedBB(0.9375D, 0.03125D, 0.03125D, 1.0D, 0.96875D, 1.96875D), new AxisAlignedBB(0.0D, 0.03125D, 0.03125D, 0.0625D, 0.96875D, 1.96875D)};

    public BlockArchitectTableNEW(String unlName, String regName) {
        super(Material.WOOD, unlName, regName);
        this.setSoundType(SoundType.WOOD);
        this.setResistance(5.0F);
        this.setHardness(2.0F);
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

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
}
