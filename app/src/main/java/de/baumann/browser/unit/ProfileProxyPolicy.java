package de.baumann.browser.unit;

import java.net.URI;
import java.util.Locale;

/** Pure validation/normalization contract for a profile-owned WebView proxy. */
public final class ProfileProxyPolicy {
    private ProfileProxyPolicy() { }

    public static String normalize(String value) {
        if (value == null) return "";
        String normalized = value.trim();
        if (normalized.isEmpty()) return "";
        if (normalized.indexOf('@') >= 0 || normalized.indexOf('\\') >= 0) return "";
        try {
            URI uri = new URI(normalized);
            String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
            if (!("http".equals(scheme) || "https".equals(scheme) || "socks5".equals(scheme))) return "";
            if (uri.getHost() == null || uri.getHost().trim().isEmpty()) return "";
            int port = uri.getPort();
            if (port < 1 || port > 65535) return "";
            if (uri.getRawQuery() != null || uri.getRawFragment() != null
                    || (uri.getPath() != null && !uri.getPath().isEmpty())) return "";
            return uri.toString();
        } catch (Exception ignored) {
            return "";
        }
    }

    public static boolean isValid(String value) {
        return !normalize(value).isEmpty();
    }

    public static String normalizeBypassRules(String value) {
        if (value == null) return "";
        String normalized = value.trim();
        if (normalized.isEmpty()) return "";
        String[] parts = normalized.split(",");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            String rule = part.trim();
            if (rule.isEmpty() || rule.indexOf(' ') >= 0 || rule.indexOf('\n') >= 0 || rule.indexOf('\r') >= 0) continue;
            if (result.length() > 0) result.append(',');
            result.append(rule);
        }
        return result.toString();
    }
}
