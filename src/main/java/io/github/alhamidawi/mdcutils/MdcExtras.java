package io.github.alhamidawi.mdcutils;

import java.io.Closeable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for managing MDC (Mapped Diagnostic Context) key-value pairs using
 * the SLF4J MDC API. The utility provides methods to add key-value pairs to the MDC
 * context and revert changes after use through the use of {@link MdcResourceCollection}.
 * This class is designed for use with logging frameworks that support MDC,
 * facilitating the tracking of contextual information such as user IDs or session IDs.
 * The added context is automatically reverted upon closing the resources.
 * This class cannot be instantiated.
 */
public class MdcExtras {

    private static final String NULL_KEY_ERROR = "Null keys are not allowed.";

    private MdcExtras() {
    }

    /**
     * Creates an {@link MdcResourceCollection} by adding a specified single key-value pair by converting the value to
     * a string representation. This method facilitates the management of MDC (Mapped Diagnostic Context) context
     * modifications in a structured and resource-safe manner.
     *
     * @param key the key to be added to the MDC context, must not be null
     * @param value the value associated with the key, stored as a string representation
     * @return an {@link MdcResourceCollection} containing the key-value pair added to the MDC context
     * @throws IllegalArgumentException if the key is null
     */
    public static MdcResourceCollection with(String key, Object value) {
        return builder().add(key, value)
                .build();
    }

    /**
     * Creates and returns a new instance of the {@code Builder} class.
     *
     * @return a new {@code Builder} instance for creating {@link MdcResourceCollection} objects
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Map<String, String> extras = new LinkedHashMap<>();

        private Builder() {
        }

        /**
         * Adds a key-value pair to the builder's internal map. The value is converted to
         * a string representation before storage. The key must not be null.
         *
         * @param key the key to be added to the map, must not be null
         * @param value the value associated with the key, converted to a string representation
         * @return the current instance of the builder for method chaining
         * @throws IllegalArgumentException if the key is null
         */
        public Builder add(String key, Object value) {
            if(key == null) {
                throw new IllegalArgumentException(NULL_KEY_ERROR);
            }

            this.extras.put(key, String.valueOf(value));
            return this;
        }

        /**
         * Constructs an {@link MdcResourceCollection} containing MDC resources based on
         * the key-value pairs stored in the builder's internal map. Each key-value pair
         * is converted into an {@link MdcResource}, which manages the addition of the
         * corresponding entry to the MDC context and its subsequent cleanup.
         *
         * @return an {@link MdcResourceCollection} containing the MDC resources created
         *         from the builder's internal map of key-value pairs
         */
        public MdcResourceCollection build() {
            var closeableResources = this.extras.entrySet()
                    .stream()
                    .map(entry -> MdcResource.of(entry.getKey(), entry.getValue()))
                    .toList();
            return new MdcResourceCollection(closeableResources);
        }
    }

    /**
     * The MdcResourceCollection class manages a collection of {@link MdcResource} objects, enabling
     * batch handling and cleanup of MDC (Mapped Diagnostic Context) key-value pairs. This class
     * implements the {@link AutoCloseable} interface to provide a mechanism for ensuring that
     * all the managed resources are closed in a systematic manner.
     * This class is most commonly used together with utilities like {@code MdcExtras} to manage
     * multiple MDC context modifications collectively and ensure proper cleanup through a
     * resource-based approach.
     */
    public static final class MdcResourceCollection implements Closeable {

        private final List<MdcResource> closeableResources;

        private MdcResourceCollection(Collection<MdcResource> closeableResources) {
            this.closeableResources = List.copyOf(closeableResources);
        }

        @Override
        public void close() {
            this.closeableResources.forEach(MdcResource::close);
        }
    }
}
