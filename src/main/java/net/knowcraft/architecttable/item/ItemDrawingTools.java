package net.knowcraft.architecttable.item;

import net.knowcraft.architecttable.block.*;
import net.knowcraft.architecttable.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by oOMitchOo on 14.10.2016.
 */
public class ItemDrawingTools extends ItemBase{
    public ItemDrawingTools(String unlName, String regName) {
        super(unlName, regName);

        this.setMaxDamage(0);
        this.setMaxStackSize(1);
    }

    /* // TODO: Write an action which will be executed, when a plankBlock is being right-clicked.
    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BLOCK;
    } */

    // TODO: Hier kann bei erfolgreicher MultiBlock-Formung auch die Spieler-Datei erzeugt werden, wenn sie noch nicht existiert.
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        int planksMeta = 0;
        // Check if planks are being right-clicked. (to be exact: oak planks)
        if(worldIn.getBlockState(pos).getBlock() == Blocks.PLANKS) {
            // The planksMeta determines with which planks variant (oak, spruce,...) the multiBlock is build.
            planksMeta = worldIn.getBlockState(pos).getValue(BlockPlanks.VARIANT).getMetadata();
            // Try to build the multiBlock.
            boolean builtMultiBlock = checkAndBuildMultiBlock(playerIn, worldIn, pos, planksMeta);
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
    }

    private boolean checkAndBuildMultiBlock(EntityPlayer playerIn, World worldIn, BlockPos planksPos, int planksMeta) {
        // I assume that the player either stands in front of the planks block with the chest to his right
        // or in front of the planks block with the chest behind hit. Everything else would be too circuitous.

        // Horizontal facing: S-W-N-E
        EnumFacing playerFacing = playerIn.getHorizontalFacing();

        switch (playerFacing) {
            case NORTH: // Checks for chest (and signs) in North (neg Z) and East (pos X). -> Multiblock facing: South
                return checkForChestAndFrames(worldIn, planksPos, planksMeta, playerIn, playerFacing, 1, -1); // Could also have used: EnumFacing.NORTH.getAxisDirection().getOffset(); (= -1 because neg. Z)
            case SOUTH: // Checks for chest (and signs) in South (pos Z) and West (neg X). -> Multiblock facing: North
                return checkForChestAndFrames(worldIn, planksPos, planksMeta, playerIn, playerFacing, -1, 1);
            case WEST: // Checks for chest (and signs) in West (neg X) and North (neg Z). -> Multiblock facing: East
                return checkForChestAndFrames(worldIn, planksPos, planksMeta, playerIn, playerFacing, -1, -1);
            case EAST: // Checks for chest (and signs) in East (pox X) and South (pox Z). -> Multiblock facing: West
                return checkForChestAndFrames(worldIn, planksPos, planksMeta, playerIn, playerFacing, 1, 1);
            default: return false; // Should not be possible.
        }
    }

    /** Checks in two given directions from a given BlockPos if chest and signs are in place for Multiblock. */
    private boolean checkForChestAndFrames (World worldIn, BlockPos planksPos, int planksMeta, EntityPlayer playerIn, EnumFacing playerFacing, int xOff, int zOff) {
        boolean isFrameOverPlanks = false;
        boolean isFrameOverChest = false;
        EntityItemFrame itemFrameOverPlanks = null;
        EntityItemFrame itemFrameOverChest = null;

        // Look for chest in xOff direction.
        if (worldIn.getBlockState(planksPos.add(xOff, 0, 0)).getBlock() == Blocks.CHEST) {

            // Lists all ItemFrames above the planksBlock.
            List<EntityItemFrame> itemFramesOverPlanksBlock = worldIn.getEntitiesWithinAABB(EntityItemFrame.class, Block.FULL_BLOCK_AABB.offset(planksPos.add(0, 1, 0)));
            // Lists all ItemFrames above the chest.
            List<EntityItemFrame> itemFramesOverChestBlock = worldIn.getEntitiesWithinAABB(EntityItemFrame.class, Block.FULL_BLOCK_AABB.offset(planksPos.add(xOff, 1, 0)));

            // The players facing direction determines the multiBlocks facing direction.
            if (playerFacing == EnumFacing.NORTH || playerFacing == EnumFacing.EAST) {
                for (EntityItemFrame entry : itemFramesOverPlanksBlock) {
                    if(entry != null && entry.facingDirection == EnumFacing.SOUTH) { itemFrameOverPlanks = entry; isFrameOverPlanks = true; break; }
                }
                for (EntityItemFrame entry : itemFramesOverChestBlock) {
                    if(entry != null && entry.facingDirection == EnumFacing.SOUTH) { itemFrameOverChest = entry; isFrameOverChest = true; break; }
                }
                // If it finds no ItemFrames or another architect table is too close: return false. (else build the multiBlock and return true)
                if (!isFrameOverPlanks || !isFrameOverChest || otherArchitectTableTooClose(worldIn, planksPos, EnumFacing.SOUTH)) return false;
                else { buildMultiBlock(worldIn, planksPos, planksMeta, playerIn, EnumFacing.SOUTH, itemFrameOverPlanks, itemFrameOverChest); return true; }
            } else if (playerFacing == EnumFacing.SOUTH || playerFacing == EnumFacing.WEST) {
                for (EntityItemFrame entry : itemFramesOverPlanksBlock) {
                    if(entry != null && entry.facingDirection == EnumFacing.NORTH) { itemFrameOverPlanks = entry; isFrameOverPlanks = true; break; }
                }
                for (EntityItemFrame entry : itemFramesOverChestBlock) {
                    if(entry != null && entry.facingDirection == EnumFacing.NORTH) { itemFrameOverChest = entry; isFrameOverChest = true; break; }
                }
                // If it finds no ItemFrames or another architect table is too close: return false. (else build the multiBlock and return true)
                if (!isFrameOverPlanks || !isFrameOverChest || otherArchitectTableTooClose(worldIn, planksPos, EnumFacing.NORTH)) return false;
                else { buildMultiBlock(worldIn, planksPos, planksMeta, playerIn, EnumFacing.NORTH, itemFrameOverPlanks, itemFrameOverChest); return true; }
            }
        }
        // Look for chest in zOff direction.
        if (worldIn.getBlockState(planksPos.add(0, 0, zOff)).getBlock() == Blocks.CHEST) {

            // Lists all ItemFrames above the planksBlock.
            List<EntityItemFrame> itemFramesOverPlanksBlock = worldIn.getEntitiesWithinAABB(EntityItemFrame.class, Block.FULL_BLOCK_AABB.offset(planksPos.add(0, 1, 0)));
            // Lists all ItemFrames above the chest.
            List<EntityItemFrame> itemFramesOverChest = worldIn.getEntitiesWithinAABB(EntityItemFrame.class, Block.FULL_BLOCK_AABB.offset(planksPos.add(0, 1, zOff)));

            // The players facing direction determines the multiBlocks facing direction.
            if (playerFacing == EnumFacing.NORTH || playerFacing == EnumFacing.WEST) {
                for (EntityItemFrame entry : itemFramesOverPlanksBlock) {
                    if(entry != null && entry.facingDirection == EnumFacing.EAST) { itemFrameOverPlanks = entry; isFrameOverPlanks = true; break; }
                }
                for (EntityItemFrame entry : itemFramesOverChest) {
                    if(entry != null && entry.facingDirection == EnumFacing.EAST) { itemFrameOverChest = entry; isFrameOverChest = true; break; }
                }
                // If it finds no ItemFrames or another architect table is too close: return false. (else build the multiBlock and return true)
                if (!isFrameOverPlanks || !isFrameOverChest || otherArchitectTableTooClose(worldIn, planksPos, EnumFacing.EAST)) return false;
                else { buildMultiBlock(worldIn, planksPos, planksMeta, playerIn, EnumFacing.EAST, itemFrameOverPlanks, itemFrameOverChest); return true; }
            } else if (playerFacing == EnumFacing.SOUTH || playerFacing == EnumFacing.EAST) {
                for (EntityItemFrame entry : itemFramesOverPlanksBlock) {
                    if(entry != null && entry.facingDirection == EnumFacing.WEST) { itemFrameOverPlanks = entry; isFrameOverPlanks = true; break; }
                }
                for (EntityItemFrame entry : itemFramesOverChest) {
                    if(entry != null && entry.facingDirection == EnumFacing.WEST) { itemFrameOverChest = entry; isFrameOverChest = true; break; }
                }
                // If it finds no ItemFrames or another architect table is too close: return false. (else build the multiBlock and return true)
                if (!isFrameOverPlanks || !isFrameOverChest || otherArchitectTableTooClose(worldIn, planksPos, EnumFacing.WEST)) return false;
                else { buildMultiBlock(worldIn, planksPos, planksMeta, playerIn, EnumFacing.WEST, itemFrameOverPlanks, itemFrameOverChest); return true; }
            }
        }

        // When both if-statements don't return true no multiBlock can be build.
        return false;
    }

    /** This helping method checks if another architect table is too close by where the multiBlock would get build. */
    private boolean otherArchitectTableTooClose(World worldIn, BlockPos plankPos, EnumFacing multiBlockFacing)
    {
        // The following block-grid should demonstrate which positions are being checked. (bird's eye view)
        // pPos is plankPos from where the multiBlock will be build. Beside it is the chest (ch) and that constellation determines the multiBlockFacing.
        // The same must be done for one level higher (where the pinboard blocks are).
        //  #----#----#----#----#
        //  |    |  x |  x |    |
        //  #----#----#----#----#
        //  |  x |pPos| ch |  x |       |   multiBlock-
        //  #----#----#----#----#       V   Facing
        //  |    |  x |  x |    |
        //  #----#----#----#----#
        if (multiBlockFacing == EnumFacing.SOUTH) {
            // A list of BlockPos which must not contain an architect table (look above for explanation).
            // TODO: Write a method, which gets the groundLevelForbiddenPlaces based on the multiBlockFacing.
            BlockPos[] groundLevelForbiddenPlaces = new BlockPos[] {plankPos.north(), plankPos.north().east(), plankPos.east(2), plankPos.south().east(), plankPos.south(), plankPos.west() };
            for (BlockPos pos : groundLevelForbiddenPlaces)
            {
                if (worldIn.getBlockState(pos).getBlock() instanceof BlockArchitectTableNEW) return true;
                if (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockArchitectTableNEW) return true; // same for the pinboard level
            }
            return false;
        } else if (multiBlockFacing == EnumFacing.WEST) {
            BlockPos[] groundLevelForbiddenPlaces = new BlockPos[] {plankPos.north(), plankPos.east(), plankPos.east().south(), plankPos.south(2), plankPos.west().south(), plankPos.west() };
            for (BlockPos pos : groundLevelForbiddenPlaces)
            {
                if (worldIn.getBlockState(pos).getBlock() instanceof BlockArchitectTableNEW) return true;
                if (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockArchitectTableNEW) return true; // same for the pinboard level
            }
            return false;
        } else if (multiBlockFacing == EnumFacing.NORTH) {
            BlockPos[] groundLevelForbiddenPlaces = new BlockPos[] {plankPos.north(), plankPos.east(), plankPos.south(), plankPos.south().west(), plankPos.west(2), plankPos.north().west() };
            for (BlockPos pos : groundLevelForbiddenPlaces)
            {
                if (worldIn.getBlockState(pos).getBlock() instanceof BlockArchitectTableNEW) return true;
                if (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockArchitectTableNEW) return true; // same for the pinboard level
            }
            return false;
        } else if (multiBlockFacing == EnumFacing.EAST) {
            BlockPos[] groundLevelForbiddenPlaces = new BlockPos[] {plankPos.north(2), plankPos.north().east(), plankPos.east(), plankPos.south(), plankPos.west(), plankPos.north().west() };
            for (BlockPos pos : groundLevelForbiddenPlaces)
            {
                if (worldIn.getBlockState(pos).getBlock() instanceof BlockArchitectTableNEW) return true;
                if (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockArchitectTableNEW) return true; // same for the pinboard level
            }
            return false;
        } else return true;
    }

    // TODO: For now items are dropped funky. Because they are dropped on the logical server and the logical client doesn't know how they are dropped for the first moment. (they get teleported in mid air)
    private void buildMultiBlock (World worldIn, BlockPos planksPos, int planksMeta, EntityPlayer playerIn, EnumFacing multiBlockFacing, EntityItemFrame itemFrameOverPlanks, EntityItemFrame itemFrameOverChest) {
        int tableMetaCount = 0;

        // Replacing planksBlock with BlockTableLeft.
        worldIn.setBlockState(planksPos, ModBlocks.TABLE_LEFT.getStateFromMeta(planksMeta).withProperty(BlockArchitectTableNEW.FACING, multiBlockFacing));
        worldIn.playEvent(2001, planksPos, BlockTableLeft.getStateId(ModBlocks.TABLE_LEFT.getDefaultState())); // StateId is only used for crashs... and uses the Blocks ID (?).
        // Killing itemFrameOverPlanks and placing BlockPinboardLeft.
        if(!worldIn.isRemote && itemFrameOverPlanks != null) { itemFrameOverPlanks.dropItemOrSelf(playerIn, false); itemFrameOverPlanks.setDead(); }
        if(worldIn.isRemote && itemFrameOverPlanks != null) { itemFrameOverPlanks.setDropItemsWhenDead(false); itemFrameOverPlanks.setDead(); }
        worldIn.setBlockState(planksPos.up(), ModBlocks.PINBOARD_LEFT.getDefaultState().withProperty(BlockArchitectTableNEW.FACING, multiBlockFacing));
        worldIn.playEvent(2001, planksPos.up(), BlockPinboardLeft.getStateId(ModBlocks.PINBOARD_LEFT.getDefaultState()));

        // Now for BlockTableRight and BlockPinboardRight.
        switch (multiBlockFacing) {
            case NORTH:
                // Replacing chestBlock with BlockTableRight.
                worldIn.setBlockState(planksPos.west(), ModBlocks.TABLE_RIGHT.getStateFromMeta(planksMeta).withProperty(BlockArchitectTableNEW.FACING, multiBlockFacing));
                worldIn.playEvent(2001, planksPos.west(), BlockTableRight.getStateId(ModBlocks.TABLE_RIGHT.getDefaultState()));
                // Killing itemFrameOverChest and placing BlockPinboardRight.
                if(!worldIn.isRemote && itemFrameOverChest != null) { itemFrameOverChest.dropItemOrSelf(playerIn, false); itemFrameOverChest.setDead(); }
                if(worldIn.isRemote && itemFrameOverChest != null) { itemFrameOverChest.setDropItemsWhenDead(false); itemFrameOverChest.setDead(); }
                worldIn.setBlockState(planksPos.up().west(), ModBlocks.PINBOARD_RIGHT.getDefaultState().withProperty(BlockArchitectTableNEW.FACING, multiBlockFacing));
                worldIn.playEvent(2001, planksPos.up().west(), BlockPinboardLeft.getStateId(ModBlocks.PINBOARD_LEFT.getDefaultState()));
                break;
            case SOUTH:
                // Replacing chestBlock with BlockTableRight.
                worldIn.setBlockState(planksPos.east(), ModBlocks.TABLE_RIGHT.getStateFromMeta(planksMeta).withProperty(BlockArchitectTableNEW.FACING, multiBlockFacing));
                worldIn.playEvent(2001, planksPos.east(), BlockTableRight.getStateId(ModBlocks.TABLE_RIGHT.getDefaultState()));
                // Killing itemFrameOverChest and placing BlockPinboardRight.
                if(!worldIn.isRemote && itemFrameOverChest != null) { itemFrameOverChest.dropItemOrSelf(playerIn, false); itemFrameOverChest.setDead(); }
                if(worldIn.isRemote && itemFrameOverChest != null) { itemFrameOverChest.setDropItemsWhenDead(false); itemFrameOverChest.setDead(); }
                worldIn.setBlockState(planksPos.up().east(), ModBlocks.PINBOARD_RIGHT.getDefaultState().withProperty(BlockArchitectTableNEW.FACING, multiBlockFacing));
                worldIn.playEvent(2001, planksPos.up().east(), BlockPinboardLeft.getStateId(ModBlocks.PINBOARD_LEFT.getDefaultState()));
                break;
            case WEST:
                // Replacing chestBlock with BlockTableRight.
                worldIn.setBlockState(planksPos.south(), ModBlocks.TABLE_RIGHT.getStateFromMeta(planksMeta).withProperty(BlockArchitectTableNEW.FACING, multiBlockFacing));
                worldIn.playEvent(2001, planksPos.south(), BlockTableRight.getStateId(ModBlocks.TABLE_RIGHT.getDefaultState()));
                // Killing itemFrameOverChest and placing BlockPinboardRight.
                if(!worldIn.isRemote && itemFrameOverChest != null) { itemFrameOverChest.dropItemOrSelf(playerIn, false); itemFrameOverChest.setDead(); }
                if(worldIn.isRemote && itemFrameOverChest != null) { itemFrameOverChest.setDropItemsWhenDead(false); itemFrameOverChest.setDead(); }
                worldIn.setBlockState(planksPos.up().south(), ModBlocks.PINBOARD_RIGHT.getDefaultState().withProperty(BlockArchitectTableNEW.FACING, multiBlockFacing));
                worldIn.playEvent(2001, planksPos.up().south(), BlockPinboardLeft.getStateId(ModBlocks.PINBOARD_LEFT.getDefaultState()));
                break;
            case EAST:
                // Replacing chestBlock with BlockTableRight.
                worldIn.setBlockState(planksPos.north(), ModBlocks.TABLE_RIGHT.getStateFromMeta(planksMeta).withProperty(BlockArchitectTableNEW.FACING, multiBlockFacing));
                worldIn.playEvent(2001, planksPos.north(), BlockTableRight.getStateId(ModBlocks.TABLE_RIGHT.getDefaultState()));
                // Killing itemFrameOverChest and placing BlockPinboardRight.
                if(!worldIn.isRemote && itemFrameOverChest != null) { itemFrameOverChest.dropItemOrSelf(playerIn, false); itemFrameOverChest.setDead(); }
                if(worldIn.isRemote && itemFrameOverChest != null) { itemFrameOverChest.setDropItemsWhenDead(false); itemFrameOverChest.setDead(); }
                worldIn.setBlockState(planksPos.up().north(), ModBlocks.PINBOARD_RIGHT.getDefaultState().withProperty(BlockArchitectTableNEW.FACING, multiBlockFacing));
                worldIn.playEvent(2001, planksPos.up().north(), BlockPinboardLeft.getStateId(ModBlocks.PINBOARD_LEFT.getDefaultState()));
                break;
        }
    }
}
