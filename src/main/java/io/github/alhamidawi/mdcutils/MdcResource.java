package io.github.alhamidawi.mdcutils;

import java.io.Closeable;
import org.slf4j.MDC;

/**
 * The MdcResource class is a utility that facilitates setting and restoring MDC (Mapped Diagnostic Context)
 * key-value pairs for use in logging frameworks that support SLF4J MDC integration. The class implements
 * the {@link Closeable} interface to ensure context cleanup when an instance is closed.
 * Each instance of MdcResource manages a specific MDC key, storing its previous value (if any)
 * and reverting the MDC state upon closure.
 */
class MdcResource implements Closeable {

    private final String key;
    private final String previousValue;

    private MdcResource(String key, String previousValue) {
        this.key = key;
        this.previousValue = previousValue;
    }

    static MdcResource of(String key, String value) {
        String previous = MDC.get(key);
        MDC.put(key, value);
        return new MdcResource(key, previous);
    }

    String getPreviousValue() {
        return previousValue;
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
