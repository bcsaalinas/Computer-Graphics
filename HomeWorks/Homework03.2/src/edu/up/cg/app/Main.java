package edu.up.cg.app;

import edu.up.cg.compression.ImageCompressor;
import edu.up.cg.compression.ImageCompressor.CompressedData;
import edu.up.cg.image.ImageHandler;
import edu.up.cg.io.CompressedFileIO;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String inputPath = "input.png";
        String compressedPath = "compressed.cmp";
        String outputPath = "decompressed.png";
        int blockSize = 2;

        ImageHandler imageHandler = new ImageHandler();
        ImageCompressor compressor = new ImageCompressor(blockSize);
        CompressedFileIO fileIO = new CompressedFileIO();

        try {
            //compress
            BufferedImage original = imageHandler.load(inputPath);
            int width = original.getWidth();
            int height = original.getHeight();
            System.out.println("Image: " + width + "x" + height);
            System.out.println("Block size: " + blockSize + "x" + blockSize);

            CompressedData compressed = compressor.compress(original);
            fileIO.save(compressed, compressedPath);


            //decompress
            System.out.println("\n Decompression:");
            CompressedData loaded = fileIO.load(compressedPath);
            BufferedImage decompressed = compressor.decompress(loaded);
            imageHandler.save(decompressed, outputPath);
            System.out.println("saved: " + outputPath);



        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}