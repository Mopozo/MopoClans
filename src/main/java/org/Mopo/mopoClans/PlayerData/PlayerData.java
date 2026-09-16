package org.Mopo.mopoClans.PlayerData;

import java.util.UUID;

public class PlayerData {
    private UUID uuid;
    private String clanName;
    private String clanRank;

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
}
