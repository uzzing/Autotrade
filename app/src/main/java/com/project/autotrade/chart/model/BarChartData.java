package com.project.autotrade.chart.model;

public class BarChartData {
    private int xValue;
    private float yValue;

    public BarChartData() {}

    public BarChartData(int xValue, float yValue) {
        this.xValue = xValue;
        this.yValue = yValue;
    }

    public int getxValue() {
        return xValue;
    }

    public void setxValue(int xValue) {
        this.xValue = xValue;
    }

    public float getyValue() {
        return yValue;
    }

    public void setyValue(float yValue) {
        this.yValue = yValue;
    }
}
