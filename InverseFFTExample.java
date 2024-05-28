
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class InverseFFTExample {

 


    // Метод для вычисления обратного преобразования Фурье и создания изображения
    public static BufferedImage[] computeInverseFFT(double[][] redPartRe, double[][] redPartIm, 
    double[][] greenPartRe,  double[][] greenPartIm,
    double[][] bluePartRe, double[][] bluePartIm) {
        int width = 256;
        int height = 256;

        double[][] redPartOutRe = new double[width][height];
        double[][] redPartOutIm = new double[width][height];
        double[][] redFinalRe = new double[width][height];
        double[][] redFinalIm = new double[width][height];

        double[][] greenPartOutRe = new double[width][height];
        double[][] greenPartOutIm = new double[width][height];
        double[][] greenFinalRe = new double[width][height];
        double[][] greenFinalIm = new double[width][height];

        double[][] bluePartOutRe = new double[width][height];
        double[][] bluePartOutIm = new double[width][height];
        double[][] blueFinalRe = new double[width][height];
        double[][] blueFinalIm = new double[width][height];

        // обратное Фурье по строкам
        for (int k = 0; k < height; k++) {
            for (int j = 0; j < width; j++) {
                for (int i = 0; i < width; i++) {
                    double angle = 2 * Math.PI * i * j / width;
                    redPartOutRe[j][k] += redPartRe[i][k] * Math.cos(angle) + redPartIm[i][k] * Math.sin(angle);
                    redPartOutIm[j][k] += redPartRe[i][k] * Math.sin(angle) - redPartIm[i][k] * Math.cos(angle);

                    greenPartOutRe[j][k] += greenPartRe[i][k] * Math.cos(angle) + greenPartIm[i][k] * Math.sin(angle);
                    greenPartOutIm[j][k] += greenPartRe[i][k] * Math.sin(angle) - greenPartIm[i][k] * Math.cos(angle);

                    bluePartOutRe[j][k] += bluePartRe[i][k] * Math.cos(angle) + bluePartIm[i][k] * Math.sin(angle);
                    bluePartOutIm[j][k] += bluePartRe[i][k] * Math.sin(angle) - bluePartIm[i][k] * Math.cos(angle);


                   

                }
            }
        }

        // обратное Фурье по столбцам
        for (int k = 0; k < width; k++) {
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < height; i++) {
                    double angle = 2 * Math.PI * i * j / height;
                    redFinalRe[k][j] += redPartOutRe[k][i] * Math.cos(angle) + redPartOutIm[k][i] * Math.sin(angle);
                    redFinalIm[k][j] += redPartOutRe[k][i] * Math.sin(angle) - redPartOutIm[k][i] * Math.cos(angle);

                    greenFinalRe[k][j] += greenPartOutRe[k][i] * Math.cos(angle) + greenPartOutIm[k][i] * Math.sin(angle);
                    greenFinalIm[k][j] += greenPartOutRe[k][i] * Math.sin(angle)- greenPartOutIm[k][i] * Math.cos(angle);

                    blueFinalRe[k][j] += bluePartOutRe[k][i] * Math.cos(angle) + bluePartOutIm[k][i] * Math.sin(angle);
                    blueFinalIm[k][j] += bluePartOutRe[k][i] * Math.sin(angle) - bluePartOutIm[k][i] * Math.cos(angle);

              

                }
            }
        }

          // Нормализация по длине исходной области
          double normalizationFactor = height * width;
          for (int i = 0; i < height; i++) {
              for (int j = 0; j < width; j++) {
                  redFinalRe[i][j] /= normalizationFactor;
                  greenFinalRe[i][j] /= normalizationFactor;
                  blueFinalRe[i][j] /= normalizationFactor;
  
              }
          }
  
          for (int i = 0; i < height; i++) {
              for (int j = 0; j < width; j++) {
                  redFinalRe[i][j] *= Math.pow(-1, i + j);
                  greenFinalRe[i][j] *= Math.pow(-1, i + j);
                  blueFinalRe[i][j] *= Math.pow(-1, i + j);
  
              }
          }
  
          double maxRed = 0;
          double minRed = 0;
          double maxGreen = 0;
          double minGreen = 0;
          double maxBlue = 0;
          double minBlue = 0;

          for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                double bufRed = redFinalRe[i][j];
                double bufGreen = greenFinalRe[i][j];
                double bufBlue = blueFinalRe[i][j];

                if (bufRed > maxRed) {
                    maxRed = bufRed;
                }
                if (bufRed < minRed) {
                    minRed = bufRed;
                }

                if (bufGreen > maxGreen) {
                    maxGreen = bufGreen;
                }
                if (bufGreen < minGreen) {
                    minGreen = bufGreen;
                }

                if (bufBlue > maxBlue) {
                    maxBlue = bufBlue;
                }
                if (bufBlue < minBlue) {
                    minBlue = bufBlue;
                }

            }
        }
        // Создание изображения только с красным каналом
     
        BufferedImage redImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImage greenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImage blueImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);


        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int redValue = (int) Math.round((redFinalRe[i][j] - minRed) / (maxRed - minRed) * 255);
                redValue = Math.max(0, Math.min(255, redValue));
                // Установка пикселя для соответствующего изображения
                redImage.setRGB(width - 1 - i, height - 1 - j, (redValue << 16)); // Красный канал в наиболее значимом

                int greenValue = (int) Math.round((greenFinalRe[i][j] - minGreen) / (maxGreen - minGreen) * 255);
                greenValue = Math.max(0, Math.min(255, greenValue));
                // Установка пикселя для соответствующего изображения
                greenImage.setRGB(width - 1 - i, height - 1 - j, (greenValue << 8)); // Красный канал в наиболее
                                                                                     // значимом

                int blueValue = (int) Math.round((blueFinalRe[i][j] - minBlue) / (maxBlue - minBlue) * 255);
                blueValue = Math.max(0, Math.min(255, blueValue));
                // Установка пикселя для соответствующего изображения
                blueImage.setRGB(width - 1 - i, height - 1 - j, (blueValue << 0)); // Красный канал в наиболее значимом
                                                                                   // // байте
            }
        }

        return new BufferedImage[] { redImage, greenImage, blueImage };
    }

    // Метод для сохранения изображения
    public static void saveImage(BufferedImage image, String outputPath) {
        File outputFile = new File(outputPath);
        try {
            ImageIO.write(image, "bmp", outputFile);
            System.out.println("Image saved successfully: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   
}