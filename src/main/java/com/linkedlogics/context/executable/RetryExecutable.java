package com.linkedlogics.context.executable;

import com.linkedlogics.LogicContext;
import com.linkedlogics.annotation.RetryLogic;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class RetryExecutable implements LogicExecutable {
    private int maxAttempts;
    private long delay;
    private TimeUnit unit;
    private Class<? extends Throwable>[] exclude;
    private Class<? extends Throwable>[] include;
    private LogicExecutable executable;

    public RetryExecutable(RetryLogic retry, LogicExecutable executable) {
        this.executable = executable;
        this.maxAttempts = retry.attempts();
        this.delay = retry.delay();
        this.unit = retry.unit();
        this.include = retry.include();
        this.exclude = retry.exclude();
    }

    @Override
    public Optional<Map<String, Object>> execute(LogicContext context) {
        try {
            int attempts = 1;
            while (true) {
                try {
                    return executable.execute(context);
                } catch (Throwable e) {
                    if (exclude != null) {
                        for (Class exceptionClass : exclude) {
                            if (exceptionClass == e.getClass() || exceptionClass.isAssignableFrom(e.getClass())) {
                                throw e;
                            }
                        }
                    }

                    if (attempts == maxAttempts) {
                        throw e;
                    } else {
                        boolean mustRetry = false;
                        for (Class exceptionClass : include) {
                            if (exceptionClass == e.getClass() || exceptionClass.isAssignableFrom(e.getClass())) {
                                mustRetry = true;
                                break;
                            }
                        }

                        if (!mustRetry) {
                            throw e;
                        } else {
                            attempts++;
                            Thread.sleep(unit.toMillis(delay));
                        }
                    }
                }
            }
        } catch (InterruptedException e) {

        }

        return null ;
    }
}
