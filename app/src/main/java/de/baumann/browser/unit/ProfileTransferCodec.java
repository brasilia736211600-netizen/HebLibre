package de.baumann.browser.unit;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import de.baumann.browser.database.Record;

/** Versioned portable representation for app-owned profile state. */
public final class ProfileTransferCodec {
    private static final String PLAIN_HEADER = "HEBLIBRE_PROFILE_V1_PLAIN";
    private static final String ENCRYPTED_HEADER = "HEBLIBRE_PROFILE_V1_AES_GCM";
    private static final int SALT_BYTES = 16;
    private static final int IV_BYTES = 12;
    private static final int KEY_BITS = 256;
    private static final int PBKDF2_ITERATIONS = 120_000;

    private ProfileTransferCodec() {
    }

    public static String encodePlain(ProfileMetadata metadata,
                                     List<Record> history,
                                     List<Record> bookmarks,
                                     List<Record> tabs) {
        StringBuilder out = new StringBuilder();
        out.append(PLAIN_HEADER).append('\n');
        line(out, "id", metadata.getId());
        line(out, "name", metadata.getName());
        line(out, "color", metadata.getColor());
        line(out, "icon", metadata.getIcon());
        line(out, "notes", metadata.getNotes());
        line(out, "tags", joinTags(metadata.getTags()));
        line(out, "group", metadata.getGroup());
        appendRecords(out, "HISTORY", history);
        appendRecords(out, "BOOKMARKS", bookmarks);
        appendRecords(out, "TABS", tabs);
        return out.toString();
    }

    public static String encodeEncrypted(ProfileMetadata metadata,
                                          List<Record> history,
                                          List<Record> bookmarks,
                                          List<Record> tabs,
                                          String password) {
        requirePassword(password);
        byte[] salt = randomBytes(SALT_BYTES);
        byte[] iv = randomBytes(IV_BYTES);
        try {
            SecretKey key = deriveKey(password, salt);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
            byte[] ciphertext = cipher.doFinal(encodePlain(metadata, history, bookmarks, tabs)
                    .getBytes(StandardCharsets.UTF_8));
            StringBuilder out = new StringBuilder();
            out.append(ENCRYPTED_HEADER).append('\n');
            line(out, "salt", toHex(salt));
            line(out, "iv", toHex(iv));
            line(out, "ciphertext", toHex(ciphertext));
            return out.toString();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unable to encrypt profile export", e);
        }
    }

    public static TransferPackage decode(String content, String password) {
        if (content == null) {
            throw new IllegalArgumentException("Empty profile export");
        }
        String normalized = content.trim();
        if (normalized.startsWith(ENCRYPTED_HEADER)) {
            return decodeEncrypted(normalized, password);
        }
        if (!normalized.startsWith(PLAIN_HEADER)) {
            throw new IllegalArgumentException("Unsupported profile export format");
        }
        return decodePlain(normalized);
    }

    private static TransferPackage decodeEncrypted(String content, String password) {
        requirePassword(password);
        List<String> lines = lines(content);
        String saltHex = findValue(lines, "salt");
        String ivHex = findValue(lines, "iv");
        String ciphertextHex = findValue(lines, "ciphertext");
        try {
            SecretKey key = deriveKey(password, fromHex(saltHex));
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, fromHex(ivHex)));
            String plain = new String(cipher.doFinal(fromHex(ciphertextHex)), StandardCharsets.UTF_8);
            return decodePlain(plain.trim());
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Incorrect password or damaged profile export", e);
        }
    }

    private static TransferPackage decodePlain(String content) {
        List<String> lines = lines(content);
        String id = decode(findValue(lines, "id"));
        String name = decode(findValue(lines, "name"));
        String color = decode(findValue(lines, "color"));
        String icon = decode(findValue(lines, "icon"));
        String notes = decode(findValue(lines, "notes"));
        String tagsValue = decode(findValue(lines, "tags"));
        String group = decode(findValue(lines, "group"));
        ProfileMetadata metadata = new ProfileMetadata(
                id,
                name,
                color,
                icon,
                notes,
                splitTags(tagsValue),
                group);

        List<Record> history = new ArrayList<>();
        List<Record> bookmarks = new ArrayList<>();
        List<Record> tabs = new ArrayList<>();
        List<Record> target = null;
        for (String line : lines) {
            if ("HISTORY".equals(line)) {
                target = history;
            } else if ("BOOKMARKS".equals(line)) {
                target = bookmarks;
            } else if ("TABS".equals(line)) {
                target = tabs;
            } else if (line.startsWith("R|")) {
                if (target == null) {
                    throw new IllegalArgumentException("Record found outside a section");
                }
                target.add(parseRecord(line));
            }
        }
        return new TransferPackage(metadata, history, bookmarks, tabs);
    }

    private static Record parseRecord(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 4) {
            throw new IllegalArgumentException("Malformed profile record");
        }
        long time;
        try {
            time = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Malformed profile record timestamp", e);
        }
        return new Record(decode(parts[2]), decode(parts[3]), time, -1);
    }

    private static void appendRecords(StringBuilder out, String section, List<Record> records) {
        out.append(section).append('\n');
        if (records == null) {
            return;
        }
        for (Record record : records) {
            if (record == null) {
                continue;
            }
            out.append("R|")
                    .append(record.getTime())
                    .append('|')
                    .append(encode(record.getTitle()))
                    .append('|')
                    .append(encode(record.getURL()))
                    .append('\n');
        }
    }

    private static void line(StringBuilder out, String key, String value) {
        out.append(key).append('=').append(encode(value)).append('\n');
    }

    private static String encode(String value) {
        try {
            return URLEncoder.encode(value == null ? "" : value, "UTF-8");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value == null ? "" : value, "UTF-8");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid profile export encoding", e);
        }
    }

    private static String joinTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (String tag : tags) {
            if (result.length() > 0) {
                result.append(',');
            }
            result.append(tag);
        }
        return result.toString();
    }

    private static List<String> splitTags(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String[] values = tags.split(",");
        List<String> result = new ArrayList<>();
        Collections.addAll(result, values);
        return result;
    }

    private static List<String> lines(String content) {
        String[] values = content.split("\\R");
        List<String> result = new ArrayList<>();
        Collections.addAll(result, values);
        return result;
    }

    private static String findValue(List<String> lines, String key) {
        String prefix = key + "=";
        for (String line : lines) {
            if (line.startsWith(prefix)) {
                return line.substring(prefix.length());
            }
        }
        throw new IllegalArgumentException("Missing profile export field: " + key);
    }

    private static SecretKey deriveKey(String password, byte[] salt) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_BITS);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        } finally {
            spec.clearPassword();
        }
    }

    private static void requirePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters");
        }
    }

    private static byte[] randomBytes(int length) {
        byte[] value = new byte[length];
        new SecureRandom().nextBytes(value);
        return value;
    }

    private static String toHex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            result.append(String.format(java.util.Locale.ROOT, "%02x", value & 0xff));
        }
        return result.toString();
    }

    private static byte[] fromHex(String value) {
        if (value == null || (value.length() & 1) != 0) {
            throw new IllegalArgumentException("Invalid hex payload");
        }
        byte[] result = new byte[value.length() / 2];
        for (int i = 0; i < result.length; i++) {
            int high = Character.digit(value.charAt(i * 2), 16);
            int low = Character.digit(value.charAt(i * 2 + 1), 16);
            if (high < 0 || low < 0) {
                throw new IllegalArgumentException("Invalid hex payload");
            }
            result[i] = (byte) ((high << 4) | low);
        }
        return result;
    }

    public static final class TransferPackage {
        private final ProfileMetadata metadata;
        private final List<Record> history;
        private final List<Record> bookmarks;
        private final List<Record> tabs;

        private TransferPackage(ProfileMetadata metadata,
                                List<Record> history,
                                List<Record> bookmarks,
                                List<Record> tabs) {
            this.metadata = metadata;
            this.history = Collections.unmodifiableList(new ArrayList<>(history));
            this.bookmarks = Collections.unmodifiableList(new ArrayList<>(bookmarks));
            this.tabs = Collections.unmodifiableList(new ArrayList<>(tabs));
        }

        public ProfileMetadata getMetadata() { return metadata; }
        public List<Record> getHistory() { return history; }
        public List<Record> getBookmarks() { return bookmarks; }
        public List<Record> getTabs() { return tabs; }
    }
}
