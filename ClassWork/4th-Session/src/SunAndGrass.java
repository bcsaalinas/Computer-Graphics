import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class  SunAndGrass {
    public static void main(String[] args){


        int width = 400;
        int height = 300;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // sun setup
        int sunX = 100;
        int sunY = 70;
        int sunRadius = 40;
        int rayLength = 35;

        // grass wave
        double waveBottom = height * 0.75;
        double waveSize = 15.0;
        double waveFrequency = 80.0;

        // the 8 ray angles
        double[] rayAngles = new double[8];
        for(int r = 0; r < 8; r++){
            rayAngles[r] = r * (Math.PI / 4);
        }

        // draw pixel by pixel
        for(int i = 0; i < width; i++){

            // grass wave height at this x position
            double grassY = waveBottom + waveSize * Math.sin((2 * Math.PI * i) / waveFrequency);

            for(int j = 0; j < height; j++){

                // check grass area first
                if(j >= grassY){
                    image.setRGB(i, j, Color.GREEN.getRGB());
                }
                else {

                    // sky area check sun and rays
                    int dx = i - sunX;
                    int dy = j - sunY;
                    double dist = Math.sqrt(dx*dx + dy*dy);

                    // inside sun
                    if(dist <= sunRadius){
                        image.setRGB(i, j, Color.YELLOW.getRGB());
                    }
                    // in ray zone
                    else if(dist > sunRadius && dist <= sunRadius + rayLength){
                        // what angle pixel at
                        double pixelAngle = Math.atan2(dy, dx);

                        // normalize to 0 to 2*pi
                        if(pixelAngle < 0){
                            pixelAngle += 2 * Math.PI;
                        }

                        // check if close to any ray angle
                        boolean onRay = false;
                        for(int r = 0; r < 8; r++){
                            double rayAngle = rayAngles[r];
                            if(rayAngle < 0){
                                rayAngle += 2 * Math.PI;
                            }

                            double angleDiff = Math.abs(pixelAngle - rayAngle);

                            if(angleDiff > Math.PI){
                                angleDiff = 2 * Math.PI - angleDiff;
                            }

                            double thickness = Math.atan2(2.5, dist);

                            if(angleDiff < thickness){
                                onRay = true;
                                break;
                            }
                        }

                        if(onRay){
                            image.setRGB(i, j, new Color(200, 120, 120).getRGB());
                        } else {
                            image.setRGB(i, j, Color.WHITE.getRGB());
                        }
                    }
                    else {
                        // not sun, not rays then white sky
                        image.setRGB(i, j, Color.WHITE.getRGB());
                    }
                }
            }
        }
        try{
            ImageIO.write(image, "png", new File("sun_grass.png"));
            System.out.println("image saved");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}