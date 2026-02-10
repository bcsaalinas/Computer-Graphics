package edu.up.cg.io;

import edu.up.cg.compression.ImageCompressor.CompressedData;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


 //total size = 9 bytes header + 3 bytes per block.

public class CompressedFileIO {

    public void save(CompressedData data, String filename) throws IOException {
        DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(filename))
        );

        out.writeInt(data.width);
        out.writeInt(data.height);
        out.writeByte(data.blockSize);

        for (int[] block : data.blocks) {
            out.writeByte(block[0]); // r
            out.writeByte(block[1]); // g
            out.writeByte(block[2]); // b
        }

        out.close();
    }

    public CompressedData load(String filename) throws IOException {
        DataInputStream in = new DataInputStream(
                new BufferedInputStream(new FileInputStream(filename))
        );

        int width = in.readInt();
        int height = in.readInt();
        int blockSize = in.readUnsignedByte();

        List<int[]> blocks = new ArrayList<>();
        while (in.available() > 0) {
            int r = in.readUnsignedByte();
            int g = in.readUnsignedByte();
            int b = in.readUnsignedByte();
            blocks.add(new int[]{r, g, b});
        }

        in.close();
        return new CompressedData(width, height, blockSize, blocks);
    }
}