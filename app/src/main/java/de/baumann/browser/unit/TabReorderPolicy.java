package de.baumann.browser.unit;

/**
 * Keeps tab movement direction semantics in one dependency-free contract.
 */
public final class TabReorderPolicy {
    public static final int LEFT = -1;
    public static final int RIGHT = 1;

    private TabReorderPolicy() {
    }

    public static boolean isValidDirection(int direction) {
        return direction == LEFT || direction == RIGHT;
    }
}
