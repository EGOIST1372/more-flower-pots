package com.eternalhelldevs.moreflowerpots.blocks;

import com.eternalhelldevs.moreflowerpots.properties.FlowerProperty;
import com.eternalhelldevs.moreflowerpots.util.Flower;
/*
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TemplatePotBlock extends Block {
    public static final FlowerProperty FLOWER = FlowerProperty.create("flower", Flower.FLOWERS.values());
    protected static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);

    public TemplatePotBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FLOWER, Flower.NONE));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FLOWER);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (state.getBlock() instanceof TemplatePotBlock) {
            Block flower = state.get(TemplatePotBlock.FLOWER).getBlock();
            if (!player.isCreative() && !flower.equals(Blocks.AIR)) {
                BlockPos playerPos = new BlockPos(player.getX(), player.getY(), player.getZ());
                Block.dropStacks(world, playerPos, new ItemStack(flower));
            }
        }
    }

    public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockHitResult hit) {
        boolean potIsEmpty;
        ItemStack itemstack = player.getStackInHand(handIn);
        Item item = itemstack.getItem();
        Block flowerIn = Blocks.AIR;
        if (item instanceof BlockItem && Flower.getAllFlowerBlocks().contains(((BlockItem)item).getBlock())) {
            flowerIn = ((BlockItem)item).getBlock();
        }
        boolean doesNotHoldFlower = flowerIn == Blocks.AIR;
        boolean bl = potIsEmpty = (worldIn.getBlockState(pos).get(FLOWER)).getBlock() == Blocks.AIR;
        if (doesNotHoldFlower != potIsEmpty) {
            if (potIsEmpty) {
                Identifier rl = Registries.BLOCK.getId(flowerIn);
                worldIn.setBlockState(pos, this.getDefaultState().with(FLOWER, (rl.getNamespace().equals("biomesoplenty") ? Flower.FLOWERS.get("bop_" + rl.getPath()) : Flower.FLOWERS.get(rl.getPath()))), 3);
                player.incrementStat(Stats.POT_FLOWER);
                if (!player.getAbilities().creativeMode) {
                    itemstack.decrement(1);
                }
            } else {
                ItemStack itemStack1 = new ItemStack((worldIn.getBlockState(pos).get(FLOWER)).getBlock());
                if (itemstack.isEmpty()) {
                    player.setStackInHand(handIn, itemStack1);
                } else if (!player.giveItemStack(itemStack1)) {
                    player.dropItem(itemStack1, false);
                }
                worldIn.setBlockState(pos, this.getDefaultState(), 3);
            }
        }
        return ActionResult.SUCCESS;
    }
}
*/
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.stat.Stats;

public class TemplatePotBlock extends Block {
    // Example EnumProperty for custom flower logic.
    public static final EnumProperty<FlowerType> FLOWER = EnumProperty.of("flower", FlowerType.class);

    public TemplatePotBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FLOWER, FlowerType.NONE));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FLOWER);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                             PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack held = player.getStackInHand(hand);

        FlowerType currentFlower = state.get(FLOWER);

        // Placing a flower
        if (currentFlower == FlowerType.NONE && held.getItem() instanceof BlockItem bi) {
            Block block = bi.getBlock();
            if (block.getDefaultState().isIn(BlockTags.FLOWERS)) {
                if (!world.isClient) {
                    // Set the block's flower property
                    world.setBlockState(pos, state.with(FLOWER, FlowerType.fromBlock(block)), Block.NOTIFY_ALL);

                    if (!player.getAbilities().creativeMode) {
                        held.decrement(1);
                    }
                    // Modern vanilla stat handling: still valid for vanilla use
                    player.incrementStat(Stats.USE_ITEM.get(bi.getItem()));
                }
                return ActionResult.success(world.isClient);
            }
        }
        // Removing a flower
        else if (currentFlower != FlowerType.NONE && held.isEmpty()) {
            if (!world.isClient) {
                ItemStack flowerStack = new ItemStack(currentFlower.getBlock().asItem());
                // Offer to player inventory first. If full, drop
                if (!player.getInventory().offerOrDrop(flowerStack)) {
                    player.dropItem(flowerStack, false);
                }
                world.setBlockState(pos, state.with(FLOWER, FlowerType.NONE), Block.NOTIFY_ALL);
            }
            return ActionResult.success(world.isClient);
        }

        return ActionResult.PASS;
    }
    
    // Example inner enum for demonstration; replace with your custom logic or Flower registry
    public enum FlowerType {
        NONE(Blocks.AIR),
        DANDELION(Blocks.DANDELION),
        POPPY(Blocks.POPPY),
        // Add more types as needed

        ;
        private final Block block;

        FlowerType(Block block) {
            this.block = block;
        }

        public Block getBlock() {
            return block;
        }

        public static FlowerType fromBlock(Block block) {
            for (FlowerType type : values()) {
                if (type.block == block) {
                    return type;
                }
            }
            return NONE;
        }
    }
}
