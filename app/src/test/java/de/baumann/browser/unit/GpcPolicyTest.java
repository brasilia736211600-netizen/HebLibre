package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * TDD contract for the local Global Privacy Control request signal.
 */
public class GpcPolicyTest {

    @Test
    public void enabledReturnsGpcSignalValue() {
        assertEquals("1", GpcPolicy.headerValue(true));
    }

    @Test
    public void disabledReturnsNoSignal() {
        assertNull(GpcPolicy.headerValue(false));
    }
}
