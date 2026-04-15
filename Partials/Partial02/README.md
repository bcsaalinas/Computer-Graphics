# Video Creator
Java pipeline that turns a collection of geotagged photos and videos into a narrated vertical travel video.

## Dependencies
The following tools must be installed and available in your system `PATH`:

- [ExifTool](https://exiftool.org) — metadata extraction
- [FFmpeg](https://ffmpeg.org) — media conversion and video assembly

## Environment
```bash
export GEMINI_API_KEY="your_key_here"
```

## Build
```bash
cd Partial02
mkdir -p out
javac -d out src/edu/up/cg/**/*.java src/edu/up/cg/Main.java
```

## Run
```bash
java -cp out edu.up.cg.Main /absolute/path/to/input /absolute/path/to/output.mp4
```

If the output path is omitted, the program writes `travel_video.mp4` inside the input directory.

## Supported formats
| Type   | Formats         |
|--------|-----------------|
| Images | `.jpg`, `.jpeg`, `.png` |
| Videos | `.mp4`, `.mov`  |

> **Note:** HEIC images are not supported. Convert them to JPEG before use.
> - **Mac:** right-click → Quick Actions → Convert Image → JPEG
> - **Windows:** open in Photos app → Save as → JPEG

## How it works
1. Reads GPS and date metadata from each file using ExifTool
2. Converts all media to JPEG/MP4 using FFmpeg
3. Sorts media oldest to newest by EXIF date
4. Generates an AI intro image using the Gemini API
5. Generates voice descriptions for each media item
6. Renders a portrait video (1080×1920) with narration
7. Closes with a map showing first and last GPS location and an AI-generated inspirational phrase