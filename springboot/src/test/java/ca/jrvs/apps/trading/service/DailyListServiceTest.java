package ca.jrvs.apps.trading.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;


public class DailyListServiceTest {

    private DailyListService dailyListService;

    private final String testFilePath = "src/test/resources/tickerDailyList.txt";

    @Before
    public void setUp() throws Exception {
        dailyListService = new DailyListService();
        dailyListService.setFilePath(testFilePath);

        File testFile = new File(testFilePath);
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    public void initialize() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFilePath))) {
            writer.write("AAPL\nGOOGL\nMSFT");
        }

        dailyListService.initialize();

        Set<String> expectedTickers = new HashSet<>();
        expectedTickers.add("AAPL");
        expectedTickers.add("GOOGL");
        expectedTickers.add("MSFT");

        assertEquals(expectedTickers, dailyListService.getList());
    }

    @Test
    public void addTicker_Success() {
        dailyListService.initialize();
        dailyListService.addTicker("AMZN");

        assertTrue(dailyListService.getList().contains("AMZN"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addTicker_Failure_InvalidTicker() {
        dailyListService.addTicker("");
    }

    @Test
    public void getList_Success() {
        dailyListService.initialize();
        assertTrue(dailyListService.getList().isEmpty());

        dailyListService.addTicker("NFLX");
        assertFalse(dailyListService.getList().isEmpty());
    }

    @After
    public void tearDown() throws Exception {
        Files.deleteIfExists(Paths.get(testFilePath));
    }
}
