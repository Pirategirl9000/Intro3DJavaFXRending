module org.example.fx_3d {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens org.example.fx_3d to javafx.fxml;
    exports org.example.fx_3d;
}