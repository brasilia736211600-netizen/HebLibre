package de.baumann.browser.unit;

/**
 * Bounded privacy policy for WebView media permission requests.
 *
 * When enabled, camera and microphone capture requests are blocked. This is
 * intentionally narrower than disabling the WebRTC engine itself.
 */
public final class WebRtcPermissionPolicy {

    private static final String VIDEO_CAPTURE = "android.webkit.resource.VIDEO_CAPTURE";
    private static final String AUDIO_CAPTURE = "android.webkit.resource.AUDIO_CAPTURE";

    private WebRtcPermissionPolicy() {
        // Utility class.
    }

    public static boolean shouldBlock(boolean privacyGuardEnabled, String resource) {
        if (!privacyGuardEnabled || resource == null) {
            return false;
        }
        return VIDEO_CAPTURE.equals(resource) || AUDIO_CAPTURE.equals(resource);
    }
}
