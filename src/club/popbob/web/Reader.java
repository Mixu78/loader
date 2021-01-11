package club.popbob.web;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

import club.popbob.Cheat;
import com.google.gson.*;

public class Reader {
    public static String data;

    static {
        try {
            data = new Scanner(new URL("https://popbob.club/data.json").openStream()).useDelimiter("\\A").next();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Vector<String> getCheats() {
        Vector<String> result = new Vector<>();
        JsonElement element = JsonParser.parseString(data);
        for (Map.Entry<String, JsonElement> entry: element.getAsJsonObject().entrySet()) {
            result.add(entry.getKey());
        }
        return result;
    }

    public static Cheat getCheatData(String cheat) {
        JsonObject jsonObject = (JsonObject) JsonParser.parseString(data).getAsJsonObject().get(cheat);
        return new Gson().fromJson(jsonObject, Cheat.class);
    }
}
