package shordinger.astralsorcery;

import static shordinger.astralsorcery.Tags.MODID;

import java.util.Map;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.CommonProxy;
import shordinger.astralsorcery.common.auxiliary.CelestialGatewaySystem;
import shordinger.astralsorcery.common.base.Mods;
import shordinger.astralsorcery.common.base.ShootingStarHandler;
import shordinger.astralsorcery.common.cmd.CommandAstralSorcery;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.PerkEffectHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.tree.PerkTree;
import shordinger.astralsorcery.common.data.DataPatreonFlares;
import shordinger.astralsorcery.common.data.SyncDataHolder;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.data.config.ConfigDataAdapter;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.data.world.WorldCacheManager;
import shordinger.astralsorcery.common.event.ClientInitializedEvent;
import shordinger.astralsorcery.common.event.listener.EventHandlerEntity;
import shordinger.astralsorcery.common.integrations.mods.jei.util.JEISessionHandler;
import shordinger.astralsorcery.common.starlight.network.StarlightTransmissionHandler;
import shordinger.astralsorcery.common.util.PlayerActivityManager;

@Mod(modid = MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = "[1.7.10]")
public class AstralSorcery {

    public static String MODID = Tags.MODID;
    public static final Logger log = LogManager.getLogger(MODID);

    @SidedProxy(
        clientSide = "com.astralsorcery.gtnh_astralsorcery.ClientProxy",
        serverSide = "com.astralsorcery.gtnh_astralsorcery.CommonProxy")
    public static CommonProxy proxy;
    @Mod.Instance(Tags.MODID)
    public static AstralSorcery instance;

    private static boolean devEnvChache = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        event.getModMetadata().version = Tags.VERSION;
        devEnvChache = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

        proxy.setupConfiguration();

        Config.loadAndSetup(event.getSuggestedConfigurationFile());

        proxy.registerConfigDataRegistries();
        Config.loadDataRegistries(event.getModConfigurationDirectory());
        Config.loadConfigRegistries(ConfigDataAdapter.LoadPhase.PRE_INIT);

        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Config.loadConfigRegistries(ConfigDataAdapter.LoadPhase.INIT);
        MinecraftForge.EVENT_BUS.register(this);
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Config.loadConfigRegistries(ConfigDataAdapter.LoadPhase.POST_INIT);
        proxy.postInit();
    }

    @NetworkCheckHandler
    public boolean checkModLists(Map<String, String> modList, Side side) {
        if (side == Side.SERVER) {
            boolean jeiFound = modList.containsKey(Mods.JEI.modid);
            if (Mods.JEI.isPresent()) {
                notifyServerConnection(jeiFound);
            }
        }
        return true;
    }

    @Optional.Method(modid = "jei")
    private void notifyServerConnection(boolean jeiFound) {
        JEISessionHandler.getInstance()
            .setJeiOnServer(jeiFound);
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandAstralSorcery());
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        CelestialGatewaySystem.instance.onServerStart();
    }

    @SubscribeEvent
    public void onClientFinish(ClientInitializedEvent event) {
        proxy.clientFinishedLoading();
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        ResearchManager.saveAndClearServerCache();
        StarlightTransmissionHandler.getInstance()
            .serverCleanHandlers();
        PerkEffectHelper.perkCooldowns.clear();
        EventHandlerEntity.invulnerabilityCooldown.clear();
        EventHandlerEntity.ritualFlight.clear();
        EventHandlerEntity.attackStack.clear();
        EventHandlerEntity.spawnDenyRegions.clear();
        ((DataPatreonFlares) SyncDataHolder.getDataServer(SyncDataHolder.DATA_PATREON_FLARES)).cleanUp(Side.SERVER);
        PerkAttributeHelper.clearServer();
        ShootingStarHandler.getInstance()
            .clearServerCache();
        PlayerActivityManager.INSTANCE.clearCache(Side.SERVER);
    }

    @Mod.EventHandler
    public void onServerStop(FMLServerStoppedEvent event) {
        WorldCacheManager.wipeCache();
        AttributeTypeRegistry.getTypes()
            .forEach(t -> t.clear(Side.SERVER));
        PerkTree.PERK_TREE.clearCache(Side.SERVER);
    }

    public static boolean isRunningInDevEnvironment() {
        return devEnvChache;
    }

    // static {
    // FluidRegistry.enableUniversalBucket();
    // }
}
