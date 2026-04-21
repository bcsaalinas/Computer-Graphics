package edu.up.cg.io;

import edu.up.cg.image.ImageConverter;
import edu.up.cg.image.ImageScaler;
import edu.up.cg.metadata.MetadataReader;
import edu.up.cg.models.MediaItem;
import edu.up.cg.models.RawMetadata;

import javax.imageio.ImageIO;
import java.io.File;

public class MediaLoader {

    public MediaItem loadMedia(String path) throws Exception {
        MetadataReader metadataReader = new MetadataReader();
        RawMetadata rawMetadata = metadataReader.readMetadata(path);

        boolean isVideo = MediaFormats.isVideoFile(path);

        // use original path for videos, convert only images
        String mediaPath = path;
        if (!isVideo) {
            ImageConverter imageConverter = new ImageConverter();
            mediaPath = imageConverter.convertToJpeg(path);
        }

        MediaItem mediaItem = new MediaItem(mediaPath);

        // scale only for image inputs, skip for videos
        if (!isVideo && mediaItem.getImage() != null) {
            ImageScaler imageScaler = new ImageScaler();
            mediaItem.setImage(imageScaler.scaleImage(mediaItem.getImage()));
            ImageIO.write(mediaItem.getImage(), "jpg", new File(mediaPath));
        }

        mediaItem.setRawMetadata(rawMetadata);
        return mediaItem;
    }

}
