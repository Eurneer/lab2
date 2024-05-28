import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SpectrumProcessor {
    public static final String INPUT_IMAGE_PATH = "kat256.bmp";
    public static final String OUTPUT_SPECTRUM_IMAGE_PATH = "path_to_output_spectrum_image.bmp";

    public static void main(String[] args) {
        try {
            // Загрузка изображения
            BufferedImage inputImage = ImageIO.read(new File(INPUT_IMAGE_PATH));

            // Применение преобразования Фурье и обработка спектра
            BufferedImage processedImage = processImage(inputImage);

            // Сохранение конечного спектра
            saveImage(processedImage, OUTPUT_SPECTRUM_IMAGE_PATH);

            System.out.println("Processed spectrum image saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для обработки изображения (применение преобразования Фурье и логарифмирование спектра)
    public static BufferedImage processImage(BufferedImage inputImage) throws IOException {
        int width = 256;
        int height = 256;

        double[][] redPart = new double[width][height];
        double[][] greenPart = new double[width][height];
        double[][] bluePart = new double[width][height];

        // Заполнение массивов цветовых каналов данными изображения с умножением на (-1)^(x+y)
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = inputImage.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                redPart[x][y] = red * Math.pow(-1, x + y);
                greenPart[x][y] = green * Math.pow(-1, x + y);
                bluePart[x][y] = blue * Math.pow(-1, x + y);
            }
        }

        // Применение преобразования Фурье к каждому цветовому каналу
        BufferedImage redSpectrumImage = computeFFT(redPart, bluePart);

        return redSpectrumImage;
    }

    public static BufferedImage computeFFT(double[][] realPart, double[][] imaginaryPart) {
        int width = realPart.length;
        int height = realPart[0].length;
    
        double[][] realPartRows = new double[width][height];
        double[][] imaginaryPartRows = new double[width][height];
        double[][] realPartColumns = new double[width][height];
        double[][] imaginaryPartColumns = new double[width][height];
    
        // Преобразование Фурье по строкам
        for (int k = 0; k < height; k++) {
            for (int j = 0; j < width; j++) {
                for (int i = 0; i < width; i++) {
                    double angle = 2 * Math.PI * i * j / width;
                    realPartRows[j][k] += realPart[i][k] * Math.cos(angle) - imaginaryPart[i][k] * Math.sin(angle);
                    imaginaryPartRows[j][k] += realPart[i][k] * Math.sin(angle) + imaginaryPart[i][k] * Math.cos(angle);
                }
            }
        }
    
        // Преобразование Фурье по столбцам
        for (int k = 0; k < width; k++) {
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < height; i++) {
                    double angle = 2 * Math.PI * i * j / height;
                    realPartColumns[k][j] += realPartRows[k][i] * Math.cos(angle) - imaginaryPartRows[k][i] * Math.sin(angle);
                    imaginaryPartColumns[k][j] += realPartRows[k][i] * Math.sin(angle) + imaginaryPartRows[k][i] * Math.cos(angle);
                }
            }
        }
    
        // Логарифмирование спектра и нормализация
        double maxRed = Double.NEGATIVE_INFINITY;
        double minRed = Double.POSITIVE_INFINITY;
        double[][] logSpectrum = new double[width][height];
    
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                double re = realPartColumns[i][j];
                double im = imaginaryPartColumns[i][j];
                double magnitude = Math.sqrt(re * re + im * im);
                double bufRed = Math.log(magnitude + 1);
                logSpectrum[i][j] = bufRed;
    
                if (bufRed > maxRed) {
                    maxRed = bufRed;
                }
                if (bufRed < minRed) {
                    minRed = bufRed;
                }
            }
        }
    
        BufferedImage spectrumImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                double bufRed = logSpectrum[i][j];
                int redValue = (int) ((bufRed - minRed) / (maxRed - minRed) * 255);
                redValue = Math.max(0, Math.min(255, redValue)); // Ensure it's within [0, 255]
                Color color = new Color(redValue, 0, 0);
                spectrumImage.setRGB(i, j, color.getRGB());
            }
        }
    
        return spectrumImage;
    }

    // Метод для сохранения изображения
    public static void saveImage(BufferedImage image, String outputPath) throws IOException {
        File outputfile = new File(outputPath);
        ImageIO.write(image, "bmp", outputfile);
    }
}
