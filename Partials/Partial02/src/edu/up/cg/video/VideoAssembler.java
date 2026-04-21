package edu.up.cg.video;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

// combines images/videos with audio into clips, then concatenates them into one final video
public class VideoAssembler {

    // turns a still image + audio track into a portrait mp4 clip
    // the clip duration matches the audio length
    public void imageToClip(String imagePath, String audioPath, String outputPath) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-y",
                "-loop", "1", "-i", imagePath,
                "-i", audioPath,
                "-c:v", "libx264",
                "-c:a", "aac", "-b:a", "192k",
                "-shortest",
                "-pix_fmt", "yuv420p",
                outputPath
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // drain output so ffmpeg never blocks on a full pipe buffer
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while (reader.readLine() != null) {}

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("ffmpeg imageToClip failed for: " + imagePath);
        }
    }

    // joins all clips in order into one final mp4 without re-encoding
    public void concatenate(List<String> clipPaths, String outputPath) throws Exception {
        // ffmpeg concat demuxer requires a text file listing each clip
        File concatFile = File.createTempFile("concat_list", ".txt");
        StringBuilder list = new StringBuilder();
        for (String path : clipPaths) {
            list.append("file '").append(path).append("'\n");
        }
        Files.writeString(concatFile.toPath(), list.toString());

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-y",
                "-f", "concat", "-safe", "0",
                "-i", concatFile.getAbsolutePath(),
                "-c", "copy",
                outputPath
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while (reader.readLine() != null) {}

        int exitCode = process.waitFor();
        concatFile.delete();

        if (exitCode != 0) {
            throw new RuntimeException("ffmpeg concatenate failed for output: " + outputPath);
        }
    }
}
