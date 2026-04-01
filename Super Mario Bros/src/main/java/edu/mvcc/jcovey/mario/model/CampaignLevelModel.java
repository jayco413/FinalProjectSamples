package edu.mvcc.jcovey.mario.model;

import java.nio.file.Path;

public class CampaignLevelModel extends LevelModel {
    private final String worldText;
    private final int shortcutDigit;
    private CampaignLevelModel nextLevel;

    public CampaignLevelModel(String worldText, int shortcutDigit, String areaId, Path levelPath) {
        super(areaId, levelPath);
        this.worldText = worldText;
        this.shortcutDigit = shortcutDigit;
    }

    public String getWorldText() {
        return worldText;
    }

    public int getShortcutDigit() {
        return shortcutDigit;
    }

    public CampaignLevelModel getNextLevel() {
        return nextLevel;
    }

    public void setNextLevel(CampaignLevelModel nextLevel) {
        this.nextLevel = nextLevel;
    }
}
