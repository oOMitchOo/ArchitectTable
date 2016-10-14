package net.knowcraft.architecttable.item;

import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.IProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
                boolean builtMultiBlock = checkAndBuildMultiBlock(worldIn, pos, facing);
                if (builtMultiBlock) {
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

    private boolean checkAndBuildMultiBlock(World worldIn, BlockPos planksPos, EnumFacing playerFacing) {
        // Ich gehe davon aus, dass der Spieler entweder so steht, dass die Truhe rechts von den Planks ist,
        // oder so, dass die Truhe hinter den Planks ist. (Alles andere wäre zu umständlich)

        // Horizontal Facing: S-W-N-E ist 0-1-2-3
        int horizontalIndex = playerFacing.getHorizontalIndex();

        if(horizontalIndex == 0) {
            // Check for chest (and frames) in South (pos Z) and West (neg X). -> Multiblock facing: North
            return checkForChestAndFrames(worldIn, planksPos, playerFacing, -1, 1);
        } else if (horizontalIndex == 1) {
            // Check for chest (and frames) in West (neg X) and North (neg Z). -> Multiblock facing: East
            return checkForChestAndFrames(worldIn, planksPos, playerFacing, -1, -1);
        } else if (horizontalIndex == 2) {
            // Check for chest (and frames) in North (neg Z) and East (pos X). -> Multiblock facing: South
            return checkForChestAndFrames(worldIn, planksPos, playerFacing, 1, -1);
        } else if (horizontalIndex == 3) {
            // Check for chest (and frames) in East (pox X) and South (pox Z). -> Multiblock facing: West
            return checkForChestAndFrames(worldIn, planksPos, playerFacing, 1, 1);
        } else {
            return false;
        }
    }

    /** Checks in two given directions from a given BlockPos if chest and signs are in place for Multiblock. */
    private boolean checkForChestAndFrames (World worldIn, BlockPos planksPos, EnumFacing playerFacing, int xOff, int zOff) {
        // Erst nach Truhe schauen.
        if (worldIn.getBlockState(planksPos.add(xOff, 0, 0)).getBlock() == Blocks.CHEST) {
            // Jetzt noch nach Signs schauen.
            if ((worldIn.getBlockState(planksPos.add(0, 1, 0)).getBlock() == Blocks.WALL_SIGN || worldIn.getBlockState(planksPos.add(0, 1, 0)).getBlock() == Blocks.STANDING_SIGN)
                    && (worldIn.getBlockState(planksPos.add(xOff, 1, 0)).getBlock() == Blocks.WALL_SIGN || worldIn.getBlockState(planksPos.add(xOff, 1, 0)).getBlock() == Blocks.STANDING_SIGN))
            {
                // Der Länge nach in Richtung xOff aufbauen. -> Multiblock facing: South oder North.
                if (playerFacing == EnumFacing.NORTH || playerFacing == EnumFacing.EAST) {
                    buildMultiBlock(worldIn, planksPos, EnumFacing.SOUTH);
                } else if (playerFacing == EnumFacing.SOUTH || playerFacing == EnumFacing.WEST) {
                    buildMultiBlock(worldIn, planksPos, EnumFacing.NORTH);
                }
                return true;
            } else return false;
        } else if (worldIn.getBlockState(planksPos.add(0, 0, zOff)) == Blocks.CHEST) {
            // Jetzt noch nach Signs schauen.
            if ((worldIn.getBlockState(planksPos.add(0, 1, 0)).getBlock() == Blocks.WALL_SIGN || worldIn.getBlockState(planksPos.add(0, 1, 0)).getBlock() == Blocks.STANDING_SIGN)
                    && (worldIn.getBlockState(planksPos.add(0, 1, zOff)).getBlock() == Blocks.WALL_SIGN || worldIn.getBlockState(planksPos.add(0, 1, zOff)).getBlock() == Blocks.STANDING_SIGN))
            {
                // Der Länge nach in Richtung zOff aufbauen. -> Multiblock facing: East oder West.
                if (playerFacing == EnumFacing.NORTH || playerFacing == EnumFacing.WEST) {
                    buildMultiBlock(worldIn, planksPos, EnumFacing.EAST);
                } else if (playerFacing == EnumFacing.SOUTH || playerFacing == EnumFacing.EAST) {
                    buildMultiBlock(worldIn, planksPos, EnumFacing.WEST);
                }
                return true;
            } else return false;
        } else {
            return false;
        }
    }

    private void buildMultiBlock (World worldIn, BlockPos planksPos, EnumFacing multiBlockFacing) {
        int tableMetaCount = 0;
        if (multiBlockFacing == EnumFacing.NORTH) {
            for (int y = 0; y < 2; ++y){
                for (int x = 0; x > -2; --x) {
                    worldIn.setBlockState(planksPos.add(x, y, 0), Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.byMetadata(tableMetaCount)));
                    ++tableMetaCount;
                }
            }
        } else if (multiBlockFacing == EnumFacing.EAST) {
            for (int y = 0; y < 2; ++y) {
                for (int z = 0; z > -2; --z) {
                    worldIn.setBlockState(planksPos.add(0, y, z), Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.byMetadata(tableMetaCount)));
                    ++tableMetaCount;
                }
            }
        } else if (multiBlockFacing == EnumFacing.SOUTH) {
            for (int y = 0; y < 2; ++y){
                for (int x = 0; x < 2; ++x) {
                    worldIn.setBlockState(planksPos.add(x, y, 0), Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.byMetadata(tableMetaCount)));
                    ++tableMetaCount;
                }
            }
        } else if (multiBlockFacing == EnumFacing.WEST) {
            for (int y = 0; y < 2; ++y) {
                for (int z = 0; z < 2; ++z) {
                    worldIn.setBlockState(planksPos.add(0, y, z), Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.byMetadata(tableMetaCount)));
                    ++tableMetaCount;
                }
            }
        }
    }
}
