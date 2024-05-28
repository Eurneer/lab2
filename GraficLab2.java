import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import javafx.scene.control.Button;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GraficLab2 extends Application {

    private ImageView originalImage;
    private ImageView redImageView;
    private ImageView greenImageView;
    private ImageView blueImageView;
    private ImageView redImageSpec;
    private ImageView greenImageSpec;
    private ImageView blueImageSpec;
    private ImageView redImageRec;
    private ImageView greenImageRec;
    private ImageView blueImageRec;
    private Stage primaryStage;
    private static final String OUTPUT_RECONSTRUCTED_IMAGE_PATH_RED = "reconstructed_red_output.bmp";
    private static final String OUTPUT_RECONSTRUCTED_IMAGE_PATH_GREEN = "reconstructed_green_output.bmp";
    private static final String OUTPUT_RECONSTRUCTED_IMAGE_PATH_BLUE = "reconstructed_blue_output.bmp";

    private static final String OUTPUT_FILTER_IMAGE_PATH_RED = "filter_red_output.bmp";
    private static final String OUTPUT_FILTER_IMAGE_PATH_GREEN = "filter_green_output.bmp";
    private static final String OUTPUT_FILTER_IMAGE_PATH_BLUE = "filter_blue_output.bmp";

    int height = 256;
    int width = 256;

    double[][] redPartRe = new double[width][height];
    double[][] redPartIm = new double[width][height];

    double[][] greenPartRe = new double[width][height];
    double[][] greenPartIm = new double[width][height];

    double[][] bluePartRe = new double[width][height];
    double[][] bluePartIm = new double[width][height];

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; // Сохраняем ссылку на primaryStage
        // Создаем кнопку для выбора изображения
        Button chooseImageButton = new Button("Выберите изображение");

        // Устанавливаем обработчик события при нажатии на кнопку
        chooseImageButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                chooseImage(primaryStage);
            }
        });

        Button RGBButton = new Button("Разложение на RGB");
        Button FurieButton = new Button("Применить Фурье");
        Button FilterButton = new Button("Применить Фильтр");
        Button ReturnButton = new Button("Вернуть изображение");

        // Создаем кнопку для разложения изображения на каналы RGB
        RGBButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (originalImage != null) {
                    SeparateColorChannels.decomposeRGB(originalImage);

                    // Создаем ImageView для каждого канала и отображаем его
                    redImageView = createImageView("red_channel.jpg");
                    greenImageView = createImageView("green_channel.jpg");
                    blueImageView = createImageView("blue_channel.jpg");

                    // Отображаем каждый канал в соответствующем ImageView
                    displayImage(redImageView, 100, 400);
                    displayImage(greenImageView, 300, 400);
                    displayImage(blueImageView, 500, 400);
                } else {
                    // Выводим сообщение об ошибке, если изображение не было загружено
                    showAlert("Изображение не было загружено", "Ошибка");
                }
            }
        });

        // Создаем кнопку для разложения изображения на каналы RGB
        FurieButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (originalImage != null) {
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(originalImage.getImage(), null);
                    try {
                        FourierTransform.performFourierTransform(bufferedImage);
                        // Создаем ImageView для каждого канала и отображаем его
                        redImageSpec = createImageView("spectrum_output_red.bmp");
                        greenImageSpec = createImageView("spectrum_output_green.bmp");
                        blueImageSpec = createImageView("spectrum_output_blue.bmp");

                        // Создаем ImageView для каждого канала и отображаем его
                        // redImageRec = createImageView("reconstructed_red_output.bmp");
                        // greenImageRec = createImageView("reconstructed_green_output.bmp");
                        // blueImageRec = createImageView("reconstructed_blue_output.bmp");

                        // Отображаем каждый канал в соответствующем ImageView
                        displayImage(redImageSpec, 100, 600);
                        displayImage(greenImageSpec, 300, 600);
                        displayImage(blueImageSpec, 500, 600);

                        // displayImage(redImageRec, 100, 800);
                        // displayImage(greenImageRec, 300, 800);
                        // displayImage(blueImageRec, 500, 800);
                    } catch (IOException e) {
                        showAlert("Ошибка при обработке изображения: " + e.getMessage(), "Ошибка");
                    }
                } else {
                    // Выводим сообщение об ошибке, если изображение не было загружено
                    showAlert("Изображение не было загружено", "Ошибка");
                }
            }
        });

        ReturnButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (originalImage != null) {
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(originalImage.getImage(), null);
                    try {
                        // Получаем спектр изображения
                        double[][][][] spectrum = FourierTransform.processImage(bufferedImage);
        
                        // Проверяем, что спектр получен правильно
                        if (spectrum != null && spectrum.length == 3 && spectrum[0].length == 2 &&
                                spectrum[1].length == 2 && spectrum[2].length == 2) {
        
                            // Извлекаем части спектра для каждого канала
                            double[][] redPartRe = spectrum[0][0];
                            double[][] redPartIm = spectrum[0][1];
                            double[][] greenPartRe = spectrum[1][0];
                            double[][] greenPartIm = spectrum[1][1];
                            double[][] bluePartRe = spectrum[2][0];
                            double[][] bluePartIm = spectrum[2][1];
        
                            // Задаем параметр D0 для фильтра
                            double D0 = 30.0;
        
                            // Применяем фильтр к спектру
                            BufferedImage[] filteredImages = GaussianHighPassFilter.applyGaussianHighPassFilter(
                                    redPartRe, redPartIm, greenPartRe, greenPartIm, bluePartRe, bluePartIm, D0);
        
                            // Сохраняем отфильтрованные изображения
                            saveImage(filteredImages[0], OUTPUT_FILTER_IMAGE_PATH_RED);
                            saveImage(filteredImages[1], OUTPUT_FILTER_IMAGE_PATH_GREEN);
                            saveImage(filteredImages[2], OUTPUT_FILTER_IMAGE_PATH_BLUE);
        
                            // Восстанавливаем изображения из отфильтрованного спектра
                            BufferedImage[] reconstructedImages = InverseFFTExample.computeInverseFFT(
                                redPartRe, redPartIm, greenPartRe, greenPartIm, bluePartRe, bluePartIm);
        
                            // Сохраняем восстановленные изображения
                            saveImage(reconstructedImages[0], OUTPUT_RECONSTRUCTED_IMAGE_PATH_RED);
                            saveImage(reconstructedImages[1], OUTPUT_RECONSTRUCTED_IMAGE_PATH_GREEN);
                            saveImage(reconstructedImages[2], OUTPUT_RECONSTRUCTED_IMAGE_PATH_BLUE);
        
                            // Отображаем восстановленные изображения
                            redImageRec = createImageView(OUTPUT_RECONSTRUCTED_IMAGE_PATH_RED);
                            displayImage(redImageRec, 100, 800);
                            greenImageRec = createImageView(OUTPUT_RECONSTRUCTED_IMAGE_PATH_GREEN);
                            displayImage(greenImageRec, 300, 800);
                            blueImageRec = createImageView(OUTPUT_RECONSTRUCTED_IMAGE_PATH_BLUE);
                            displayImage(blueImageRec, 500, 800);
                        } else {
                            showAlert("Неправильный формат данных спектра", "Ошибка");
                        }
                    } catch (IOException e) {
                        showAlert("Ошибка при обработке изображения: " + e.getMessage(), "Ошибка");
                    }
                } else {
                    showAlert("Изображение не было загружено", "Ошибка");
                }
            }
        });
        
        

        FilterButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (originalImage != null) {
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(originalImage.getImage(), null);
                    try {
                        double[][][][] spectrum = FourierTransform.processImage(bufferedImage);

                        if (spectrum.length == 3 && spectrum[0].length == 2 && spectrum[0][0].length == height
                                && spectrum[0][0][0].length == width) {
                            redPartRe = spectrum[0][0];
                            redPartIm = spectrum[0][1];
                            greenPartRe = spectrum[1][0];
                            greenPartIm = spectrum[1][1];
                            bluePartRe = spectrum[2][0];
                            bluePartIm = spectrum[2][1];

                            double D0 = 0;
                            BufferedImage[] filteredImages = GaussianHighPassFilter.applyGaussianHighPassFilter(
                                    redPartRe, redPartIm, greenPartRe, greenPartIm, bluePartRe, bluePartIm, D0);

                            saveImage(filteredImages[0], OUTPUT_FILTER_IMAGE_PATH_RED);
                            saveImage(filteredImages[1], OUTPUT_FILTER_IMAGE_PATH_GREEN);
                            saveImage(filteredImages[2], OUTPUT_FILTER_IMAGE_PATH_BLUE);

                            redImageRec = createImageView(OUTPUT_FILTER_IMAGE_PATH_RED);
                            displayImage(redImageRec, 700, 800);
                            greenImageRec = createImageView(OUTPUT_FILTER_IMAGE_PATH_GREEN);
                            displayImage(greenImageRec, 900, 800);
                            blueImageRec = createImageView(OUTPUT_FILTER_IMAGE_PATH_BLUE);
                            displayImage(blueImageRec, 1100, 800);
                        } else {
                            showAlert("Неправильный формат данных спектра", "Ошибка");
                        }
                    } catch (IOException e) {
                        showAlert("Ошибка при обработке изображения: " + e.getMessage(), "Ошибка");
                    }
                } else {
                    showAlert("Изображение не было загружено", "Ошибка");
                }
            }
        });

        // Создаем контейнер HBox для размещения кнопок выбора и разложения
        HBox buttonBox = new HBox(10); // горизонтальное расположение с отступами
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(chooseImageButton, RGBButton, FurieButton, FilterButton, ReturnButton);

        // Создаем верхний макет BorderPane
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10)); // Устанавливаем отступы

        // устанавливаем контейнер с кнопками
        root.setTop(buttonBox);

        // Создаем область для отображения изображения (панель Pane)
        Pane imagePane = new Pane();
        root.setCenter(imagePane);

        // Создаем сцену
        Scene scene = new Scene(root, 600, 400);

        // Устанавливаем сцену для primaryStage (главного окна)
        primaryStage.setScene(scene);
        primaryStage.setTitle("Задание Lab 2"); // Заголовок окна
        primaryStage.show(); // Отображаем окно
    }

    // Метод для выбора изображения
    private void chooseImage(Stage primaryStage) {
        // Создаем диалоговое окно выбора файла
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите изображение");

        // Устанавливаем фильтр для выбора только изображений
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Изображения", "*.jpg", "*.png",
                "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(imageFilter);

        // Открываем диалоговое окно выбора файла
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        // Если файл выбран, загружаем его в ImageView
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());

            // Очищаем предыдущее содержимое панели с изображением
            Pane imagePane = (Pane) ((BorderPane) primaryStage.getScene().getRoot()).getCenter();
            imagePane.getChildren().clear();

            // Отображаем изображение в центре панели с установкой размеров
            originalImage = new ImageView(image);
            originalImage.setFitWidth(400); // Устанавливаем ширину
            originalImage.setFitHeight(300); // Устанавливаем высоту
            originalImage.setPreserveRatio(true); // Сохраняем пропорции

            // Позиционируем изображение внутри панели
            originalImage.setLayoutX(100); // Позиция по X
            originalImage.setLayoutY(50); // Позиция по Y

            // Добавляем изображение на панель
            imagePane.getChildren().add(originalImage);
        }
    }

    // Метод для создания ImageView из файла
    private ImageView createImageView(String filename) {
        // Загружаем изображение из файла
        Image image = new Image("file:" + filename);

        // Создаем ImageView для отображения изображения
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200); // Устанавливаем ширину
        imageView.setFitHeight(150); // Устанавливаем высоту
        imageView.setPreserveRatio(true); // Сохраняем пропорции

        return imageView;
    }

    // Метод для отображения ImageView на указанных координатах
    private void displayImage(ImageView imageView, double x, double y) {
        imageView.setLayoutX(x); // Устанавливаем позицию по X
        imageView.setLayoutY(y); // Устанавливаем позицию по Y

        // Получаем панель с изображением
        Pane imagePane = (Pane) ((BorderPane) primaryStage.getScene().getRoot()).getCenter();
        imagePane.getChildren().add(imageView); // Добавляем ImageView на панель
    }

    // Метод для вывода сообщения об ошибке
    private void showAlert(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        // Запускаем JavaFX приложение
        launch(args);
    }

    // Определим метод saveImage
    public static void saveImage(BufferedImage image, String filePath) {
        try {
            File output = new File(filePath);
            ImageIO.write(image, "bmp", output);
            System.out.println("Image saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }
}
