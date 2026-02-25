package edu.up.cg.model;

import java.awt.image.BufferedImage;


//wrapper for images when we are chaining operations
public class ImageWrapper {


    private BufferedImage image;

    public ImageWrapper(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
