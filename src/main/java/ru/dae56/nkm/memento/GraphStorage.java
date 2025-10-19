package ru.dae56.nkm.memento;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

public class GraphStorage {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void saveToFile(GraphMemento memento, String path) {
        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(memento, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GraphMemento loadFromFile(String path) {
        try (FileReader reader = new FileReader(path)) {
            return gson.fromJson(reader, GraphMemento.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

