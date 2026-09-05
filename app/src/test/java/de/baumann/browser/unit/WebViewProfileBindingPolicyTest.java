package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WebViewProfileBindingPolicyTest {

    @Test
    public void defaultProfileDoesNotRequireBinding() {
        assertFalse(WebViewProfileBindingPolicy.shouldBind(
                ProfileIdentity.DEFAULT_PROFILE_ID, true));
    }

    @Test
    public void unsupportedMultiProfileDoesNotBind() {
        assertFalse(WebViewProfileBindingPolicy.shouldBind("work", false));
    }

    @Test
    public void namedProfileBindsWhenSupported() {
        assertTrue(WebViewProfileBindingPolicy.shouldBind("work", true));
    }

    @Test
    public void blankProfileFallsBackToDefault() {
        assertFalse(WebViewProfileBindingPolicy.shouldBind("   ", true));
    }
}
