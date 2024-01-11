package shordinger.wrapper.net.minecraft.network.play;

import shordinger.wrapper.net.minecraft.network.INetHandler;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketAnimation;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketChatMessage;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketClickWindow;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketClientSettings;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketClientStatus;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketCloseWindow;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketConfirmTeleport;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketConfirmTransaction;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketCustomPayload;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketEnchantItem;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketEntityAction;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketHeldItemChange;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketInput;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketKeepAlive;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketPlaceRecipe;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketPlayer;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketPlayerAbilities;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketPlayerDigging;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketRecipeInfo;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketResourcePackStatus;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketSeenAdvancements;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketSpectate;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketSteerBoat;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketTabComplete;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketUpdateSign;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketUseEntity;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketVehicleMove;

public interface INetHandlerPlayServer extends INetHandler {

    void handleAnimation(CPacketAnimation packetIn);

    /**
     * Process chat messages (broadcast back to clients) and commands (executes)
     */
    void processChatMessage(CPacketChatMessage packetIn);

    /**
     * Retrieves possible tab completions for the requested command string and sends them to the client
     */
    void processTabComplete(CPacketTabComplete packetIn);

    /**
     * Processes the client status updates: respawn attempt from player, opening statistics or achievements, or
     * acquiring 'open inventory' achievement
     */
    void processClientStatus(CPacketClientStatus packetIn);

    /**
     * Updates serverside copy of client settings: language, render distance, chat visibility, chat colours, difficulty,
     * and whether to show the cape
     */
    void processClientSettings(CPacketClientSettings packetIn);

    /**
     * Received in response to the server requesting to confirm that the client-side open container matches the servers'
     * after a mismatched container-slot manipulation. It will unlock the player's ability to manipulate the container
     * contents
     */
    void processConfirmTransaction(CPacketConfirmTransaction packetIn);

    /**
     * Enchants the item identified by the packet given some convoluted conditions (matching window, which
     * should/shouldn't be in use?)
     */
    void processEnchantItem(CPacketEnchantItem packetIn);

    /**
     * Executes a container/inventory slot manipulation as indicated by the packet. Sends the serverside result if they
     * didn't match the indicated result and prevents further manipulation by the player until he confirms that it has
     * the same open container/inventory
     */
    void processClickWindow(CPacketClickWindow packetIn);

    void func_194308_a(CPacketPlaceRecipe p_194308_1_);

    /**
     * Processes the client closing windows (container)
     */
    void processCloseWindow(CPacketCloseWindow packetIn);

    /**
     * Synchronizes serverside and clientside book contents and signing
     */
    void processCustomPayload(CPacketCustomPayload packetIn);

    /**
     * Processes left and right clicks on entities
     */
    void processUseEntity(CPacketUseEntity packetIn);

    /**
     * Updates a players' ping statistics
     */
    void processKeepAlive(CPacketKeepAlive packetIn);

    /**
     * Processes clients perspective on player positioning and/or orientation
     */
    void processPlayer(CPacketPlayer packetIn);

    /**
     * Processes a player starting/stopping flying
     */
    void processPlayerAbilities(CPacketPlayerAbilities packetIn);

    /**
     * Processes the player initiating/stopping digging on a particular spot, as well as a player dropping items
     */
    void processPlayerDigging(CPacketPlayerDigging packetIn);

    /**
     * Processes a range of action-types: sneaking, sprinting, waking from sleep, opening the inventory or setting jump
     * height of the horse the player is riding
     */
    void processEntityAction(CPacketEntityAction packetIn);

    /**
     * Processes player movement input. Includes walking, strafing, jumping, sneaking; excludes riding and toggling
     * flying/sprinting
     */
    void processInput(CPacketInput packetIn);

    /**
     * Updates which quickbar slot is selected
     */
    void processHeldItemChange(CPacketHeldItemChange packetIn);

    /**
     * Update the server with an ItemStack in a slot.
     */
    void processCreativeInventoryAction(CPacketCreativeInventoryAction packetIn);

    void processUpdateSign(CPacketUpdateSign packetIn);

    void processTryUseItemOnBlock(CPacketPlayerTryUseItemOnBlock packetIn);

    /**
     * Called when a client is using an item while not pointing at a block, but simply using an item
     */
    void processTryUseItem(CPacketPlayerTryUseItem packetIn);

    void handleSpectate(CPacketSpectate packetIn);

    void handleResourcePackStatus(CPacketResourcePackStatus packetIn);

    void processSteerBoat(CPacketSteerBoat packetIn);

    void processVehicleMove(CPacketVehicleMove packetIn);

    void processConfirmTeleport(CPacketConfirmTeleport packetIn);

    void handleRecipeBookUpdate(CPacketRecipeInfo p_191984_1_);

    void handleSeenAdvancements(CPacketSeenAdvancements p_194027_1_);
}
