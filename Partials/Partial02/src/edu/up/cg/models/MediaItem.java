package edu.up.cg.models;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class MediaItem {

    //object that will hold image/video information and pass it around to the other classes
    private String path;
    private String filetype;
    private int width;
    private int height;
    private BufferedImage image;
    private LocalDateTime date;
    private double latitude;
    private double longitude;
    private int orientation;

    public MediaItem(String path) throws IOException {
    this.path = path;
    this.image = ImageIO.read(new File(path));
    this.filetype = path.substring(path.lastIndexOf(".") + 1);
    this.width = image.getWidth();
    this.height = image.getHeight();
    // set default values for metadata, will be updated later when we read the actual metadata from the file
    this.latitude = 0.0;
    this.longitude = 0.0;
    this.date = LocalDateTime.now();
    this.orientation = 0;

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


    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}
