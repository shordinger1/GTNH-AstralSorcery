/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.crafttweaker.network;

import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.helper.CraftingAccessManager;
import shordinger.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import shordinger.astralsorcery.common.crafting.infusion.recipes.BasicInfusionRecipe;
import shordinger.astralsorcery.common.util.ByteBufUtils;
import shordinger.wrapper.net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InfusionRecipeAdd
 * Created by HellFirePvP
 * Date: 27.02.2017 / 01:59
 */
public class InfusionRecipeAdd implements SerializeableRecipe {

    private ItemStack out;
    private ItemHandle in;
    private boolean consumeAll;
    private float consumeChance;
    private int craftingTickTime;

    InfusionRecipeAdd() {
    }

    public InfusionRecipeAdd(ItemHandle in, ItemStack out, boolean consumeMultiple, float consumeChance,
                             int craftingTickTime) {
        this.in = in;
        this.out = out;
        this.consumeAll = consumeMultiple;
        this.consumeChance = consumeChance;
        this.craftingTickTime = craftingTickTime;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.INFUSION_ADD;
    }

    @Override
    public void read(ByteBuf buf) {
        this.in = ItemHandle.deserialize(buf);
        this.out = ByteBufUtils.readItemStack(buf);
        this.consumeAll = buf.readBoolean();
        this.consumeChance = buf.readFloat();
        this.craftingTickTime = buf.readInt();
    }

    @Override
    public void write(ByteBuf buf) {
        this.in.serialize(buf);
        ByteBufUtils.writeItemStack(buf, this.out);
        buf.writeBoolean(this.consumeAll);
        buf.writeFloat(this.consumeChance);
        buf.writeInt(this.craftingTickTime);
    }

    @Override
    public void applyRecipe() {
        CraftingAccessManager.registerMTInfusion(compile());
    }

    public AbstractInfusionRecipe compile() {
        return new BasicInfusionRecipe(out, this.in) {

            @Override
            public int craftingTickTime() {
                return craftingTickTime;
            }

            @Override
            public boolean doesConsumeMultiple() {
                return consumeAll;
            }
        }.setLiquidStarlightConsumptionChance(consumeChance);
    }

}
