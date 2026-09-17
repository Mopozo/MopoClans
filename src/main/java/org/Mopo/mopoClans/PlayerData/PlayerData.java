package org.Mopo.mopoClans.PlayerData;

import mopo.mopoLib.yml.MopoYaml;
import org.Mopo.mopoClans.MopoClans;

import java.io.File;
import java.util.UUID;

public class PlayerData {
    private UUID uuid;
    private String clanName;
    private String clanRank;
    private boolean isAdmin;
    private File playerFile;




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

    PlayerData(UUID uuid) {
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
}
