package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GraphFrame extends JFrame {

    private double[] openPrice, closePrice, highPrice, lowPrice, volume;
    private String[] dates;

    public GraphFrame(double[] openPrice, double[] closePrice, double[] highPrice, double[] lowPrice, double[] volume, String[] dates, String calcs) {
        this.openPrice = reverseArray(openPrice);
        this.closePrice = reverseArray(closePrice);
        this.highPrice = reverseArray(highPrice);
        this.lowPrice = reverseArray(lowPrice);
        this.volume = reverseArray(volume);
        this.dates = reverseArray(dates);


        setTitle("Stock Price Charts");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        add(createChartPanel("Open Price", this.openPrice));
        add(createChartPanel("Close Price", this.closePrice));
        add(createChartPanel("High Price", this.highPrice));
        add(createChartPanel("Low Price", this.lowPrice));
        add(createChartPanel("Volume", this.volume));
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.append(calcs);
        add(textArea);
        setVisible(true);
    }

    private JPanel createChartPanel(String title, double[] data) {
        String chartTitle = title + " Chart";
        String categoryAxisLabel = "Dates";
        String valueAxisLabel = title;

        DefaultCategoryDataset dataset = createDataset(data);

        JFreeChart chart = ChartFactory.createLineChart(
                chartTitle,
                categoryAxisLabel,
                valueAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Get the plot of the chart
        CategoryPlot plot = chart.getCategoryPlot();

        // Get the range axis (value axis)
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();

        // Find the minimum value in the data
        double minValue = Double.MAX_VALUE;
        for (double value : data) {
            if (value < minValue) {
                minValue = value;
            }
        }


        rangeAxis.setLowerBound(minValue * 0.9);

        return new ChartPanel(chart);
    }

    private DefaultCategoryDataset createDataset(double[] data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < dates.length; i++) {
            dataset.addValue(data[i], "Price", dates[i]);
        }
        return dataset;
    }

    private double[] reverseArray(double[] array) {
        double[] reversed = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }

    private String[] reverseArray(String[] array) {
        String[] reversed = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }


}