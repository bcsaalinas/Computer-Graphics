package edu.up.cg.ai;

import java.io.*;
import java.nio.file.Files;

// converts text to a loudness-normalized WAV audio file using OpenAI TTS
public class OpenAITTS {

    private static final String TTS_URL = "https://api.openai.com/v1/audio/speech";

    private final String apiKey;

    public OpenAITTS() {
        this.apiKey = System.getenv("OPEN_API_KEY");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            throw new RuntimeException("OPEN_API_KEY environment variable is not set");
        }
    }

    // converts text to audio, normalizes loudness, and saves it as a WAV file at outputPath
    public String generateAudio(String text, String outputPath) throws Exception {
        // OpenAI returns a valid WAV file directly — no base64 decoding or header writing needed
        String rawWavPath = outputPath.replace(".wav", "_raw.wav");
        downloadAudio(text, rawWavPath);

        normalizeLoudness(rawWavPath, outputPath);

        new File(rawWavPath).delete();
        return outputPath;
    }

    // calls the OpenAI TTS API via curl and saves the WAV response directly to rawWavPath
    private void downloadAudio(String text, String rawWavPath) throws Exception {
        String requestBody = buildTtsRequest(text);

        File tempFile = File.createTempFile("tts_request", ".json");
        Files.writeString(tempFile.toPath(), requestBody);

        ProcessBuilder pb = new ProcessBuilder(
                "curl", "-s", "--max-time", "60", "-X", "POST",
                TTS_URL,
                "-H", "Authorization: Bearer " + apiKey,
                "-H", "Content-Type: application/json",
                "-d", "@" + tempFile.getAbsolutePath(),
                "--output", rawWavPath
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // drain stdout — curl writes audio to the file, stdout only carries errors
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while (reader.readLine() != null) {}

        int exitCode = process.waitFor();
        tempFile.delete();

        if (exitCode != 0) {
            throw new RuntimeException("curl failed with exit code: " + exitCode);
        }
    }

    // builds the JSON request body for the TTS API
    private String buildTtsRequest(String text) {
        return "{\"model\":\"tts-1\","
                + "\"input\":\"" + escapeJson(text) + "\","
                + "\"voice\":\"nova\","
                + "\"response_format\":\"wav\"}";
    }

    // runs ffmpeg loudnorm to bring the audio within YouTube's loudness standards
    // targets: -14 LUFS integrated loudness, -1 dBTP true peak, 7 LU LRA
    private void normalizeLoudness(String inputPath, String outputPath) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-y", "-i", inputPath,
                "-af", "loudnorm=I=-14:TP=-1:LRA=7",
                outputPath
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // drain stdout so ffmpeg never blocks on a full pipe buffer
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while (reader.readLine() != null) {}

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("ffmpeg loudnorm failed with exit code: " + exitCode);
        }
    }

    // escapes special characters so the text can be safely embedded inside a JSON value
    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
