package testrail30;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.file.Path;


public class Log {

    public static String logFileName = "log.txt";
    Path startUpPath = new ApplicationStartUpPath().getApplicationStartUp();
    File file = new File(startUpPath + "/" + logFileName);


    // Create new log-file if it doesn't exist
    public Log() throws IOException {
        if(!file.exists()){
            file.createNewFile();
        }
    }


    // add log into the logfile to next line tab
    public void println(String text){
        FileWriter fr = null;
        try {
            fr = new FileWriter(file, true);
            fr.write(text + "\r\n");
            System.out.println(text);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // add log into the logfile inline
    public void print(String text) throws UnsupportedEncodingException, MalformedURLException {
        FileWriter fr = null;
        try {
            fr = new FileWriter(file, true);
            fr.write(text);
            System.out.print(text);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
