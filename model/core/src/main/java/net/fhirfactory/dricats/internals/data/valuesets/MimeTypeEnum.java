package net.fhirfactory.dricats.internals.data.valuesets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MIME type enumeration with constructor details and dynamic registry populated by parsing
 * the mimetypes.txt file in this package. We cannot create enum constants at runtime, but we
 * load all entries into an internal registry to enable lookups and iteration over the full set
 * of MIME definitions from the file.
 *
 * Note on identifiers: Some file extensions start with digits. Java identifiers cannot start
 * with a digit, so any enum constants representing such types would need prefixing (e.g., EXT_3DM).
 * For the dynamic registry loaded from the text file, this is not a constraint.
 */
public enum MimeTypeEnum {
    // Keep a small baseline of common constants; the rest are available via the dynamic registry
    EXT_3DM("x-world", "x-3dmf", "3dm"),
    EXT_7Z("application", "x-7z-compressed", "7z"),
    A("application", "octet-stream", "a"),
    AAB("application", "x-authorware-bin", "aab"),

    PDF("application", "pdf", "pdf"),
    PNG("image", "png", "png"),
    JPG("image", "jpeg", "jpg"),
    JPEG("image", "jpeg", "jpeg"),
    GIF("image", "gif", "gif"),
    SVG("image", "svg+xml", "svg"),
    WEBP("image", "webp", "webp"),

    XML("application", "xml", "xml"),
    JSON("application", "json", "json"),
    YAML("application", "x-yaml", "yaml"),
    YML("application", "x-yaml", "yml"),
    HTML("text", "html", "html"),
    HTM("text", "html", "htm"),
    CSS("text", "css", "css"),
    CSV("text", "csv", "csv"),
    TXT("text", "plain", "txt"),

    BASE64("application", "base64", "base64"),
    MM("application", "base64", "mm"),

    JS("application", "javascript", "js"),
    ZIP("application", "zip", "zip"),

    MP4("video", "mp4", "mp4"),
    MPEG("video", "x-motion-jpeg", "mpeg"),
    WEBM("video", "webm", "webm"),
    MP3("audio", "mpeg", "mp3"),
    WAV("audio", "wav", "wav"),
    MKV("video", "x-matroska", "mkv"),
    MKA("audio", "x-matroska", "mka"),
    OGG_AUDIO("audio", "ogg", "ogg"),
    OGG_VIDEO("video", "ogv", "ogv");

    public static final class MimeDef {
        private final String extension; // without leading dot
        private final String type;
        private final String subType;

        public MimeDef(String extension, String type, String subType) {
            this.extension = extension;
            this.type = type;
            this.subType = subType;
        }
        public String getExtension() { return extension; }
        public String getType() { return type; }
        public String getSubType() { return subType; }
        public String toMimeString() { return type + "/" + subType; }
        @Override public String toString() { return extension + " -> " + toMimeString(); }
    }

    private final String type;
    private final String subType;
    private final String extension;

    private static final Map<String, MimeTypeEnum> BY_EXTENSION = new HashMap<>();

    // Dynamic registry parsed from mimetypes.txt
    private static final List<MimeDef> ALL_FROM_FILE;
    private static final Map<String, List<MimeDef>> BY_EXTENSION_FROM_FILE;
    // Derived map to expose combined extensions (comma-separated) for enum constants
    private static final Map<MimeTypeEnum, String> EXTENSIONS_FOR_ENUM = new HashMap<>();

    static {
        for (MimeTypeEnum v : values()) {
            BY_EXTENSION.put(v.extension, v);
        }
        List<MimeDef> loaded = loadFromMimeTypesResource();
        ALL_FROM_FILE = Collections.unmodifiableList(loaded);
        Map<String, List<MimeDef>> map = new HashMap<>();
        for (MimeDef def : loaded) {
            map.computeIfAbsent(def.getExtension(), k -> new ArrayList<>()).add(def);
        }
        BY_EXTENSION_FROM_FILE = Collections.unmodifiableMap(map);

        // Build combined extensions list for each enum constant by matching MIME type
        Map<String, List<String>> byMime = new HashMap<>();
        for (MimeDef def : loaded) {
            byMime.computeIfAbsent(def.toMimeString(), k -> new ArrayList<>()).add(def.getExtension());
        }
        for (MimeTypeEnum v : values()) {
            List<String> combined = new ArrayList<>();
            // Always include the enum's own primary extension first
            combined.add(v.extension);
            List<String> extra = byMime.get(v.toMimeString());
            if (extra != null) {
                for (String e : extra) {
                    if (!combined.contains(e)) {
                        combined.add(e);
                    }
                }
            }
            if (combined.size() > 1) {
                EXTENSIONS_FOR_ENUM.put(v, String.join(",", combined));
            }
        }
    }

    MimeTypeEnum(String type, String subType, String extension) {
        this.type = type;
        this.subType = subType;
        this.extension = extension;
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

    public String getExtension() {
        String combined = EXTENSIONS_FOR_ENUM.get(this);
        return combined != null ? combined : extension;
    }

    public String toMimeString() {
        return type + "/" + subType;
    }

    @Override
    public String toString() {
        return toMimeString();
    }

    public static MimeTypeEnum fromExtension(String extension) {
        if (extension == null) {
            return null;
        }
        String key = normalizeExtension(extension);
        return BY_EXTENSION.get(key);
    }

    public static List<MimeDef> getAllFromFile() {
        return ALL_FROM_FILE;
    }

    public static List<MimeDef> getByExtensionFromFile(String extension) {
        if (extension == null) {
            return Collections.emptyList();
        }
        return BY_EXTENSION_FROM_FILE.getOrDefault(normalizeExtension(extension), Collections.emptyList());
    }

    public static boolean supportsExtension(String extension) {
        if (extension == null) { return false; }
        String key = normalizeExtension(extension);
        return BY_EXTENSION.containsKey(key) || BY_EXTENSION_FROM_FILE.containsKey(key);
    }

    private static String normalizeExtension(String extension) {
        String key = extension.startsWith(".") ? extension.substring(1) : extension;
        return key.toLowerCase();
    }

    private static List<MimeDef> loadFromMimeTypesResource() {
        String resourcePath = "/net/fhirfactory/dricats/internals/data/valuesets/mimetypes.txt";
        InputStream in = MimeTypeEnum.class.getResourceAsStream(resourcePath);
        if (in == null) {
            // Try relative to the class (same package), in case of non-leading slash resource resolution
            in = MimeTypeEnum.class.getResourceAsStream("mimetypes.txt");
        }
        if (in == null) {
            // As a last resort, attempt to load via the context class loader
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
                in = cl.getResourceAsStream(resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath);
            }
        }
        if (in == null) {
            // File may be in source tree not packaged as a resource. Return empty list to avoid startup failure.
            return Collections.emptyList();
        }
        List<MimeDef> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                // Expected format: .ext<TAB>type/subtype
                int tab = line.indexOf('\t');
                if (tab < 0) {
                    // try splitting by whitespace
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 2) {
                        addEntry(result, parts[0], parts[1]);
                    }
                } else {
                    String ext = line.substring(0, tab).trim();
                    String mime = line.substring(tab + 1).trim();
                    addEntry(result, ext, mime);
                }
            }
        } catch (IOException e) {
            // Swallow and return what we have
        }
        return result;
    }

    private static void addEntry(List<MimeDef> result, String extToken, String mimeToken) {
        if (extToken == null || mimeToken == null) { return; }
        String ext = extToken.startsWith(".") ? extToken.substring(1) : extToken;
        int slash = mimeToken.indexOf('/');
        if (slash <= 0 || slash == mimeToken.length() - 1) { return; }
        String type = mimeToken.substring(0, slash).trim();
        String subType = mimeToken.substring(slash + 1).trim();
        if (ext.isEmpty() || type.isEmpty() || subType.isEmpty()) { return; }
        result.add(new MimeDef(ext.toLowerCase(), type.toLowerCase(), subType));
    }
}
