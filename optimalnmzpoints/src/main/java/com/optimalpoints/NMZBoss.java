package com.optimalpoints;

import lombok.Getter;

public class NMZBoss {
    @Getter
    private final String name;
    @Getter
    private final int normalValue;
    @Getter
    private final int hardValue;

    NMZBoss(String name, int normalValue, int hardValue){
        this.name = name;
        this.normalValue = normalValue;
        this.hardValue = hardValue;
    }
}
