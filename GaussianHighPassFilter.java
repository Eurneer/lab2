import java.awt.image.BufferedImage;
import java.io.IOException;

public class GaussianHighPassFilter {

    public static void main(String[] args) throws IOException {
        // Сохранение и дальнейшие действия с результатом
    }

    public static BufferedImage[] applyGaussianHighPassFilter(
            double[][] redRe, double[][] redIm,
            double[][] greenRe, double[][] greenIm,
            double[][] blueRe, double[][] blueIm, double D0) {
        int height = 256;
        int width = 256;

        BufferedImage redImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImage greenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImage blueImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int u = 0; u < height; u++) {
            for (int v = 0; v < width; v++) {
                //double Duvred = Math.sqrt(Math.pow(u - height / 2.0, 2) + Math.pow(v - height / 2.0, 2));
                double Duvred = Math.sqrt(Math.pow((u-(height/2)),2)+(Math.pow((v-(width/2)),2)));
                //double Huvred = 1 - Math.exp(-Math.pow(Duvred, 2) / (2 * Math.pow(D0, 2)));
                double Huvred = 1 / (Math.pow((D0) / ((Duvred)),2));
                redRe[u][v] *= Huvred;
                redIm[u][v] *= Huvred;

                double Duvgreen = Math.sqrt(Math.pow(u - width / 2.0, 2) + Math.pow(v - width / 2.0, 2));
                double Huvgreen = 1 - Math.exp(-Math.pow(Duvgreen, 2) / (2 * Math.pow(D0, 2)));
                greenRe[u][v] *= (1 - Huvgreen);
                greenIm[u][v] *= (1 - Huvgreen);

                double Duvblue = Math.sqrt(Math.pow(u - height / 2.0, 2) + Math.pow(v - width / 2.0, 2));
                double Huvblue = 1 - Math.exp(-Math.pow(Duvblue, 2) / (2 * Math.pow(D0, 2)));
                blueRe[u][v] *= (1 - Huvblue);
                blueIm[u][v] *= (1 - Huvblue);
            }
        }

          // Сборка изображения для каждого канала
          for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = (int) Math.min(Math.max(redRe[y][x], 0), 255);
                int g = (int) Math.min(Math.max(greenRe[y][x], 0), 255);
                int b = (int) Math.min(Math.max(blueRe[y][x], 0), 255);

                redImage.setRGB(x, y, (r << 16) | (0 << 8) | 0); // Красный канал
                greenImage.setRGB(x, y, (0 << 16) | (g << 8) | 0); // Зеленый канал
                blueImage.setRGB(x, y, (0 << 16) | (0 << 8) | b); // Синий канал
            }
        }

        return new BufferedImage[] { redImage, greenImage, blueImage };

        
    }

    
}

// import java.awt.image.BufferedImage;
// import java.io.File;
// import java.io.IOException;
// // import java.io.File;
// // import javax.imageio.ImageIO;

// import javax.imageio.ImageIO;

// public class GaussianHighPassFilter {

// public static void main(String[] args) throws IOException {
// // Задаем размеры массива
// int width = 256; // Ширина изображения
// int height = 256; // Высота изображения

// // Создаем массивы для действительной и мнимой частей
// double[][] redRe = new double[height][width];
// double[][] redIm = new double[height][width];

// double[][] greenRe = new double[height][width];
// double[][] greenIm = new double[height][width];

// double[][] blueRe = new double[height][width];
// double[][] blueIm = new double[height][width];

// // Здесь вы можете заполнить массивы реальных и мнимых частей данными

// // Применяем гауссов фильтр
// double D0 = 30.0;
// BufferedImage[] resultImages = applyGaussianHighPassFilter(redRe, redIm,
// greenRe, greenIm, blueRe, blueIm, D0);

// // Save the images (optional)
// ImageIO.write(resultImages[0], "png", new File("red_filtered.png"));
// ImageIO.write(resultImages[1], "png", new File("green_filtered.png"));
// ImageIO.write(resultImages[2], "png", new File("blue_filtered.png"));

// System.out.println("Фильтр Гаусса применен к изображению.");

// // Здесь можно выполнить дальнейшие действия с обработанными данными,
// // например, сохранить их в файл или выполнить другие операции обработки
// }

// public static BufferedImage[] applyGaussianHighPassFilter(
// double[][] redRe, double[][] redIm,
// double[][] greenRe, double[][] greenIm,
// double[][] blueRe, double[][] blueIm, double D0) {
// int height = 256;
// int width = 256;

// // BufferedImage redImage = new BufferedImage(width, height,
// BufferedImage.TYPE_INT_RGB);
// // BufferedImage greenImage = new BufferedImage(width, height,
// BufferedImage.TYPE_INT_RGB);
// // BufferedImage blueImage = new BufferedImage(width, height,
// BufferedImage.TYPE_INT_RGB);

// for (int u = 0; u < height; u++) {
// for (int v = 0; v < width; v++) {
// double Duvred = Math.sqrt(Math.pow(u - height / 2.0, 2) + Math.pow(v - width
// / 2.0, 2));
// double Huvred = 1 - Math.exp(-Math.pow(Duvred, 2) / (2 * Math.pow(D0, 2)));
// redRe[u][v] *= (1 - Huvred); // high-pass filter
// redIm[u][v] *= (1 - Huvred); // high-pass filter

// double Duvgreen = Math.sqrt(Math.pow(u - height / 2.0, 2) + Math.pow(v -
// width / 2.0, 2));
// double Huvgreen = 1 - Math.exp(-Math.pow(Duvgreen, 2) / (2 * Math.pow(D0,
// 2)));
// greenRe[u][v] *= (1 - Huvgreen); // high-pass filter
// greenIm[u][v] *= (1 - Huvgreen); // high-pass filter

// double Duvblue = Math.sqrt(Math.pow(u - height / 2.0, 2) + Math.pow(v - width
// / 2.0, 2));
// double Huvblue = 1 - Math.exp(-Math.pow(Duvblue, 2) / (2 * Math.pow(D0, 2)));
// blueRe[u][v] *= (1 - Huvblue); // high-pass filter
// blueIm[u][v] *= (1 - Huvblue); // high-pass filter
// }
// }

// // Добавление сообщения о выполненной работе
// System.out.println("Фильтр Гаусса применен к изображению.");
// return null;

// }
// }

// import javax.imageio.ImageIO;
// import java.awt.image.BufferedImage;
// import java.io.File;
// import java.io.IOException;

// public class GaussianHighPassFilter {

// public static void main(String[] args) throws IOException {
// // Путь к входному и выходному файлам изображения
// String inputImagePath = "spectrum_output.bmp";
// String outputImagePath = "output_Gaysfiltered.bmp";

// // Чтение изображения
// BufferedImage inputImage = ImageIO.read(new File(inputImagePath));

// // Преобразование изображения в массив комплексных чисел
// int width = inputImage.getWidth();
// int height = inputImage.getHeight();
// double[][] real = new double[height][width];
// double[][] imaginary = new double[height][width];

// for (int y = 0; y < height; y++) {
// for (int x = 0; x < width; x++) {
// int rgb = inputImage.getRGB(x, y);
// real[y][x] = ((rgb >> 16) & 0xFF) / 255.0; // предположим, что реальная часть
// в красном канале
// imaginary[y][x] = ((rgb >> 8) & 0xFF) / 255.0; // предположим, что мнимая
// часть в зеленом канале
// }
// }

// // Применение гауссового фильтра высоких частот
// double D0 = 30.0;
// applyGaussianHighPassFilter(real, imaginary, D0);

// // Преобразование обратно в изображение (для визуализации сохраняем только
// // действительную часть)
// BufferedImage outputImage = new BufferedImage(width, height,
// BufferedImage.TYPE_INT_RGB);
// for (int y = 0; y < height; y++) {
// for (int x = 0; x < width; x++) {
// int r = (int) (Math.min(Math.max(real[y][x] * 255, 0), 255));
// int g = (int) (Math.min(Math.max(imaginary[y][x] * 255, 0), 255));
// int rgb = (r << 16) | (g << 8);
// outputImage.setRGB(x, y, rgb);
// }
// }

// // Запись выходного изображения
// ImageIO.write(outputImage, "bmp", new File(outputImagePath));
// }

// private static void applyGaussianHighPassFilter(double[][] real, double[][]
// imaginary, double D0) {
// int height = real.length;
// int width = real[0].length;

// for (int u = 0; u < height; u++) {
// for (int v = 0; v < width; v++) {
// double Duv = Math.sqrt(Math.pow(u - height / 2.0, 2) + Math.pow(v - width /
// 2.0, 2));
// double Huv = 1 - Math.exp(-Math.pow(Duv, 2) / (2 * Math.pow(D0, 2)));
// real[u][v] *= (1 - Huv); // high-pass filter
// imaginary[u][v] *= (1 - Huv); // high-pass filter
// }
// }
// }
// }
