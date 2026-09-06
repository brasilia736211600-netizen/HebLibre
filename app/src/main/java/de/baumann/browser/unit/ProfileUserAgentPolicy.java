package de.baumann.browser.unit;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/** Curated browser-compatibility UA presets; this is not a fingerprint-evasion engine. */
public final class ProfileUserAgentPolicy {
    private static final Map<String, String> PRESETS = new LinkedHashMap<>();

    static {
        PRESETS.put("android_chrome_152", "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/152.0.0.0 Mobile Safari/537.36");
        PRESETS.put("android_firefox_155", "Mozilla/5.0 (Android 10; Mobile; rv:155.0) Gecko/155.0 Firefox/155.0");
        PRESETS.put("windows_chrome_152", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/152.0.0.0 Safari/537.36");
        PRESETS.put("windows_edge_152", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/152.0.0.0 Safari/537.36 Edg/152.0.0.0");
        PRESETS.put("mac_safari_26", "Mozilla/5.0 (Macintosh; Intel Mac OS X 26_0) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/26.0 Safari/605.1.15");
        PRESETS.put("iphone_safari_26", "Mozilla/5.0 (iPhone; CPU iPhone OS 26_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/26.0 Mobile/15E148 Safari/604.1");
        PRESETS.put("linux_chrome_152", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/152.0.0.0 Safari/537.36");
    }

    private ProfileUserAgentPolicy() { }

    public static String resolvePreset(String presetId) {
        if (presetId == null) return "";
        String value = PRESETS.get(presetId.trim().toLowerCase(Locale.ROOT));
        return value == null ? "" : value;
    }

    public static boolean isValidPreset(String presetId) {
        return !resolvePreset(presetId).isEmpty();
    }

    public static String normalizeCustom(String value) {
        if (value == null) return "";
        String normalized = value.trim();
        return normalized.length() > 1024 ? "" : normalized;
    }

    public static Map<String, String> presets() {
        return new LinkedHashMap<>(PRESETS);
    }
}
