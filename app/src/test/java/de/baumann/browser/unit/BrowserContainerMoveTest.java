package de.baumann.browser.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.view.View;

import de.baumann.browser.browser.AlbumController;
import de.baumann.browser.browser.BrowserContainer;

import org.junit.Test;

public class BrowserContainerMoveTest {
    private static final class FakeAlbum implements AlbumController {
        private final String id;

        FakeAlbum(String id) {
            this.id = id;
        }

        @Override
        public View getAlbumView() {
            return null;
        }

        @Override
        public void activate() {
        }

        @Override
        public void deactivate() {
        }

        @Override
        public String toString() {
            return id;
        }
    }

    @Test
    public void moveRightPreservesExistingControllerInstances() {
        BrowserContainer container = new BrowserContainer();
        FakeAlbum first = new FakeAlbum("first");
        FakeAlbum second = new FakeAlbum("second");
        FakeAlbum third = new FakeAlbum("third");
        container.add(first);
        container.add(second);
        container.add(third);

        assertTrue(container.move(0, 1));
        assertEquals(second, container.get(0));
        assertEquals(first, container.get(1));
        assertEquals(third, container.get(2));
    }

    @Test
    public void moveLeftPreservesExistingControllerInstances() {
        BrowserContainer container = new BrowserContainer();
        FakeAlbum first = new FakeAlbum("first");
        FakeAlbum second = new FakeAlbum("second");
        FakeAlbum third = new FakeAlbum("third");
        container.add(first);
        container.add(second);
        container.add(third);

        assertTrue(container.move(2, -1));
        assertEquals(first, container.get(0));
        assertEquals(third, container.get(1));
        assertEquals(second, container.get(2));
    }
}
