/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.util.resource.AssetLibrary;
import shordinger.astralsorcery.client.util.resource.AssetLoader;
import shordinger.astralsorcery.client.util.resource.BindableResource;
import shordinger.astralsorcery.common.CommonProxy;
import shordinger.astralsorcery.common.util.DamageUtil;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PotionBleed
 * Created by HellFirePvP
 * Date: 18.11.2016 / 01:51
 */
public class PotionBleed extends PotionCustomTexture {

    private static Object texBuffer = null;

    public PotionBleed() {
        super(true, 0x751200);
        setPotionName("effect.as.bleed");
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity instanceof EntityPlayer && !entity.getEntityWorld().isRemote
            && entity.getEntityWorld() instanceof WorldServer
            && entity.getEntityWorld()
            .getMinecraftServer()
            .isPVPEnabled()) {
            return;
        }
        int preTime = entity.hurtResistantTime;
        DamageUtil.attackEntityFrom(entity, CommonProxy.dmgSourceBleed, 0.5F * (amplifier + 1));
        entity.hurtResistantTime = Math.max(preTime, entity.hurtResistantTime);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BindableResource getResource() {
        if (texBuffer == null) {
            texBuffer = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "potion_bleed");
        }
        return (BindableResource) texBuffer;
    }
}
