package com.project.autotrade;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AutoTrade {
    int lowPrice;
    int highPrice;
    int tradePrice;

    AutoTrade(String lowPrice, String highPrice, String tradePrice) {
        this.lowPrice = Integer.parseInt(lowPrice);
        this.highPrice = Integer.parseInt(highPrice);
        this.tradePrice = Integer.parseInt(tradePrice);
    }

    public void trade() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        LocalDateTime startTime = LocalDateTime.now().with(LocalTime.of(9,0,0));
        LocalDateTime endTime = startTime.plusDays(1).with(LocalTime.of(8,59,50));

        if (startTime.isBefore(now) && endTime.isAfter(now)) {
            double targetPrice = getTargetPrice();
        }

        // remove miliseconds ("HH:ss:mm")
        System.out.println(now);
        System.out.println(startTime);
        System.out.println(endTime);
        System.out.println(startTime.isBefore(endTime));
    }

    public double getTargetPrice() {
        return tradePrice + (highPrice - lowPrice) * 0.3;
    }
}
