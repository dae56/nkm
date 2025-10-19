module ru.dae56.nkm {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.google.gson;

    // Открываем пакеты для FXML
    opens ru.dae56.nkm to javafx.fxml;
    opens ru.dae56.nkm.utils to javafx.fxml;
    opens ru.dae56.nkm.graphElements to javafx.fxml;

    // Открываем пакет для Gson
    opens ru.dae56.nkm.memento to com.google.gson;

    // Экспортируем пакеты
    exports ru.dae56.nkm;
    exports ru.dae56.nkm.utils;
    exports ru.dae56.nkm.graphElements;
    exports ru.dae56.nkm.memento;
    exports ru.dae56.nkm.controllers;
    opens ru.dae56.nkm.controllers to javafx.fxml;
}
