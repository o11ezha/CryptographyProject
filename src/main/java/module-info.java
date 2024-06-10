module com.example.goofyconverter {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.goofyconverter to javafx.fxml;
    exports com.example.goofyconverter;
    exports com.example.goofyconverter.Backend;
    opens com.example.goofyconverter.Backend to javafx.fxml;
}