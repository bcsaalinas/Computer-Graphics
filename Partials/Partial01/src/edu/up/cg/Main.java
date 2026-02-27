package edu.up.cg;

import edu.up.cg.io.ImageLoader;
import edu.up.cg.io.ImageSaver;
import edu.up.cg.model.ImageWrapper;
import edu.up.cg.model.Region;
import edu.up.cg.operations.ImageCrop;
import edu.up.cg.operations.ImageInvertion;
import edu.up.cg.operations.ImageRotation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;

public class Main {


    //center dog coordinates are p1(900,700)  p2(1300,1700)
    public static void main(String[] args) {


        startEditor();

    }

    //start the editor, do operations and save the image, simple ui
    public static void startEditor() {
        System.out.println("Image Editor, enter the path of your image: ");
        Scanner scanner = new Scanner(System.in);
        ImageWrapper wrapper = new ImageWrapper(null);
        //save the image in a wrapper so we can apply operations on it and chain them


        //keep asking for the path until we can load the image
        while (true) {
            String path = scanner.nextLine();
            try {
                ImageLoader loader = new ImageLoader(path);
                BufferedImage image = loader.loadImage();
                if (image == null) {
                    System.out.println("Error loading image: invalid or unreadable file.");
                    System.out.println("Enter a valid image path: ");
                    continue;
                }
                System.out.println("Image loaded");
                wrapper = new ImageWrapper(image);
                break;
            } catch (java.io.IOException e) {
                System.out.println("Error loading image: " + e.getMessage());
                System.out.println("Enter a valid image path: ");
            }
        }

        //simple menu for operations
        String option = "";
        while (!option.equals("5")) {
            System.out.println("1- Crop Image");
            System.out.println("2- Invert Image");
            System.out.println("3- Rotate Image");
            System.out.println("4- Save Image");
            System.out.println("5- Exit");
            option = scanner.nextLine();

            switch (option) {
                case "1":
                    System.out.println("Enter the coordinates of the region to crop (x1 y1 x2 y2): ");
                    //validation for the coords
                    String[] coords = scanner.nextLine().split(" ");

                    int x1 = Integer.parseInt(coords[0]);
                    int y1 = Integer.parseInt(coords[1]);
                    int x2 = Integer.parseInt(coords[2]);
                    int y2 = Integer.parseInt(coords[3]);
                    ImageCrop crop = new ImageCrop(new Region(x1, y1, x2, y2));
                    wrapper = crop.apply(wrapper);
                    break;
                case "2":
                    ImageInvertion invertion = new ImageInvertion();
                    wrapper = invertion.apply(wrapper);
                    break;
                case "3":
                    System.out.println("Enter the angle to rotate (90, 180, 270): ");
                    int angle = Integer.parseInt(scanner.nextLine());
                    ImageRotation rotate = new ImageRotation(angle);
                    wrapper = rotate.apply(wrapper);
                    break;
                case "4":
                    System.out.println("Enter the path to save the image: ");
                    String savePath = scanner.nextLine();
                    System.out.println("Enter the format to save the image ");
                    String format = scanner.nextLine();
                    savePath = savePath.endsWith("." + format) ? savePath : savePath + "." + format;
                    ImageSaver.saveImage(new File(savePath), wrapper.getImage(), format);
                    break;
                case "5":
                    System.out.println("Exiting");
                    break;
                default:
                    System.out.println("Invalid option");
            }

        }

    }

}
