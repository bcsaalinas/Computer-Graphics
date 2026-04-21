package edu.up.cg.ai;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;

// converts text to a loudness-normalized wav audio file using Gemini TTS
public class GeminiTTS {

    private static final String TTS_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-tts:generateContent";

    // audio format that Gemini TTS returns
    private static final int SAMPLE_RATE = 24000;
    private static final int BITS_PER_SAMPLE = 16;
    private static final int CHANNELS = 1;

    private final String apiKey;

    public GeminiTTS() {
        this.apiKey = System.getenv("GEMINI_API_KEY");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            throw new RuntimeException("GEMINI_API_KEY environment variable is not set");
        }
    }

    // converts text to audio, normalizes loudness, and saves it as a WAV file at outputPath
    public String generateAudio(String text, String outputPath) throws Exception {
        String pcmBase64 = requestAudio(text);
        byte[] pcmBytes = Base64.getDecoder().decode(pcmBase64);

        // Gemini returns raw PCM with no file header — write a valid WAV file first
        String rawWavPath = outputPath.replace(".wav", "_raw.wav");
        writeWavFile(pcmBytes, rawWavPath);

        // normalize loudness to meet YouTube standards
        normalizeLoudness(rawWavPath, outputPath);

        new File(rawWavPath).delete();
        return outputPath;
    }

    // calls the Gemini TTS API via curl and returns the base64-encoded raw PCM audio
    private String requestAudio(String text) throws Exception {
        String requestBody = buildTtsRequest(text);

        File tempFile = File.createTempFile("tts_request", ".json");
        Files.writeString(tempFile.toPath(), requestBody);

        ProcessBuilder pb = new ProcessBuilder(
                "curl", "-s", "--max-time", "60", "-X", "POST",
                TTS_URL,
                "-H", "x-goog-api-key: " + apiKey,
                "-H", "Content-Type: application/json",
                "-d", "@" + tempFile.getAbsolutePath()
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        int exitCode = process.waitFor();
        tempFile.delete();

        if (exitCode != 0) {
            throw new RuntimeException("curl failed with exit code: " + exitCode);
        }

        return extractAudioData(response.toString());
    }

    // builds the JSON request body for the TTS API
    private String buildTtsRequest(String text) {
        return "{\"contents\":[{\"parts\":[{\"text\":\"" + escapeJson(text) + "\"}]}],"
                + "\"generationConfig\":{\"responseModalities\":[\"AUDIO\"],"
                + "\"speechConfig\":{\"voiceConfig\":{\"prebuiltVoiceConfig\":{\"voiceName\":\"Aoede\"}}}}}";
    }

    // finds the base64 audio payload inside the Gemini TTS JSON response
    private String extractAudioData(String jsonResponse) {
        // the audio lives inside inlineData — find that block first, then find "data" inside it
        String marker = "\"inlineData\"";
        int markerIndex = jsonResponse.indexOf(marker);
        if (markerIndex == -1) {
            throw new RuntimeException("TTS response did not contain audio data: " + jsonResponse);
        }

        String key = "\"data\":";
        int keyIndex = jsonResponse.indexOf(key, markerIndex);
        if (keyIndex == -1) {
            throw new RuntimeException("TTS response missing data field: " + jsonResponse);
        }

        // move past the key and the opening quote, then read until the closing quote
        int start = jsonResponse.indexOf('"', keyIndex + key.length()) + 1;
        int end = jsonResponse.indexOf('"', start);

        return jsonResponse.substring(start, end);
    }

    // writes raw PCM bytes to disk as a valid WAV file by prepending the standard 44-byte header
    private void writeWavFile(byte[] pcmBytes, String path) throws Exception {
        int dataSize = pcmBytes.length;
        int byteRate = SAMPLE_RATE * CHANNELS * BITS_PER_SAMPLE / 8;
        int blockAlign = CHANNELS * BITS_PER_SAMPLE / 8;

        FileOutputStream fos = new FileOutputStream(path);

        // RIFF header
        fos.write(new byte[]{'R', 'I', 'F', 'F'});
        writeInt32LE(fos, 36 + dataSize);  // total file size minus the 8 bytes for "RIFF" and the size field itself
        fos.write(new byte[]{'W', 'A', 'V', 'E'});

        // fmt chunk
        fos.write(new byte[]{'f', 'm', 't', ' '});
        writeInt32LE(fos, 16);             // fmt chunk is always 16 bytes for PCM
        writeInt16LE(fos, 1);             // audio format 1 = PCM (uncompressed)
        writeInt16LE(fos, CHANNELS);
        writeInt32LE(fos, SAMPLE_RATE);
        writeInt32LE(fos, byteRate);
        writeInt16LE(fos, blockAlign);
        writeInt16LE(fos, BITS_PER_SAMPLE);

        // data chunk
        fos.write(new byte[]{'d', 'a', 't', 'a'});
        writeInt32LE(fos, dataSize);
        fos.write(pcmBytes);

        fos.close();
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

    // writes a 32-bit integer in little-endian byte order (WAV requires little-endian)
    private void writeInt32LE(FileOutputStream fos, int value) throws Exception {
        fos.write(value & 0xFF);
        fos.write((value >> 8) & 0xFF);
        fos.write((value >> 16) & 0xFF);
        fos.write((value >> 24) & 0xFF);
    }

    // writes a 16-bit integer in little-endian byte order
    private void writeInt16LE(FileOutputStream fos, int value) throws Exception {
        fos.write(value & 0xFF);
        fos.write((value >> 8) & 0xFF);
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
