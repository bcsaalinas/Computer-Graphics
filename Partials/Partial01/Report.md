# Partial 01 Report + How to run it

## What the program does:
1. The program first has to load the image it will use as reference to perform operations on.
2. An image wrapper is created so that on the same iterations, the program reads info from the current state of the image and we write and change data to create a new image wrapper, its like having 2 notebooks, one with notes and one with a blank page, as we are reading what is on the notebook with info, we are writing on the blank one.
3. Set the new image to be the current wrapper, in that way, we can chain operations
4. When we are finished, save the image locally.

## How it works:
I divided the program into 3 packages,
1. **io**, which will have 2 classes **ImageLoader** and **ImageSaver** to load and save images locally.
2. **model**, which is the basic structure of the image, it has the classes **Region** as it represents 2 points in a 2D area, and **ImageWrapper** which is the wrapper explained previously.
3. **operations** which include all the operations we can perform on a wrapper (crop, invert colors and rotate), I created an interface **ImageOperation** that has a method apply, which each sub-class will recieve and return an image wrapper.

### operations:
- **Crop**: We use a region class (2 points), start at p1 and end in p2, returning a new imageWrapper with new dimensions.
- **Invertion** Iterate trough the image and get the main rgb color at each pixel and re-define the new color substracting red, green and blue values from 255, and group them using bit maniupulation.
- **Rotate** Use a switch statement for each angle,
- for 90 degrees, the x coordinates will be the old y coordinates and the y coordinates will be the max width - currentX coordinate
- for 180 the x and y coordinates will be inverted (same formula for 90 degrees but for both coords in this case)
- for 270 the y coordinate will be the old x coord, and we will use again the formula but the x coordinates will be the max height - currentY coord.

I used a switch to handle the angles based on the angle given when you construct the ImageRotation class.

4. **Main** on the main class I created a method **startEditor** which will handle loading the image, performing operations and saving the image with simple switch statements from the user's input.

## How to run it
Just run the Main java class and drop an image on the project and specify the proper path for it, you can perform operations in any order. **make sure to save before exiting** 
