package ca.jrvs.apps.trading.model;

import jakarta.persistence.*;

@Entity
@Table(name = "position")
public class Position {
    @Id
    @Column(name = "account_id")
    private Integer accountId;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "position")
    private Integer position;

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}