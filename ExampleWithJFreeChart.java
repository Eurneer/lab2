import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ExampleWithJFreeChart {

    public static void main(String[] args) throws IOException {
        // Путь к входному и выходному файлам изображения
        String inputImagePath = "spectrum_output.bmp";
        String outputImagePath = "output_filtered.bmp";

        // Чтение изображения
        BufferedImage inputImage = ImageIO.read(new File(inputImagePath));

        // Преобразование изображения в массив комплексных чисел
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        double[][] real = new double[height][width];
        double[][] imaginary = new double[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = inputImage.getRGB(x, y);
                real[y][x] = ((rgb >> 16) & 0xFF) / 255.0; // предположим, что реальная часть в красном канале
                imaginary[y][x] = ((rgb >> 8) & 0xFF) / 255.0; // предположим, что мнимая часть в зеленом канале
            }
        }

        // Применение фильтра высоких частот Баттерворта
        int n = 2;
        double D0 = 30;
        applyButterworthHighPassFilter(real, imaginary, n, D0);

        // Преобразование обратно в изображение (для визуализации сохраняем только действительную часть)
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = (int) (Math.min(Math.max(real[y][x] * 255, 0), 255));
                int g = (int) (Math.min(Math.max(imaginary[y][x] * 255, 0), 255));
                int rgb = (r << 16) | (g << 8);
                outputImage.setRGB(x, y, rgb);
            }
        }

        // Запись выходного изображения
        ImageIO.write(outputImage, "bmp", new File(outputImagePath));
    }

    private static void applyButterworthHighPassFilter(double[][] real, double[][] imaginary, int n, double D0) {
        //массив действительной части
        int height = real.length;
        //возвращение длинны первого подмассива
        int width = real[0].length;

        for (int u = 0; u < height; u++) {
            for (int v = 0; v < width; v++) {
                double Duv = Math.sqrt(Math.pow(u - height / 2.0, 2) + Math.pow(v - width / 2.0, 2));
                double Huv = 1 / (1 + Math.pow(D0 / Duv, 2 * n));
                real[u][v] *= (1-Huv);
                imaginary[u][v] *= (1-Huv);
            }
        }
    }
}
