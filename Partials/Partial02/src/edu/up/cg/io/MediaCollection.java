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
        List<MediaItem> items = new ArrayList<>();

        if (files == null) {
            return items;
        }

        MediaLoader mediaLoader = new MediaLoader();

        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }

            String filePath = file.getAbsolutePath();
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
