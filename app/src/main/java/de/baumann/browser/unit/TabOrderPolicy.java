package de.baumann.browser.unit;

public final class TabOrderPolicy {
    private TabOrderPolicy() { }

    public static int targetIndex(int size, int currentIndex, int direction) {
        if (size <= 0 || currentIndex < 0 || currentIndex >= size) {
            return -1;
        }
        if (direction == 0) {
            return currentIndex;
        }
        int target = currentIndex + (direction < 0 ? -1 : 1);
        if (target < 0) {
            return 0;
        }
        if (target >= size) {
            return size - 1;
        }
        return target;
    }
}
