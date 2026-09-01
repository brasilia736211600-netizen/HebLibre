package de.baumann.browser.browser;

import android.view.View;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

/**
 * Characterization test for BrowserContainer after converting its tab list
 * from static (process-wide) to instance-scoped state (P1 step 2). Verifies
 * that basic list operations behave as before, and that two separate
 * BrowserContainer instances no longer share state - the core capability
 * this step introduces, and a prerequisite for any future per-profile tab
 * container. remove()/clear() are not covered here: they cast the
 * controller to the concrete android.webkit.WebView-based NinjaWebView,
 * which requires a real Android runtime and is unrelated to the
 * static -> instance change under test.
 */
public class BrowserContainerTest {

    private static class FakeAlbumController implements AlbumController {
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
    }

    @Test
    public void addGetIndexOfSize_behaveAsFlatList() {
        BrowserContainer container = new BrowserContainer();
        AlbumController a = new FakeAlbumController();
        AlbumController b = new FakeAlbumController();

        container.add(a);
        container.add(b);

        assertEquals(2, container.size());
        assertSame(a, container.get(0));
        assertSame(b, container.get(1));
        assertEquals(0, container.indexOf(a));
        assertEquals(1, container.indexOf(b));
        assertEquals(2, container.list().size());
    }

    @Test
    public void twoInstances_doNotShareState() {
        BrowserContainer containerOne = new BrowserContainer();
        BrowserContainer containerTwo = new BrowserContainer();

        containerOne.add(new FakeAlbumController());

        assertEquals(1, containerOne.size());
        assertEquals(0, containerTwo.size());
        assertNotSame(containerOne.list(), containerTwo.list());
    }
}
