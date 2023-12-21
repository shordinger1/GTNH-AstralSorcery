package com.astralsorcery.gtnh_astralsorcery.lib;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;

public class MaterialsAS {

    private final MapColor color;
    private boolean blocksMovement = true;
    private boolean canBurn = false;
    private boolean isLiquid = false;
    private boolean isReplaceable = false;
    private boolean isSolid = true;
    private boolean isOpaque = true;

    public MaterialsAS(MapColor color) {
        this.color = color;
    }

    public MaterialsAS liquid() {
        this.isLiquid = true;
        return this;
    }

    public MaterialsAS notSolid() {
        this.isSolid = false;
        return this;
    }

    public MaterialsAS doesNotBlockMovement() {
        this.blocksMovement = false;
        return this;
    }

    public MaterialsAS notOpaque() {
        this.isOpaque = false;
        return this;
    }

    public MaterialsAS flammable() {
        this.canBurn = true;
        return this;
    }

    public MaterialsAS replaceable() {
        this.isReplaceable = true;
        return this;
    }

    public Material build() {
        Material m;
        if (isLiquid) m = new MaterialLiquid(color);
        else m = new Material(color);
        if (isReplaceable) m.setReplaceable();
        return m;
    }

    public static Material MARBLE = new MaterialsAS(MapColor.snowColor).build();
    public static Material BLACK_MARBLE = new MaterialsAS(MapColor.blackColor).build();
    public static Material INFUSED_WOOD = new MaterialsAS(MapColor.woodColor).build();

}
