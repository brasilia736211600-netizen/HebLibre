package de.baumann.browser.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Read-only checks for profile configuration consistency; it never mutates signals. */
public final class ProfileConsistencyPolicy {
    private ProfileConsistencyPolicy() { }

    public static List<String> findIssues(Map<String, String> settings) {
        List<String> issues = new ArrayList<>();
        if (settings == null) return issues;

        String ua = value(settings, "userAgent");
        String preset = value(settings, "ua_preset");
        String custom = value(settings, "userAgent_custom");
        String language = value(settings, "preferred_language");
        String proxy = value(settings, "proxy_url");

        if (!preset.isEmpty() && !ProfileUserAgentPolicy.isValidPreset(preset)) {
            issues.add("UA preset is unknown");
        }
        if (!custom.isEmpty() && ProfileUserAgentPolicy.normalizeCustom(custom).isEmpty()) {
            issues.add("Custom UA exceeds the supported length");
        }
        if (!language.isEmpty() && !ProfileLanguagePolicy.isValid(language)) {
            issues.add("Preferred language is invalid");
        }
        if (!proxy.isEmpty() && !ProfileProxyPolicy.isValid(proxy)) {
            issues.add("Proxy URL is invalid");
        }

        String lowerUa = ua.toLowerCase(Locale.ROOT);
        if (lowerUa.contains("windows") && language.startsWith("ar-")) {
            issues.add("Informational: Windows UA with Arabic language is valid, but verify the intended locale deliberately");
        }
        if (lowerUa.contains("iphone") && lowerUa.contains("android")) {
            issues.add("UA contains conflicting mobile platform markers");
        }
        if (!custom.isEmpty() && !custom.equals(ua)) {
            issues.add("Custom UA and effective UA differ; verify which value is intended");
        }
        return issues;
    }

    private static String value(Map<String, String> settings, String key) {
        String encoded = settings.get(key);
        return encoded != null && encoded.startsWith("s:") ? encoded.substring(2) : "";
    }
}
