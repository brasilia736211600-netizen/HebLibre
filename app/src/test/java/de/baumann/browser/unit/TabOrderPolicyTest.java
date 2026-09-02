package de.baumann.browser.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TabOrderPolicyTest {
    @Test
    public void moveLeftClampsAtFirstTab() {
        assertEquals(0, TabOrderPolicy.targetIndex(3, 0, -1));
    }

    @Test
    public void moveRightClampsAtLastTab() {
        assertEquals(2, TabOrderPolicy.targetIndex(3, 2, 1));
    }

    @Test
    public void moveRightAdvancesOnePosition() {
        assertEquals(2, TabOrderPolicy.targetIndex(4, 1, 1));
    }

    @Test
    public void moveLeftRetreatsOnePosition() {
        assertEquals(1, TabOrderPolicy.targetIndex(4, 2, -1));
    }

    @Test
    public void invalidInputsAreStable() {
        assertEquals(-1, TabOrderPolicy.targetIndex(0, 0, 1));
        assertEquals(-1, TabOrderPolicy.targetIndex(3, -1, 1));
        assertEquals(-1, TabOrderPolicy.targetIndex(3, 3, -1));
        assertEquals(1, TabOrderPolicy.targetIndex(3, 1, 0));
    }
}
