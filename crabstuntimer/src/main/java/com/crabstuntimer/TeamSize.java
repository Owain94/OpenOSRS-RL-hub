package com.crabstuntimer;

public enum TeamSize {
    ONE(50),
    TWO_TO_THREE(30),
    FOUR_TO_FIVE(20),
    SIX_PLUS(10);

    private int stunDuration;

    TeamSize(int stunDuration) {
        this.stunDuration = stunDuration;
    }

    public int getStunDuration() {
        return stunDuration;
    }
}
