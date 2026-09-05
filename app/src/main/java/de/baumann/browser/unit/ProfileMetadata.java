package de.baumann.browser.unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Metadata for one reusable browsing profile.
 *
 * The profile id is the stable ownership key used by profile-scoped data.
 * This value does not imply WebView storage isolation; that requires the
 * separate WebView multi-profile capability seam.
 */
public final class ProfileMetadata {

    private final String id;
    private final String name;
    private final String color;
    private final String icon;
    private final String notes;
    private final List<String> tags;
    private final String group;

    public ProfileMetadata(String id, String name, String color, String icon,
                           String notes, List<String> tags, String group) {
        this.id = requireNonBlank(id, "id");
        this.name = requireNonBlank(name, "name");
        this.color = normalizeOptional(color);
        this.icon = normalizeOptional(icon);
        this.notes = normalizeOptional(notes);
        this.tags = normalizeTags(tags);
        this.group = normalizeOptional(group);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getIcon() {
        return icon;
    }

    public String getNotes() {
        return notes;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getGroup() {
        return group;
    }

    private static String requireNonBlank(String value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " must not be null or blank");
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be null or blank");
        }
        return normalized;
    }

    private static String normalizeOptional(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    private static List<String> normalizeTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> normalized = new LinkedHashSet<>();
        for (String tag : tags) {
            if (tag == null) {
                continue;
            }
            String value = tag.trim();
            if (!value.isEmpty()) {
                normalized.add(value);
            }
        }
        return Collections.unmodifiableList(new ArrayList<>(normalized));
    }
}
