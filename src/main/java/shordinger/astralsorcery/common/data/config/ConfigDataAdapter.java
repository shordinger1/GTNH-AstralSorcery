/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.data.config;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConfigDataAdapter
 * Created by HellFirePvP
 * Date: 05.11.2017 / 09:44
 */
public interface ConfigDataAdapter<T extends ConfigDataAdapter.DataSet> {

    public Iterable<T> getDefaultDataSets();

    public String getDataFileName();

    public String getDescription();

    /**
     * Try add a entry to the data-set. The return value defines what happened:
     * - null: Adding the element failed due to an error in the format
     * - Empty optional: Adding the element failed, however reason being contextual information or pack-configurations.
     * Does not omit the generic format-error message, suggesting the method already gave appropiate information
     * - Optional with value: Everything went fine.
     */
    @Nullable
    public Optional<T> appendDataSet(String str);

    public void resetRegistry();

    default public LoadPhase getLoadPhase() {
        return LoadPhase.PRE_INIT;
    }

    @Nonnull
    default public String[] serializeDataSet() {
        List<String> defaultValueStrings = new LinkedList<>();
        for (T data : getDefaultDataSets()) {
            defaultValueStrings.add(data.serialize());
        }
        String[] out = new String[defaultValueStrings.size()];
        return defaultValueStrings.toArray(out);
    }

    public static enum LoadPhase {

        PRE_INIT,
        INIT,
        POST_INIT

    }

    public static interface DataSet {

        @Nonnull
        public String serialize();

        public static class StringElement implements DataSet {

            private final String str;

            public StringElement(@Nonnull String str) {
                this.str = str;
            }

            @Nonnull
            @Override
            public String serialize() {
                return str;
            }
        }
    }

}
