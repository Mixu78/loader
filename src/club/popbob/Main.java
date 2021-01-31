package club.popbob;

import club.popbob.gui.*;
import club.popbob.web.Reader;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Main {
    public static Config config;
    public static void main(String[] args) throws IOException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        //Create a directory for our cheats to be stored
        File makeCFE = new File(System.getenv("APPDATA") + "\\cfe\\");
        File makeLibs = new File(System.getenv("APPDATA") + "\\cfe\\libs\\");
        if(!makeCFE.exists()) {
            makeCFE.mkdirs();
        }
        if(!makeLibs.exists()) {
            makeLibs.mkdirs();
        }

        //Load configuration
        File configFile = new File(System.getenv("APPDATA") + "\\cfe\\config.json");
        if (!configFile.exists())
            configFile.createNewFile();
        config = new Config(configFile);

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new gui();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println(config.getConfig());
                config.saveConfig();
            }
        }, "Shutdown"));
    }
}
