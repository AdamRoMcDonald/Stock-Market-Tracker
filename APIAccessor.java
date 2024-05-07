package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import org.json.JSONObject;

import static java.lang.Math.round;

//https://api.twelvedata.com/time_series?&start_date=2020-01-06&end_date=2020-05-06&symbol=aapl&interval=1day&apikey=xxx
public class APIAccessor {
    public void infoLoader(String[] ticker, String startDate, String endDate, HashTable centralTable) {
        for (String c : ticker){

            String APIKey = "";
            String URL = "https://api.twelvedata.com/time_series?&start_date=" + startDate + "&end_date=" + endDate + "&symbol=" + c + "&interval=1day&apikey=" + APIKey;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                parseJsonResponse(response.body(), centralTable, c);
            }  catch (Exception e) {
                System.out.println("\n---<Error: " + e.getMessage()+" - It's likely that "+c+" was not a valid ticker.>---\n");
            }
        }

    }

    private static void parseJsonResponse(String responseBody, HashTable centralTable, String ticker) {
        JSONObject json = new JSONObject(responseBody);

        json.getJSONArray("values").forEach(obj -> {
            JSONObject data = (JSONObject) obj;
            String date = data.getString("datetime");
            double open = data.getDouble("open");
            double high = data.getDouble("high");
            double low = data.getDouble("low");
            double close = data.getDouble("close");
            long volume = data.getLong("volume");
            System.out.println("Symbol: " +ticker);
            System.out.println("Date: "+date);
            System.out.println("Open: $"+open);
            System.out.println("High: $"+high);
            System.out.println("Low: $"+low);
            System.out.println("Close: $"+close);
            System.out.println("Volume: "+volume);
            System.out.println();
            if(centralTable.get(centralTable.purify(date, ticker)) == null) {
                centralTable.put(centralTable.purify(date, ticker),"Date: "+date+"\nOpen: $"+open+"\nHigh: $"+high+"\nLow: $"+low+"\nClose: $"+close+"\nVolume: "+volume);
            }

        });
    }
}


class Driver {
    public static void main(String[] args) throws IOException {
        Scanner scanTool = new Scanner(System.in);
        HashTable centralTable = new HashTable();

        APIAccessor accessor = new APIAccessor();


        boolean bool = true;
        while (bool) {
            int saveFlag;
            startingUI sUI = new startingUI();
            System.out.println("Choose an option:\n1) Load in new info\n2) Use loaded info\n3) Load previous saveFile\n4) Exit");
            String choice = scanTool.nextLine();
            switch (choice) {
                case "1": /**If user wants to load in new info*/
                    saveFlag = 0;
                    sUI.userInputStart(saveFlag, scanTool, accessor, centralTable);
                    break;


                case "2": /**If user wants to access stored info */
                    saveFlag = 1;
                    sUI.userInputStart(saveFlag, scanTool, accessor, centralTable);
                    break;

                case "3": /**If user wants to read their previously loaded data (from a previous session) back into centralTable */
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader("StockMarkHashSave.txt"));
                        String tempLine;
                        while ((tempLine = reader.readLine()) != null) {
                            String[] parts = tempLine.split(" ", 2);
                            if (parts.length == 2) {
                                String key = parts[0];
                                String value = parts[1];
                                centralTable.put(key, value);

                            }
                        }
                    } catch (FileNotFoundException e) {
                        System.out.println("\n---<File Not Found - You likely are on your first use of the program or didn't exit using option four>---\n");
                    }

                    break;

                case "4":
                    try {
                        File saveFile = new File("StockMarkHashSave.txt");
                        writeHashTableToFile(centralTable, saveFile);
                        System.out.println("Data saved to file: StockMarkHashSave.txt");
                    } catch (IOException e) {
                        System.err.println("Error writing to file: " + e.getMessage());
                    }
                    bool = false;
                    break;
            }
        }
    }

        private static void writeHashTableToFile (HashTable centralTable, File file) throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (LinkedList<HashTable.Entry<String, String>> list : centralTable.table) {
                    if (list != null) {
                        for (HashTable.Entry<String, String> entry : list) {
                            writer.write(entry.key + " " + entry.value + "\n");
                        }
                    }
                }
            }
        }
        }

class startingUI{
    String[] ticker;
    String startDate;
    String endDate;
    int stockNum;
    public startingUI(){

    }


    public void userInputStart(int saveFlag, Scanner scanTool, APIAccessor SMIP, HashTable centralTable){
        singleDay sD = new singleDay();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        stockNum = 1;
        ticker = new String[stockNum];
        for(int i = 0 ; i < stockNum; i++){ /** This accounts for future option of loading in multiple stocks**/
            System.out.println("Please enter the ticker");
            ticker[i] = scanTool.nextLine();
        }
        switch (saveFlag){
            case 0: /** If the user simply wants to load in one day**/
                System.out.println("Choose an option:\n1) View one day\n2) View multiple days");
                switch (scanTool.nextLine()) {
                    case "1":
                        sD.doItOneDay(scanTool);
                        SMIP.infoLoader(ticker, sD.getStartDate(), sD.getEndDate(), centralTable);
                        System.out.println("Successfully loaded information from " + sD.startDate + " concerning the " + ticker[0] + " stock into hashtable.");
                        break;

                    case "2": /** If the user wants to load in multiple days**/
                        sD.doItTwoDay(scanTool);
                        SMIP.infoLoader(ticker, sD.getStartDate(), sD.getEndDate(), centralTable);
                        System.out.println("Successfully loaded information from " + sD.startDate + " to " + sD.endDate + " concerning ");

                        break;
                }
                break;
            case 1:
                System.out.println("Please choose an option:\n1) View saved data\n2) Calculate rate of change information");
                switch (scanTool.nextLine()){
                    case "1":
                        System.out.println("Please choose an option:\n1) View saved data from a single day\n2) View saved data from multiple days");
                        switch (scanTool.nextLine()) {
                            case "1":
                                sD.doItOneDay(scanTool);
                                if (centralTable.get(centralTable.purify(sD.getStartDate(), ticker[0])) != null) {
                                    System.out.println(centralTable.get(centralTable.purify(sD.getStartDate(), ticker[0])));
                                    System.out.println("Successfully displayed information from " + sD.startDate + " concerning " + ticker[0] + " stock from the hashtable.");
                                }
                                break;

                            case "2":
                                sD.doItTwoDay(scanTool);
                                LocalDate LOCstart = LocalDate.parse(sD.startDate, formatter);
                                LocalDate LOCend = LocalDate.parse(sD.endDate, formatter);
                                LocalDate currentDate = LOCend;
                                String[] tempCheckArr = new String[sD.daysBetween(LOCstart, LOCend)];
                                int counter = 0;
                                while (!currentDate.isBefore(LOCstart)) {
                                    for (String c : ticker) {
                                        if (centralTable.get(centralTable.purify(currentDate.toString(), c)) != null) {
                                            System.out.println(centralTable.get(centralTable.purify(currentDate.toString(), c))+"\n");
                                            tempCheckArr[counter] = c;
                                            counter++;
                                        }
                                    }
                                    currentDate = currentDate.minusDays(1); //Current date updates to the next with each iteration of the while loop.
                                }
                                if(counter != 0) {
                                    System.out.println(tempCheckArr.length);
                                    System.out.print("Available data from " + sD.getStartDate() + " to " + sD.getEndDate() + " concerning ");
                                    for (String c : ticker) {
                                        System.out.print(c + ", ");
                                    }
                                    System.out.println(" successfully printed from hashtable");
                                    break;
                                }
                                System.out.println("This information was not loaded, loading it now.");
                                SMIP.infoLoader(ticker, sD.getStartDate(), sD.getEndDate(), centralTable);
                        }
                        break;

                    case "2":
                        sD.doItTwoDay(scanTool);
                        LocalDate LOCstart = LocalDate.parse(sD.getStartDate(), formatter);
                        LocalDate LOCend = LocalDate.parse(sD.getEndDate(), formatter);
                        LocalDate currentDate = LOCend;
                        double[] openValues = new double[sD.daysBetween(LOCstart, LOCend)];
                        double[] volumeValues = new double[sD.daysBetween(LOCstart, LOCend)];
                        int counter = 0;
                        while (!currentDate.isBefore(LOCstart)) {
                            for (String c : ticker) {
                                if (centralTable.get(centralTable.purify(currentDate.toString(), c)) != null) {
                                    String info = centralTable.get(centralTable.purify(currentDate.toString(), c)).toString();
                                    System.out.println(info);
                                    int tempPriceIndex = info.indexOf('n') + 4;
                                    openValues[counter] = Double.parseDouble(info.substring(tempPriceIndex,tempPriceIndex+5));
                                    int tempValueIndex = info.indexOf('V');
                                    volumeValues[counter] = Double.parseDouble(info.substring(tempValueIndex+9));
                                    counter++;
                                } else{
                                    System.out.println("No data available for "+currentDate);
                                }
                            }
                            currentDate = currentDate.minusDays(1); //Current date updates to the next with each iteration of the while loop.
                        }
                        double sumOpen = 0;
                        double sumVolume = 0;
                        for(int i = 0; i < openValues.length; i++){
                            sumOpen += openValues[i];
                            sumVolume += volumeValues[i];
                        }
                        double averageOpen = sumOpen/(openValues.length-counter);
                        double changePercentage = (openValues[0] - openValues[openValues.length-1])/((openValues.length-1)-counter);
                        double averageVolume = sumVolume / openValues.length-1;
                        System.out.println("Average opening price: $" + averageOpen);
                        System.out.println("Average volume: " + averageVolume);
                        System.out.println("Change percentage: " + changePercentage+"%");
                        System.out.println("Data from " + startDate + " to " + endDate + " concerning ");
                        if(averageVolume != 0) {
                            for (String c : ticker) {
                                System.out.print(c + ", ");
                            }
                            System.out.println(" successfully printed from hashtable");
                        }
                }
        }
    }
}




class singleDay{
    String startDate;
    String endDate;
    public void doItOneDay(Scanner scanTool){
        System.out.println("Please enter the date (YYYY-MM-DD): ");
        setStartDate(scanTool.nextLine());

        try { /** Here I calculate the next days date and set it as the end date, as the user only wants to see one day but start and enddates are both required. */
            LocalDate date = LocalDate.parse(startDate);
            LocalDate nextDay = date.plusDays(1);
            setEndDate(nextDay.format(DateTimeFormatter.ISO_DATE));
        } catch (DateTimeParseException e) {
            System.out.println("---<Invalid date format - Format should be YYYY-MM-DD>---");
            doItOneDay(scanTool);
        }


    }
    public void doItTwoDay(Scanner scanTool){
        System.out.println();
        System.out.println("\n---<Note that 'Starting date' means furthest away date>---\nPlease enter the starting date (YYYY-MM-DD): ");
        try{
            setStartDate(scanTool.nextLine());}
        catch(DateTimeParseException e){
            System.out.println("---<Invalid date format - Format should be YYYY-MM-DD>---");
            doItTwoDay(scanTool);
        }
        System.out.println("Please enter the ending date (YYYY-MM-DD): ");
        try {
            setEndDate(scanTool.nextLine());
        } catch (DateTimeParseException e) {
            System.out.println("---<Invalid date format - Format should be YYYY-MM-DD>---");
            doItTwoDay(scanTool);
        }

    }
    public void setStartDate(String startDate) {
        LocalDate date = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE); //This is here purely to throw an error if the user puts in wrong formatted date.
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        LocalDate date = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE); //This is here purely to throw an error if the user puts in wrong formatted date.
        this.endDate = endDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public int daysBetween(LocalDate start, LocalDate end){
        long daysBetween = ChronoUnit.DAYS.between(start, end);
        return (int) daysBetween;
    }


}


class HashTable<K, V> {
    public intTableSize = 3;
    public LinkedList<Entry<K, V>>[] table;
    public int numElements;

    public HashTable() {
        hashTable = new LinkedList[tableSize];
        numElements = 0;
    }

    public void put(K key, V value) {
        if (key == null){
            System.out.println("Key cannot be null, please change");
            
        }

        int curPos = getIndex(key);
        if (hashTable[curPos] == null) {
            hastTable[curPos] = new LinkedList<>();
        }


        for (Entry<K, V> entry : hashTable[curPos]) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }

        hashTable[curPos].add(new Entry<>(key, value));
        numElements++;


        if (size > table.length * 0.75) {
            resize();
        }
    }

    public V get(K key) {
        if (key == null)
            throw new IllegalArgumentException("Key cannot be null");

        int index = getIndex(key);
        if (table[index] != null) {
            for (Entry<K, V> entry : table[index]) {
                if (entry.key.equals(key)) {
                    return entry.value;
                }
            }
        }
        return null;
    }

    public boolean remove(K key) {
        if (key == null)
            throw new IllegalArgumentException("Key cannot be null");

        int index = getIndex(key);
        if (table[index] != null) {
            table[index].removeIf(entry -> entry.key.equals(key));
            size--;
            return true;
        }
        return false;
    }

    private int getIndex(K key) {
        return Math.abs(key.hashCode()) % table.length;
    }

    private void resize() {
        LinkedList<Entry<K, V>>[] oldTable = table;
        table = new LinkedList[intFindNextPrime(table.length * 2)];
        size = 0;
        for (LinkedList<Entry<K, V>> list : oldTable) {
            if (list != null) {
                for (Entry<K, V> entry : list) {
                    put(entry.key, entry.value);
                }
            }
        }
    }

    public static class Entry<K, V> {
        K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }


    public int intFindNextPrime(int num){
        for(int i = 2 ; i < num ; i++){
            if(num % i == 0){
                return intFindNextPrime(num+1);
            }
        }
        return num;
    }



    public String purify(String date, String ticker){
        date = date.replaceAll("-","");
        int intDate = Integer.parseInt(date);
        int numRepOfTick = 0;
        for (int i = 0; i < ticker.length(); i++) {
            numRepOfTick += (int) ticker.charAt(i);
        }
        int totalKey = intDate + numRepOfTick;
        String totalKeyStr = Integer.toString(totalKey);
        return totalKeyStr;


    }


}
