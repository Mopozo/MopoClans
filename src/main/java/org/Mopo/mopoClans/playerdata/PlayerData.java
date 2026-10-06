package org.Mopo.mopoClans.playerdata;

import org.Mopo.mopoClans.inbox.Inbox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {
    private UUID uuid;
    private String clanName;
    private String clanRank;
    private Inbox inbox;
    private boolean isAdmin;
    private File playerFile;
    private final List<String> incomingClanInvites = new ArrayList<>();

    PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public String getClanName() {
        return clanName;
    }

    public void setClanName(String clanName) {
        this.clanName = clanName;
    }

    public String getClanRank() {
        return clanRank;
    }

    public void setClanRank(String clanRank) {
        this.clanRank = clanRank;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public File getPlayerFile() {
        return playerFile;
    }

    public void setPlayerFile(File playerFile) {
        this.playerFile = playerFile;
    }

    public Inbox getInbox() {return inbox;}

    public void setInbox(Inbox inbox) {this.inbox = inbox;}

    public List<String> getIncomingClanInvites() {
        return incomingClanInvites;
    }

    public void addIncomingClanInvites(String clanName) {
        if (!this.incomingClanInvites.contains(clanName)) {
            this.incomingClanInvites.add(clanName);
        }
    }

    public void removeIncomingClanInvite(String clanName) {
        this.incomingClanInvites.remove(clanName);
    }

    public void setIncomingClanInvites(List<String> clanNames) {
        this.incomingClanInvites.clear();
        if (clanNames != null) {
            this.incomingClanInvites.addAll(clanNames);
        }
    }
}
