package fr.isep.ii3510.assignment4;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CurrencyExchangeRate {
    private String currencyCode;    //base currency
    private List<Currency> ratesList;

    public CurrencyExchangeRate(String currencyCode) {
        this.currencyCode = currencyCode;
        ratesList = new ArrayList<>();
    }


    public Double getRate(String code) {
        for (int i = 0; i < ratesList.size(); i++) {
            if (ratesList.get(i).getCurrencyCode().equals(code)) {
                Log.d("Volley response", "Base =" + this.currencyCode + " | Rate found " + ratesList.get(i).getCurrencyCode() + " " + ratesList.get(i).getRate());
                return ratesList.get(i).getRate();
            }
        }
        return null;
    }

    public void addCurrencyRate(Currency currency) {
        this.ratesList.add(currency);
    }

    @Override
    public String toString() {
        return "CurrencyExchangeRate{" +
                "currencyCode='" + currencyCode + '\'' +
                ", ratesList=" + ratesList +
                '}';
    }
}
