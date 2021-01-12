package club.popbob;

import club.popbob.gui.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Main {
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

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new gui();
    }
}
