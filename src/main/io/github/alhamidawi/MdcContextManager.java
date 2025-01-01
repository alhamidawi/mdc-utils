package io.github.alhamidawi;

import java.io.Closeable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MdcContextManager {

    private MdcContextManager() {
    }

    public static CloseableExtras with(String key, Object value) {
        return builder().add(key, value)
                .build();
    }

    public static MdcContextManagerBuilder builder() {
        return new MdcContextManagerBuilder();
    }

    public static class MdcContextManagerBuilder {

        private final Map<String, String> extras = new LinkedHashMap<>();

        private MdcContextManagerBuilder() {
        }

        public MdcContextManagerBuilder add(String key, Object value) {
            if(key == null) {
                throw new IllegalArgumentException("Null keys are not allowed.");
            }

            this.extras.put(key, value != null ? String.valueOf(value) : null);
            return this;
        }

        public CloseableExtras build() {
            List<MDCClosableWithRollback> closeables = this.extras.entrySet()
                    .stream()
                    .map(entry -> MDCClosableWithRollback.createAndPut(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
            return new CloseableExtras(closeables);
        }
    }

    public static final class CloseableExtras implements Closeable {

        private final Collection<MDCClosableWithRollback> closeables;

        private CloseableExtras(Collection<MDCClosableWithRollback> closeables) {
            this.closeables = Collections.unmodifiableCollection(closeables);
        }

        @Override
        public void close() {
            this.closeables.forEach(MDCClosableWithRollback::close);
        }
    }
}
