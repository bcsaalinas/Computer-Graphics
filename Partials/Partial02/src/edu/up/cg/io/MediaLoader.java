package edu.up.cg.io;

import edu.up.cg.image.ImageConverter;
import edu.up.cg.image.ImageScaler;
import edu.up.cg.metadata.MetadataReader;
import edu.up.cg.models.MediaItem;
import edu.up.cg.models.RawMetadata;

public class MediaLoader {

    public MediaItem loadMedia(String path) throws Exception {
        // read raw metadata from original file
        MetadataReader metadataReader = new MetadataReader();
        RawMetadata rawMetadata = metadataReader.readMetadata(path);

        // convert file to jpeg and get converted path
        ImageConverter imageConverter = new ImageConverter();
        String convertedPath = imageConverter.convertToJpeg(path);

        // create media item from converted file
        MediaItem mediaItem = new MediaItem(convertedPath);

        // scale the image and update the item
        ImageScaler imageScaler = new ImageScaler();
        mediaItem.setImage(imageScaler.scaleImage(mediaItem.getImage()));

        // attach metadata and return
        mediaItem.setRawMetadata(rawMetadata);
        return mediaItem;
    }

}
