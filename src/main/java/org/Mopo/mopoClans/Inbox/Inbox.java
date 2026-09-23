package org.Mopo.mopoClans.Inbox;

import mopo.mopoLib.MopoLib;
import mopo.mopoLib.inventories.BufferManager;
import mopo.mopoLib.inventories.InventoryBuffer;
import org.Mopo.mopoClans.MopoClans;
import org.Mopo.mopoClans.PlayerData.PlayerData;
import org.Mopo.mopoClans.PlayerData.PlayerDataManager;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class Inbox {

    private InventoryBuffer buffer;
    private UUID ownerUUID;
    private UUID inboxUUID;
    private BufferManager bufferManager = MopoLib.bufferManager;
    private List<ItemStack> contents;
    private File playerFile;

    public Inbox(UUID playerUUID) {
        this.ownerUUID = playerUUID;
        this.inboxUUID = UUID.randomUUID();
        this.playerFile = new File(MopoClans.getInboxFolder(), playerUUID.toString() + ".yml");
        this.buffer = MopoClans.getBUFFERMANAGER().createOrGetBuffer(playerUUID.toString() + "InboxBuffer");
    }




    public InventoryBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(InventoryBuffer buffer) {
        this.buffer = buffer;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public UUID getInboxUUID() {
        return inboxUUID;
    }

    public void setInboxUUID(UUID inboxUUID) {
        this.inboxUUID = inboxUUID;
    }

    public void setContents(List<ItemStack> contents) {
        this.contents = contents;
    }

    public File getPlayerFile() {
        return playerFile;
    }

    public void setPlayerFile(File playerFile) {
        this.playerFile = playerFile;
    }

    public List<ItemStack> getContents() {
        return contents;
    }
}
