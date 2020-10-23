package com.essencerunning;

import lombok.Getter;

import java.util.Objects;

@Getter
public class EssenceRunner implements Comparable<EssenceRunner> {

    private final String rsn;
    private int pureEssenceTraded;
    private int bindingNecklaceTraded;

    public EssenceRunner(final String rsn) {
        this.rsn = rsn;
        this.pureEssenceTraded = 0;
        this.bindingNecklaceTraded = 0;
    }

    public void increasePureEssenceTraded(final int amount) {
        this.pureEssenceTraded += amount;
    }

    public void increaseBindingNecklaceTraded(final int amount) {
        this.bindingNecklaceTraded += amount;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EssenceRunner that = (EssenceRunner) o;

        return this.rsn.equals(that.rsn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.rsn);
    }

    @Override
    public int compareTo(final EssenceRunner that) {
        final int compareEssence = Integer.compare(that.pureEssenceTraded, this.pureEssenceTraded);
        final int compareBinding = Integer.compare(that.bindingNecklaceTraded, this.bindingNecklaceTraded);

        return compareEssence != 0 ? compareEssence : compareBinding != 0 ? compareBinding : this.rsn.compareTo(that.rsn);
    }
}
