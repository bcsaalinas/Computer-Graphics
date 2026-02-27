package edu.up.cg.operations;

import edu.up.cg.model.ImageWrapper;

import java.awt.image.BufferedImage;

//operation for rotating an image
public class ImageRotation implements ImageOperation {

    private int angle;

    public ImageRotation(int angle) {
        this.angle = angle;
    }

    @Override
    public ImageWrapper apply(ImageWrapper image) {

        int width = image.getImage().getWidth();
        int height = image.getImage().getHeight();

        //create new wrapper with the new dimensions and rotate at certain angle
        switch (angle){

            case 90:

                ImageWrapper newWrapper = new ImageWrapper(new BufferedImage(height, width, image.getImage().getType()));
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        int rgb = image.getImage().getRGB(i, j);
                        //now we consider j as x and width - i
                        newWrapper.getImage().setRGB(j,width - i - 1, rgb);
                    }
                }

                image.setImage(newWrapper.getImage());
                break;

            case 180:
                ImageWrapper newWrapper1 = new ImageWrapper(new BufferedImage(width, height, image.getImage().getType()));
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        int rgb = image.getImage().getRGB(i, j);
                        //now we consider width - i and height - j
                        newWrapper1.getImage().setRGB(width - i - 1, height - j - 1, rgb);
                    }
                }
                image.setImage(newWrapper1.getImage());
                break;

            case 270:
                ImageWrapper newWrapper2 = new ImageWrapper(new BufferedImage(height, width, image.getImage().getType()));
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        int rgb = image.getImage().getRGB(i, j);
                        //now we consider height - j and i
                        newWrapper2.getImage().setRGB(height - j - 1, i, rgb);
                    }
                }


                image.setImage(newWrapper2.getImage());
                break;
            default:
                System.out.println("invalid angle");
                break;

        }



        return image;
    }
}
