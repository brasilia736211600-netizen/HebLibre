package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * TDD contract for the bounded WebRTC media-permission guard.
 */
public class WebRtcPermissionPolicyTest {

    @Test
    public void privacyGuardBlocksVideoCapture() {
        assertTrue(WebRtcPermissionPolicy.shouldBlock(true, "android.webkit.resource.VIDEO_CAPTURE"));
    }

    @Test
    public void privacyGuardBlocksAudioCapture() {
        assertTrue(WebRtcPermissionPolicy.shouldBlock(true, "android.webkit.resource.AUDIO_CAPTURE"));
    }

    @Test
    public void disabledGuardDoesNotBlockMedia() {
        assertFalse(WebRtcPermissionPolicy.shouldBlock(false, "android.webkit.resource.VIDEO_CAPTURE"));
    }

    @Test
    public void unrelatedResourceIsNotBlocked() {
        assertFalse(WebRtcPermissionPolicy.shouldBlock(true, "android.webkit.resource.PROTECTED_MEDIA_ID"));
    }
}
