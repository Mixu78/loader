package club.popbob;

import club.popbob.gui.*;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        //Create a directory for our cheats to be stored
        File makeFile = new File(System.getenv("APPDATA") + "\\cfe\\");
        if(!makeFile.exists()) {
            makeFile.mkdirs();
        }

        new gui();
    }
}
