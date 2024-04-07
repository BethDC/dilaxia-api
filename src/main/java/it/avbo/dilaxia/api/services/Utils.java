package it.avbo.dilaxia.api.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

public class Utils {
    public static Optional<String> stringFromReader(BufferedReader reader) {
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException exception) {
            return Optional.empty();
        }
        return Optional.of(sb.toString());
    }
}
