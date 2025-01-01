package io.github.alhamidawi;

import org.slf4j.MDC;

import java.io.Closeable;

class MDCClosableWithRollback implements Closeable {

    private final String key;
    private final String previousValue;

    private MDCClosableWithRollback(String key, String previousValue) {
        this.key = key;
        this.previousValue = previousValue;
    }


    static MDCClosableWithRollback createAndPut(String key, String value) {
        String previous = MDC.get(key);
        MDC.put(key, value);
        return new MDCClosableWithRollback(key, previous);
    }



    @Override
    public void close() {
        if(previousValue != null) {
            MDC.put(key, previousValue);
        } else {
            MDC.remove(key);
        }
    }
}
