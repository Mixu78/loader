package club.popbob;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class Config {
    private final ConfigObj config;
    public final File configFile;
    private final String defaultConfig = "{cheatUpdates: {}, bigrat: false}";

    public static class ConfigObj {
        public Map<String, String> cheatUpdates;
        public boolean bigrat = false;
    }

    public Config(File configFile) {
        this.configFile = configFile;
        String contents = defaultConfig;
        long size = 0;
        try {
            contents = String.join("", Files.readAllLines(configFile.toPath()));
            size = Files.size(configFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (size == 0)
            contents = defaultConfig;
        //contents will probably be read properly
        this.config = new Gson().fromJson(contents, ConfigObj.class);
    }

    public ConfigObj getConfig() {
        return this.config;
    }

    public void saveConfig() {
        String json = new Gson().toJson(this.config);
        FileWriter writer;
        System.out.println(json);

        this.configFile.delete();
        try {
            this.configFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            writer = new FileWriter(this.configFile);
            writer.write(json == null ? defaultConfig : json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
