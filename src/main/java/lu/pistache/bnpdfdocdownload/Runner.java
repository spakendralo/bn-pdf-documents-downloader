package lu.pistache.bnpdfdocdownload;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Runner {
    public static final String CHROMEDRIVER = "/Library/Developer/ChromeDriver/chromedriver";
    private static final Logger logger = LoggerFactory.getLogger(Runner.class);

    public static void main(String[] args) throws ConfigurationException, FileNotFoundException, ParseException {
        System.setProperty("webdriver.chrome.driver", CHROMEDRIVER);
        if (args.length < 3) {
            logger.error("Please provide the chrome download folder as parameter, the dateFrom, and dateTo (in dd/MM/yyyy format)");
            System.exit(1);
        }
        Date fromDate = new SimpleDateFormat("dd/MM/yyyy").parse(args[1]);
        Date toDate = new SimpleDateFormat("dd/MM/yyyy").parse(args[2]);
        logger.debug("Input parameters: ");
        logger.debug(args[0]);
        logger.debug(args[1]);
        logger.debug(args[2]);
        logger.info("Staring...");
        logger.info("Connecting to Chrome...");
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        WebDriver driver = new ChromeDriver(options);
        logger.info("Starting downloader...");
        new Thread(new Downloader(driver, args[0], fromDate, toDate)).start();
    }
}
