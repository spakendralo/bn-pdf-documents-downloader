package lu.pistache.bnpdfdocdownload;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;

import static lu.pistache.bnpdfdocdownload.Downloader.reformatName;

public class FileNameTest {
    @Test
    public void fileNameTest() throws ParseException {
        String input = "17/02/2022 - this could be a file";
        Assert.assertEquals(reformatName(input), "20220217_this could be a file");

        input = "some_ alternative name?";
        Assert.assertEquals(reformatName(input), input);

    }



}


