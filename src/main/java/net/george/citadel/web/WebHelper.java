package net.george.citadel.web;

import net.george.citadel.Citadel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class WebHelper {
    @Nullable
    public static BufferedReader getURLContents(@NotNull String urlString, @NotNull String backupFileLocation){
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            InputStream stream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);

            return new BufferedReader(reader);
        } catch (Exception exception) { // Malformed URL, Offline, etc.
            Citadel.LOGGER.catching(exception);
        }

        try { // Backup
            return new BufferedReader(new InputStreamReader(Objects.requireNonNull(Class.class.getClassLoader().getResourceAsStream(backupFileLocation)), StandardCharsets.UTF_8));
        } catch (NullPointerException exception) { // Can't parse backupFileLocation
            Citadel.LOGGER.catching(exception);
        }

        return null;
    }
}
