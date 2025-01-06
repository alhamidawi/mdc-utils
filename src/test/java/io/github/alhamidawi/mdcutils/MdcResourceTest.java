package io.github.alhamidawi.mdcutils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class MdcResourceTest {

    @BeforeEach
    void beforeEach() {
        MDC.clear();
    }

    @Test
    void shouldSuccessfullyCreateMdcResource() {
        // given
        var key = "Iaculisullamcorper";
        var value = "Necparturient";

        // when
        var result = MdcResource.of(key, value);

        // then
        assertNotNull(result);

        var context = MDC.getCopyOfContextMap();
        assertNotNull(context);
        assertTrue(context.containsKey(key));
        assertEquals(value, context.get(key));
    }

    @Test
    void shouldPersistPreviousValue() {
        // given
        var key = "Antemauris";
        var previousValue = "Fringilla";
        var value = "Tempornetus";
        MDC.put(key, previousValue);

        // when
        var result = MdcResource.of(key, value);

        // then
        assertNotNull(result);
        assertEquals(previousValue, result.getPreviousValue());
        var context = MDC.getCopyOfContextMap();
        assertNotNull(context);
        assertTrue(context.containsKey(key));
        assertEquals(value, context.get(key));
    }

    @Test
    void shouldSuccessfullyRemoveKeyFromMdcAfterClosing() {
        // given
        var key = "Iaculisullamcorper";
        var value = "Necparturient";
        var resource = MdcResource.of(key, value);

        // when
        resource.close();

        // then
        var context = MDC.getCopyOfContextMap();
        assertNotNull(context);
        assertFalse(context.containsKey(key));
    }

    @Test
    void shouldSuccessfullyAddPreviousValueToMdcAfterClosing() {
        // given
        var key = "Velipsum";
        var value = "Loremmagnis";
        var previousValue = "Lectusaptent";
        MDC.put(key, previousValue);
        var resource = MdcResource.of(key, value);

        // when
        resource.close();

        // then
        var context = MDC.getCopyOfContextMap();
        assertNotNull(context);
        assertTrue(context.containsKey(key));
        assertEquals(previousValue, context.get(key));
    }


}
