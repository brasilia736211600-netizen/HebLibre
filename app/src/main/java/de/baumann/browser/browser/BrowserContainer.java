package de.baumann.browser.browser;

import de.baumann.browser.view.NinjaWebView;

import java.util.LinkedList;
import java.util.List;

public class BrowserContainer {
    private final List<AlbumController> list = new LinkedList<>();

    public AlbumController get(int index) {
        return list.get(index);
    }

    public synchronized void add(AlbumController controller) {
        list.add(controller);
    }
    public synchronized void add(AlbumController controller, int index) { list.add(index, controller); }

    public synchronized void remove(AlbumController controller) {
        ((NinjaWebView) controller).destroy();
        list.remove(controller);
    }

    public int indexOf(AlbumController controller) {
        return list.indexOf(controller);
    }

    public List<AlbumController> list() {
        return list;
    }

    public int size() {
        return list.size();
    }

    public synchronized void clear() {
        for (AlbumController albumController : list) {
            ((NinjaWebView) albumController).destroy();
        }
        list.clear();
    }
}
