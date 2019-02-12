package com.antonromanov.temperaturerest.model;

/**
 * Класс через который мы передаем температру в сервис с датчиков в HTTP POST.
 */
public class PR {

    private Double temp;

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public PR(Double temp) {
        this.temp = temp;
    }

    public PR() {
    }
}
