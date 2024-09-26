package ca.jrvs.apps.trading.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class EODQuote {
    private String ticker;
    @JsonProperty("close")
    private float lastPrice;
    @JsonProperty("low")
    private float bidPrice;
    @JsonProperty("volume")
    private int bidSize;
    @JsonProperty("high")
    private float askPrice;
    @JsonProperty("volume")
    private int askSize;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public float getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(float lastPrice) {
        this.lastPrice = lastPrice;
    }

    public float getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(float bidPrice) {
        this.bidPrice = bidPrice;
    }

    public int getBidSize() {
        return bidSize;
    }

    public void setBidSize(int bidSize) {
        this.bidSize = bidSize;
    }

    public float getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(float askPrice) {
        this.askPrice = askPrice;
    }

    public int getAskSize() {
        return askSize;
    }

    public void setAskSize(int askSize) {
        this.askSize = askSize;
    }
}
