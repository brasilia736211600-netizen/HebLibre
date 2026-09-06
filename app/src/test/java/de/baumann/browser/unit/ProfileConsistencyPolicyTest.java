package de.baumann.browser.unit;

import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ProfileConsistencyPolicyTest {
    @Test public void validProfileHasNoConfigurationIssues() {
        Map<String, String> settings = new LinkedHashMap<>();
        settings.put("ua_preset", "s:android_chrome_152");
        settings.put("userAgent_custom", "s:");
        settings.put("userAgent", "s:" + ProfileUserAgentPolicy.resolvePreset("android_chrome_152"));
        settings.put("preferred_language", "s:en-US");
        settings.put("proxy_url", "s:http://proxy.example.com:8080");
        assertTrue(ProfileConsistencyPolicy.findIssues(settings).isEmpty());
    }

    @Test public void conflictingMobileUaIsReported() {
        Map<String, String> settings = Collections.singletonMap(
                "userAgent", "s:Mozilla/5.0 (iPhone; Android 10)");
        List<String> issues = ProfileConsistencyPolicy.findIssues(settings);
        assertTrue(issues.stream().anyMatch(value -> value.contains("conflicting mobile platform")));
    }

    @Test public void invalidNetworkAndLanguageAreReported() {
        Map<String, String> settings = new LinkedHashMap<>();
        settings.put("proxy_url", "s:proxy.example.com");
        settings.put("preferred_language", "s:not a language");
        List<String> issues = ProfileConsistencyPolicy.findIssues(settings);
        assertTrue(issues.size() >= 2);
    }
}
