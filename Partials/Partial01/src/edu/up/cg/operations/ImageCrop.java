package edu.up.cg.operations;

import edu.up.cg.model.ImageWrapper;
import edu.up.cg.model.Region;

import java.awt.image.BufferedImage;
import java.nio.Buffer;

public class ImageCrop implements ImageOperation {

    private final Region region;

    public ImageCrop( Region region) {
    this.region = region;

    }



    //create a new image with the dimensions of the region and copy the pixels from the original image to the new image
    @Override
    public ImageWrapper apply(ImageWrapper image) {

        int x1 = region.getX1();
        int y1 = region.getY1();
        int x2 = region.getX2();
        int y2 = region.getY2();
        int width = x2 - x1;
        int height = y2 - y1;

        //create new image wrapper with the new dimensions
        BufferedImage newImage = new BufferedImage(width, height, image.getImage().getType());


        for (int i = x1; i < x2 ; i++) {

            for (int j = y1; j < y2; j++) {
                int rgb = image.getImage().getRGB(i,j);
                newImage.setRGB(i-x1, j-y1, rgb);

            }
        }

        //change the wrapper to the changed image
        image.setImage(newImage);
        return image;
    }



}
