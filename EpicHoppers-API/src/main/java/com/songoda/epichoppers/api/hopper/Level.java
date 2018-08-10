package com.songoda.epichoppers.api.hopper;

import java.util.List;

public interface Level {
    List<String> getDescription();

    int getLevel();

    int getRange();

    int getAmount();

    int getBlockBreak();

    int getSuction();

    int getCostExperience();

    int getCostEconomy();
}
