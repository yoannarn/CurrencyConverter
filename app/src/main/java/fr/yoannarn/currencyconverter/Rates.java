package fr.yoannarn.currencyconverter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Rates implements Serializable {
    private Map<String, Double> rates = new HashMap<>();
    private String date;
    private String base;

    public Rates(){
        rates = new HashMap<>();
    }

    @JsonIgnore
    public Set<String> getCurrencies() {
        return rates.keySet();
    }

    public Double getRate(String currency) {
        return rates.get(currency);
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
        if(!rates.containsKey(base))
        rates.put(base, 1.0);
    }

    //BASE EUR
    @JsonIgnore
    public double getCurrentyConversion(String currentyRef, String currenty, double value) {
        double rateRef = rates.get(currentyRef);
        double rate = rates.get(currenty);

        return value / rateRef * rate;
    }

}
