package com.linkedlogics.flow;

import java.util.concurrent.TimeUnit;

public class LogicRetry {
    private int attempts ;
    private long delay ;
    private TimeUnit unit ;
    private Class<? extends Throwable>[] exclude ;
    private Class<? extends Throwable>[] include ;

    public LogicRetry(int attempts, long delay, TimeUnit unit, Class<? extends Throwable>[] include, Class<? extends Throwable>[] exclude) {
        this.attempts = attempts;
        this.delay = delay;
        this.unit = unit;
        this.include = include;
        this.exclude = exclude;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public Class<? extends Throwable>[] getExclude() {
        return exclude;
    }

    public void setExclude(Class<? extends Throwable>[] exclude) {
        this.exclude = exclude;
    }

    public Class<? extends Throwable>[] getInclude() {
        return include;
    }

    public void setInclude(Class<? extends Throwable>[] include) {
        this.include = include;
    }
}
