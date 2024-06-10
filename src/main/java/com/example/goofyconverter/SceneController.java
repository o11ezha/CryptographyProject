package com.example.goofyconverter;

import com.example.goofyconverter.Backend.Chartlogic;
import com.example.goofyconverter.Backend.Converter;
import com.example.goofyconverter.Backend.Encryption;
import com.example.goofyconverter.Backend.ResultOfExtraction;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class SceneController {
    @FXML
    private TextArea messageField;

    @FXML
    private TextField keyField;

    @FXML
    private TextField keyField2;

    @FXML
    private TextField stegoKeyField;

    @FXML
    private TextField stegoKeyField2;

    @FXML
    private TextArea messageField2;

    @FXML
    private Text noBrowsedImg1;

    @FXML
    private Text noBrowsedImg2;

    @FXML
    private Text ramSpeech1;

    @FXML
    private Text ramSpeech2;

    @FXML
    private Text remSpeech;

    @FXML
    private Text remSpeech2;

    @FXML
    private Text msgLabel;

    @FXML
    private Text graphicLabel;

    @FXML
    private Text sizeStego;

    @FXML
    private Label pathPNGlabel;

    @FXML
    private BarChart<String, Number> Ahisto;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private Button browseBMP;

    @FXML
    private Button browsePNG;

    @FXML
    private Button coordinatesButton;

    @FXML
    private Button ramChangeButton;

    @FXML
    private Button downloadMsg;

    @FXML
    private Button remChangeButton;

    @FXML
    private Button ramChangePic;

    @FXML
    private Button remChangePic;

    @FXML
    private CheckBox keyCheck1;

    @FXML
    private CheckBox keyCheck2;

    @FXML
    private VBox ramVbox1;

    @FXML
    private VBox remVbox;

    @FXML
    private Pane imgRamPane;

    @FXML
    private Pane imgRemPane;

    @FXML
    private ImageView predImage1;

    @FXML
    private ImageView predImage2;

    private static String pathBMP;

    private static String pathPNG;

    private static ResultOfExtraction encoded;

    private final TranslateTransition ramTransition = new TranslateTransition(Duration.millis(500));

    private final TranslateTransition remTransition = new TranslateTransition(Duration.millis(500));

    private final String documentsDirectory = System.getProperty("user.home") + File.separator + "Documents";

    public void browseDir(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите папку");


        if (event.getSource() == browseBMP) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.bmp"));
        } else if (event.getSource() == browsePNG) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.png"));
            coordinatesButton.setVisible(false);
        }

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            switch (file.getName().substring(file.getName().lastIndexOf('.'))) {
                case ".bmp" -> {
                    pathBMP = file.getAbsolutePath();
                    predImage1.setImage(new Image(pathBMP));

                    pathPNGlabel.setText("");
                    ramSpeech1.setVisible(false);
                    ramSpeech2.setVisible(false);
                    noBrowsedImg1.setVisible(false);
                }
                case ".png" -> {
                    pathPNG = file.getAbsolutePath();
                    predImage2.setImage(new Image(pathPNG));

                    remSpeech.setVisible(false);
                    remSpeech2.setVisible(false);
                    msgLabel.setVisible(false);
                    messageField2.setVisible(false);

                    noBrowsedImg2.setVisible(false);
                    coordinatesButton.setVisible(false);
                    downloadMsg.setVisible(false);
                }
                default -> System.out.println("Неверный формат файла");
            }
        }
    }

    @FXML
    private void handleKeyCheck1(ActionEvent event) {
        keyField.setDisable(!keyCheck1.isSelected());
    }

    @FXML
    private void handleKeyCheck2(ActionEvent event) {
        keyField2.setDisable(!keyCheck2.isSelected());
    }

    public void encrypt(ActionEvent event) {
        ramSpeech1.setText("Обработка...");
        ramSpeech1.setVisible(true);

        pathPNGlabel.setText("");
        ramSpeech2.setVisible(false);
        noBrowsedImg1.setVisible(false);

        if (pathBMP == null) {
            ramSpeech1.setText("Выберите файл для стеганографии");
            return;
        }

        String message = messageField.getText();
        String key = keyField.getText();
        String stegoKey = stegoKeyField.getText();

        if (message.isEmpty() || stegoKey.isEmpty()) {
            ramSpeech1.setText("Присмотритесь к полям стегоключа и сообщения. Они не должны быть пустыми.");
        } else {
            Encryption encryption = new Encryption();
            Converter converter = new Converter();
            byte[] Data = message.getBytes(StandardCharsets.UTF_8);

            if (keyCheck1.isSelected() && !key.isEmpty()) {
                Data = encryption.encryptMessage(message, key);
            } else if (keyCheck1.isSelected() && key.isEmpty()) {
                ramSpeech1.setText("Ключ шифрования не может быть пустым. Не забудьте про него.");
                return;
            }

            String outputPath = converter.convertToJpeg(Data, pathBMP, stegoKey);

            ramSpeech1.setText("Ваша картинка с секретом сохранена здесь:");
            pathPNGlabel.setText(outputPath);
            ramSpeech2.setVisible(true);
        }
    }

    public void decrypt(ActionEvent event) {
        remSpeech2.setText("Обработка...");
        remSpeech2.setVisible(true);

        remSpeech.setVisible(false);
        msgLabel.setVisible(false);
        messageField2.setVisible(false);

        noBrowsedImg2.setVisible(false);
        coordinatesButton.setVisible(false);
        downloadMsg.setVisible(false);

        if (pathPNG == null) {
            remSpeech2.setText("Выберите файл для расшифровки");
            return;
        }

        String key = keyField2.getText();
        String stegoKey = stegoKeyField2.getText();

        if (stegoKey.isEmpty()) {
            remSpeech2.setText("Не могли бы Вы заполнить поле стегоключа?..");
        } else {
            try {
                Converter converter = new Converter();

                encoded = converter.extractMessage(pathPNG, stegoKey);

                String message = new String(encoded.getMessage(), StandardCharsets.UTF_8);

                if (keyCheck2.isSelected() && !key.isEmpty()) {
                    Encryption encryption = new Encryption();
                    message = encryption.decryptMessage(encoded.getMessage(), key);
                } else if (keyCheck2.isSelected() && key.isEmpty()) {
                    remSpeech2.setText("Кажется, Вы пропустили ключ шифрования.. Заполните, пожалуйста~");
                    return;
                }

                messageField2.setText(message);

                coordinatesButton.setVisible(true);
                downloadMsg.setVisible(true);

                remSpeech2.setVisible(false);
                msgLabel.setVisible(true);
                messageField2.setVisible(true);
                remSpeech.setVisible(true);
            } catch (Exception e) {
                remSpeech2.setText("Не удалось извлечь сообщение.. Вы не ошиблись со стегоключом? Уверены, что в картинке точно есть зашифрованное сообщение?..");
            }
        }
    }

    public void openChartWindow(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chart-view.fxml"));
        Parent root = loader.load();

        if (pathPNG == null) {
            return;
        }

        Stage stage = new Stage();
        stage.setTitle("График");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void drawChart(ActionEvent event) throws IOException {
        Ahisto.setVisible(true);

        BufferedImage img = ImageIO.read(new File(pathPNG));
        double sizeStegocontainer = (double) (img.getHeight() * img.getWidth() * 3) / 8388608;
        String formattedSize = String.format(Locale.US, "%.5f", sizeStegocontainer);

        graphicLabel.setVisible(true);
        sizeStego.setVisible(true);
        sizeStego.setText("Объём стегоконтейнера: " + formattedSize + " Мб");

        yAxis.setLabel("Intensity");
        xAxis.setLabel("Count");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();

        Chartlogic chartlogic = new Chartlogic();

        int[] histogram = chartlogic.createChart(pathPNG);

        for (int i = 0; i < 256; i++) {
            data.add(new XYChart.Data<>(String.valueOf(i), histogram[i]));
        }

        series.setName("Intensity");
        series.setData(data);

        Ahisto.getData().add(series);

        xAxis.setTickLabelRotation(90);
        xAxis.setGapStartAndEnd(false);
        xAxis.setCategories(FXCollections.observableArrayList(
                data.stream().map(XYChart.Data::getXValue)
                        .collect(Collectors.toList())));

        Ahisto.setBarGap(0);
        Ahisto.setCategoryGap(1);

        Ahisto.setMinWidth(800);
    }

    public void getCoordinates(ActionEvent event) {
        if (pathPNG == null && !coordinatesButton.isVisible()) {
            return;
        }

        File file = new File(pathPNG);
        String filename = file.getName().substring(0, file.getName().lastIndexOf('.'));

        String outputCoorFileName = documentsDirectory + File.separator + filename + "_coordinates.txt";

        List<Point> coordinates = encoded.getPixelCoordinates();

        try (FileWriter writer = new FileWriter(outputCoorFileName)) {
            for (Point point : coordinates) {
                writer.write(point.x + "," + point.y + System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadMessage(ActionEvent event) {
        if (pathPNG == null) {
            return;
        }

        File file = new File(pathPNG);
        String filename = file.getName().substring(0, file.getName().lastIndexOf('.'));

        String outputFileName = documentsDirectory + File.separator + filename + "_message.txt";

        try (FileWriter writer = new FileWriter(outputFileName)) {
            writer.write(messageField2.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeHomeScene(VBox vbox, TranslateTransition transition) {
        transition.setNode(vbox);
        transition.setToY(-400);

        transition.setOnFinished(event -> transition.stop());

        transition.play();

    }

    public void changeRemHomeScene() {
        changeHomeScene(remVbox, remTransition);
    }

    public void changeRamHomeScene() {
        changeHomeScene(ramVbox1, ramTransition);
    }

    public void backHomeScene(VBox vbox, TranslateTransition transition) {
        transition.setNode(vbox);
        transition.setToY(0);

        transition.setOnFinished(event -> transition.stop());

        transition.play();
    }

    public void backRemHomeScene() {
        backHomeScene(remVbox, remTransition);
    }

    public void backRamHomeScene() {
        backHomeScene(ramVbox1, ramTransition);
    }

    public void confirmPic(ActionEvent event) {
        if (event.getSource() == ramChangeButton) {
            if (pathBMP == null) {
                noBrowsedImg1.setVisible(true);
                return;
            }
            imgRamPane.setVisible(false);
        } else if (event.getSource() == remChangeButton) {
            if (pathPNG == null) {
                noBrowsedImg2.setVisible(true);
                return;
            }
            imgRemPane.setVisible(false);
        }
    }

    public void changePic(ActionEvent event) {
        if (event.getSource() == ramChangePic) {
            imgRamPane.setVisible(true);
            ramTransition.play();
        } else if (event.getSource() == remChangePic) {
            imgRemPane.setVisible(true);
            remTransition.play();
        }
    }
}