package io.github.alhamidawi.mdcutils;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class MdcExtrasTest {

    @Test
    void shouldAddSingleKeyValueToMdc() {
        // given
        var key = "Laciniaac";
        var value = "Laciniaquis";

        // when - then
        try(var ignore = MdcExtras.with(key, value)) {
            var context = MDC.getCopyOfContextMap();
            assertNotNull(context);
            assertTrue(context.containsKey(key));
            assertEquals(value, context.get(key));
        }
    }

    @Test
    void shouldAddMultipleKeyValuesToMdc() {
        // given
        var key1 = "Laciniaac";
        var value1 = "Laciniaquis";
        var key2 = "Imperdietrhoncus";
        var value2 = "Dictumstcommodo";

        // when - then
        try(var ignore = MdcExtras.builder()
                .add(key1, value1)
                .add(key2, value2)
                .build()
        ) {
            var context = MDC.getCopyOfContextMap();
            assertNotNull(context);
            assertTrue(context.containsKey(key1));
            assertTrue(context.containsKey(key2));
            assertEquals(value1, context.get(key1));
            assertEquals(value2, context.get(key2));
        }
    }

    @Test
    void shouldSuccessfullyRemoveFromMdc() {
        // given
        var key1 = "Laciniaac";
        var value1 = "Laciniaquis";
        var key2 = "Imperdietrhoncus";
        var value2 = "Dictumstcommodo";

        // when - then
        try(var ignore = MdcExtras.builder()
                .add(key1, value1)
                .add(key2, value2)
                .build()
        ) {
            var context = MDC.getCopyOfContextMap();
            assertNotNull(context);
            assertTrue(context.containsKey(key1));
            assertTrue(context.containsKey(key2));
        } finally {
            var context = MDC.getCopyOfContextMap();
            assertNotNull(context);
            assertFalse(context.containsKey(key1));
            assertFalse(context.containsKey(key2));
        }
    }

    @Test
    void shouldProperlyHandleMdc() {
        // given
        var key = "Odiotorquent";
        var value1 = "Ultriciesneque";
        var value2 = "Temporin";
        MDC.put(key, value1);

        // when - then
        try(var ignore = MdcExtras.with(key, value2)) {
            var context = MDC.getCopyOfContextMap();
            assertNotNull(context);
            assertTrue(context.containsKey(key));
            assertEquals(value2, context.get(key));
        } finally {
            var context = MDC.getCopyOfContextMap();
            assertNotNull(context);
            assertTrue(context.containsKey(key));
            assertEquals(value1, context.get(key));
        }
    }

    @Test
    void shouldThrowIllegalArgumentExceptionOnNullKey() {
        // given - when - then
        assertThrows(IllegalArgumentException.class, this::execute);
    }

    // we extracted as assertThrows requires lambda
    private void execute() {
        try(var ignore = MdcExtras.with(null, "value")) {
            var context = MDC.getCopyOfContextMap();
            assertNotNull(context);
        }
    }

}
