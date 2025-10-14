module ru.dae56.nkm {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens ru.dae56.nkm to javafx.fxml;
    exports ru.dae56.nkm;
}