package edu.up.cg.operations;

import edu.up.cg.model.ImageWrapper;
//interface for all operations that can be applied to an image
public interface ImageOperation {

    //we recieve and return an ImageWrapper so we can chain operations
    public ImageWrapper apply(ImageWrapper image);



}
