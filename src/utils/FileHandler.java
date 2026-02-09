package utils;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileHandler {
    private static final String DATA_DIR = "data";
    
    static {
        // Create data directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Error creating data directory: " + e.getMessage());
        }
    }

    public static <T> List<T> loadData(String filename, java.util.function.Function<String, T> parser) {
        Path filePath = Paths.get(DATA_DIR, filename);
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try {
            return Files.lines(filePath)
                    .filter(line -> !line.trim().isEmpty())
                    .map(parser)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error loading data from " + filename + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static <T> boolean saveData(String filename, List<T> data) {
        Path filePath = Paths.get(DATA_DIR, filename);
        try {
            List<String> lines = data.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
            Files.write(filePath, lines);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving data to " + filename + ": " + e.getMessage());
            return false;
        }
    }

    public static <T> boolean appendData(String filename, T data) {
        Path filePath = Paths.get(DATA_DIR, filename);
        try {
            Files.write(filePath, (data.toString() + System.lineSeparator()).getBytes(), 
                       StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            System.err.println("Error appending data to " + filename + ": " + e.getMessage());
            return false;
        }
    }
}