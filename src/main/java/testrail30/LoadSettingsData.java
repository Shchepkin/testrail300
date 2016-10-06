package testrail30;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadSettingsData {

    public static String settingsFileName = "settings.ini";

    public Map getSettings() throws IOException {

        Log log = new Log();
        Map<String, String> settingsMap = new HashMap<String, String>();


        // Load full settings from file to the settings list
        // File with settings must be in the project folder
        log.print("Creating full list of settings from file... ");
        Path pathOfSettingsFile = Paths.get(new ApplicationStartUpPath().getApplicationStartUp() + "/" + settingsFileName);
        List<String> listOfSettings = Files.readAllLines(pathOfSettingsFile, StandardCharsets.UTF_8);
        log.println(" Done");


        // Processing data in full list of settings:
        // 1. Delete empty lines
        // 2. Delete comment lines
        log.print("Sorting data in the full list of settings... ");
        for (int i = 0; i < listOfSettings.size(); ) {
            if (listOfSettings.get(i).isEmpty() || listOfSettings.get(i).startsWith("#")) {
                listOfSettings.remove(i);
            } else i++;
        }
        log.println(" Done");


        // Creation Map with settings:
        log.print("Creation Map with settings... ");
        for (String i : listOfSettings) {
            String[] split = i.split("-->");
            settingsMap.put(split[0], split[1]);
        }
        log.println(" Done");


        return settingsMap;
    }
}
