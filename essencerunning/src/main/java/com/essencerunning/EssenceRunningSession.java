package com.essencerunning;

import lombok.Getter;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@Getter
public class EssenceRunningSession {

    private final Set<EssenceRunner> runners;
    private int totalFireRunesCrafted;

    public EssenceRunningSession() {
        this.runners = new TreeSet<>();
    }

    public void updateRunnerStatistic(final String rsn, final int pureEssenceTraded, final int bindingNecklaceTraded) {
        final Optional<EssenceRunner> optional = runners.stream().filter(i -> i.getRsn().equals(rsn)).findFirst();
        final EssenceRunner runner = optional.isPresent() ? optional.get() : new EssenceRunner(rsn);

        this.runners.remove(runner);
        runner.increasePureEssenceTraded(pureEssenceTraded);
        runner.increaseBindingNecklaceTraded(bindingNecklaceTraded);

        this.runners.add(runner);
    }

    public void updateCrafterStatistic(final int fireRunesCrafted) {
        this.totalFireRunesCrafted += fireRunesCrafted;
    }

    public void reset() {
        this.runners.clear();
        this.totalFireRunesCrafted = 0;
    }
}
