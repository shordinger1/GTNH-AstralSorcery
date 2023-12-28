//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package shordinger.astralsorcery.migration;

import static shordinger.astralsorcery.AstralSorcery.log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

import net.minecraftforge.common.util.EnumHelper;

import org.objectweb.asm.Type;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.discovery.ASMDataTable;

public enum CapabilityManager {

    INSTANCE;

    private final IdentityHashMap<String, Capability<?>> providers = Maps.newIdentityHashMap();
    private final IdentityHashMap<String, List<Function<Capability<?>, Object>>> callbacks = Maps.newIdentityHashMap();

    private CapabilityManager() {
    }

    /**
     * @deprecated
     */
    @Deprecated
    public <T> void register(Class<T> type, Capability.IStorage<T> storage, Class<? extends T> implementation) {
        Preconditions
            .checkArgument(implementation != null, "Attempted to register a capability with no default implementation");
        this.register(type, storage, () -> {
            try {
                return implementation.newInstance();
            } catch (IllegalAccessException | InstantiationException var2) {
                throw new RuntimeException(var2);
            }
        });
    }

    public <T> void register(Class<T> type, Capability.IStorage<T> storage, Callable<? extends T> factory) {
        Preconditions.checkArgument(type != null, "Attempted to register a capability with invalid type");
        Preconditions
            .checkArgument(storage != null, "Attempted to register a capability with no storage implementation");
        Preconditions.checkArgument(
            factory != null,
            "Attempted to register a capability with no default implementation factory");
        String realName = type.getName()
            .intern();
        Preconditions.checkState(
            !this.providers.containsKey(realName),
            "Can not register a capability implementation multiple times: %s",
            realName);
        Capability<T> cap = new Capability(realName, storage, factory);
        this.providers.put(realName, cap);
        List list = this.callbacks.get(realName);
        if (list != null) {

            for (Object o : list) {
                Function func = (Function) o;
                func.apply(cap);
            }
        }

    }

    public void injectCapabilities(ASMDataTable data) {

        for (ASMDataTable.ASMData entry : data.getAll(CapabilityInject.class.getName())) {
            final String targetClass = entry.getClassName();
            final String targetName = entry.getObjectName();
            Type type = (Type) entry.getAnnotationInfo()
                .get("value");
            if (type == null) {
                log.warn("Unable to inject capability at {}.{} (Invalid Annotation)", targetClass, targetName);
            } else {
                final String capabilityName = type.getInternalName()
                    .replace('/', '.')
                    .intern();
                List list = this.callbacks.computeIfAbsent(capabilityName, (k) -> Lists.newArrayList());
                if (entry.getObjectName()
                    .indexOf(40) > 0) {
                    list.add(new Function<Capability<?>, Object>() {

                        public Object apply(Capability<?> input) {
                            try {
                                Method[] var2 = Class.forName(targetClass)
                                    .getDeclaredMethods();

                                for (Method mtd : var2) {
                                    if (targetName.equals(mtd.getName() + Type.getMethodDescriptor(mtd))) {
                                        if ((mtd.getModifiers() & 8) != 8) {
                                            log.warn(
                                                "Unable to inject capability {} at {}.{} (Non-Static)",
                                                capabilityName,
                                                targetClass,
                                                targetName);
                                            return null;
                                        }

                                        mtd.setAccessible(true);
                                        mtd.invoke((Object) null, input);
                                        return null;
                                    }
                                }

                                log.warn(
                                    "Unable to inject capability {} at {}.{} (Method Not Found)",
                                    capabilityName,
                                    targetClass,
                                    targetName);
                            } catch (Exception var6) {
                                log.warn(
                                    "Unable to inject capability {} at {}.{}",
                                    capabilityName,
                                    targetClass,
                                    targetName,
                                    var6);
                            }

                            return null;
                        }
                    });
                } else {
                    list.add(new Function<Capability<?>, Object>() {

                        public Object apply(Capability<?> input) {
                            try {
                                Field field = Class.forName(targetClass)
                                    .getDeclaredField(targetName);
                                if ((field.getModifiers() & 8) != 8) {
                                    log.warn(
                                        "Unable to inject capability {} at {}.{} (Non-Static)",
                                        capabilityName,
                                        targetClass,
                                        targetName);
                                    return null;
                                }

                                EnumHelper.setFailsafeFieldValue(field, (Object) null, input);
                            } catch (Exception var3) {
                                log.warn(
                                    "Unable to inject capability {} at {}.{}",
                                    capabilityName,
                                    targetClass,
                                    targetName,
                                    var3);
                            }

                            return null;
                        }
                    });
                }
            }
        }

    }
}
