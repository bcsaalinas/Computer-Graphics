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

    // scales a video to portrait, replaces its audio with the narration, and outputs an mp4 clip
    // if the video ends before the audio, the last frame is frozen until the audio finishes
    public void videoToClip(String videoPath, String audioPath, String outputPath) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-y",
                "-i", videoPath,
                "-i", audioPath,
                // scale and crop to portrait, then pad with the last frame indefinitely so -shortest can cut at audio end
                "-filter_complex", "[0:v]scale=1080:1920:force_original_aspect_ratio=increase,crop=1080:1920,tpad=stop_mode=clone:stop=-1[v]",
                "-map", "[v]", "-map", "1:a:0",
                "-c:v", "libx264",
                "-c:a", "aac", "-b:a", "192k",
                "-shortest",
                "-pix_fmt", "yuv420p",
                outputPath
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while (reader.readLine() != null) {}

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("ffmpeg videoToClip failed for: " + videoPath);
        }
    }

    // joins all clips in order into one final mp4 without re-encoding
    public void concatenate(List<String> clipPaths, String outputPath) throws Exception {
        // ffmpeg concat demuxer requires a text file listing each clip
        File concatFile = File.createTempFile("concat_list", ".txt");
        StringBuilder list = new StringBuilder();
        for (String path : clipPaths) {
            // use absolute paths so ffmpeg resolves them correctly regardless of where the list file lives
            list.append("file '").append(new File(path).getAbsolutePath()).append("'\n");
        }
        Files.writeString(concatFile.toPath(), list.toString());

        // re-encode instead of copy so clips with different framerates or sample rates merge cleanly
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-y",
                "-f", "concat", "-safe", "0",
                "-i", concatFile.getAbsolutePath(),
                "-c:v", "libx264", "-c:a", "aac", "-b:a", "192k",
                outputPath
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder ffmpegOutput = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            ffmpegOutput.append(line).append("\n");
        }

        int exitCode = process.waitFor();
        concatFile.delete();

        if (exitCode != 0) {
            throw new RuntimeException("ffmpeg concatenate failed:\n" + ffmpegOutput);
        }
    }
}
