/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.nodes.key;

import shordinger.astralsorcery.common.base.OreTypes;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import shordinger.astralsorcery.common.constellation.perk.types.IPlayerTickPerk;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.data.config.entry.ConfigEntry;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.util.BlockStateCheck;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.wrapper.net.minecraft.block.BlockStone;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.common.config.Configuration;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyStoneEnrichment
 * Created by HellFirePvP
 * Date: 17.07.2018 / 20:10
 */
public class KeyStoneEnrichment extends KeyPerk implements IPlayerTickPerk {

    private static final BlockStateCheck stoneCheck = new CleanStoneCheck();

    private int enrichmentRadius = 3;
    private int chanceToEnrich = 70;

    public KeyStoneEnrichment(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {
            @Override
            public void loadFromConfig(Configuration cfg) {
                enrichmentRadius = cfg.getInt("Effect_Radius", getConfigurationSection(), enrichmentRadius, 1, 35,
                        "Defines the radius where a random position to generate a ore at is searched");
                chanceToEnrich = cfg.getInt("Chance_To_CreateOre", getConfigurationSection(), chanceToEnrich, 2, 4_000_000,
                        "Sets the chance (Random.nextInt(chance) == 0) to try to see if a random stone next to the player should get turned into an ore; the lower the more likely");
            }
        });
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        this.enrichmentRadius = MathHelper.ceil(this.enrichmentRadius * multiplier);
        this.chanceToEnrich = MathHelper.ceil(this.chanceToEnrich * multiplier);
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if (side == Side.SERVER) {
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            float modChance = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, chanceToEnrich);
            if(rand.nextInt(Math.round(Math.max(modChance, 1))) == 0) {
                float enrRad = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, enrichmentRadius);
                Vector3 vec = Vector3.atEntityCenter(player).add(
                        (rand.nextFloat() * enrRad * 2) - enrRad,
                        (rand.nextFloat() * enrRad * 2) - enrRad,
                        (rand.nextFloat() * enrRad * 2) - enrRad);
                BlockPos pos = vec.toBlockPos();
                if(stoneCheck.isStateValid(player.getEntityWorld().getBlockState(pos))) {
                    ItemStack blockStack = OreTypes.AEVITAS_ORE_PERK.getRandomOre(rand);
                    if(!blockStack.isEmpty()) {
                        IBlockState state = ItemUtils.createBlockState(blockStack);
                        if(state != null) {
                            player.getEntityWorld().setBlockState(pos, state);
                        }
                    }
                }
            }
        }
    }

    private static class CleanStoneCheck implements BlockStateCheck {

        @Override
        public boolean isStateValid(IBlockState state) {
            return state.getBlock() == Blocks.STONE && state.getValue(BlockStone.VARIANT).equals(BlockStone.EnumType.STONE);
        }

    }

}
