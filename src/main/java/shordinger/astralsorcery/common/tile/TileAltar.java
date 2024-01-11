/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.ClientScheduler;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.client.util.PositionedLoopSound;
import shordinger.astralsorcery.client.util.SpriteLibrary;
import shordinger.astralsorcery.common.block.network.BlockAltar;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import shordinger.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import shordinger.astralsorcery.common.crafting.IGatedRecipe;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import shordinger.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import shordinger.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import shordinger.astralsorcery.common.crafting.helper.ShapeMap;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.data.world.WorldCacheManager;
import shordinger.astralsorcery.common.data.world.data.StructureMatchingBuffer;
import shordinger.astralsorcery.common.entities.EntityFlare;
import shordinger.astralsorcery.common.item.base.IWandInteract;
import shordinger.astralsorcery.common.item.base.ItemConstellationFocus;
import shordinger.astralsorcery.common.item.block.ItemBlockAltar;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.lib.MultiBlockArrays;
import shordinger.astralsorcery.common.lib.Sounds;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktParticleEvent;
import shordinger.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import shordinger.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import shordinger.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import shordinger.astralsorcery.common.structure.change.ChangeSubscriber;
import shordinger.astralsorcery.common.structure.match.StructureMatcherPatternArray;
import shordinger.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import shordinger.astralsorcery.common.util.*;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.structure.array.PatternBlockArray;
import shordinger.astralsorcery.common.util.log.LogCategory;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.SoundCategory;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.common.ForgeHooks;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileAltar
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:18
 */
public class TileAltar extends TileReceiverBaseInventory implements IWandInteract, IMultiblockDependantTile {

    private static final Random rand = new Random();

    private float posDistribution = -1;

    private ActiveCraftingTask craftingTask = null;
    private Object clientCraftSound = null;

    private AltarLevel level = AltarLevel.DISCOVERY;
    private ChangeSubscriber<StructureMatcherPatternArray> structureMatch = null;
    private boolean multiblockMatches = false;

    private ItemStack focusItem = ItemStack.EMPTY;
    private boolean doesSeeSky = false;
    private int starlightStored = 0;

    public TileAltar() {
        super(25);
    }

    public TileAltar(AltarLevel level) {
        super(25, EnumFacing.UP);
        this.level = level;
    }

    @Override
    protected ItemHandlerTile createNewItemHandler() {
        return new ItemHandlerTileFiltered(this) {
            @Override
            public boolean canInsertItem(int slot, ItemStack toAdd, @Nonnull ItemStack existing) {
                if(!super.canInsertItem(slot, toAdd, existing)) {
                    return false;
                }
                AltarLevel al = TileAltar.this.getAltarLevel();
                if(al == null) {
                    al = AltarLevel.DISCOVERY;
                }
                int allowed = al.getAccessibleInventorySize();
                return slot >= 0 && slot < allowed;
            }
        };
    }

    public void receiveStarlight(@Nullable IWeakConstellation type, double amount) {
        if(amount <= 0.001) return;

        starlightStored = Math.min(getMaxStarlightStorage(), (int) (starlightStored + (amount * 200D)));
        markForUpdate();
    }

    @Override
    public void update() {
        super.update();

        if((ticksExisted & 15) == 0) {
            updateSkyState(MiscUtils.canSeeSky(this.getWorld(), this.getPos(), true, this.doesSeeSky));
        }


        if(!world.isRemote) {
            boolean needUpdate = false;

            matchStructure();

            needUpdate = starlightPassive(needUpdate);
            needUpdate = doTryCraft(needUpdate);

            if(needUpdate) {
                markForUpdate();
            }
        } else {
            if(getActiveCraftingTask() != null) {
                doCraftEffects();
                doCraftSound();
            }
            if(getAltarLevel() != null &&
                    getAltarLevel().ordinal() >= AltarLevel.TRAIT_CRAFT.ordinal() &&
                    getMultiblockState()) {
                playAltarEffects();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playAltarEffects() {
        if(Minecraft.isFancyGraphicsEnabled() && rand.nextBoolean()) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    getPos().getX() + 0.5,
                    getPos().getY() + 4.4,
                    getPos().getZ() + 0.5);
            p.motion((rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.15F).setColor(Color.WHITE).setMaxAge(25);
        }
    }

    @SideOnly(Side.CLIENT)
    private void doCraftSound() {
        if(Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) > 0) {
            if(clientCraftSound == null || ((PositionedLoopSound) clientCraftSound).hasStoppedPlaying()) {
                clientCraftSound = SoundHelper.playSoundLoopClient(Sounds.attunement, new Vector3(this), 0.25F, 1F,
                        () -> isInvalid() ||
                                Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) <= 0 ||
                                craftingTask == null);
            }
        } else {
            clientCraftSound = null;
        }
    }

    @Nullable
    public IConstellation getFocusedConstellation() {
        if (!focusItem.isEmpty() && focusItem.getItem() instanceof ItemConstellationFocus) {
            return ((ItemConstellationFocus) focusItem.getItem()).getFocusConstellation(focusItem);
        }
        return null;
    }

    @Nonnull
    public ItemStack getFocusItem() {
        return focusItem;
    }

    public void setFocusStack(@Nonnull ItemStack stack) {
        this.focusItem = stack;
        markForUpdate();
    }

    @Override
    public void onBreak() {
        super.onBreak();

        if (!world.isRemote && !focusItem.isEmpty()) {
            ItemUtils.dropItemNaturally(world,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    focusItem);
            this.focusItem = ItemStack.EMPTY;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB box = super.getRenderBoundingBox().expand(0, 5, 0);
        if(level != null && level.ordinal() >= AltarLevel.TRAIT_CRAFT.ordinal()) {
            box = box.grow(3, 0, 3);
        }
        return box;
    }

    @SideOnly(Side.CLIENT)
    private void doCraftEffects() {
        craftingTask.getRecipeToCraft().onCraftClientTick(this,
                craftingTask.getState(), ClientScheduler.getClientTick(), rand);
    }

    private void matchStructure() {
        PatternBlockArray structure = this.getRequiredStructure();
        if (structure != null) {
            if (this.structureMatch == null) {
                this.structureMatch = PatternMatchHelper.getOrCreateMatcher(getWorld(), getPos(), structure);
            }
        }

        boolean matches = structure == null || this.structureMatch.matches(this.getWorld());
        if (matches != this.multiblockMatches) {
            LogCategory.STRUCTURE_MATCH.info(() ->
                    "Structure match updated: " + this.getClass().getName() + " at " + this.getPos() +
                            " (" + this.multiblockMatches + " -> " + matches + ")");
            this.multiblockMatches = matches;
            this.markForUpdate();
        }
    }


    private boolean doTryCraft(boolean needUpdate) {
        if(craftingTask == null) return needUpdate;
        AbstractAltarRecipe altarRecipe = craftingTask.getRecipeToCraft();
        if(!doesRecipeMatch(altarRecipe, true)) {
            abortCrafting();
            return true;
        }
        if(!altarRecipe.fulfillesStarlightRequirement(this)) {
            if(craftingTask.shouldPersist(this)) {
                craftingTask.setState(ActiveCraftingTask.CraftingState.PAUSED);
                return true;
            }
            abortCrafting();
            return true;
        }
        if((ticksExisted % 5) == 0) {
            if(matchDownMultiblocks(altarRecipe.getNeededLevel()) == null) {
                abortCrafting();
                return true;
            }
        }
        if(craftingTask.isFinished()) {
            finishCrafting();
            return true;
        }
        if(!craftingTask.tick(this)) {
            craftingTask.setState(ActiveCraftingTask.CraftingState.WAITING);
            return true;
        }
        ActiveCraftingTask.CraftingState prev = craftingTask.getState();
        craftingTask.setState(ActiveCraftingTask.CraftingState.ACTIVE);
        craftingTask.getRecipeToCraft().onCraftServerTick(
                this,
                ActiveCraftingTask.CraftingState.ACTIVE,
                craftingTask.getTicksCrafting(),
                craftingTask.getTotalCraftingTime(),
                rand);
        return (prev != craftingTask.getState()) || needUpdate;
    }

    private void finishCrafting() {
        if(craftingTask == null) return; //Wtf

        AbstractAltarRecipe recipe = craftingTask.getRecipeToCraft();
        ShapeMap current = copyGetCurrentCraftingGrid();
        ItemStack out = recipe.getOutput(current, this); //Central item helps defining output - probably, eventually.
        if(!out.isEmpty()) {
            out = ItemUtils.copyStackWithSize(out, out.getCount());
        }

        ForgeHooks.setCraftingPlayer(craftingTask.tryGetCraftingPlayerServer());
        recipe.handleInputConsumption(this, craftingTask, getInventoryHandler());
        ForgeHooks.setCraftingPlayer(null);

        if(!out.isEmpty() && !(out.getItem() instanceof ItemBlockAltar)) {
            if(out.getCount() > 0) {
                ItemUtils.dropItem(world, pos.getX() + 0.5, pos.getY() + 1.3, pos.getZ() + 0.5, out).setNoDespawn();
            }
        }

        starlightStored = Math.max(0, starlightStored - recipe.getPassiveStarlightRequired());

        if (!recipe.allowsForChaining() || !doesRecipeMatch(recipe, false) ||
                matchDownMultiblocks(recipe.getNeededLevel()) == null) {
            if(getAltarLevel().ordinal() >= AltarLevel.CONSTELLATION_CRAFT.ordinal()) {
                Vector3 pos = new Vector3(getPos()).add(0.5, 0, 0.5);
                PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CRAFT_FINISH_BURST, pos.getX(), pos.getY() + 0.05, pos.getZ());
                PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(getWorld(), getPos(), 32));
            }
            craftingTask.getRecipeToCraft().onCraftServerFinish(this, rand);

            if(!recipe.getOutputForMatching().isEmpty()) {
                ItemStack match = recipe.getOutputForMatching();
                if(match.getItem() instanceof ItemBlockAltar) {
                    TileAltar.AltarLevel to = TileAltar.AltarLevel.values()[
                            MathHelper.clamp(match.getItemDamage(), 0, AltarLevel.values().length - 1)];
                    tryForceLevelUp(to, true);
                }
            }
            ResearchManager.informCraftingAltarCompletion(this, craftingTask);
            SoundHelper.playSoundAround(Sounds.craftFinish, world, getPos(), 1F, 1.7F);
            EntityFlare.spawnAmbient(world, new Vector3(this).add(-3 + rand.nextFloat() * 7, 0.6, -3 + rand.nextFloat() * 7));
            craftingTask = null;
        }
        markForUpdate();
    }

    public ShapeMap copyGetCurrentCraftingGrid() {
        ShapeMap current = new ShapeMap();
        for (int i = 0; i < 9; i++) {
            ShapedRecipeSlot slot = ShapedRecipeSlot.values()[i];
            ItemStack stack = getInventoryHandler().getStackInSlot(i);
            if(!stack.isEmpty()) {
                current.put(slot, new ItemHandle(ItemUtils.copyStackWithSize(stack, 1)));
            }
        }
        return current;
    }

    public boolean tryForceLevelUp(AltarLevel to, boolean doLevelUp) {
        int curr = getAltarLevel().ordinal();
        if(curr >= to.ordinal()) return false;
        if(getAltarLevel().next() != to) return false;

        if(!doLevelUp) return true;
        return levelUnsafe(getAltarLevel().next());
    }

    private boolean levelUnsafe(AltarLevel to) {
        this.level = to;
        this.multiblockMatches = false;
        this.structureMatch = null;
        return world.setBlockState(getPos(), BlocksAS.blockAltar.getDefaultState().withProperty(BlockAltar.ALTAR_TYPE, level.getCorrespondingAltarType()));
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    private void abortCrafting() {
        this.craftingTask = null;
        markForUpdate();
    }

    private boolean starlightPassive(boolean needUpdate) {
        if(starlightStored > 0) needUpdate = true;
        starlightStored *= 0.95;

        WorldSkyHandler handle = ConstellationSkyHandler.getInstance().getWorldHandler(getWorld());
        if(doesSeeSky() && handle != null) {
            int yLevel = getPos().getY();
            if(yLevel > 40) {
                float collect = 160;

                float dstr;
                if(yLevel > 120) {
                    dstr = 1F + ((yLevel - 120) / 272F);
                } else {
                    dstr = (yLevel - 20) / 100F;
                }

                if(posDistribution == -1) {
                    posDistribution = SkyCollectionHelper.getSkyNoiseDistribution(world, pos);
                }

                collect *= dstr;
                collect *= (0.6 + (0.4 * posDistribution));
                collect *= 0.2 + (0.8 * ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(getWorld()));

                starlightStored = Math.min(getMaxStarlightStorage(), (int) (starlightStored + collect));
                return true;
            }
        }
        return needUpdate;
    }

    @Nullable
    public ActiveCraftingTask getActiveCraftingTask() {
        return craftingTask;
    }

    public boolean getMultiblockState() {
        return this.multiblockMatches;
    }

    @Override
    @Nullable
    public PatternBlockArray getRequiredStructure() {
        return getAltarLevel().getPattern();
    }

    @Nonnull
    @Override
    public BlockPos getLocationPos() {
        return this.getPos();
    }

    public float getAmbientStarlightPercent() {
        return ((float) starlightStored) / ((float) getMaxStarlightStorage());
    }

    public int getStarlightStored() {
        return starlightStored;
    }

    public int getMaxStarlightStorage() {
        return getAltarLevel().getStarlightMaxStorage();
    }

    public boolean doesRecipeMatch(AbstractAltarRecipe recipe, boolean ignoreStarlightRequirement) {
        if(!recipe.getOutputForMatching().isEmpty()) {
            ItemStack match = recipe.getOutputForMatching();
            if(match.getItem() instanceof ItemBlockAltar) {
                TileAltar.AltarLevel to = TileAltar.AltarLevel.values()[
                        MathHelper.clamp(match.getItemDamage(), 0, AltarLevel.values().length - 1)];
                if(getAltarLevel().ordinal() >= to.ordinal()) {
                    return false;
                }
            }
        }
        return recipe.matches(this, getInventoryHandler(), ignoreStarlightRequirement);
    }

    @Override
    public void onInteract(World world, BlockPos pos, EntityPlayer player, EnumFacing side, boolean sneaking) {
        if(!world.isRemote) {
            if(getActiveCraftingTask() != null) {
                AbstractAltarRecipe altarRecipe = craftingTask.getRecipeToCraft();
                if(matchDownMultiblocks(altarRecipe.getNeededLevel()) == null ||
                        !doesRecipeMatch(altarRecipe, false)) {
                    abortCrafting();
                    return;
                }
            }

            findRecipe(player);
        }
    }

    @Nullable
    public AltarLevel matchDownMultiblocks(AltarLevel levelDownTo) {
        for (int i = getAltarLevel().ordinal(); i >= levelDownTo.ordinal(); i--) {
            AltarLevel al = AltarLevel.values()[i];
            PatternBlockArray pattern = al.getPattern();
            if (pattern == null || pattern.matches(this.getWorld(), this.getPos())) {
                return al;
            }
        }
        return null;
    }

    private void findRecipe(EntityPlayer crafter) {
        if(craftingTask != null) return;

        AbstractAltarRecipe recipe = AltarRecipeRegistry.findMatchingRecipe(this, false);
        if(recipe instanceof IGatedRecipe) {
            if(!((IGatedRecipe) recipe).hasProgressionServer(crafter)) return;
        }
        if(recipe != null) {
            int divisor = Math.max(0, this.getAltarLevel().ordinal() - recipe.getNeededLevel().ordinal());
            divisor = (int) Math.round(Math.pow(2, divisor));
            this.craftingTask = new ActiveCraftingTask(recipe, divisor, crafter.getUniqueID());
            markForUpdate();
        }
    }

    protected void updateSkyState(boolean seesSky) {
        boolean update = doesSeeSky != seesSky;
        this.doesSeeSky = seesSky;
        if(update) {
            markForUpdate();
        }
    }

    public boolean doesSeeSky() {
        return doesSeeSky;
    }

    public AltarLevel getAltarLevel() {
        return level;
    }

    public int getCraftingRecipeWidth() {
        return 3;
    }

    public int getCraftingRecipeHeight() {
        return 3;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.level = AltarLevel.values()[compound.getInteger("level")];
        this.starlightStored = compound.getInteger("starlight");
        this.multiblockMatches = compound.getBoolean("multiblockMatches");

        if(compound.hasKey("craftingTask")) {
            this.craftingTask = ActiveCraftingTask.deserialize(compound.getCompoundTag("craftingTask"), this.craftingTask);
        } else {
            this.craftingTask = null;
        }

        this.focusItem = ItemStack.EMPTY;
        if(compound.hasKey("focusItem")) {
            this.focusItem = new ItemStack(compound.getCompoundTag("focusItem"));
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setInteger("level", level.ordinal());
        compound.setInteger("starlight", starlightStored);
        compound.setBoolean("multiblockMatches", multiblockMatches);

        if (!focusItem.isEmpty()) {
            NBTHelper.setAsSubTag(compound, "focusItem", this.focusItem::writeToNBT);
        }

        if (craftingTask != null) {
            compound.setTag("craftingTask", craftingTask.serialize());
        }
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blockaltar.general.name";
    }

    @Override
    @Nonnull
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TransmissionReceiverAltar(at);
    }

    public void onPlace(AltarLevel level) {
        this.level = level;
        markForUpdate();
    }

    @SideOnly(Side.CLIENT)
    public static void finishBurst(PktParticleEvent event) {
        EffectHandler.getInstance().textureSpritePlane(SpriteLibrary.spriteCraftBurst, Vector3.RotAxis.Y_AXIS.clone()).setPosition(event.getVec()).setScale(5 + rand.nextInt(2)).setNoRotation(rand.nextInt(360));
    }

    public static enum AltarLevel {

        DISCOVERY          (9,  () -> null),
        ATTUNEMENT         (13, () -> MultiBlockArrays.patternAltarAttunement),
        CONSTELLATION_CRAFT(21, () -> MultiBlockArrays.patternAltarConstellation),
        TRAIT_CRAFT        (25, () -> MultiBlockArrays.patternAltarTrait),
        BRILLIANCE         (25, () -> null);

        private final int maxStarlightStorage;
        private final int accessibleInventorySize;
        private final Provider<PatternBlockArray> patternProvider;

        AltarLevel(int invSize, Provider<PatternBlockArray> patternProvider) {
            this.patternProvider = patternProvider;
            this.accessibleInventorySize = invSize;
            this.maxStarlightStorage = (int) (1000 * Math.pow(2, ordinal()));
        }

        public BlockAltar.AltarType getCorrespondingAltarType() {
            return BlockAltar.AltarType.values()[ordinal()];
        }

        @Nullable
        public PatternBlockArray getPattern() {
            return patternProvider.provide();
        }

        public int getStarlightMaxStorage() {
            return maxStarlightStorage;
        }

        public int getAccessibleInventorySize() {
            return accessibleInventorySize;
        }

        public BlockAltar.AltarType getType() {
            return BlockAltar.AltarType.values()[ordinal()];
        }

        public AltarLevel next() {
            if(this == BRILLIANCE) return this;
            return AltarLevel.values()[ordinal() + 1];
        }

    }

    public static class TransmissionReceiverAltar extends SimpleTransmissionReceiver {

        public TransmissionReceiverAltar(BlockPos thisPos) {
            super(thisPos);
        }

        @Override
        public void onStarlightReceive(World world, boolean isChunkLoaded, IWeakConstellation type, double amount) {
            if(isChunkLoaded) {
                TileAltar ta = MiscUtils.getTileAt(world, getLocationPos(), TileAltar.class, false);
                if(ta != null) {
                    ta.receiveStarlight(type, amount);
                }
            }
        }

        @Override
        public TransmissionClassRegistry.TransmissionProvider getProvider() {
            return new AltarReceiverProvider();
        }

    }

    public static class AltarReceiverProvider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public TransmissionReceiverAltar provideEmptyNode() {
            return new TransmissionReceiverAltar(null);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":TransmissionReceiverAltar";
        }

    }

}
