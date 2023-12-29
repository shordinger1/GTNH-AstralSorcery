/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024 <p>
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.core.transform;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import org.objectweb.asm.tree.ClassNode;
import shordinger.astralsorcery.core.ASMTransformationException;
import shordinger.astralsorcery.core.AstralCore;
import shordinger.astralsorcery.core.ClassPatch;
import shordinger.astralsorcery.core.SubClassTransformer;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralPatchTransformer
 * Created by HellFirePvP
 * Date: 05.12.2016 / 16:46
 */
public class AstralPatchTransformer implements SubClassTransformer {

    private static final String PATCH_PACKAGE = "shordinger.astralsorcery.core.patch";

    private static ClassPatch currentPatch = null;

    private final Map<String, List<ClassPatch>> availablePatches = new HashMap<>();

    public AstralPatchTransformer() throws IOException {
        AstralCore.log.info("[AstralTransformer] Loading patches...");
        int loaded = loadClassPatches();
        AstralCore.log.info("[AstralTransformer] Initialized! Loaded " + loaded + " class patches!");
    }

    private int loadClassPatches() throws IOException {
        ImmutableSet<ClassPath.ClassInfo> classes = ClassPath.from(
                Thread.currentThread()
                    .getContextClassLoader())
            .getTopLevelClassesRecursive(PATCH_PACKAGE);
        List<Class> patchClasses = new LinkedList<>();
        for (ClassPath.ClassInfo info : classes) {
            if (info.getName()
                .startsWith(PATCH_PACKAGE)) {
                patchClasses.add(info.load());
            }
        }
        int load = 0;
        for (Class patchClass : patchClasses) {
            if (ClassPatch.class.isAssignableFrom(patchClass) && !Modifier.isAbstract(patchClass.getModifiers())) {
                try {
                    ClassPatch patch = (ClassPatch) patchClass.newInstance();
                    if (!availablePatches.containsKey(patch.getClassName())) {
                        availablePatches.put(patch.getClassName(), new LinkedList<>());
                    }
                    availablePatches.get(patch.getClassName())
                        .add(patch);
                    load++;
                } catch (Exception exc) {
                    throw new IllegalStateException("Could not load ClassPatch: " + patchClass.getSimpleName(), exc);
                }
            }
        }
        if (load == 0) {
            AstralCore.log
                .info("[AstralTransformer] Found 0 Transformers! Trying to recover with direct references...");
            String[] references = new String[]{
                "shordinger.astralsorcery.core.patch.helper.PatchApplyPotionEffectEvent",
                "shordinger.astralsorcery.core.patch.helper.PatchUpdateElytra",
                "shordinger.astralsorcery.core.patch.helper.PatchModifyEnchantmentLevels",
                "shordinger.astralsorcery.core.patch.helper.PatchModifyEnchantmentLevelsTooltip",
                "shordinger.astralsorcery.core.patch.helper.PatchModifyEnchantmentLevelsTooltipEvent",
                "shordinger.astralsorcery.core.patch.helper.PatchSunBrightnessWorldClient",
                "shordinger.astralsorcery.core.patch.helper.PatchSunBrightnessWorldCommon",
                "shordinger.astralsorcery.core.patch.helper.PatchEntityRendererExtendedEntityReach",
                "shordinger.astralsorcery.core.patch.helper.PatchServerExtendEntityInteractReach",
                "shordinger.astralsorcery.core.patch.helper.PatchEntityLivingBaseWaterSlowDown",
                "shordinger.astralsorcery.core.patch.helper.PatchPostProcessAttributes",
                "shordinger.astralsorcery.core.patch.helper.PatchAddPlayerAttribute",
                "shordinger.astralsorcery.core.patch.helper.PatchSetPlayerAttribute",

                "shordinger.astralsorcery.core.patch.hook.PatchRunicShieldingHook"};
            for (String str : references) {
                try {
                    ClassPatch c = (ClassPatch) Class.forName(str)
                        .newInstance();
                    if (!availablePatches.containsKey(c.getClassName())) {
                        availablePatches.put(c.getClassName(), new LinkedList<>());
                    }
                    availablePatches.get(c.getClassName())
                        .add(c);
                    load++;
                } catch (Exception exc) {
                    AstralCore.log.warn("Could not load ClassPatch: " + str);
                    exc.printStackTrace();
                }
            }
        }

        return load;
    }

    @Override
    public void transformClassNode(ClassNode cn, String transformedClassName, String obfName) {
        if (!availablePatches.isEmpty()) {
            List<ClassPatch> patches = availablePatches.get(transformedClassName);
            if (patches != null && !patches.isEmpty()) {
                AstralCore.log.info(
                    "[AstralTransformer] Transforming " + obfName
                        + " : "
                        + transformedClassName
                        + " with "
                        + patches.size()
                        + " patches!");
                try {
                    for (ClassPatch patch : patches) {
                        if (!patch.canExecuteForSide(AstralCore.side)) {
                            AstralCore.log.info(
                                "[AstralTransformer] Skipping " + patch.getClass()
                                    .getSimpleName()
                                    .toUpperCase() + " as it can't be applied for side " + AstralCore.side);
                            continue;
                        }
                        currentPatch = patch;
                        patch.transform(cn);
                        AstralCore.log.info(
                            "[AstralTransformer] Applied patch " + patch.getClass()
                                .getSimpleName()
                                .toUpperCase());
                        currentPatch = null;
                    }
                } catch (Exception exc) {
                    throw new ASMTransformationException(
                        "Applying ClassPatches failed (ClassName: " + obfName
                            + " - "
                            + transformedClassName
                            + ") - Rethrowing exception!",
                        exc);
                }
            }
        }
    }

    @Override
    public String getIdentifier() {
        return "Patch based transformer";
    }

    @Override
    public void addErrorInformation() {
        if (currentPatch != null) {
            AstralCore.log.warn(
                "Patcher was in active patch: " + currentPatch.getClass()
                    .getSimpleName());
        }
    }

    @Override
    public boolean isTransformRequired(String transformedClassName) {
        return availablePatches.containsKey(transformedClassName);
    }

}
