package edu.up.cg.compression;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


public class ImageCompressor {

    private final int blockSize;






    public ImageCompressor(int blockSize) {
        this.blockSize = blockSize;
        //size of each block in pixels


    }


    public CompressedData compress(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        //how many blocks fit in each direction (round up for edge blocks)
        int blocksX = (width + blockSize - 1) / blockSize;
        int blocksY = (height + blockSize - 1) / blockSize;

        List<int[]> blocks = new ArrayList<>();

        // iterate block by block
        for (int by = 0; by < blocksY; by++) {
            for (int bx = 0; bx < blocksX; bx++) {

                // where does this block start and end in pixel coordinates
                int startX = bx * blockSize;
                int startY = by * blockSize;
                // handle edge blocks that dont fill a full square
                int endX = Math.min(startX + blockSize, width);
                int endY = Math.min(startY + blockSize, height);

                // Sum all r, g, b values in this block
                long sumR = 0, sumG = 0, sumB = 0;
                int pixelCount = 0;

                for (int y = startY; y < endY; y++) {
                    for (int x = startX; x < endX; x++) {
                        int rgb = image.getRGB(x, y);
                        sumR += (rgb >> 16) & 0xFF;
                        sumG += (rgb >> 8) & 0xFF;
                        sumB += rgb & 0xFF;
                        pixelCount++;
                    }
                }

                // calculate average
                int avgR = (int) (sumR / pixelCount);
                int avgG = (int) (sumG / pixelCount);
                int avgB = (int) (sumB / pixelCount);

                blocks.add(new int[]{avgR, avgG, avgB});
            }
        }

        return new CompressedData(width, height, blockSize, blocks);
    }


    //decompress by painting each block with its stored average color
    public BufferedImage decompress(CompressedData data) {
        BufferedImage image = new BufferedImage(
                data.width, data.height, BufferedImage.TYPE_INT_RGB
        );

        int blocksX = (data.width + data.blockSize - 1) / data.blockSize;
        int blockIndex = 0;

        int blocksY = (data.height + data.blockSize - 1) / data.blockSize;

        for (int by = 0; by < blocksY; by++) {
            for (int bx = 0; bx < blocksX; bx++) {
                int[] block = data.blocks.get(blockIndex);
                int rgb = (block[0] << 16) | (block[1] << 8) | block[2];

                //paint every pixel in this block with the average color
                int startX = bx * data.blockSize;
                int startY = by * data.blockSize;
                int endX = Math.min(startX + data.blockSize, data.width);
                int endY = Math.min(startY + data.blockSize, data.height);

                for (int y = startY; y < endY; y++) {
                    for (int x = startX; x < endX; x++) {
                        image.setRGB(x, y, rgb);
                    }
                }

                blockIndex++;
            }
        }

        return image;
    }

    //hold compressed data
    public static class CompressedData {
        public final int width;
        public final int height;
        public final int blockSize;
        public final List<int[]> blocks; // each: {avgR, avgG, avgB}

        public CompressedData(int width, int height, int blockSize, List<int[]> blocks) {
            this.width = width;
            this.height = height;
            this.blockSize = blockSize;
            this.blocks = blocks;
        }
    }
}