package edu.up.cg.models;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MediaItem {

    private String path;
    private String filetype;
    private int width;
    private int height;
    private BufferedImage image;
    private RawMetadata rawMetadata;
    private String audioPath;

    public MediaItem(String path) throws IOException {
        this.path = path;
        this.filetype = path.substring(path.lastIndexOf(".") + 1);
        this.rawMetadata = new RawMetadata();


        if(isImage(filetype)){
            this.image = ImageIO.read(new File(path));
            if (image != null) {
                this.width = image.getWidth();
                this.height = image.getHeight();
            }
        }

    }

    public String getPath() {
        return path;
    }

    public String getFiletype() {
        return filetype;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        if (image != null) {
            this.width = image.getWidth();
            this.height = image.getHeight();
        }
    }

    public RawMetadata getRawMetadata() {
        return rawMetadata;
    }

    public void setRawMetadata(RawMetadata rawMetadata) {
        this.rawMetadata = rawMetadata;
    }

    public void setPath(String path) {
        this.path = path;
        this.filetype = path.substring(path.lastIndexOf(".") + 1);
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public boolean isVideo() {
        String lowerType = filetype.toLowerCase();
        return lowerType.equals("mp4") || lowerType.equals("mov") || lowerType.equals("avi")
                || lowerType.equals("mkv") || lowerType.equals("wmv") || lowerType.equals("m4v");
    }

    private boolean isImage(String filetype){
        String lowerType = filetype.toLowerCase();
        return lowerType.equals("jpg") || lowerType.equals("jpeg") || lowerType.equals("png");
    }
}
