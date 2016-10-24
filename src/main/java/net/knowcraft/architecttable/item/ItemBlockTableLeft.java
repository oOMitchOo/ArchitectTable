package net.knowcraft.architecttable.item;

import net.knowcraft.architecttable.block.BlockArchitectTableNEW;
import net.knowcraft.architecttable.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by oOMitchOo on 24.10.2016.
 */
public class ItemBlockTableLeft extends ItemBlock {
    public ItemBlockTableLeft(Block block, String unlName, String regName) {
        super(block);
        this.setUnlocalizedName(unlName);
        this.setRegistryName(regName);
    }

    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    @Override
    public int getMetadata(int damage) { return damage; }

    /**
     * Called when a Block is right-clicked with this Item
     */
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        // Wenn der geklickte Block nicht replaceable ist, wird der gesetzt Block an die Seite des geklickten Blocks gesetzt.
        if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(facing);
        }

        // Hier wird gecheckt, ob in unmittelbarer Nähe schon ein ArchitectTable steht. Das darf nicht sein, da getActualState sonst nicht richtig funktioniert und der Multiblock teilweise nicht richtig ausgerichtet sein wird.
        // Es muss geguckt werden in: pos.offset(spielerrichtung), pos.offset(spielerRichtung.opposite), pos.offset(spielerRichtung.opposite.turnY)
        // Wenn auf tl (Vogelperspektive) gesetzt wird, dann ist "rechts" davon table_right (tr) und auf den Positionen x darf kein ArchitectTable sein.
        // UND EINE EBENE DARÜBER!!!
        //  #----#----#----#----#
        //  |    |  x |  x |    |
        //  #----#----#----#----#
        //  |  x | tl | tr |  x |
        //  #----#----#----#----#
        //  |    |  x |  x |    |
        //  #----#----#----#----#
        EnumFacing playerFacing = playerIn.getHorizontalFacing();
        BlockPos[] groundLevelForbiddenPlaces = new BlockPos[] {pos.offset(playerFacing), pos.offset(playerFacing).offset(playerFacing.rotateY()), pos.offset(playerFacing.rotateY(), 2), pos.offset(playerFacing.getOpposite()).offset(playerFacing.rotateY()), pos.offset(playerFacing.getOpposite()), pos.offset(playerFacing.getOpposite().rotateY()) };
        for (BlockPos forbPos : groundLevelForbiddenPlaces) {
            if (worldIn.getBlockState(forbPos).getBlock() instanceof BlockArchitectTableNEW) return EnumActionResult.FAIL;
            // Die up() sorgt dafür, dass auch die Ebene über groundLevel gecheckt wird.
            if (worldIn.getBlockState(forbPos.up()).getBlock() instanceof BlockArchitectTableNEW) return EnumActionResult.FAIL;
        }

        // Hier wird konkret bestimmt, ob der Block überhaupt gesetzt werden kann (genug Items in der Hand, Spieler Rechte, steht nah genug dran...)
        // Ich habe noch ein paar Bedingungen mehr rangehängt, damit wir genug freie Plätze für den Multiblock garantiert haben.
        EnumFacing buildingDirect = playerIn.getHorizontalFacing().rotateY(); // Macht South zu West, West zu North, North zu East, East zu South
        if (stack.stackSize != 0 && playerIn.canPlayerEdit(pos, facing, stack) && worldIn.canBlockBePlaced(this.block, pos, false, facing, (Entity)null, stack) && worldIn.canBlockBePlaced(this.block, pos.up(), false, facing, (Entity)null, stack)
                && worldIn.canBlockBePlaced(this.block, pos.offset(buildingDirect), false, facing, (Entity)null, stack) && worldIn.canBlockBePlaced(this.block, pos.up().offset(buildingDirect), false, facing, (Entity)null, stack))
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
        // In world.setBlockState wird gecheckt: this.isOutsideBuildHeight(pos) && !this.isRemote && this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD
        // Buildinghöhe für die anderen Blöcke checken sollte reichen, da WorldType beim setzen des LEFT_TABLE gleich danach gecheckt wird.
        EnumFacing multiBlockFacingDirect = player.getHorizontalFacing().getOpposite();
        int meta = this.getMetadata(stack.getMetadata()); // stack.getMetadata() would be enough but I like this more.

        if (pos.up().getY() >= 256) return false;

        // leftTable wird normal probiert zu setzen, wenn erfolgreich, werden die anderen Blöcke gesetzt.
        if (!world.setBlockState(pos, newState, 3)) return false;
        else {
            IBlockState rightTable = ModBlocks.TABLE_RIGHT.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.byMetadata(meta)).withProperty(BlockArchitectTableNEW.FACING, player.getHorizontalFacing().getOpposite());
            IBlockState leftPinboard = ModBlocks.PINBOARD_LEFT.getDefaultState().withProperty(BlockArchitectTableNEW.FACING, player.getHorizontalFacing().getOpposite());
            IBlockState rightPinboard = ModBlocks.PINBOARD_RIGHT.getDefaultState().withProperty(BlockArchitectTableNEW.FACING, player.getHorizontalFacing().getOpposite());

            world.setBlockState(pos.offset(player.getHorizontalFacing().rotateY()), rightTable, 3);
            world.setBlockState(pos.up(), leftPinboard, 3);
            world.setBlockState(pos.up().offset(player.getHorizontalFacing().rotateY()), rightPinboard, 3);
        }

        // TODO: Hier könnte TileEntities erzeugt werden für den ArchitectTable.
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == this.block) // Vielleicht noch nach den anderen drei Blöcken schauen? Ich weiß nicht, ob setBlockState schief gehen kann...
        {
            setTileEntityNBT(world, player, pos, stack);
            this.block.onBlockPlacedBy(world, pos, state, player, stack);
        }

        return true;
    }
}
