/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.ClientScheduler;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.controller.orbital.OrbitalEffectController;
import shordinger.astralsorcery.client.effect.controller.orbital.OrbitalPropertiesInfuser;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.auxiliary.LiquidStarlightChaliceHandler;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.crafting.IGatedRecipe;
import shordinger.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import shordinger.astralsorcery.common.crafting.infusion.ActiveInfusionTask;
import shordinger.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.entities.EntityFlare;
import shordinger.astralsorcery.common.item.base.IWandInteract;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.lib.MultiBlockArrays;
import shordinger.astralsorcery.common.lib.Sounds;
import shordinger.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import shordinger.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import shordinger.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import shordinger.astralsorcery.common.structure.array.PatternBlockArray;
import shordinger.astralsorcery.common.structure.change.ChangeSubscriber;
import shordinger.astralsorcery.common.structure.match.StructureMatcherPatternArray;
import shordinger.astralsorcery.common.tile.base.TileReceiverBase;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.PatternMatchHelper;
import shordinger.astralsorcery.common.util.SoundHelper;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.log.LogCategory;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.SoundCategory;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.common.util.Constants;
import shordinger.wrapper.net.minecraftforge.fluids.FluidStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileStarlightInfuser
 * Created by HellFirePvP
 * Date: 11.12.2016 / 17:11
 */
public class TileStarlightInfuser extends TileReceiverBase implements IWandInteract, IMultiblockDependantTile {

    public static final BlockPos[] offsetsLiquidStarlight = new BlockPos[]{new BlockPos(-2, -1, -1),
        new BlockPos(-2, -1, 0), new BlockPos(-2, -1, 1), new BlockPos(2, -1, -1), new BlockPos(2, -1, 0),
        new BlockPos(2, -1, 1), new BlockPos(-1, -1, -2), new BlockPos(0, -1, -2), new BlockPos(1, -1, -2),
        new BlockPos(-1, -1, 2), new BlockPos(0, -1, 2), new BlockPos(1, -1, 2)};

    private ActiveInfusionTask craftingTask = null;

    private Object clientOrbitalCrafting = null;
    private Object clientOrbitalCraftingMirror = null;

    private ItemStack stack = ItemStack.EMPTY;
    private ChangeSubscriber<StructureMatcherPatternArray> structureMatch = null;
    private boolean hasMultiblock = false, doesSeeSky = false;

    @Override
    public void update() {
        super.update();

        if ((ticksExisted & 15) == 0) {
            updateSkyState();
        }

        if (!world.isRemote) {
            updateMultiblockState();

            if (doTryCraft()) {
                markForUpdate();
            }
        } else {
            if (craftingTask != null) {
                doClientCraftEffects();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void doClientCraftEffects() {
        craftingTask.getRecipeToCraft()
            .onCraftClientTick(this, ClientScheduler.getClientTick(), rand);

        if (clientOrbitalCrafting == null || ((OrbitalEffectController) clientOrbitalCrafting).isRemoved()) {
            OrbitalPropertiesInfuser prop = new OrbitalPropertiesInfuser(this, false);
            OrbitalEffectController ctrl = EffectHandler.getInstance()
                .orbital(prop, prop, null);
            ctrl.setOffset(new Vector3(this).add(0.5, 0, 0.5));
            ctrl.setOrbitAxis(Vector3.RotAxis.Y_AXIS);
            ctrl.setOrbitRadius(2);
            ctrl.setTicksPerRotation(80);
            clientOrbitalCrafting = ctrl;
        }
        if (clientOrbitalCraftingMirror == null
            || ((OrbitalEffectController) clientOrbitalCraftingMirror).isRemoved()) {
            OrbitalPropertiesInfuser prop = new OrbitalPropertiesInfuser(this, true);
            OrbitalEffectController ctrl = EffectHandler.getInstance()
                .orbital(prop, prop, null);
            ctrl.setOffset(new Vector3(this).add(0.5, 0, 0.5));
            ctrl.setOrbitAxis(Vector3.RotAxis.Y_AXIS);
            ctrl.setTicksPerRotation(80);
            ctrl.setTickOffset(ctrl.getMaxAge() / 2);
            ctrl.setOrbitRadius(2);
            clientOrbitalCraftingMirror = ctrl;
        }

        Vector3 target = new Vector3(this).add(0.5, 0.8, 0.5);
        for (BlockPos bp : craftingTask.getPendingChalicePositions()) {
            for (int i = 0; i < 4; i++) {
                Vector3 from = new Vector3(bp)
                    .add(-0.2 + rand.nextFloat() * 1.4, 1.1 + rand.nextFloat() * 1.4, -0.2 + rand.nextFloat() * 1.4);
                Vector3 mov = target.clone()
                    .subtract(from)
                    .normalize()
                    .multiply(0.05 + 0.05 * rand.nextFloat());
                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(from.getX(), from.getY(), from.getZ());
                p.motion(mov.getX(), mov.getY(), mov.getZ())
                    .setMaxAge(30 + rand.nextInt(25));
                p.gravity(0.004)
                    .scale(0.25F)
                    .setColor(Color.WHITE);
                if (rand.nextInt(4) == 0) {
                    p.setColor(IConstellation.major);
                }
            }
        }
    }

    private boolean doTryCraft() {
        if (craftingTask == null) return false;

        AbstractInfusionRecipe altarRecipe = craftingTask.getRecipeToCraft();
        if (!altarRecipe.matches(this)) {
            abortCrafting();
            return true;
        }
        if (craftingTask.isFinished()) {
            finishCrafting();
            return true;
        }
        boolean changed = false;
        if (craftingTask.tick(this)) {
            changed = true;
        }
        craftingTask.getRecipeToCraft()
            .onCraftServerTick(this, craftingTask.getTicksCrafting(), rand);
        return changed;
    }

    private void finishCrafting() {
        if (craftingTask == null) return;

        AbstractInfusionRecipe altarRecipe = craftingTask.getRecipeToCraft();
        ItemStack out = altarRecipe.getOutput(this);
        if (!out.isEmpty()) {
            out = ItemUtils.copyStackWithSize(out, out.getCount());
        }

        if (altarRecipe.mayDeleteInput(this)) {
            this.stack = ItemStack.EMPTY;
        } else {
            altarRecipe.handleInputDecrement(this);
        }

        if (!out.isEmpty()) {
            if (out.getCount() > 0) {
                ItemUtils.dropItem(world, pos.getX() + 0.5, pos.getY() + 1.3, pos.getZ() + 0.5, out)
                    .setNoDespawn();
            }
        }
        int size = offsetsLiquidStarlight.length;
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            indexes.add(i);
        }
        Collections.shuffle(indexes);
        while (size > 0) {
            BlockPos offset = offsetsLiquidStarlight[indexes.get(size - 1)];
            size--;
            if (world.rand.nextFloat() < craftingTask.getRecipeToCraft()
                .getLiquidStarlightConsumptionChance()) {
                if (!craftingTask.getSupportingChalices()
                    .isEmpty()) {
                    TileChalice tc = craftingTask.getSupportingChalices()
                        .get(
                            rand.nextInt(
                                craftingTask.getSupportingChalices()
                                    .size()));
                    if (tc != null) {
                        tc.getTank()
                            .drain(new FluidStack(BlocksAS.fluidLiquidStarlight, 400), true);
                        tc.markForUpdate();
                    }
                } else {
                    world.setBlockToAir(getPos().add(offset));
                }
                EntityFlare.spawnAmbient(
                    world,
                    new Vector3(this).add(-3 + rand.nextFloat() * 7, 0.6, -3 + rand.nextFloat() * 7));
                if (!altarRecipe.doesConsumeMultiple()) break;
            }
        }
        craftingTask.getRecipeToCraft()
            .onCraftServerFinish(this, rand);
        ResearchManager.informCraftingInfusionCompletion(this, craftingTask);
        SoundHelper.playSoundAround(Sounds.craftFinish, world, getPos(), 1F, 1.7F);
        EntityFlare
            .spawnAmbient(world, new Vector3(this).add(-3 + rand.nextFloat() * 7, 0.6, -3 + rand.nextFloat() * 7));
        craftingTask = null;
    }

    private void updateMultiblockState() {
        if (this.structureMatch == null) {
            this.structureMatch = PatternMatchHelper.getOrCreateMatcher(getWorld(), getPos(), getRequiredStructure());
        }
        boolean found = this.structureMatch.matches(getWorld());
        if (found != this.hasMultiblock) {
            LogCategory.STRUCTURE_MATCH.info(
                () -> "Structure match updated: " + this.getClass()
                    .getName() + " at " + this.getPos() + " (" + this.hasMultiblock + " -> " + found + ")");
            this.hasMultiblock = found;
            markForUpdate();
        }
    }

    private void updateSkyState() {
        boolean seesSky = MiscUtils.canSeeSky(this.getWorld(), this.getPos(), true, this.doesSeeSky);
        boolean update = doesSeeSky != seesSky;
        this.doesSeeSky = seesSky;
        if (update) {
            markForUpdate();
        }
    }

    @Nullable
    @Override
    public PatternBlockArray getRequiredStructure() {
        return MultiBlockArrays.patternStarlightInfuser;
    }

    @Nonnull
    @Override
    public BlockPos getLocationPos() {
        return this.getPos();
    }

    @Nonnull
    public ItemStack getInputStack() {
        return stack;
    }

    public void setStack(@Nonnull ItemStack stack) {
        this.stack = stack;
    }

    public boolean hasMultiblock() {
        return hasMultiblock;
    }

    public boolean doesSeeSky() {
        return doesSeeSky;
    }

    private void findRecipe(EntityPlayer crafter) {
        if (craftingTask != null) return;

        AbstractInfusionRecipe recipe = InfusionRecipeRegistry.findMatchingRecipe(this);
        if (recipe instanceof IGatedRecipe) {
            if (!((IGatedRecipe) recipe).hasProgressionServer(crafter)) return;
        }
        if (recipe != null) {
            this.craftingTask = new ActiveInfusionTask(recipe, crafter.getUniqueID());
            this.craftingTask.addChalices(
                LiquidStarlightChaliceHandler.findNearbyChalicesThatContain(
                    this,
                    new FluidStack(BlocksAS.fluidLiquidStarlight, this.craftingTask.getChaliceRequiredAmount())));
            markForUpdate();
        }
    }

    public void abortCrafting() {
        this.craftingTask = null;
        markForUpdate();
    }

    public ActiveInfusionTask getCraftingTask() {
        return craftingTask;
    }

    @SideOnly(Side.CLIENT)
    public OrbitalEffectController getClientOrbitalCrafting() {
        if (clientOrbitalCrafting == null) return null;
        return (OrbitalEffectController) clientOrbitalCrafting;
    }

    @SideOnly(Side.CLIENT)
    public OrbitalEffectController getClientOrbitalCraftingMirror() {
        if (clientOrbitalCraftingMirror == null) return null;
        return (OrbitalEffectController) clientOrbitalCraftingMirror;
    }

    public boolean canCraft() {
        return hasMultiblock() && !isInvalid() && doesSeeSky();
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.stack = NBTHelper.getStack(compound, "stack");
        this.hasMultiblock = compound.getBoolean("mbState");
        this.doesSeeSky = compound.getBoolean("seesSky");

        boolean wasNull = this.craftingTask == null;
        this.craftingTask = null;
        if (compound.hasKey("recipeId") && compound.hasKey("recipeTick")) {
            int recipeId = compound.getInteger("recipeId");
            AbstractInfusionRecipe recipe = InfusionRecipeRegistry.getRecipe(recipeId);
            if (recipe == null) {
                AstralSorcery.log.info(
                    "Recipe with unknown/invalid ID found: " + recipeId + " for Starlight Infuser at " + getPos());
            } else {
                UUID uuidCraft = UUID.fromString(compound.getString("crafterUUID"));
                int tick = compound.getInteger("recipeTick");

                NBTTagList tl = compound.getTagList("chalicePositions", Constants.NBT.TAG_COMPOUND);
                List<BlockPos> tcList = new LinkedList<>();
                for (int i = 0; i < tl.tagCount(); i++) {
                    tcList.add(NBTHelper.readBlockPosFromNBT(tl.getCompoundTagAt(i)));
                }

                this.craftingTask = new ActiveInfusionTask(recipe, uuidCraft);
                this.craftingTask.forceTick(tick);
                this.craftingTask.addPendingChalicePositions(tcList);
            }
        }
        if (!wasNull && this.craftingTask == null) {
            clientOrbitalCrafting = null;
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        NBTHelper.setStack(compound, "stack", stack);
        compound.setBoolean("mbState", hasMultiblock);
        compound.setBoolean("seesSky", doesSeeSky);

        if (craftingTask != null) {
            compound.setInteger(
                "recipeId",
                craftingTask.getRecipeToCraft()
                    .getUniqueRecipeId());
            compound.setInteger("recipeTick", craftingTask.getTicksCrafting());
            compound.setString("crafterUUID", craftingTask.getPlayerCraftingUUID());
            NBTTagList chalicePositions = new NBTTagList();
            for (TileChalice tc : craftingTask.getSupportingChalices()) {
                NBTTagCompound cmp = new NBTTagCompound();
                NBTHelper.writeBlockPosToNBT(tc.getPos(), cmp);
                chalicePositions.appendTag(cmp);
            }
            compound.setTag("chalicePositions", chalicePositions);
        }
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blockstarlightinfuser.name";
    }

    @Override
    @Nonnull
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TransmissionReceiverStarlightInfuser(at);
    }

    public void onInteract(EntityPlayer playerIn, EnumHand heldHand, ItemStack heldItem) {
        if (!playerIn.getEntityWorld().isRemote) {
            if (playerIn.isSneaking()) {
                if (!stack.isEmpty()) {
                    playerIn.inventory.placeItemBackInInventory(world, stack);
                    stack = ItemStack.EMPTY;
                    world.playSound(
                        null,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        SoundEvents.ENTITY_ITEM_PICKUP,
                        SoundCategory.PLAYERS,
                        0.5F,
                        world.rand.nextFloat() * 0.2F + 0.8F);
                    markForUpdate();
                }
            } else {
                if (!heldItem.isEmpty()) {
                    if (stack.isEmpty()) {
                        this.stack = ItemUtils.copyStackWithSize(heldItem, 1);
                        if (!playerIn.isCreative()) {
                            heldItem.setCount(heldItem.getCount() - 1);
                        }
                        if (heldItem.getCount() <= 0) {
                            playerIn.setHeldItem(heldHand, ItemStack.EMPTY);
                        }
                        world.playSound(
                            null,
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            SoundEvents.ENTITY_ITEM_PICKUP,
                            SoundCategory.PLAYERS,
                            0.5F,
                            world.rand.nextFloat() * 0.2F + 0.8F);
                        markForUpdate();
                    }
                }
            }
        }
    }

    @Override
    public void onInteract(World world, BlockPos pos, EntityPlayer player, EnumFacing side, boolean sneak) {
        if (!world.isRemote) {
            findRecipe(player);
        }
    }

    public static class TransmissionReceiverStarlightInfuser extends SimpleTransmissionReceiver {

        public TransmissionReceiverStarlightInfuser(BlockPos thisPos) {
            super(thisPos);
        }

        @Override
        public void onStarlightReceive(World world, boolean isChunkLoaded, IWeakConstellation type, double amount) {
            // No-Op
        }

        @Override
        public TransmissionClassRegistry.TransmissionProvider getProvider() {
            return new StarlightInfuserReceiverProvider();
        }

    }

    public static class StarlightInfuserReceiverProvider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public TransmissionReceiverStarlightInfuser provideEmptyNode() {
            return new TransmissionReceiverStarlightInfuser(null);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":TransmissionReceiverStarlightInfuser";
        }

    }
}
