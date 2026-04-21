package edu.up.cg.io;

public final class MediaFormats {

    private MediaFormats() {
    }

    // video file extensions
    private static final String[] videoExtensions = {
            ".mp4", ".mov", ".avi", ".mkv", ".wmv", ".m4v"
    };

    // image file extensions
    private static final String[] imageExtensions = {
            ".jpg", ".jpeg", ".png"
    };

    public static boolean isVideoFile(String path) {
        return hasAnyExtension(path, videoExtensions);
    }

    public static boolean isValidMediaFile(String path) {
        return hasAnyExtension(path, imageExtensions) || hasAnyExtension(path, videoExtensions);
    }

    private static boolean hasAnyExtension(String path, String[] extensions) {
        String lowerPath = path.toLowerCase();
        for (String extension : extensions) {
            if (lowerPath.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
