package com.astralsorcery.gtnh_astralsorcery.common.block;

import net.minecraft.block.Block;

public class Properties {

    float hardness;
    float Resistance;
    int harvestLevel;
    String harvestTool;
    Block.SoundType soundType;

    public Properties(float hardness, float resistance, int harvestLevel, String harvestTool,
        Block.SoundType soundType) {
        this.hardness = hardness;
        Resistance = resistance;
        this.harvestLevel = harvestLevel;
        this.harvestTool = harvestTool;
        this.soundType = soundType;
    }

    public Block setProperties(Block block) {
        block.setStepSound(this.soundType);
        block.setHarvestLevel(this.harvestTool, this.harvestLevel);
        block.setHardness(this.hardness);
        block.setResistance(this.Resistance);
        return block;
    }

    public static Properties defaultMarble = new Properties(3f, 5f, 0, "pickaxe", Block.soundTypeStone);
    public static Properties coatedGlass = new Properties(1f, 5f, 0, null, Block.soundTypeGlass);

    public static Block defaultMarble(Block block) {
        return defaultMarble.setProperties(block);
    }

    public static Block defaultBlackMarble(Block block) {
        return defaultMarble.setProperties(block);
    }

    public static Block coatedGlass(Block block) {
        return coatedGlass.setProperties(block);
    }
    //
    // public static Block.Properties defaultAir(Block block) {
    // return Block.Properties.create(Material.AIR, MaterialColor.AIR)
    // .doesNotBlockMovement();
    // }
    //
    // public static Block.Properties defaultSand(Block block) {
    // return Block.Properties.create(Material.SAND, MaterialColor.SAND)
    // .hardnessAndResistance(0.5F)
    // .harvestTool(ToolType.SHOVEL)
    // .sound(SoundType.SAND);
    // }
    //
    // public static Block.Properties defaultRock(Block block) {
    // return Block.Properties.create(Material.ROCK, MaterialColor.STONE)
    // .hardnessAndResistance(1.5F, 6.0F)
    // .harvestTool(ToolType.PICKAXE)
    // .sound(SoundType.STONE);
    // }
    //
    // public static Block.Properties defaultMetal(Block block) {
    // return Block.Properties.create(Material.IRON, color)
    // .hardnessAndResistance(1.5F, 6.0F)
    // .harvestTool(ToolType.PICKAXE)
    // .harvestLevel(1)
    // .sound(SoundType.METAL);
    // }
    //
    // public static Block.Properties defaultPlant(Block block) {
    // return Block.create(Material.PLANTS)
    // .doesNotBlockMovement()
    // .hardnessAndResistance(0)
    // .sound(SoundType.PLANT);
    // }
    //
    // public static Block defaultTickingPlant(Block block) {
    // return Block.Properties.create(Material.PLANTS)
    // .doesNotBlockMovement()
    // .tickRandomly()
    // .hardnessAndResistance(0)
    // .sound(SoundType.PLANT);
    // }
    //
    // public static Block defaultGoldMachinery(Block block) {
    // return Block.Properties.create(Material.IRON, MaterialColor.GOLD)
    // .hardnessAndResistance(1.0F, 4.0F)
    // .sound(SoundType.STONE);
    // }
}
