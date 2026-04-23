# Video Creator

Java app that turns geotagged photos and videos into a narrated portrait travel video (1080×1920).

## Requirements

Install and make available in your `PATH`:

- [ExifTool](https://exiftool.org) — reads GPS and date metadata
- [FFmpeg](https://ffmpeg.org) — media conversion and video assembly

## Environment variables

```bash
export OPEN_API_KEY="your_openai_key"
export MAPBOX_TOKEN="your_mapbox_token"
```

## Supported formats

| Type   | Extensions |
|--------|------------|
| Images | `.jpg` `.jpeg` `.png` |
| Videos | `.mp4` `.mov` `.avi` `.mkv` `.wmv` `.m4v` |

## How it works

1. User selects media files via the GUI
2. ExifTool reads GPS coordinates and capture date from each file
3. Files are sorted oldest to newest
4. OpenAI GPT-4o describes each photo/video for narration
5. OpenAI TTS converts each description to audio (loudness-normalized to YouTube standards)
6. DALL-E 3 generates an opening image capturing the essence of the journey
7. Mapbox generates a map pinpointing the first and last GPS location
8. OpenAI generates an inspirational phrase for the map slide
9. FFmpeg assembles everything into a single portrait MP4

## Output

The final video and generated assets (`opening_image.png`, `map.png`) are saved in the same folder as the input files.
