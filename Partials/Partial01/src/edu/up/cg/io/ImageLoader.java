package edu.up.cg.io;

import java.awt.image.BufferedImage;


//class for loading images locally
public class ImageLoader {

    private final String path;

    public ImageLoader(String path) {
        this.path = path;
    }

    public BufferedImage loadImage() {
        try {
            return javax.imageio.ImageIO.read(new java.io.File(path));
        } catch (java.io.IOException e) {
            System.out.println("error loading image: " + e.getMessage());
            return null;
        }
    }



}
