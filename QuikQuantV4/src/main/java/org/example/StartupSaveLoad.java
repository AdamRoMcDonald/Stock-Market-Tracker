package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class StartupSaveLoad {

    public StartupSaveLoad(StockHashTable table) {
        String filePath = "SMHSave.txt"; // Update with the correct file path


        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;
            while ((line = br.readLine()) != null) {
//                while (!line.isBlank()) {
                    // Print each line read for debugging
                    System.out.println("Read line: " + line);


                    if (line.trim().isEmpty()) {
                        continue; // Skip empty lines
                    }
                    String symbol = line.split(" ")[1] + " " + line.split(" ")[2];


                    // Read key
                    String key = line.split(" ")[0];
                    System.out.println("Processing key: " + key); // Debugging

                    // Read the rest of the stock data

                    String date = readValue(br.readLine(), "Date");
                    double open = parseDouble(readValue(br.readLine(), "Open"), "Open");
                    double high = parseDouble(readValue(br.readLine(), "High"), "High");
                    double low = parseDouble(readValue(br.readLine(), "Low"), "Low");
                    double close = parseDouble(readValue(br.readLine(), "Close"), "Close");
                    int volume = parseInt(readValue(br.readLine(), "Volume"), "Volume");



                    // Create StockData object
                    StockData stockData = new StockData(symbol, date, open, high, low, close, volume);

                    // Insert into hashtable
                    String stockDataStr =  symbol + '\n' + "Date: " + date + '\n' + "Open: " + open + '\n' + "High: " + high + '\n' + "Low: " + low + '\n' + "Close: " + close + '\n' + "Volume: " + volume;
                    table.put(key, stockDataStr + "\n");

                    // Skip the empty line between blocks if present
                    line = br.readLine();
                    if (line != null && !line.trim().isEmpty()) {
                        System.out.println("Unexpected line after block: " + line); // Debugging
                    }

//                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Example usage
        System.out.println(table.get("20240907"));
        System.out.println(table.get("20240906"));
        System.out.println(table.get("20240904"));
    }

    private static String readValue(String line, String fieldName) {
        // Print the line being processed for debugging
        System.out.println("Reading value for " + fieldName + ": " + line);

        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid line format for " + fieldName + ": line is empty or null");
        }
        String[] parts = line.split(": ", 2);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid line format for " + fieldName + ": " + line);
        }
        return parts[1];
    }

    private static double parseDouble(String value, String fieldName) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format for " + fieldName + ": " + value);
        }
    }

    private static int parseInt(String value, String fieldName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format for " + fieldName + ": " + value);
        }
    }
}

class StockData {
    String symbol;
    String date;
    double open;
    double high;
    double low;
    double close;
    int volume;

    public StockData(String symbol, String date, double open, double high, double low, double close, int volume) {
        this.symbol = symbol;
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "StockData{" +
                "symbol='" + symbol + '\'' +
                ", date='" + date + '\'' +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                '}';
    }
}
