package org.Mopo.mopoClans.ClanData;

import mopo.mopoLib.yml.MopoYaml;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class Clan {

    private String clanName;
    private String clanTag;
    private int maxMembers;
    private UUID clanLeaderUUID;
    private List<UUID> clanAdminsUUID;
    private List<UUID> clanMembersUUID;
    private int clanPoints;
    private double clanBalance;
    private int clanLevel;
    private double clanEXP;
    private File clanFile;
    private MopoYaml yml;

    Clan(String clanName, String clanTag, UUID leaderUUID) {
        this.clanName = clanName;
        this.clanTag = clanTag;
        this.clanLevel = 0;
        this.clanEXP = 0;
        this.clanPoints = 0;
        this.clanBalance = 0;
        this.clanLeaderUUID = leaderUUID;
    }

    Clan() {
        this.clanName = null;
        this.clanTag = null;
        this.clanLevel = 0;
        this.clanEXP = 0;
        this.clanPoints = 0;
        this.clanBalance = 0;
        this.clanLeaderUUID = null;
    }


    public String getClanName() {
        return clanName;
    }

    public void setClanName(String clanName) {
        this.clanName = clanName;
    }

    public String getClanTag() {
        return clanTag;
    }

    public void setClanTag(String clanTag) {
        this.clanTag = clanTag;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public UUID getClanLeaderUUID() {
        return clanLeaderUUID;
    }

    public void setClanLeaderUUID(UUID clanLeaderUUID) {
        this.clanLeaderUUID = clanLeaderUUID;
    }

    public List<UUID> getClanAdminsUUID() {
        return clanAdminsUUID;
    }

    public void setClanAdminsUUID(UUID clanAdminsUUID) {
        this.clanAdminsUUID.add(clanAdminsUUID);
    }

    public List<UUID> getClanMembersUUID() {
        return clanMembersUUID;
    }

    public void addClanMembersUUID(UUID clanMembersUUID) {
        this.clanMembersUUID.add(clanMembersUUID);
    }

    public void removeClanMembersUUID(UUID clanMemberUUID) {this.clanMembersUUID.remove(clanMemberUUID);}

    public int getClanPoints() {
        return clanPoints;
    }

    public void setClanPoints(int clanPoints) {
        this.clanPoints = clanPoints;
    }

    public double getClanBalance() {
        return clanBalance;
    }

    public void setClanBalance(double clanBalance) {
        this.clanBalance = clanBalance;
    }

    public int getClanLevel() {
        return clanLevel;
    }

    public void setClanLevel(int clanLevel) {
        this.clanLevel = clanLevel;
    }

    public double getClanEXP() {
        return clanEXP;
    }

    public void setClanEXP(double clanEXP) {
        this.clanEXP = clanEXP;
    }

    public File getClanFile() {
        return clanFile;
    }

    public void setClanFile(File clanFile) {
        this.clanFile = clanFile;
    }

    public MopoYaml getYml() {
        return MopoYaml.loadFromFile(clanFile);
    }
}
