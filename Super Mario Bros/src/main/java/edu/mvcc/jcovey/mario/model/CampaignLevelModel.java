package edu.mvcc.jcovey.mario.model;

import java.nio.file.Path;

public class CampaignLevelModel extends LevelModel {
    private final String worldText;
    private CampaignLevelModel nextLevel;

    public CampaignLevelModel(String worldText, String areaId, Path levelPath) {
        super(areaId, levelPath);
        this.worldText = worldText;
    }

    public String getWorldText() {
        return worldText;
    }

    public CampaignLevelModel getNextLevel() {
        return nextLevel;
    }

    public void setNextLevel(CampaignLevelModel nextLevel) {
        this.nextLevel = nextLevel;
    }
}
