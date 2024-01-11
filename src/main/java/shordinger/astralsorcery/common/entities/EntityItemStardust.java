/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.entities;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.item.crystal.base.ItemRockCrystalBase;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktParticleEvent;
import shordinger.astralsorcery.common.util.EntityUtils;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.item.EntityItem;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityItemStardust
 * Created by HellFirePvP
 * Date: 14.09.2016 / 17:42
 */
public class EntityItemStardust extends EntityItem implements EntityStarlightReacttant {

    private static final AxisAlignedBB boxCraft = new AxisAlignedBB(-0.6, -0.2, -0.6, 0.6, 0.2, 0.6);

    public static final int TOTAL_MERGE_TIME = 30 * 20;
    private int inertMergeTick = 0;

    public EntityItemStardust(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityItemStardust(World worldIn) {
        super(worldIn);
    }

    public EntityItemStardust(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (age + 5 >= this.lifespan) {
            age = 0;
        }

        if (Config.craftingLiqCelestialCrystalForm) {
            checkMergeConditions();
        }
    }

    private void checkMergeConditions() {
        if (world.isRemote) {
            if (canCraft()) {
                spawnCraftingParticles();
            }
        } else {
            if (canCraft()) {
                inertMergeTick++;
                if (inertMergeTick >= TOTAL_MERGE_TIME && rand.nextInt(20) == 0) {
                    buildCelestialCrystals();
                }
            } else {
                inertMergeTick = 0;
            }
        }
    }

    private void buildCelestialCrystals() {
        if (world.setBlockState(getPosition(), BlocksAS.celestialCrystals.getDefaultState())) {
            PacketChannel.CHANNEL.sendToAllAround(
                new PktParticleEvent(PktParticleEvent.ParticleEventType.CELESTIAL_CRYSTAL_FORM, posX, posY, posZ),
                PacketChannel.pointFromPos(world, getPosition(), 64));

            getItem().setCount(getItem().getCount() - 1);
            List<Entity> foundItems = world.getEntitiesInAABBexcluding(
                this,
                boxCraft.offset(posX, posY, posZ)
                    .grow(0.1),
                EntityUtils.selectItemClassInstaceof(ItemRockCrystalBase.class));
            if (foundItems.size() > 0) {
                EntityItem ei = (EntityItem) foundItems.get(0);
                ItemStack stack = ei.getItem();
                getItem().setCount(getItem().getCount() - 1);
                stack.setCount(stack.getCount() - 1);
                if (stack.getCount() <= 0) {
                    ei.setDead();
                } else {
                    ei.setItem(stack);
                }
            }
        } else {
            inertMergeTick -= 20; // Retry later...
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnCraftingParticles() {
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
            posX + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1),
            posY + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1),
            posZ + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1));
        p.motion(
            rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1));
        p.gravity(0.2);
        p.scale(0.2F);
    }

    @SideOnly(Side.CLIENT)
    public static void spawnFormationParticles(PktParticleEvent event) {

    }

    private boolean canCraft() {
        if (!isInLiquidStarlight(this)) return false;

        List<Entity> foundItems = world.getEntitiesInAABBexcluding(
            this,
            boxCraft.offset(posX, posY, posZ),
            EntityUtils.selectItemClassInstaceof(ItemRockCrystalBase.class));
        return foundItems.size() > 0;
    }

}
