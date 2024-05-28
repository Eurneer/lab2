import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class FourierTransform {

    private static final String SPECTRUM_IMAGE_PATH_RED = "spectrum_output_red.bmp";
    private static final String SPECTRUM_IMAGE_PATH_GREEN = "spectrum_output_green.bmp";
    private static final String SPECTRUM_IMAGE_PATH_BLUE = "spectrum_output_blue.bmp";
    // private static final String OUTPUT_RECONSTRUCTED_IMAGE_PATH_RED = "reconstructed_red_output.bmp";
    // private static final String OUTPUT_RECONSTRUCTED_IMAGE_PATH_GREEN = "reconstructed_green_output.bmp";
    // private static final String OUTPUT_RECONSTRUCTED_IMAGE_PATH_BLUE = "reconstructed_blue_output.bmp";

    public static void performFourierTransform(BufferedImage originalImage) throws IOException {
        double[][][][] spectrum = processImage(originalImage);
        double[][][] spectrumImageRed = spectrum[0];
        double[][][] spectrumImageGreen = spectrum[1];
        double[][][] spectrumImageBlue = spectrum[2];

        // BufferedImage[] reconstructedImages = computeInverseFFT(
        //         spectrumImageRed[0], spectrumImageRed[1],
        //         spectrumImageGreen[0], spectrumImageGreen[1],
        //         spectrumImageBlue[0], spectrumImageBlue[1]);

        // saveImage(reconstructedImages[0], OUTPUT_RECONSTRUCTED_IMAGE_PATH_RED);
        // saveImage(reconstructedImages[1], OUTPUT_RECONSTRUCTED_IMAGE_PATH_GREEN);
        // saveImage(reconstructedImages[2], OUTPUT_RECONSTRUCTED_IMAGE_PATH_BLUE);

        System.out.println("Reconstructed image saved successfully.");
    }

    // Метод для обработки изображения (применение преобразования Фурье и
    // логарифмирование спектра)
    public static double[][][][] processImage(BufferedImage inputImage) throws IOException {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();

        double[][] redPartRe = new double[width][height];
        double[][] redPartIm = new double[width][height];

        double[][] greenPartRe = new double[width][height];
        double[][] greenPartIm = new double[width][height];

        double[][] bluePartRe = new double[width][height];
        double[][] bluePartIm = new double[width][height];

        // Заполнение массивов цветовых каналов данными изображения с умножением на
        // (-1)^(x+y)
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = inputImage.getRGB(i, j);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = (rgb >> 0) & 0xFF;

                redPartRe[i][j] = red * Math.pow(-1, i + j);
                redPartIm[i][j] = 0.0;
                greenPartRe[i][j] = green * Math.pow(-1, i + j);
                greenPartIm[i][j] = 0.0;
                bluePartRe[i][j] = blue * Math.pow(-1, i + j);
                bluePartIm[i][j] = 0.0;

            }
        }

        // Применение преобразования Фурье к каждому цветовому каналу
        double[][][][] spectreImages = computeFFT(redPartRe, redPartIm, greenPartRe, greenPartIm, bluePartRe,
                bluePartIm);

        return spectreImages;
    }

    public static double[][][][] computeFFT(
            double[][] redPartRe, double[][] redPartIm,
            double[][] greenPartRe, double[][] greenPartIm,
            double[][] bluePartRe, double[][] bluePartIm) {
        int width = 256;
        int height = 256;

        double[][] redPartOutRe = new double[width][height];
        double[][] redPartOutIm = new double[width][height];
        double[][] redSpecRe = new double[width][height];
        double[][] redSpecIm = new double[width][height];

        double[][] greenPartOutRe = new double[width][height];
        double[][] greenPartOutIm = new double[width][height];
        double[][] greenSpecRe = new double[width][height];
        double[][] greenSpecIm = new double[width][height];

        double[][] bluePartOutRe = new double[width][height];
        double[][] bluePartOutIm = new double[width][height];
        double[][] blueSpecRe = new double[width][height];
        double[][] blueSpecIm = new double[width][height];

        // Преобразование Фурье по строкам
        for (int k = 0; k < height; k++) {
            for (int j = 0; j < width; j++) {
                for (int i = 0; i < width; i++) {
                    double angle = (2 * Math.PI * i * j) / width;
                    redPartOutRe[j][k] += redPartRe[i][k] * Math.cos(angle) + redPartIm[i][k] * Math.sin(angle);
                    redPartOutIm[j][k] += redPartRe[i][k] * Math.sin(angle) - redPartIm[i][k] * Math.cos(angle);

                    greenPartOutRe[j][k] += greenPartRe[i][k] * Math.cos(angle) + greenPartIm[i][k] * Math.sin(angle);
                    greenPartOutIm[j][k] += greenPartRe[i][k] * Math.sin(angle) - greenPartIm[i][k] * Math.cos(angle);

                    bluePartOutRe[j][k] += bluePartRe[i][k] * Math.cos(angle) + bluePartIm[i][k] * Math.sin(angle);
                    bluePartOutIm[j][k] += bluePartRe[i][k] * Math.sin(angle) - bluePartIm[i][k] * Math.cos(angle);

                }
            }
        }

        // Преобразование Фурье по столбцам
        for (int k = 0; k < width; k++) {
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < height; i++) {
                    double angle = (2 * Math.PI * i * j) / height;
                    redSpecRe[k][j] += redPartOutRe[k][i] * Math.cos(angle) + redPartOutIm[k][i] * Math.sin(angle);
                    redSpecIm[k][j] += redPartOutRe[k][i] * Math.sin(angle) - redPartOutIm[k][i] * Math.cos(angle);

                    greenSpecRe[k][j] += greenPartOutRe[k][i] * Math.cos(angle)
                            + greenPartOutIm[k][i] * Math.sin(angle);
                    greenSpecIm[k][j] += greenPartOutRe[k][i] * Math.sin(angle)
                            - greenPartOutIm[k][i] * Math.cos(angle);

                    blueSpecRe[k][j] += bluePartOutRe[k][i] * Math.cos(angle) + bluePartOutIm[k][i] * Math.sin(angle);
                    blueSpecIm[k][j] += bluePartOutRe[k][i] * Math.sin(angle) - bluePartOutIm[k][i] * Math.cos(angle);
                }
            }
        }

        // Логарифмирование спектра и нормализация
        double maxRed = 0;
        double minRed = 0;
        double maxGreen = 0;
        double minGreen = 0;
        double maxBlue = 0;
        double minBlue = 0;

        double[][] logSpectrumRed = new double[width][height];
        double[][] logSpectrumGreen = new double[width][height];
        double[][] logSpectrumBlue = new double[width][height];

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                double reRed = redSpecRe[i][j];
                double imRed = redSpecIm[i][j];
                double magnitudeRed = Math.sqrt(reRed * reRed + imRed * imRed);
                double reGreen = greenSpecRe[i][j];
                double imGreen = greenSpecIm[i][j];
                double magnitudeGreen = Math.sqrt(reGreen * reGreen + imGreen * imGreen);
                double reBlue = blueSpecRe[i][j];
                double imBlue = blueSpecIm[i][j];
                double magnitudeBlue = Math.sqrt(reBlue * reBlue + imBlue * imBlue);

                double bufRed = Math.log(magnitudeRed + 1);
                double bufGreen = Math.log(magnitudeGreen + 1);
                double bufBlue = Math.log(magnitudeBlue + 1);
                logSpectrumRed[i][j] = bufRed;
                logSpectrumGreen[i][j] = bufGreen;
                logSpectrumBlue[i][j] = bufBlue;

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

        // Сохранение изображения спектра
        saveSpectrumImage(logSpectrumRed, logSpectrumGreen, logSpectrumBlue, width, height, maxRed, minRed, maxGreen,
                minGreen, maxBlue, minBlue);

        return new double[][][][] { { redSpecRe, redSpecIm }, { greenSpecRe, greenSpecIm },
                { blueSpecRe, blueSpecIm } };
    }

    public static void saveSpectrumImage(double[][] logSpectrumRed, double[][] logSpectrumGreen,
            double[][] logSpectrumBlue, int width, int height, double maxRed, double minRed, double maxGreen,
            double minGreen, double maxBlue, double minBlue) {
        BufferedImage spectrumImageRed = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImage spectrumImageGreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImage spectrumImageBlue = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int normalizedValueRed = (int) (255 * (logSpectrumRed[j][i] - minRed) / (maxRed - minRed));
                normalizedValueRed = Math.max(0, Math.min(255, normalizedValueRed));

                int normalizedValueGreen = (int) (255 * (logSpectrumGreen[j][i] - minGreen) / (maxGreen - minGreen));
                normalizedValueGreen = Math.max(0, Math.min(255, normalizedValueGreen));

                int normalizedValueBlue = (int) (255 * (logSpectrumBlue[j][i] - minBlue) / (maxBlue - minBlue));
                normalizedValueBlue = Math.max(0, Math.min(255, normalizedValueBlue));

                Color colorRed = new Color(normalizedValueRed, 0, 0);
                spectrumImageRed.setRGB(j, i, colorRed.getRGB());

                Color colorGreen = new Color(0, normalizedValueGreen, 0);
                spectrumImageGreen.setRGB(j, i, colorGreen.getRGB());

                Color colorBlue = new Color(0, 0, normalizedValueBlue);
                spectrumImageBlue.setRGB(j, i, colorBlue.getRGB());
            }
        }
        try {
            File outputFileRed = new File(SPECTRUM_IMAGE_PATH_RED);
            ImageIO.write(spectrumImageRed, "bmp", outputFileRed);
            File outputFileGreen = new File(SPECTRUM_IMAGE_PATH_GREEN);
            ImageIO.write(spectrumImageGreen, "bmp", outputFileGreen);
            File outputFileBlue = new File(SPECTRUM_IMAGE_PATH_BLUE);
            ImageIO.write(spectrumImageBlue, "bmp", outputFileBlue);
            System.out.println("Spectrum image saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving spectrum image: " + e.getMessage());
        }

    }

    //Метод для вычисления обратного преобразования Фурье и создания изображения
    public static BufferedImage[] computeInverseFFT(double[][] redPartRe, double[][] redPartIm, double[][] greenPartRe,
            double[][] greenPartIm,
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

                    greenFinalRe[k][j] += greenPartOutRe[k][i] * Math.cos(angle)
                            + greenPartOutIm[k][i] * Math.sin(angle);
                    greenFinalIm[k][j] += greenPartOutRe[k][i] * Math.sin(angle)
                            - greenPartOutIm[k][i] * Math.cos(angle);

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
