package org.example;

import java.util.Arrays;

public class VolatilityCalculator {


    // Function to calculate logarithmic returns
    public static double calculateLogReturns(double[] prices) {
        double[] returns = new double[prices.length - 1];
        for (int i = 1; i < prices.length; i++) {
            returns[i - 1] = Math.log(prices[i] / prices[i - 1]);
        }
        return calculateVolatility(returns);


    }

    // Function to calculate volatility
    public static double calculateVolatility(double[] returns) {
        double sumSquaredReturns = 0;
        double meanReturn = calculateMean(returns);

        // Calculate sum of squared differences from the mean
        for (double ret : returns) {
            sumSquaredReturns += Math.pow((ret - meanReturn), 2);
        }

        // Calculate volatility (standard deviation)
        double volatility = Math.sqrt(sumSquaredReturns / (returns.length - 1)) * Math.sqrt(252); // Assuming 252 trading days in a year
        return volatility;
    }

    // Function to calculate mean of an array of values
    public static double calculateMean(double[] values) {
        double sum = Arrays.stream(values).sum();
        return sum / values.length;
    }
}