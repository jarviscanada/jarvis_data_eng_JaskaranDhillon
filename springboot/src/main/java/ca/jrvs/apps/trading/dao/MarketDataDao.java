package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.dto.EODQuoteRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MarketDataDao {
    @Value("${API_KEY}")
    private String apiKey;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public MarketDataDao(OkHttpClient okHttpClient, ObjectMapper objectMapper) {
        this.client = okHttpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Get an IexQuote
     *
     * @param ticker
     * @throws IllegalArgumentException      if a given ticker is invalid
     * @throws DataRetrievalFailureException if HTTP request failed
     */
    public Optional<EODQuoteRequestDTO> findById(String ticker) {
        if (ticker == null || ticker.trim().isEmpty()) {
//            logger.error("Attempting to buy share with invalid symbol");
            throw new IllegalArgumentException("Invalid symbol provided.");
        }

        ticker = ticker.toUpperCase();
        String url = "https://eodhd.com/api/real-time/" + ticker + ".US?api_token=" + apiKey + "&fmt=json";
        Optional<String> quote = executeHttpGet(url);

        EODQuoteRequestDTO eodQuoteRequestDTO;
        if (quote.isPresent()) {
            try {
                JsonNode rootNode = objectMapper.readTree(quote.get());
                eodQuoteRequestDTO = objectMapper.treeToValue(rootNode, EODQuoteRequestDTO.class);
                eodQuoteRequestDTO.setTicker(ticker);
                eodQuoteRequestDTO.setAskSize(eodQuoteRequestDTO.getBidSize());
                return Optional.of(eodQuoteRequestDTO);

            } catch (Exception e) {
                throw new RuntimeException("Failed to parse data: " + e.getMessage());
            }
        } else {
            throw new DataRetrievalFailureException("Failed to fetch data.");
        }
    }

    /**
     * Get quotes from IEX
     *
     * @param tickers is a list of tickers
     * @return a list of IexQuote objects
     * @throws IllegalArgumentException      if a given ticker is invalid
     * @throws DataRetrievalFailureException if HTTP request failed
     */
    public List<EODQuoteRequestDTO> findAllById(Iterable<String> tickers) throws JsonProcessingException {
        int i = 0;
        String initialTicker = null;
        StringBuilder tickerList = new StringBuilder();

        for (String ticker : tickers) {
            if (ticker == null || ticker.trim().isEmpty()) {
//            logger.error("Attempting to buy share with invalid symbol");
                throw new IllegalArgumentException("Invalid symbol provided.");
            } else if (i == 0) {
                initialTicker = ticker;
            } else {
                tickerList.append(ticker).append(",");
            }
            i++;
        }

        String url = "https://eodhd.com/api/real-time/" + initialTicker + "?s=" + tickerList + "&api_token=" + apiKey + "&fmt=json";
        Optional<String> quote = executeHttpGet(url);

        if (quote.isPresent()) {
            JsonNode rootNode = objectMapper.readTree(quote.get());
            List<EODQuoteRequestDTO> quoteList = objectMapper.treeToValue(rootNode, new TypeReference<List<EODQuoteRequestDTO>>() {
            });

            return quoteList;
        } else {
            throw new DataRetrievalFailureException("Failed to fetch data.");
        }
    }

    /**
     * Execute a GET request and return http entity/body as a string
     * Tip: use EntitiyUtils.toString to process HTTP entity
     *
     * @param url resource URL
     * @return http response body or Optional.empty for 404 response
     * @throws DataRetrievalFailureException if HTTP failed or status code is unexpected
     */
    private Optional<String> executeHttpGet(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return Optional.of(response.body().string());
            } else {
                throw new RuntimeException("Failed request.");
            }
        } catch (Exception e) {
            throw new DataRetrievalFailureException("Error when trying to fetch data: " + e.getMessage());
        }
    }

//    private String getDateThreeDaysAgo(){
//        return LocalDate.now().minusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE);
//    }
}
