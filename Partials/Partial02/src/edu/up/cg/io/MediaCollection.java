package edu.up.cg.io;

import edu.up.cg.models.MediaItem;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MediaCollection {

    // load all valid media from a folder and sort by metadata date
    public List<MediaItem> loadFromFolder(String folderPath) throws Exception {
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("Invalid folder path: " + folderPath);
        }

        File[] files = folder.listFiles();
        List<String> paths = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    paths.add(file.getAbsolutePath());
                }
            }
        }

        return loadFromFiles(paths);
    }

    // load valid media from an explicit list of file paths and sort by metadata date
    public List<MediaItem> loadFromFiles(List<String> filePaths) throws Exception {
        List<MediaItem> items = new ArrayList<>();
        MediaLoader mediaLoader = new MediaLoader();

        for (String filePath : filePaths) {
            // skip intermediate files that MediaLoader generates when converting videos
            if (filePath.endsWith("_converted.jpg")) {
                continue;
            }

            if (!MediaFormats.isValidMediaFile(filePath)) {
                continue;
            }

            items.add(mediaLoader.loadMedia(filePath));
        }

        // null dates go to the end
        items.sort(Comparator.comparing(
                item -> {
                    if (item.getRawMetadata() == null) {
                        return null;
                    }
                    return item.getRawMetadata().getDate();
                },
                Comparator.nullsLast(LocalDateTime::compareTo)
        ));

        return items;
    }

}
