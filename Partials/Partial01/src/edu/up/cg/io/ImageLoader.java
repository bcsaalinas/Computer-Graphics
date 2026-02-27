package edu.up.cg.io;

import java.awt.image.BufferedImage;


//class for loading images locally
public class ImageLoader {

    private final String path;

    public ImageLoader(String path) {
        this.path = path;
    }

    //load the image from the path and return it as a BufferedImage
    public BufferedImage loadImage() throws java.io.IOException {
        return javax.imageio.ImageIO.read(new java.io.File(path));
    }



}
