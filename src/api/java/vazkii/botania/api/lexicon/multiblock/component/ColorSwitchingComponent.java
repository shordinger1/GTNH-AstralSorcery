/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * <p>
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * <p>
 * File Created @ [Jun 27, 2015, 7:20:09 PM (GMT)]
 */
package vazkii.botania.api.lexicon.multiblock.component;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.world.World;

import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;
import vazkii.botania.api.BotaniaAPI;

/**
 * A multiblock component that switches through the 16 colors of the
 * minecraft spectrum. This can be used for flowers, for example.
 */
public class ColorSwitchingComponent extends MultiblockComponent {

    private final PropertyEnum<EnumDyeColor> colorProp;

    public ColorSwitchingComponent(BlockPos relPos, Block block, PropertyEnum<EnumDyeColor> colorProp) {
        super(relPos, block.getDefaultState());
        this.colorProp = colorProp;
    }

    @Override
    public IBlockState getBlockState() {
        return state.withProperty(
            colorProp,
            EnumDyeColor.byMetadata((int) (BotaniaAPI.internalHandler.getWorldElapsedTicks() / 20) % 16));
    }

    @Override
    public boolean matches(World world, BlockPos pos) {
        return world.getBlockState(pos)
            .getBlock() == getBlockState().getBlock();
    }

    @Override
    public MultiblockComponent copy() {
        return new ColorSwitchingComponent(getRelativePosition(), getBlockState().getBlock(), colorProp);
    }

}
