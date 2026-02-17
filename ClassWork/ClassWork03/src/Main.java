import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        drawTriangles();
        drawLandscape(10);
    }


    public static void drawTriangles(){
        //single string since we dont calculate anything here, just convert

        String svgContent = """
<svg version="1.1" width="400" height="300" xmlns="http://www.w3.org/2000/svg">

    <rect width="400" height="300" fill="blue"></rect>

    <polygon points="0,0 400,0 400,300" fill="red"></polygon>

</svg>
""";

        saveSVG(svgContent, "triangles");
    }



    //function to draw second image, calculate the ray angles inside the function, then build svg format
    public static void drawLandscape(int rays){

        //white background and sun, hardcoded sun coords
        int width = 400;
        int height = 300;

        int radius = 40;
        int centerX = 80;
        int centerY = 80;

        int gap = 5;
        int rayLength = 15;

        //start and end positions
        double r1 = radius + gap;
        double r2 = radius + gap + rayLength;

        StringBuilder builder = new StringBuilder();

        //open svg + background
        builder.append("""
<svg version="1.1"
     width="400" height="300"
     xmlns="http://www.w3.org/2000/svg">

  <rect width="400" height="300" fill="white"></rect>

  <circle cx="80" cy="80" r="40" fill="yellow"></circle>

""");

        //calculate ray angles and draw them
        for (int i = 0; i < rays; i++) {

            double angle = i * (2 * Math.PI / rays);

            int startX = (int) (centerX + r1 * Math.cos(angle));
            int startY = (int) (centerY + r1 * Math.sin(angle));

            int endX = (int) (centerX + r2 * Math.cos(angle));
            int endY = (int) (centerY + r2 * Math.sin(angle));

            builder.append(String.format(
                    "  <line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"orange\" stroke-width=\"3\"></line>\n",
                    startX, startY, endX, endY
            ));
        }

        //grass using cosine function with svg path

        int baseY = 220;      //grass base height
        int amplitude = 25;   //height
        int period = 80;      //distance between peaks
        int step = 5;

        StringBuilder path = new StringBuilder();

        int firstY = (int) (baseY + amplitude * Math.cos(0));
        path.append(String.format("M %d %d ", 0, firstY));

        for (int x = 0; x <= width; x += step) {
            double y = baseY + amplitude * Math.cos((2 * Math.PI * x) / period);
            path.append(String.format("L %d %d ", x, (int) y));
        }

        path.append(String.format("L %d %d ", width, height));
        path.append(String.format("L %d %d Z", 0, height));

        builder.append(String.format(
                "  <path d=\"%s\" fill=\"lime\"></path>\n",
                path.toString()
        ));

        //close svg
        builder.append("</svg>");

        saveSVG(builder.toString(), "landscape");
    }







    public static void saveSVG(String svgContent,  String filename){
        Path path = Path.of(filename + ".svg");
        try {
            Files.writeString(path, svgContent, StandardCharsets.UTF_8); //last parameter to properly format and prevent errors
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}

