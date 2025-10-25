package ru.dae56.nkm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("GUI Graph");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

// Структура устойчива, если наблюдается нечетное число отрицательных циклов. Подсчет циклов, как делать - ищем цикл, считаем произведение весов связей в цикле
// Устойчивость по возмущению и начальному значению. Строим матрицу на основе графа. Составляем характеристическое уравнение. Находим корни. Если корень характеристического уравнения меньше единицы, то структура устойчива.
// Если равна единицы, нельзя сделать вывод.
// Если начинаешь с первой вершины, то ее надо замкнуть
// Импульсы
// 1-2 формулы. 3-4 пример. Если в ноду с двух сторон приходит импульс то считается именно сумма!
//

// НКМ img_5. вес [-1; 1]
// Построение img_6. В идеале должен быть цикл. Разделение на группы (сильно, слабо), а уже от туда балуемся со значениями. Много сущей не надо.
// Она поэтому и нечеткая. Потому что есть субъективность в выборе веса ребра.\
// img_7. Импульсы в несколько вершин. Пояснение на примере.
// Консонанс и диссонанс (для расчета (примерного) правильно ли назначили вес) img_8