package fr.isep.ii3510.assignment4;

public class Currency {
    private String currencyCode;
    private Double rate;

    Currency(String currencyCode, Double rate) {
        this.currencyCode = currencyCode;
        this.rate = rate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public Double getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "currencyCode='" + currencyCode + '\'' +
                ", rate=" + rate +
                '}';
    }
}
