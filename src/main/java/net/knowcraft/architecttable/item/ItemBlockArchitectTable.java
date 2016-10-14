package net.knowcraft.architecttable.item;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by oOMitchOo on 14.10.2016.
 */
public class ItemBlockArchitectTable extends ItemBlock{
    public ItemBlockArchitectTable(Block block, String unlName, String regName) {
        super(block);
        this.setUnlocalizedName(unlName);
        this.setRegistryName(regName);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        // Wenn der geklickt Block nicht replaceable ist, wird der gesetzt Block an die Seite des geklickten Blocks gesetzt.
        if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(facing);
        }

        // Hier wird konkret bestimmt, ob der Block überhaupt gesetzt werden kann (genug Items in der Hand, Spieler Rechte, steht nah genug dran...)
        // Ich habe noch ein paar Bedingungen mehr rangehängt, damit wir genug freie Plätze für den Multiblock garantiert haben.
        EnumFacing buildingDirect = playerIn.getHorizontalFacing().rotateY(); // Macht South zu West, West zu North, North zu East, East zu South
        if (stack.stackSize != 0 && playerIn.canPlayerEdit(pos, facing, stack) && worldIn.canBlockBePlaced(this.block, pos, false, facing, (Entity)null, stack) && worldIn.getBlockState(pos.up()).getBlock() == Blocks.AIR
                && worldIn.getBlockState(pos.offset(buildingDirect)).getBlock() == Blocks.AIR && worldIn.getBlockState(pos.up().offset(buildingDirect)).getBlock() == Blocks.AIR)
        {
            int i = this.getMetadata(stack.getMetadata());
            IBlockState iblockstate1 = this.block.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, i, playerIn);

            if (placeBlockAt(stack, playerIn, worldIn, pos, facing, hitX, hitY, hitZ, iblockstate1))
            {
                SoundType soundtype = worldIn.getBlockState(pos).getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, playerIn);
                worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                --stack.stackSize;
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }

    /**
     * Called to actually place the block, after the location is determined
     * and all permission checks have been made.
     *
     * @param stack The item stack that was used to place the block. This can be changed inside the method.
     * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
     * @param side The side the player (or machine) right-clicked on.
     */
    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
    {
        // newState wurde durch onBlockPlaced bestimmt und ist hier der Left_Table mit Richtung entgegen der Spieler-Blickrichtung.
        // Das hier wird etwas schwierig, da hier einfach probiert wird, ob der Block gesetzt werden kann und wenn nicht, dann kommt ein false.
        // Wir müssten aber erst true oder false für alle 4 Blöcke haben, bevor wir setzen...
        // TODO: Hier weiter!!!
        if (!world.setBlockState(pos, newState, 3)) return false;

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == this.block)
        {
            setTileEntityNBT(world, player, pos, stack);
            this.block.onBlockPlacedBy(world, pos, state, player, stack);
        }

        return true;
    }
}
