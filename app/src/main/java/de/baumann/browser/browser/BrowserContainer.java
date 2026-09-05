package de.baumann.browser.browser;

import android.content.Context;

import de.baumann.browser.unit.ProfileCatalogStore;
import de.baumann.browser.unit.ProfileSessionStore;
import de.baumann.browser.unit.TabOrderPolicy;
import de.baumann.browser.view.NinjaWebView;

import java.util.LinkedList;
import java.util.List;

public class BrowserContainer {
    private final List<AlbumController> list = new LinkedList<>();
    private String sessionProfileId;

    public AlbumController get(int index) {
        return list.get(index);
    }

    public synchronized void add(AlbumController controller) {
        bindSessionProfile(controller);
        list.add(controller);
    }

    public synchronized void add(AlbumController controller, int index) {
        bindSessionProfile(controller);
        list.add(index, controller);
    }

    /** Moves an existing tab without destroying its WebView state. */
    public synchronized boolean move(int fromIndex, int direction) {
        int toIndex = TabOrderPolicy.targetIndex(list.size(), fromIndex, direction);
        if (toIndex < 0 || toIndex == fromIndex) {
            return toIndex == fromIndex && toIndex >= 0;
        }
        AlbumController controller = list.remove(fromIndex);
        list.add(toIndex, controller);
        return true;
    }

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
        persistSession();
        for (AlbumController albumController : list) {
            ((NinjaWebView) albumController).destroy();
        }
        list.clear();
        sessionProfileId = null;
    }

    private void bindSessionProfile(AlbumController controller) {
        if (sessionProfileId != null || controller == null || controller.getAlbumView() == null) {
            return;
        }
        Context context = controller.getAlbumView().getContext().getApplicationContext();
        sessionProfileId = ProfileCatalogStore.getActiveProfileId(context);
    }

    private void persistSession() {
        if (list.isEmpty()) {
            return;
        }
        Context context = list.get(0).getAlbumView().getContext().getApplicationContext();
        ProfileSessionStore.save(context, list, sessionProfileId);
    }
}
