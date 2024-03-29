/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package shordinger.wrapper.net.minecraftforge.common.util;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketClientSettings;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.server.management.PlayerInteractionManager;
import shordinger.wrapper.net.minecraft.stats.StatBase;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.math.Vec3d;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.WorldServer;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLCommonHandler;

// Preliminary, simple Fake Player class
public class FakePlayer extends EntityPlayerMP {

    public FakePlayer(WorldServer world, GameProfile name) {
        super(
            FMLCommonHandler.instance()
                .getMinecraftServerInstance(),
            world,
            name,
            new PlayerInteractionManager(world));
    }

    @Override
    public Vec3d getPositionVector() {
        return new Vec3d(0, 0, 0);
    }

    @Override
    public boolean canUseCommand(int i, String s) {
        return false;
    }

    @Override
    public void sendStatusMessage(ITextComponent chatComponent, boolean actionBar) {
    }

    @Override
    public void sendMessage(ITextComponent component) {
    }

    @Override
    public void addStat(StatBase par1StatBase, int par2) {
    }

    @Override
    public void openGui(Object mod, int modGuiId, World world, int x, int y, int z) {
    }

    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
        return true;
    }

    @Override
    public boolean canAttackPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void onDeath(DamageSource source) {
        return;
    }

    @Override
    public void onUpdate() {
        return;
    }

    @Override
    public Entity changeDimension(int dim, ITeleporter teleporter) {
        return this;
    }

    @Override
    public void handleClientSettings(CPacketClientSettings pkt) {
        return;
    }

    @Override
    @Nullable
    public MinecraftServer getServer() {
        return FMLCommonHandler.instance()
            .getMinecraftServerInstance();
    }
}
