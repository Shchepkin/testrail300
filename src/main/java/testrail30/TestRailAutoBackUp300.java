package testrail30;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;


public class TestRailAutoBackUp300 {

    public static void main(String[] args) throws Exception {
        System.out.println("Start application");


        // Taking and formatting current date
        Date currentDate = new Date();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat formatDateForLogHeader = new SimpleDateFormat("dd.MM.yyyy  HH.mm.ss");


        System.out.print("Creating logFile...  ");
        // Take path to folder with running this jar-file
        Path startUpPath = new ApplicationStartUpPath().getApplicationStartUp();


        // Create log Object
        Log log = new Log();
        System.out.println("Done " + "\r\nNext processes writing to the log.txt in the folder\r\n" + startUpPath);


        // Creating header
        log.println("\r\n--------------------------------------------------");
        log.println("START AT " + formatDateForLogHeader.format(currentDate));
        log.println("--------------------------------------------------");


        // Creation settingsMap object
        Map<String, String> settingsMap = new LoadSettingsData().getSettings();


        // Create FF profile dir path
        log.print("Creation FF WebDriver... ");
        File profileDir = new File(String.valueOf(startUpPath) + "/" + settingsMap.get("folderNameOfBrowserProfile"));


        // Create FFox Driver with profiled browser
        // Change Downloads option to "Always ask me where to save files"
        // (use CMD query for profile creation ---> firefox.exe -P -no-remote)
        FirefoxProfile profile = new FirefoxProfile(profileDir);
        WebDriver driver = new FirefoxDriver(profile);
        log.println(" Done");


        // Open TestRail's page URL
        log.print("Opening TestRail's page URL... ");
        driver.get(settingsMap.get("testRailUrl"));
        log.println(" Done");


        // Authorization if it didn't yet
        log.print("Authorization... ");
        if (driver.getTitle().contains("Login - TestRail")) {
            driver.findElement(By.id("name")).sendKeys(settingsMap.get("login"));
            driver.findElement(By.id("password")).sendKeys(settingsMap.get("password"));
            driver.findElement(By.xpath("//*[@type='submit']")).click();
        }
        log.println(" Done");


        // Load all kinds of projects from site list using xpath or CSS locator
        // If kind of locator incorrect - close the app
        log.print("Creating list of Projects, using ");
        List<WebElement> listOfProjects = new ArrayList<WebElement>();
        if (settingsMap.get("locatorType").equalsIgnoreCase("xpath")) {
            log.print("Xpath locator... ");
            listOfProjects = driver.findElements(By.xpath(settingsMap.get("locatorValue")));
        } else if (settingsMap.get("locatorType").equalsIgnoreCase("css")) {
            log.print("CSS locator... ");
            listOfProjects = driver.findElements(By.cssSelector(settingsMap.get("locatorValue")));
        } else {
            log.println("Wrong settings data in locator type!" +
                    "\r\nProjects didn't save!" +
                    "\r\nFinished.");
            driver.close();
            System.exit(0);
        }
        log.println(" Done");


        // Load all links from "Test Cases" tabs and put in list
        // We need to do it for creation download links, because they contain number of folders
        log.print("Creating list of Test Cases links... ");
        List<WebElement> listOfTestCasesLinks = driver.findElements(By.xpath(".//a[contains(text(),'Test Cases')]"));
        log.println(" Done");


        // Magic actions
        log.println("--------------------------------------------------");
        log.println("The total number of projects: " + listOfProjects.size());


        // Print list of projects names if number of projects <= 10
        if (listOfProjects.size() <= 10) {
            int counter = 0;
            for (WebElement i : listOfProjects) {
                log.print(i.getText());
                counter++;
                if (counter < listOfProjects.size()) {
                    log.print(" - ");
                } else log.println("");
            }
        }

        log.println("--------------------------------------------------");
        log.println("Start download actions:");


        // Take numbers of projects from Test Cases Links
        List numberOfProjects = new ArrayList();
        for (int i = 0; i < listOfTestCasesLinks.size(); i++) {
            String[] splitLinks = listOfTestCasesLinks.get(i).getAttribute("href").split("/");
            numberOfProjects.add(splitLinks[splitLinks.length - 1]);
        }


        // Run cycle for downloading all projects
        for (int i = 0; i < listOfProjects.size(); i++) {

            log.print("Downloading " + listOfProjects.get(i).getText() + " Project... ");


            // Create file object for exist checking
            String filePath = settingsMap.get("pathForSave") + formatDate.format(currentDate) + "/";
            String fileName = formatDate.format(currentDate) + "." + listOfProjects.get(i).getText() + ".xml";
            File file = new File(filePath + fileName);


            // Check is file already exist
            if (file.exists()) {
                // exist
                log.println(" Already Exist!");
            } else {
                // not exist

                // Create and open download URL from settings list and links from "Test Cases" list
                // TestRail's page URL + export link + Test Cases Link (only last number)
                // After than it waits 1 sec
                driver.get(settingsMap.get("testRailUrl") + settingsMap.get("exportLink") + numberOfProjects.get(i));
                Thread.sleep(1000);


                // Run AutoIT script from command prompt and here is the path of the script
                // After than it waits 1 sec
                Runtime.getRuntime().exec(startUpPath + "/" +
                        "TestRailExport.exe \"" +
                        listOfProjects.get(i).getText() + "\" \"" +
                        settingsMap.get("saveWindowTitle") + "\" \"" +
                        settingsMap.get("pathForSave") + "\"");
                Thread.sleep(1000);


                // Check is file successfully saved
                if (file.exists()) {
                    // exist
                    log.println(" Done");
                } else {
                    // not exist
                    log.println(" Don't saved!");
                }
            }
        }


        // Close browser and finish the app
        driver.close();
        log.println("--------------------------------------------------" +
                "\r\nFinished." +
                "\r\n--------------------------------------------------\r\n \r\n \r\n");
    }


}
