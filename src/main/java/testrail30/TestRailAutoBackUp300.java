package testrail30;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TestRailAutoBackUp300 {


    public static void main(String[] args) throws Exception {
        System.out.println("Start application");

        // Taking current date
        // Formatting current date
        Date currentDate = new Date();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat formatDateForLogHeader = new SimpleDateFormat("dd.MM.yyyy  HH.mm.ss");


        System.out.print("Creating logFile...  ");
        // Take path to folder with running this jar-file
        ApplicationStartUpPath startUpPath = new ApplicationStartUpPath();
        // Create log Object
        Log log = new Log();
        System.out.println("Done " +
                "\r\nNext processes writing to the log.txt in the folder\r\n" +
                startUpPath.getApplicationStartUp());


        // Creating header
        log.println("**************************************************");
        log.println("START AT " + formatDateForLogHeader.format(currentDate));
        log.println("**************************************************");


        LoadSettingsData settings = new LoadSettingsData();
        List<String> listOfSettings = settings.getSettings();


        // Create FFox Driver with profiled browser
        // Profile must exist, so create it before run the app
        // Change Downloads option to "Always ask me where to save files"
        // (use CMD query for profile creation ---> firefox.exe -P -no-remote)
        log.print("Creating FF WebDriver... ");
        FirefoxProfile profile = new ProfilesIni().getProfile(listOfSettings.get(8));
        WebDriver driver = new FirefoxDriver(profile);
        log.println(" Done");


        // Open TestRail's page URL
        log.print("Opening TestRail's page URL... ");
        driver.get(listOfSettings.get(0));
        log.println(" Done");


        // Authorization if it didn't yet
        log.print("Authorization... ");
        if (driver.getTitle().contains("Login - TestRail")) {
            driver.findElement(By.id("name")).sendKeys(listOfSettings.get(2));
            driver.findElement(By.id("password")).sendKeys(listOfSettings.get(3));
            driver.findElement(By.xpath("//*[@type='submit']")).click();
        }
        log.println(" Done");


        // Load all kinds of projects from site list using xpath or CSS locator
        // If kind of locator incorrect - close the app
        log.print("Creating list of Projects, using ");
        List<WebElement> listOfProjects = new ArrayList<WebElement>();
        if (listOfSettings.get(4).equalsIgnoreCase("xpath")) {
            log.print("Xpath locator... ");
            listOfProjects = driver.findElements(By.xpath(listOfSettings.get(5)));
        } else if (listOfSettings.get(4).equalsIgnoreCase("css")) {
            log.print("CSS locator... ");
            listOfProjects = driver.findElements(By.cssSelector(listOfSettings.get(5)));
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
        log.println("**************************************************");
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

        log.println("**************************************************");
        log.println("Start download actions:");


        // Take numbers of projects from Test Cases Links
        List numberOfProjects = new ArrayList();
        for (int i = 0; i < listOfTestCasesLinks.size(); i++) {
            String testCasesLink = listOfTestCasesLinks.get(i).getAttribute("href");
            String[] splitLinks = testCasesLink.split("/");
            numberOfProjects.add(splitLinks[splitLinks.length - 1]);
        }


        // Run cycle for downloading all projects
        for (int i = 0; i < listOfProjects.size(); i++) {

            log.print("Downloading " + listOfProjects.get(i).getText() + " Project... ");


            // Create file object for checking
            String filePath = listOfSettings.get(7) + formatDate.format(currentDate) + "\\";
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
                driver.get(listOfSettings.get(0) + listOfSettings.get(1) + numberOfProjects.get(i));
                Thread.sleep(1000);


                // Run AutoIT script from command prompt and here is the path of the script
                // After than it waits 1 sec
                Runtime.getRuntime().exec(startUpPath.getApplicationStartUp() + "/" + "TestRailExport.exe \"" + listOfProjects.get(i).getText() + "\" \"" + listOfSettings.get(6) + "\" \"" + listOfSettings.get(7) + "\"");
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
        log.println("**************************************************" +
                "\r\nFinished." +
                "\r\nAll projects successfully saved!" +
                "\r\n**************************************************\r\n \r\n \r\n");
    }


}
