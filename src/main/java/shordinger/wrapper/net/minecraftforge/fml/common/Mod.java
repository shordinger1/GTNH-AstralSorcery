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

package shordinger.wrapper.net.minecraftforge.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cpw.mods.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.client.IModGuiFactory;
import shordinger.wrapper.net.minecraftforge.fml.common.event.FMLEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.event.FMLInitializationEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.event.FMLInterModComms;
import shordinger.wrapper.net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.network.NetworkCheckHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.registry.GameRegistry;
import shordinger.wrapper.net.minecraftforge.fml.common.versioning.VersionRange;

/**
 * This defines a Mod to FML.
 * Any class found with this annotation applied will be loaded as a Mod. The instance that is loaded will
 * represent the mod to other Mods in the system. It will be sent various subclasses of {@link FMLEvent}
 * at pre-defined times during the loading of the game, based on where you have applied the {@link EventHandler}
 * annotation.
 *
 * <p>
 * This is a simple example of a Mod. It has the modId of "mymodid", the name of "My example mod", it is
 * version 1.0, and depends on FML being loaded.
 *
 * <pre>
 * {@code
 * package mymod;
 * // Declare that this is a mod with modId "mymodid", name "My example mod", version "1.0" and dependency on FML.
 * {@literal @}Mod(modId="mymodid",name="My example mod",version="1.0",dependencies="required-after:FML")
 * public class MyMod {
 *      // Populate this field with the instance of the mod created by FML
 *      {@literal @}Instance("mymodid")
 *      public MyMod instance;
 *
 *      // Mark this method for receiving an {@link FMLEvent} (in this case, it's the {@link FMLPreInitializationEvent})
 *      {@literal @}EventHandler public void preInit(FMLPreInitializationEvent event)
 *      {
 *          // Do stuff in pre-init phase (read config, create blocks and items, register them)
 *      }
 * }
 * }
 * </pre>
 *
 * @author cpw
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mod {

    /**
     * The unique mod identifier for this mod.
     * <b>Required to be lowercased in the english locale for compatibility. Will be truncated to 64 characters
     * long.</b>
     * <p>
     * This will be used to identify your mod for third parties (other mods), it will be used to identify your mod for
     * registries such as block and item registries.
     * By default, you will have a resource domain that matches the modid. All these uses require that constraints are
     * imposed on the format of the modid.
     */
    String modid();

    /**
     * A user friendly name for the mod
     */
    String name() default "";

    /**
     * A version string for this mod.
     * <p>
     * The version string here should be just numbers separated by dots,
     * to make specifying {@link #dependencies()} simple for other mods.
     * <p>
     * See also: <a href="https://cwiki.apache.org/confluence/display/MAVENOLD/Versioning">"Versioning" on Maven
     * Wiki</a>
     */
    String version() default "";

    /**
     * A dependency string for this mod, which specifies which mod(s) it depends on in order to run.
     * <p>
     * A dependency string must start with a combination of these prefixes, separated by "-":
     * [before, after], [required], [client, server]
     * At least one "before", "after", or "required" must be specified.
     * Then ":" and the mod id.
     * Then a version range should be specified for the mod by adding "@" and the version range.
     * The version range format is described in the javadoc here:
     * {@link VersionRange#createFromVersionSpec(java.lang.String)}
     * Then a ";".
     * <p>
     * If a "required" mod is missing, or a mod exists with a version outside the specified range,
     * the game will not start and an error screen will tell the player which versions are required.
     * <p>
     * Example:
     * Our example mod:
     * * depends on Forge and uses new features that were introduced in Forge version 14.21.1.2395
     * "required:forge@[14.21.1.2395,);"
     * <p>
     * 1.12.2 Note: for compatibility with Forge older than 14.23.0.2501 the syntax must follow this older format:
     * "required-after:forge@[14.21.1.2395,);"
     * For more explanation see https://github.com/MinecraftForge/MinecraftForge/issues/4918
     * <p>
     * * is a dedicated addon to mod1 and has to have its event handlers run after mod1's are run,
     * "required-after:mod1;"
     * * has optional integration with mod2 which depends on features introduced in mod2 version 4.7.0,
     * "after:mod2@[4.7.0,);"
     * * depends on a client-side-only rendering library called rendermod
     * "required-client:rendermod;"
     * <p>
     * The full dependencies string is all of those combined:
     * "required:forge@[14.21.1.2395,);required-after:mod1;after:mod2@[4.7.0,);required-client:rendermod;"
     * <p>
     * This will stop the game and display an error message if any of these is true:
     * The installed forge is too old,
     * mod1 is missing,
     * an old version of mod2 is present,
     * rendermod is missing on the client.
     */
    String dependencies() default "";

    /**
     * Whether to use the mcmod.info metadata by default for this mod.
     * If true, settings in the mcmod.info file will override settings in these annotations.
     */
    boolean useMetadata() default false;

    /**
     * If true, this mod will not be loaded on the Dedicated Server environment.
     * Will crash if both serverSideOnly and clientSideOnly are set to true.
     */
    boolean clientSideOnly() default false;

    /**
     * If true, this mod will not be loaded on the Client environment.
     * Will crash if both serverSideOnly and clientSideOnly are set to true.
     */
    boolean serverSideOnly() default false;

    /**
     * The acceptable range of minecraft versions that this mod will load and run in
     * The default ("empty string") indicates that the currently RUNNING minecraft version is acceptable.
     * This means ANY version that the end user adds the mod to. Modders PLEASE set this.
     * FML will refuse to run with an error if the minecraft version is not in this range across all mods.
     *
     * @return A version range as specified by the maven version range specification or the empty string
     */
    String acceptedMinecraftVersions() default "";

    /**
     * A replacement for the no-longer-existing "versionRange" of NetworkMod. Specify a remote version range
     * that this mod will accept as valid. Defaults to nothing, which is interpreted as "only this version".
     * Another special value is '*' which means accept all versions.
     * <p>
     * This is ignored if there is a {@link NetworkCheckHandler} annotation on a method in this class.
     *
     * @return A version range as specified by the maven version range specification or the empty string
     */
    String acceptableRemoteVersions() default "";

    /**
     * A version range specifying compatible save version information. If your mod follows good version numbering
     * practice <a href="http://semver.org/">Like this (http://semver.org/)</a> then this should be sufficient.
     * <p>
     * Advanced users can specify a {@link SaveInspectionHandler} instead.
     *
     * @return A version range as specified by the maven version range specification or the empty string
     */
    String acceptableSaveVersions() default "";

    /**
     * Specifying this field allows for a mod to expect a signed jar with a fingerprint matching this value.
     * The fingerprint should be SHA-1 encoded, lowercase with ':' removed. An empty value indicates that
     * the mod is not expecting to be signed.
     * <p>
     * Any incorrectness of the fingerprint, be it missing or wrong, will result in the
     * {@link FMLFingerprintViolationEvent}
     * event firing <i>prior to any other event on the mod</i>.
     *
     * @return A certificate fingerprint that is expected for this mod.
     */
    String certificateFingerprint() default "";

    /**
     * The language the mod is authored in. This will be used to control certain compatibility behaviours for this mod.
     * Valid values are currently "java", "scala"
     *
     * @return The language the mod is authored in
     */
    String modLanguage() default "java";

    /**
     * The language adapter to be used to load this mod. This overrides the value of modLanguage. The class must have a
     * public zero variable constructor and implement {@link ILanguageAdapter} just like the Java and Scala adapters.
     * <p>
     * A class with an invalid constructor or that doesn't implement {@link ILanguageAdapter} will throw an exception
     * and
     * halt loading.
     *
     * @return The full class name of the language adapter
     */
    String modLanguageAdapter() default "";

    /**
     * If your mod doesn't have a runtime persistent effect on the state of the game, and can be disabled without side
     * effects
     * (minimap mods, graphical tweak mods) then you can set true here and receive the FMLDeactivationEvent to perform
     * deactivation
     * tasks.
     * This does not affect administrative disabling through the system property fml.modStates or the config file
     * fmlModState.properties.
     * The mod will only be deactivated outside of a running game world - FML will never allow mod deactivation whilst a
     * game server
     * is running.
     *
     * @return if this mod can be deactivated whilst the game is open.
     */
    boolean canBeDeactivated() default false;

    /**
     * An optional GUI factory for this mod. This is the name of a class implementing {@link IModGuiFactory} that will
     * be instantiated
     * on the client side, and will have certain configuration/options guis requested from it.
     *
     * @return The name of a class implementing {@link IModGuiFactory}
     */
    String guiFactory() default "";

    /**
     * An optional URL to a JSON file that will be checked once per launch to determine if there is an updated
     * version of this mod and notify the end user. For more information see ForgeVersion.
     * Format is defined here: https://gist.github.com/LexManos/7aacb9aa991330523884
     *
     * @return URL to update metadata json
     */
    String updateJSON() default "";

    /**
     * A list of custom properties for this mod. Completely up to the mod author if/when they
     * want to put anything in here.
     *
     * @return an optional list of custom properties
     */
    CustomProperty[] customProperties() default {};

    /**
     * A custom key => value property pair for use with {@link Mod#customProperties()}
     *
     * @author cpw
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface CustomProperty {

        /**
         * A key. Should be unique.
         *
         * @return A key
         */
        String k();

        /**
         * A value. Can be anything.
         *
         * @return A value
         */
        String v();
    }

    /**
     * Marks the associated method as handling an FML lifecycle event.
     * The method must have a single parameter, one of the following types. This annotation
     * replaces the multiple different annotations that previously were used.
     * <p>
     * Current event classes. This first section is standard lifecycle events. They are dispatched
     * at various phases as the game starts. Each event should have information useful to that
     * phase of the lifecycle. They are fired in this order.
     * <p>
     * These suggestions are mostly just suggestions on what to do in each event.
     * <ul>
     * <li>{@link FMLPreInitializationEvent} : Run before anything else. Read your config, create blocks,
     * items, etc, and register them with the {@link GameRegistry}.</li>
     * <li>{@link FMLInitializationEvent} : Do your mod setup. Build whatever data structures you care about. Register
     * recipes,
     * send {@link FMLInterModComms} messages to other mods.</li>
     * <li>{@link FMLPostInitializationEvent} : Handle interaction with other mods, complete your setup based on
     * this.</li>
     * </ul>
     * <p>
     * These are the server lifecycle events. They are fired whenever a server is running, or about to run. Each time a
     * server
     * starts they will be fired in this sequence.
     * <ul>
     * <li>{@link FMLServerAboutToStartEvent} : Use if you need to handle something before the server has even been
     * created.</li>
     * <li>{@link FMLServerStartingEvent} : Do stuff you need to do to set up the server. register commands, tweak the
     * server.</li>
     * <li>{@link FMLServerStartedEvent} : Do what you need to with the running server.</li>
     * <li>{@link FMLServerStoppingEvent} : Do what you need to before the server has started it's shutdown
     * sequence.</li>
     * <li>{@link FMLServerStoppedEvent} : Do whatever cleanup you need once the server has shutdown. Generally only
     * useful
     * on the integrated server.</li>
     * </ul>
     * The second set of events are more specialized, for receiving notification of specific
     * information.
     * <ul>
     * <li>{@link FMLFingerprintViolationEvent} : Sent just before {@link FMLPreInitializationEvent}
     * if something is wrong with your mod signature</li>
     * <li>{@link IMCEvent} : Sent just after {@link FMLInitializationEvent} if you have IMC messages waiting
     * from other mods</li>
     * </ul>
     *
     * @author cpw
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface EventHandler {
    }

    /**
     * Populate the annotated field with the mod instance based on the specified ModId. This can be used
     * to retrieve instances of other mods.
     *
     * @author cpw
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Instance {

        /**
         * The mod object to inject into this field
         */
        String value() default "";

        /**
         * Optional owner modid, required if this annotation is on something that is not inside the main class of a mod
         * container.
         * This is required to prevent mods from classloading other, potentially disabled mods.
         */
        String owner() default "";
    }

    /**
     * Populate the annotated field with the mod's metadata.
     *
     * @author cpw
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Metadata {

        /**
         * The mod id specifying the metadata to load here
         */
        String value() default "";

        /**
         * Optional owner modid, required if this annotation is on something that is not inside the main class of a mod
         * container.
         * This is required to prevent mods from classloading other, potentially disabled mods.
         */
        String owner() default "";
    }

    /**
     * Mod instance factory method. Should return an instance of the mod. Applies only to static methods on the same
     * class as {@link Mod}.
     *
     * @author cpw
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface InstanceFactory {
    }

    /**
     * A class which will be subscribed to {@link net.minecraftforge.common.MinecraftForge.EVENT_BUS} at mod
     * construction time.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface EventBusSubscriber {

        Side[] value() default {Side.CLIENT, Side.SERVER};

        /**
         * Optional value, only nessasary if tis annotation is not on the same class that has a @Mod annotation.
         * Needed to prevent early classloading of classes not owned by your mod.
         *
         * @return
         */
        String modid() default "";
    }
}
