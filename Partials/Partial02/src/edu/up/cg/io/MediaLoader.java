package edu.up.cg.io;

import edu.up.cg.image.ImageConverter;
import edu.up.cg.metadata.MetadataReader;
import edu.up.cg.models.MediaItem;
import edu.up.cg.models.RawMetadata;

public class MediaLoader {
    //load the image from the file, get its raw metadata, convert the file to jpeg using ffmpeg, create mediaItem from converted path, attach raw metadata to mediaItem, and return the mediaItem


    public MediaItem loadMedia(String path) throws Exception {
        // read raw metadata from original file
        MetadataReader metadataReader = new MetadataReader();
        RawMetadata rawMetadata = metadataReader.readMetadata(path);

        // convert file to jpeg and get converted path
        ImageConverter imageConverter = new ImageConverter();
        String convertedPath = imageConverter.convertToJpeg(path);

        // create media item from converted file
        MediaItem mediaItem = new MediaItem(convertedPath);

        // attach metadata and return
        mediaItem.setRawMetadata(rawMetadata);
        return mediaItem;
    }

}
