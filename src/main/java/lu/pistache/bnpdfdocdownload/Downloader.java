package lu.pistache.bnpdfdocdownload;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Downloader implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Downloader.class);
    public static final int MAX_NUMBER_OF_CLICKS_ON_MORE_DOCS = 100; //should be big for real usage, e.g. 100
    //TODO: put in here the link to the documents page
    public static final String PAGE_DOCUMENTS_LIST = "";
    //TODO: put here the subpath to a "more" button, if existing. Something like "'/.../..../GiveMeMoreButton'" or "//form..."
    public static final String DOCUMENTS_MORE = "";
    //TODO: put here the subpath to the "display" part where the documents are stored. Something like "'/.../TheDisplayField'"
    public static final String DOCUMENTS_DISPLAY = "";
    private static String DOWNLOAD_FOLDER = "DOWNLOADED";
    private static String PROCESSED_FOLDER = "PROCESSED";

    private WebDriver driver;
    private Path chromeDownloadPath;
    private File downloadFolder;
    private File processedFolder;
    private Date fromDate;
    private Date toDate;

    public Downloader(WebDriver driver, String chromeDownloadLocation, Date fromDate, Date toDate) throws FileNotFoundException, ConfigurationException {
        this.driver = driver;
        downloadFolder = new File(chromeDownloadLocation + "/" + DOWNLOAD_FOLDER);
        if (!downloadFolder.exists() || !downloadFolder.isDirectory()) {
            throw new FileNotFoundException("Not existing or not a directory: " + downloadFolder);
        }
        if (getDownloadedPdfFiles().length > 0) {
            throw new ConfigurationException("Provided download folder not empty");
        }
        processedFolder = new File(chromeDownloadLocation + "/" + PROCESSED_FOLDER);
        if (!processedFolder.exists() || !processedFolder.isDirectory()) {
            throw new FileNotFoundException("Not existing or not a directory: " + processedFolder);
        }
        this.fromDate = fromDate;
        this.toDate = toDate;

    }

    @Override
    public void run() {
        //Open documents page
        driver.get(PAGE_DOCUMENTS_LIST);
        //click on More Documents as long as the button is present in order to load all documents on the screen
        //todo: stop this process when you reach a configurable date
        if (!DOCUMENTS_MORE.equals("")) {
            try {
                for (int i = 1; i != MAX_NUMBER_OF_CLICKS_ON_MORE_DOCS; i++) {
                    WebElement moreDocsForm = driver.findElement(By.xpath(DOCUMENTS_MORE));
                    logger.info("Clicking on button for loading more documents");
                    moreDocsForm.submit();
                }
            } catch (Exception e) {
                //Exception is expected at least when there's no button. Do nothing.
            }
        }

        //Get a list of all document elements that can be displayed. Loop on all of them
        List<WebElement> displayForm = driver.findElements(By.xpath(DOCUMENTS_DISPLAY));
        if (displayForm.isEmpty()) {
            logger.error("No documents found. Are you even logged in?");
        } else {
            for (WebElement displayElement : displayForm) {
                try {
                    Date dateFromDocumentLink = getDateFromDocumentLink(displayElement.getText());
                    if (dateFromDocumentLink.after(fromDate) && dateFromDocumentLink.before(toDate)) {
                        logger.debug("Downloading document with date: " + dateFromDocumentLink);
                        if (!displayElement.getText().startsWith("FUSION")) {
                            File[] downloadedFiles = getDownloadedPdfFiles();
                            if (downloadedFiles.length > 0) {
                                throw new RuntimeException("Download folder not empty. Are you running in multithread? This is not supported");
                            }

                            //Click on the document. It should open a new document and, if Chrome set correctly (see README), save automatically
                            //on disk
                            logger.info("Downloading " + displayElement.getText());
                            displayElement.click();

                            //We should get a file here. Wait for it in the download folder.
                            //Make sure that the download folder is empty because we don't know what the file name will be. We also don't
                            //know when it will appear on the disk.
                            while (true) { //wait for file to appear
                                downloadedFiles = getDownloadedPdfFiles();
                                if (downloadedFiles.length == 1) {
                                    logger.info("New file appeared:" + downloadedFiles[0].getName());
                                        File processedFile = new File(processedFolder.getAbsolutePath() + "/" + reformatName(displayElement.getText()) + "_" + downloadedFiles[0].getName());
                                        logger.info("Moving to " + processedFile.getAbsolutePath());
                                        if (processedFile.exists()) {
                                            logger.warn("Target file is already existing in final folder. Skipping.");
                                        } else {
                                            Files.move(downloadedFiles[0].toPath(), processedFile.toPath());
                                        }
                                        break;

                                }
                            }
                        }

                    }
                    else {
                        logger.debug("Skipping document with date: " + dateFromDocumentLink);
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }

            logger.info("Process finished.");
            ((JavascriptExecutor) driver).executeScript("alert('All documents downloaded');");
        }
        driver.quit();
    }

    private File[] getDownloadedPdfFiles() {
        File[] downloadedFiles;
        downloadedFiles = downloadFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".pdf");
            }
        });
        return downloadedFiles;
    }

    /**
     * Check if the date corresponds to any known format and if so, reformat it, replace spaces by underscore, etc.
     * @param input
     * @return
     */
    public static String reformatName(String input) {
        String reformattedName;
        if (stringMatchesStandardDocumentLink(input)) {
            try {
                Date parsedDate = getDateFromDocumentLink(input);

                SimpleDateFormat outputDateFormatter = new SimpleDateFormat("yyyyMMdd");
                String formattedDate = outputDateFormatter.format(parsedDate);
                String nameSubstring = input.substring(13, input.length()); //skip the " - " as it will be replaced by underscore
                reformattedName = formattedDate + "_" + nameSubstring;
                reformattedName = reformattedName.replace(" ", "_");
            } catch (ParseException e) {
                logger.warn("Error parsing. Falling back to provided name" + e);
                reformattedName = input;
            }
        } else {
            reformattedName = input;
        }
        return reformattedName;
    }

    private static Date getDateFromDocumentLink(String input) throws ParseException {
        if (stringMatchesStandardDocumentLink(input)) {
            String dateSubstring = input.substring(0, 10);
            SimpleDateFormat inputDateFormatter = new SimpleDateFormat("dd/MM/yyyy");
            Date parsedDate = inputDateFormatter.parse(dateSubstring);
            return parsedDate;
        } else return new Date();
    }

    private static boolean stringMatchesStandardDocumentLink(String input) {
        return input.matches("\\d\\d/\\d\\d/\\d\\d\\d\\d - .*");
    }

}
