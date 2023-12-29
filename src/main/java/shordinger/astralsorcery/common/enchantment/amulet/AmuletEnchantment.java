/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.enchantment.amulet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import shordinger.astralsorcery.common.enchantment.dynamic.DynamicEnchantment;
import shordinger.astralsorcery.migration.MathHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AmuletEnchantment
 * Created by HellFirePvP
 * Date: 27.01.2018 / 13:37
 */
public class AmuletEnchantment extends DynamicEnchantment {

    public AmuletEnchantment(Type type, @Nonnull Enchantment enchantment, int levelAddition) {
        super(type, enchantment, levelAddition);
    }

    public AmuletEnchantment(Type type, int levelAddition) {
        super(type, levelAddition);
    }

    @SideOnly(Side.CLIENT)
    public String getDescription() {
        String unlocKey = "amulet.enchantment." + this.type.name()
            .toLowerCase() + ".name";
        String locLevels = I18n.format("amulet.enchantment.level." + (this.levelAddition == 1 ? "one" : "more"));
        if (this.type.hasEnchantmentTag()) {
            String locEnch = I18n.format(this.enchantment.getName());
            return I18n.format(unlocKey, String.valueOf(this.levelAddition), locLevels, locEnch);
        } else {
            return I18n.format(unlocKey, String.valueOf(this.levelAddition), locLevels);
        }
    }

    public boolean canMerge(AmuletEnchantment other) {
        return this.type.equals(other.type)
            && (!this.type.hasEnchantmentTag() || this.enchantment.equals(other.enchantment));
    }

    public void merge(AmuletEnchantment src) {
        if (canMerge(src)) {
            this.levelAddition += src.levelAddition;
        }
    }

    public NBTTagCompound serialize() {
        NBTTagCompound cmp = new NBTTagCompound();
        cmp.setInteger("type", this.type.ordinal());
        cmp.setInteger("level", this.levelAddition);
        if (this.type.hasEnchantmentTag()) { // Enchantment must not be null here anyway as the type requires a ench to
            // begin with
            cmp.setString(
                "ench",
                this.enchantment.getRegistryName()
                    .toString());
        }
        return cmp;
    }

    @Nullable
    public static AmuletEnchantment deserialize(NBTTagCompound cmp) {
        int typeId = cmp.getInteger("type");
        Type type = Type.values()[MathHelper.clamp(typeId, 0, Type.values().length - 1)];
        int level = Math.max(0, cmp.getInteger("level"));
        if (type.hasEnchantmentTag()) {
            ResourceLocation res = new ResourceLocation(cmp.getString("ench"));
            // Disallow dungeontactics enchantments on the prism; see #1302
            if (res.getResourceDomain()
                .equals("dungeontactics")) {
                return null;
            }

            Enchantment e = ForgeRegistries.ENCHANTMENTS.getValue(res);
            if (e != null) {
                return new AmuletEnchantment(type, e, level);
            }
        } else {
            return new AmuletEnchantment(type, level);
        }
        return null;
    }

}
