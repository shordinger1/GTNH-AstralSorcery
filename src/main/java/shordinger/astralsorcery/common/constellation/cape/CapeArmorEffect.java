/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.cape;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.data.config.entry.ConfigEntry;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.inventory.EntityEquipmentSlot;
import shordinger.wrapper.net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeArmorEffect
 * Created by HellFirePvP
 * Date: 09.10.2017 / 23:23
 */
public abstract class CapeArmorEffect extends ConfigEntry {

    protected static final Random rand = new Random();

    private NBTTagCompound data = new NBTTagCompound();

    // MUST BE OVERWRITTEN, WITH NBTTAGCMP AS ONLY PARAM!
    protected CapeArmorEffect(NBTTagCompound cmp, String key) {
        super(Section.CAPE, key);
        this.data = cmp;
    }

    public abstract IConstellation getAssociatedConstellation();

    @SideOnly(Side.CLIENT)
    public abstract void playActiveParticleTick(EntityPlayer pl);

    public final NBTTagCompound getData() {
        return this.data;
    }

    public final void flush(EntityLivingBase entity) {
        ItemStack is = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!is.isEmpty()) {
            NBTTagCompound cmp = NBTHelper.getPersistentData(is);
            flush(cmp);
        }
    }

    public final void flush(NBTTagCompound out) {
        for (String key : this.data.getKeySet()) {
            out.setTag(key, out.getTag(key));
        }
    }

    @SideOnly(Side.CLIENT)
    protected void playConstellationCapeSparkles(EntityPlayer pl, float strength) {
        if (pl == Minecraft.getMinecraft().thePlayer && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
            strength *= 0.1F;
        }
        if (rand.nextFloat() < strength) {
            Color c = getAssociatedConstellation().getConstellationColor();
            if (c != null) {
                double x = pl.posX + rand.nextFloat() * pl.width - (pl.width / 2);
                double y = pl.posY + rand.nextFloat() * (pl.height / 2) + 0.2;
                double z = pl.posZ + rand.nextFloat() * pl.width - (pl.width / 2);

                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x, y, z);
                p.setColor(c)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(rand.nextFloat() * 0.5F + 0.3F);
                if (rand.nextInt(3) == 0) {
                    p.setColor(IConstellation.weak);
                }
                p.setMaxAge(30 + rand.nextInt(20));

                if (rand.nextFloat() < 0.8F) {
                    p = EffectHelper.genericFlareParticle(x, y, z);
                    p.setColor(Color.WHITE)
                        .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                    p.scale(rand.nextFloat() * 0.2F + 0.1F);
                    p.setMaxAge(20 + rand.nextInt(10));
                }
            }
        }
    }

}
