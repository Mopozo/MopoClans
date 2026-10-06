package org.Mopo.mopoClans.inbox;

import mopo.mopoLib.MopoLib;
import mopo.mopoLib.inventories.BufferManager;
import mopo.mopoLib.inventories.InventoryBuffer;
import mopo.mopoLib.nbt.NBT;
import mopo.mopoLib.yml.MopoYaml;
import org.Mopo.mopoClans.MopoClans;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Inbox {

    private InventoryBuffer buffer;
    private UUID ownerUUID;
    private UUID inboxUUID;
    private BufferManager bufferManager = MopoLib.bufferManager;
    private List<ItemStack> contents = new ArrayList<>();
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

    public void load() {
        MopoYaml inboxYaml = MopoYaml.loadFromFile(playerFile, true);
        String savedOwnerUUID = inboxYaml.getString("owner_uuid");
        String savedInboxUUID = inboxYaml.getString("inbox_uuid");
        if (savedOwnerUUID != null) {
            ownerUUID = parseUuid(savedOwnerUUID, ownerUUID);
        }
        if (savedInboxUUID != null) {
            inboxUUID = parseUuid(savedInboxUUID, inboxUUID);
        }
        List<?> savedContents = inboxYaml.getList("contents", List.of());
        contents = new ArrayList<>();
        for (Object item : savedContents) {
            if (item instanceof ItemStack itemStack) {
                contents.add(itemStack);
            }
        }
    }

    public void save() {
        MopoYaml inboxYaml = MopoYaml.loadFromFile(playerFile, true);
        inboxYaml.set("owner_uuid", ownerUUID.toString());
        inboxYaml.set("inbox_uuid", inboxUUID.toString());
        inboxYaml.set("contents", contents);
        inboxYaml.saveSync(playerFile);
    }

    public void addContent(ItemStack item) {
        contents.add(item.clone());
        save();
    }

    public void removeContent(ItemStack item) {
        if (contents.remove(item)) {
            save();
        }
    }

    public void removeClanInvite(String clanName) {
        boolean changed = false;
        Iterator<ItemStack> iterator = contents.iterator();
        while (iterator.hasNext()) {
            ItemStack item = iterator.next();
            if (item == null) {
                continue;
            }
            String itemClanName = NBT.get(item, nbt ->
                    nbt.hasTag("clanInvite") && nbt.hasTag("clanName") ? nbt.getString("clanName") : null);
            if (clanName.equals(itemClanName)) {
                iterator.remove();
                changed = true;
            }
        }
        if (changed) {
            save();
        }
    }

    private UUID parseUuid(String value, UUID fallback) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            MopoClans.getINSTANCE().getLogger().warning("Invalid UUID in inbox file " + playerFile + ": " + value);
            return fallback;
        }
    }
}
