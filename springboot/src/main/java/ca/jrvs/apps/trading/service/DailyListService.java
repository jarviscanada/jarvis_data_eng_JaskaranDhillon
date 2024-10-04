package ca.jrvs.apps.trading.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@Service
public class DailyListService {
    private Set<String> tickers;
    private final String filePath = "src/main/java/ca/jrvs/apps/trading/files/tickerDailyList.txt";
    @PostConstruct
    public void initialize() {
        tickers = new HashSet<>();

        Path path = Path.of(filePath);

        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    tickers.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void saveTickersToFileBeforeShutdown() {
        try {
            String result = String.join("\n", tickers);
            Files.write(Paths.get(filePath), result.getBytes());
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public void addTicker(String ticker){
        if (ticker == null || ticker.trim().isEmpty()){
            throw new IllegalArgumentException("Invalid ticker provided to add to list.");
        }
        tickers.add(ticker.toUpperCase());
    }

    public Set<String> getList(){
       return tickers;
    }
}
