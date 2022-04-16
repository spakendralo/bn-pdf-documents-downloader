package lu.pistache.bnpdfdocdownload;

import org.openqa.selenium.WebDriver;

public class KeepAlive implements Runnable {
    private WebDriver driver;
    public boolean keepRunning = true;

    public KeepAlive(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public void run() {
        while (keepRunning) {
            try {
                Thread.sleep(60000);
                //one day there will be a call to the web page here so that there is no timeout
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
