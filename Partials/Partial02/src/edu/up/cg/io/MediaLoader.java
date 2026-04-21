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
        ImageConverter imageConverter = new ImageConverter();
        ImageScaler imageScaler = new ImageScaler();

        if (isVideo) {
            // keep the original video path so the assembler can use the full clip later
            MediaItem mediaItem = new MediaItem(path);

            // extract the first frame as a JPEG so we can describe the video with AI
            String thumbnailPath = imageConverter.convertToJpeg(path);
            MediaItem thumbnail = new MediaItem(thumbnailPath);
            if (thumbnail.getImage() != null) {
                mediaItem.setImage(imageScaler.scaleImage(thumbnail.getImage()));
            }

            mediaItem.setRawMetadata(rawMetadata);
            return mediaItem;
        } else {
            String jpegPath = imageConverter.convertToJpeg(path);
            MediaItem mediaItem = new MediaItem(jpegPath);

            if (mediaItem.getImage() != null) {
                mediaItem.setImage(imageScaler.scaleImage(mediaItem.getImage()));
                ImageIO.write(mediaItem.getImage(), "jpg", new File(jpegPath));
            }

            mediaItem.setRawMetadata(rawMetadata);
            return mediaItem;
        }
    }

}
