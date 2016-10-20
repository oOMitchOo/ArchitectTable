package net.knowcraft.architecttable.item;

import net.knowcraft.architecttable.block.BlockArchitectTable;
import net.knowcraft.architecttable.helper.LogHelper;
import net.knowcraft.architecttable.init.ModBlocks;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.IProperty;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by oOMitchOo on 14.10.2016.
 */
public class ItemDrawingTools extends ItemBase{
    public ItemDrawingTools(String unlName, String regName) {
        super(unlName, regName);

        this.setMaxDamage(0);
        this.setMaxStackSize(1);
    }

    // TODO: Hier kann bei erfolgreicher MultiBlock-Formung auch die Spieler-Datei erzeugt werden, wenn sie noch nicht existiert.
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        if(worldIn.getBlockState(pos).getBlock() == Blocks.PLANKS) {
            // Genauer: Es sollen Oak-Planks sein. TODO: Zusammenführen nach Testen.
            if(worldIn.getBlockState(pos).getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.OAK) {
                boolean builtMultiBlock = checkAndBuildMultiBlock(playerIn, worldIn, pos);
                if (builtMultiBlock) {
                    // Remove the drawingTool Item, if the player isn't in creative.
                    if (!playerIn.isCreative() && (stack.getItem() instanceof ItemDrawingTools)) stack.stackSize = stack.stackSize -1;
                    return EnumActionResult.PASS;
                } else {
                    return EnumActionResult.FAIL;
                }
            } else {
                return EnumActionResult.FAIL;
            }
        } else {
            return EnumActionResult.FAIL;
        }
    }

    private boolean checkAndBuildMultiBlock(EntityPlayer playerIn, World worldIn, BlockPos planksPos) {
        // Ich gehe davon aus, dass der Spieler entweder so steht, dass die Truhe rechts von den Planks ist,
        // oder so, dass die Truhe hinter den Planks ist. (Alles andere wäre zu umständlich)

        // Horizontal Facing: S-W-N-E ist 0-1-2-3
        EnumFacing playerFacing = playerIn.getHorizontalFacing();
        int horizontalIndex = playerFacing.getHorizontalIndex();
        LogHelper.error("Der Spieler guckt nach "+playerFacing.getName()+".");
        LogHelper.error("Und der Horizontal-Index ist dabei: "+horizontalIndex+".");

        if(horizontalIndex == 0) {
            // Check for chest (and frames) in South (pos Z) and West (neg X). -> Multiblock facing: North
            return checkForChestAndSigns(worldIn, planksPos, playerFacing, -1, 1);
        } else if (horizontalIndex == 1) {
            // Check for chest (and frames) in West (neg X) and North (neg Z). -> Multiblock facing: East
            return checkForChestAndSigns(worldIn, planksPos, playerFacing, -1, -1);
        } else if (horizontalIndex == 2) {
            // Check for chest (and frames) in North (neg Z) and East (pos X). -> Multiblock facing: South
            return checkForChestAndSigns(worldIn, planksPos, playerFacing, 1, -1);
        } else if (horizontalIndex == 3) {
            // Check for chest (and frames) in East (pox X) and South (pox Z). -> Multiblock facing: West
            return checkForChestAndSigns(worldIn, planksPos, playerFacing, 1, 1);
        } else {
            return false;
        }
    }

    /** Checks in two given directions from a given BlockPos if chest and signs are in place for Multiblock. */
    private boolean checkForChestAndSigns (World worldIn, BlockPos planksPos, EnumFacing playerFacing, int xOff, int zOff) {
        // Erst nach Truhe schauen.
        if (worldIn.getBlockState(planksPos.add(xOff, 0, 0)).getBlock() == Blocks.CHEST) {
            // Jetzt noch nach Signs schauen. // TODO: Oder lieber nach Item Frames?
            if ((worldIn.getBlockState(planksPos.add(0, 1, 0)).getBlock() == Blocks.WALL_SIGN || worldIn.getBlockState(planksPos.add(0, 1, 0)).getBlock() == Blocks.STANDING_SIGN)
                    && (worldIn.getBlockState(planksPos.add(xOff, 1, 0)).getBlock() == Blocks.WALL_SIGN || worldIn.getBlockState(planksPos.add(xOff, 1, 0)).getBlock() == Blocks.STANDING_SIGN))
            {
                // Der Länge nach in Richtung xOff aufbauen. -> Multiblock facing: South oder North.
                if (playerFacing == EnumFacing.NORTH || playerFacing == EnumFacing.EAST) {
                    // Erst gucken, ob andere Architect Table zu nah dran sind, bevor gebaut wird.
                    if (otherArchitectTableTooClose(worldIn, planksPos, EnumFacing.SOUTH)) {
                        return false;
                    } else {
                        buildMultiBlock(worldIn, planksPos, EnumFacing.SOUTH);
                        return true;
                    }
                } else if (playerFacing == EnumFacing.SOUTH || playerFacing == EnumFacing.WEST) {
                    if (otherArchitectTableTooClose(worldIn, planksPos, EnumFacing.NORTH)) {
                        return false;
                    } else {
                        buildMultiBlock(worldIn, planksPos, EnumFacing.NORTH);
                        return true;
                    }
                } else return false;
            } else return false;
        }
        if (worldIn.getBlockState(planksPos.add(0, 0, zOff)).getBlock() == Blocks.CHEST) {
            // Jetzt noch nach Signs schauen.
            if ((worldIn.getBlockState(planksPos.add(0, 1, 0)).getBlock() == Blocks.WALL_SIGN || worldIn.getBlockState(planksPos.add(0, 1, 0)).getBlock() == Blocks.STANDING_SIGN)
                    && (worldIn.getBlockState(planksPos.add(0, 1, zOff)).getBlock() == Blocks.WALL_SIGN || worldIn.getBlockState(planksPos.add(0, 1, zOff)).getBlock() == Blocks.STANDING_SIGN))
            {
                // Der Länge nach in Richtung zOff aufbauen. -> Multiblock facing: East oder West.
                if (playerFacing == EnumFacing.NORTH || playerFacing == EnumFacing.WEST) {
                    // Erst gucken, ob andere Architect Table zu nah dran sind, bevor gebaut wird.
                    if (otherArchitectTableTooClose(worldIn, planksPos, EnumFacing.EAST)) {
                        return false;
                    } else {
                        buildMultiBlock(worldIn, planksPos, EnumFacing.EAST);
                        return true;
                    }
                } else if (playerFacing == EnumFacing.SOUTH || playerFacing == EnumFacing.EAST) {
                    if (otherArchitectTableTooClose(worldIn, planksPos, EnumFacing.WEST)) {
                        return false;
                    } else {
                        buildMultiBlock(worldIn, planksPos, EnumFacing.WEST);
                        return true;
                    }
                } else return false; // Should not be possible.
            } else return false;
        }
        // Wird nur aufgerufen wenn beide if-statements false sind (In beide Richtungen keine Truhe steht).
        return false;
    }

    // Check if another Architect Table Multiblock is too close to the position where one should be formed.
    // Folgendes Block-Grid macht es anschaulich (Vogelperspektive). pPos ist die plankPos von der aus gehend der MultiBlock gebaut wird.
    // "rechts" davon ist die Truhe (ch) und überall, wo ein x ist, darf kein Architect Table stehen.
    // UND EINE EBENE DARÜBER AUCH NICHT!
    //  #----#----#----#----#
    //  |    |  x |  x |    |
    //  #----#----#----#----#
    //  |  x |pPos| ch |  x |       |   Multiblock-
    //  #----#----#----#----#       V   facing
    //  |    |  x |  x |    |
    //  #----#----#----#----#
    // TODO: HIER WEITER!
    private boolean otherArchitectTableTooClose(World worldIn, BlockPos plankPos, EnumFacing multiBlockFacing)
    {
        if (multiBlockFacing == EnumFacing.SOUTH) {
            // A list of BlockPos which may not contain an architect table (look above for explanation).
            // TODO: Eine Methode schreiben, die intelligent die vier möglichen groundLevelForbiddenPlaces zusammenfasst.
            BlockPos[] groundLevelForbiddenPlaces = new BlockPos[] {plankPos.north(), plankPos.north().east(), plankPos.east(2), plankPos.south().east(), plankPos.south(), plankPos.west() };
            for (BlockPos pos : groundLevelForbiddenPlaces)
            {
                if (worldIn.getBlockState(pos).getBlock() instanceof BlockArchitectTable) return true;
                if (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockArchitectTable) return true;
            }
            return false;
        } else if (multiBlockFacing == EnumFacing.WEST) {
            BlockPos[] groundLevelForbiddenPlaces = new BlockPos[] {plankPos.north(), plankPos.east(), plankPos.east().south(), plankPos.south(2), plankPos.west().south(), plankPos.west() };
            for (BlockPos pos : groundLevelForbiddenPlaces)
            {
                if (worldIn.getBlockState(pos).getBlock() instanceof BlockArchitectTable) return true;
                if (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockArchitectTable) return true;
            }
            return false;
        } else if (multiBlockFacing == EnumFacing.NORTH) {
            BlockPos[] groundLevelForbiddenPlaces = new BlockPos[] {plankPos.north(), plankPos.east(), plankPos.south(), plankPos.south().west(), plankPos.west(2), plankPos.north().west() };
            for (BlockPos pos : groundLevelForbiddenPlaces)
            {
                if (worldIn.getBlockState(pos).getBlock() instanceof BlockArchitectTable) return true;
                if (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockArchitectTable) return true;
            }
            return false;
        } else if (multiBlockFacing == EnumFacing.EAST) {
            BlockPos[] groundLevelForbiddenPlaces = new BlockPos[] {plankPos.north(2), plankPos.north().east(), plankPos.east(), plankPos.south(), plankPos.west(), plankPos.north().west() };
            for (BlockPos pos : groundLevelForbiddenPlaces)
            {
                if (worldIn.getBlockState(pos).getBlock() instanceof BlockArchitectTable) return true;
                if (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockArchitectTable) return true;
            }
            return false;
        } else return true;
    }

    private void buildMultiBlock (World worldIn, BlockPos planksPos, EnumFacing multiBlockFacing) {
        int tableMetaCount = 0;
        if (multiBlockFacing == EnumFacing.NORTH) {
            for (int y = 0; y < 2; ++y){
                for (int x = 0; x > -2; --x) {
                    worldIn.setBlockState(planksPos.add(x, y, 0), ModBlocks.ARCHITECT_TABLE.getStateFromMeta(tableMetaCount).withProperty(BlockArchitectTable.FACING, EnumFacing.NORTH));
                    ++tableMetaCount;
                }
            }
        } else if (multiBlockFacing == EnumFacing.EAST) {
            for (int y = 0; y < 2; ++y) {
                for (int z = 0; z > -2; --z) {
                    worldIn.setBlockState(planksPos.add(0, y, z), ModBlocks.ARCHITECT_TABLE.getStateFromMeta(tableMetaCount).withProperty(BlockArchitectTable.FACING, EnumFacing.EAST));
                    ++tableMetaCount;
                }
            }
        } else if (multiBlockFacing == EnumFacing.SOUTH) {
            for (int y = 0; y < 2; ++y){
                for (int x = 0; x < 2; ++x) {
                    worldIn.setBlockState(planksPos.add(x, y, 0), ModBlocks.ARCHITECT_TABLE.getStateFromMeta(tableMetaCount).withProperty(BlockArchitectTable.FACING, EnumFacing.SOUTH));
                    ++tableMetaCount;
                }
            }
        } else if (multiBlockFacing == EnumFacing.WEST) {
            for (int y = 0; y < 2; ++y) {
                for (int z = 0; z < 2; ++z) {
                    worldIn.setBlockState(planksPos.add(0, y, z), ModBlocks.ARCHITECT_TABLE.getStateFromMeta(tableMetaCount).withProperty(BlockArchitectTable.FACING, EnumFacing.WEST));
                    ++tableMetaCount;
                }
            }
        }
    }
}
