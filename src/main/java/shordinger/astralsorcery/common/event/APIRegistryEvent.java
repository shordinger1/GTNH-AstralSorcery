/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.event;

import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.ConstellationRegistry;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.IMajorConstellation;
import shordinger.astralsorcery.common.constellation.effect.ConstellationEffect;
import shordinger.astralsorcery.common.constellation.effect.ConstellationEffectRegistry;
import shordinger.astralsorcery.common.constellation.perk.AbstractPerk;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import shordinger.astralsorcery.common.constellation.perk.reader.AttributeReader;
import shordinger.astralsorcery.common.constellation.perk.reader.AttributeReaderRegistry;
import shordinger.astralsorcery.common.constellation.perk.tree.PerkTree;
import shordinger.astralsorcery.common.util.ILocatable;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Event;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: APIRegistryEvent
 * Created by HellFirePvP
 * Date: 13.11.2016 / 15:09
 */
// Generally all misc. api hooks should use this to register their stuff.
public class APIRegistryEvent {

    public static class ConstellationEffectRegister extends Event {

        /**
         * This does NOT include config generation.
         */
        public void registerEffect(IMajorConstellation c,
                                   Function<ILocatable, ConstellationEffect> effectInstanceProvider) {
            ConstellationEffectRegistry.registerFromAPI(c, effectInstanceProvider);
        }

    }

    public static class ConstellationRegister extends Event {

        /**
         * Registers and adds the given constellation.
         */
        public void registerConstellation(IConstellation c) {
            ConstellationRegistry.registerConstellation(c);
        }

    }

    /**
     * Register perk-attribute types during this event.
     * AttributeTypeRegistry.registerPerkType
     * Called AFTER perks have been registered (or at least after the PerkRegister event has been fired)
     */
    public static class PerkAttributeTypeRegister extends Event {

        public void registerAttribute(PerkAttributeType type) {
            AttributeTypeRegistry.registerPerkType(type);
        }

        // To be called AFTER registerAttribute
        public void registerAttributeReader(PerkAttributeType type, AttributeReader reader) {
            AttributeReaderRegistry.registerTypeReader(type.getTypeString(), reader);
        }

        public void setAttributeLimit(PerkAttributeType type, float lowerBound, float upperBound) {
            AttributeTypeRegistry.limitPerkType(type, lowerBound, upperBound);
        }

    }

    /**
     * Register your perks during this event.
     * PerkTree.PERK_TREE.register
     */
    public static class PerkRegister extends Event {

        public PerkTree.PointConnector registerPerk(AbstractPerk perk) {
            return PerkTree.PERK_TREE.registerPerk(perk);
        }

    }

    /**
     * Use this event to disable certain perks
     * A note will be displayed on the perk's tooltip in case it's disabled by the pack.
     */
    public static class PerkDisable extends Event {

        private boolean perkDisabled;
        private final AbstractPerk perk;
        private final EntityPlayer player;
        private final Side side;

        public PerkDisable(AbstractPerk perk, EntityPlayer player, Side side) {
            this.perk = perk;
            this.player = player;
            this.side = side;
        }

        public AbstractPerk getPerk() {
            return perk;
        }

        public EntityPlayer getPlayer() {
            return player;
        }

        public Side getSide() {
            return side;
        }

        public boolean isPerkDisabled() {
            return perkDisabled;
        }

        public void setPerkDisabled(boolean perkDisabled) {
            this.perkDisabled = perkDisabled;
        }
    }

    /**
     * Use this event to remove perks at AS' post-init
     * Removed associated connections aswell
     */
    public static class PerkPostRemove extends Event {

        private boolean removed;
        private final AbstractPerk perk;

        public PerkPostRemove(AbstractPerk perk) {
            this.perk = perk;
        }

        public AbstractPerk getPerk() {
            return perk;
        }

        public boolean isRemoved() {
            return removed;
        }

        public void setRemoved(boolean removed) {
            this.removed = removed;
        }
    }

}
