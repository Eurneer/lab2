import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class SeparateColorChannels {

    public static void decomposeRGB(ImageView originalImageView) {
        // Получаем изображение из ImageView
        Image originalImage = originalImageView.getImage();

        if (originalImage == null) {
            System.out.println("Изображение не было загружено");
            return;
        }

        // Получаем ширину и высоту изображения
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        // Создаем изображение для каждого цветового канала
        WritableImage redChannel = new WritableImage(width, height);
        WritableImage greenChannel = new WritableImage(width, height);
        WritableImage blueChannel = new WritableImage(width, height);

        // Цикл по пикселям изображения
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // Получаем цвет пикселя
                Color pixelColor = originalImage.getPixelReader().getColor(i, j);

                // Извлекаем компоненты цвета
                double red = pixelColor.getRed();
                double green = pixelColor.getGreen();
                double blue = pixelColor.getBlue();

                // Создаем новые цвета для каждого канала
                Color redColor = new Color(red, 0, 0, 1);
                Color greenColor = new Color(0, green, 0, 1);
                Color blueColor = new Color(0, 0, blue, 1);

                // Устанавливаем цвета для соответствующих каналов
                redChannel.getPixelWriter().setColor(i, j, redColor);
                greenChannel.getPixelWriter().setColor(i, j, greenColor);
                blueChannel.getPixelWriter().setColor(i, j, blueColor);
            }
        }


        // Сохраняем разложенные каналы RGB в файлы
        saveImage(redChannel, "red_channel.jpg");
        saveImage(greenChannel, "green_channel.jpg");
        saveImage(blueChannel, "blue_channel.jpg");

        System.out.println("Каналы RGB успешно сохранены в файлы.");
    }

    // Метод для сохранения изображения в файл
    private static void saveImage(WritableImage image, String filename) {
        File file = new File(filename);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении изображения: " + e.getMessage());
        }
    }
}