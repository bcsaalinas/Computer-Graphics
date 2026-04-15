package edu.up.cg.image;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageConverter {

    public String convertToJpeg(String inputPath) throws Exception {
        Path input = Paths.get(inputPath).toAbsolutePath().normalize();
        String convertedPath = buildConvertedPath(input);
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-i", input.toString(),
                "-frames:v", "1",
                "-update", "1",
                convertedPath
        );
        pb.redirectErrorStream(true);

        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("ffmpeg failed : " + exitCode);
        }

        return convertedPath;
    }

    private String buildConvertedPath(Path input) {
        String fileName = input.getFileName().toString();

        // determine extension from file name only so dots in folders do not affect parsing
        int lastDot = fileName.lastIndexOf('.');
        boolean hasExtension = lastDot > 0 && lastDot < fileName.length() - 1;
        String stem = hasExtension ? fileName.substring(0, lastDot) : fileName;

        if (stem.endsWith(".")) {
            stem = stem.substring(0, stem.length() - 1);
        }
        if (stem.isEmpty()) {
            stem = "media";
        }

        String outputName = stem + ".jpg";
        if (fileName.equalsIgnoreCase(outputName)) {
            outputName = stem + "_converted.jpg";
        }

        Path parent = input.getParent();
        Path output = parent == null ? Paths.get(outputName) : parent.resolve(outputName);
        return output.toString();
    }
}

