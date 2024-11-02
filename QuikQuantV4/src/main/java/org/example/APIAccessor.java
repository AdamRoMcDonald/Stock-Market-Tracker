package org.example;




import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;





//https://api.twelvedata.com/time_series?&start_date=2020-01-06&end_date=2020-05-06&symbol=aapl&interval=1day&apikey=xxx
public class APIAccessor {

    public static infoPrintScreen infoScreen;
    public void infoLoader(ArrayList<String> ticker, String startDate, String endDate, StockHashTable centralTable, BufferedImage icon) throws IOException {



        //Get API Key
        infoScreen = new infoPrintScreen(ticker);


        //This is inefficient, don't need to get the API Key every time.
        BufferedReader fReader = new BufferedReader(new FileReader("userSettings.txt"));
        String APIKey = fReader.readLine();
        fReader.close();
        System.out.println(APIKey);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request;
        String URL;
        HttpResponse<String> response;


        if (ticker.size() < 2) {


            URL = "https://api.twelvedata.com/time_series?&start_date=" + startDate + "&end_date=" + endDate + "&symbol=" + ticker.get(0).toString() + "&interval=1day&apikey=" + APIKey;
            request = HttpRequest.newBuilder().uri(URI.create(URL)).build();
//            System.out.println(URL);

            //
            //https://api.twelvedata.com/time_series?&start_date="+startDate+"&end_date="+endDate+"&symbol=AAPL,INTC&interval=1day&outputsize=30&apikey=ad6c4b6204264b9ca718dacdaf1a47f4
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                parseJsonResponse(response.body(), centralTable, ticker, icon);
            } catch (Exception e) {
                System.out.println("\n---<Error: " + e.getMessage() + " - It's likely that " + ticker.get(0).toString() + " was not a valid ticker.>---\n");
            }

        } else {
            StringBuilder temp = new StringBuilder();
            for (Object o : ticker) {
                temp.append(o.toString()).append(",");
            }

            URL = "https://api.twelvedata.com/time_series?&start_date=" + startDate + "&end_date=" + endDate + "&symbol=" + temp + "&interval=1day&apikey=" + APIKey;
            System.out.println(URL);
            request = HttpRequest.newBuilder().uri(URI.create(URL)).build();
//            System.out.println("Past URL ");
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                parseJsonResponse(response.body(), centralTable, ticker, icon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void parseJsonResponse(String responseBody, StockHashTable centralTable, ArrayList<String> tickers, BufferedImage icon) {
        JSONObject json = new JSONObject(responseBody);

        // Case 1: Single symbol response
        if (json.has("meta")) {
            parseSymbol(json, centralTable);
            infoScreen.infoPrintScreen(infoScreen.tArea, this, centralTable, tickers, icon);
        }
        // Case 2: Multiple symbols in the response
        else {
            Set<String> keys = json.keySet();
            for (String key : keys) {
                if (json.getJSONObject(key).has("meta")) {
                    parseSymbol(json.getJSONObject(key), centralTable);
                }
            }
            infoScreen.infoPrintScreen(infoScreen.tArea, this, centralTable, tickers, icon);
        }
    }

    // Helper method to parse information from JSON for a specific symbol
    private void parseSymbol(JSONObject json, StockHashTable centralTable) {
        String symbol = json.getJSONObject("meta").getString("symbol");
        JSONArray values = json.getJSONArray("values");

        for (int i = 0; i < values.length(); i++) {
            JSONObject data = values.getJSONObject(i);
            String date = data.getString("datetime");

            String key = purify(date, symbol);
            if(centralTable.get(key) != null && centralTable.printOne(key).contains(symbol)){
                System.out.println("Saved time at: "+date);
                String oneBlock = centralTable.printOne(key).toString();
                System.out.println(oneBlock);
                infoScreen.buildTArea(centralTable.printOne(key).toString());
                continue;
            }


            //It will not hash the same tick with different date to the same index, so if centralTable.printOne().contains//If HT doesn't have the info already, load it in.
            double open = data.getDouble("open");
            double high = data.getDouble("high");
            double low = data.getDouble("low");
            double close = data.getDouble("close");
            long volume = data.getLong("volume");

            String value = String.format("Symbol: %s\nDate: %s\nOpen: %.2f\nHigh: %.2f\nLow: %.2f\nClose: %.2f\nVolume: %d%n",
                        symbol,date, open, high, low, close, volume);
            System.out.println(value);
            infoScreen.buildTArea(value);
            centralTable.put(key, value+"\n");




        }
    }

    // Method to generate a unique key for storing data in centralTable
    public static String purify(String date, String ticker) {
        date = date.replaceAll("-", "");
        int intDate = Integer.parseInt(date);
        int tickerHash = 0;
        for (char ch : ticker.toCharArray()) {
            tickerHash += (int) ch;
        }
        return String.valueOf(intDate + tickerHash);
    }
}
