package org.example;

import org.w3c.dom.Text;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Opens a new frame with a JTextAre where requested info is printed to. There are various buttons for the user to control what they see and to calculate based on what they see.
 * @author Adam R. McDonald
 *
 */


public class infoPrintScreen {
    JTextArea tArea = new JTextArea(15, 0);
    JFrame frame;
    JPanel panel;
    JButton doneButton;
    JComboBox stocks = new JComboBox();
    ArrayList<String> filteredLines = new ArrayList<>();





    /**
     * Constructor, sends tickerNames to buildStocks to build the combobox options.
     * @param ticker tickerNames at this point is a healthy LinkedList containing stock symbols that APIAccessor uses to load info in, or that getDatePanel uses to retrieve stored data.
     */

    public infoPrintScreen(ArrayList<String> ticker ) {
        buildStocks(ticker);
    }



    //L

    /**
     * This takes in strings either from APIAccessor (while it's loading in info), or getDatePanel while it's retrieving stored info.
     * @param s Each s is a value that goes with a key, so it's the symbol, date, open, close, etc... of a certain ticker and date key.
     * @return It returns a fully fleshed out tArea with all the info it's been fed present, each s seperated by a \n.
     */

    public JTextArea buildTArea(String s){
        tArea.insert(s+'\n', tArea.getCaretPosition());
        tArea.setCaretPosition(tArea.getDocument().getLength());
        tArea.insert("\n", tArea.getCaretPosition());
        return tArea;
    }


    /**
     * Builds the combobox which is used to select individual stocks to view in JTextArea.
     * @param ticker as previously mentioned, this is a healthy list of tickerNames. No duplicates, no extras, etc... Packaged up and ready for APIAccessor
     * @return Returns the completed ComboBox.
     */

    public JComboBox buildStocks(ArrayList<String> ticker){
        for(int i = 0; i < ticker.size(); i++){
            stocks.addItem(ticker.get(i));
        }
        return stocks;
    }


    /**
     * Creates the actual frame which holds the JTextArea and modifying buttons.
     *
     * Furthermore, it also saves the loaded info from APIAccessor into the project save file and restarts the program from MainFrame when user clicks "Done."
     *
     * @param tArea tArea is the JTextArea
     * @param AI AI refers to APIProcessor, this method has it as a parameter because MainFrame needs it in order to pass it on to next frame.
     *           It may seem tedious to pass around the same APIProccessor instance over and over again, but it means it only has to be created once, thus saving time and memory.
     * @param SHT This is the StockHashTable, also solely in this program as an input parameter for MainFrame. Again, saving time+memory.
     * @param ticker This will likely be removed.
     */


    public void infoPrintScreen(JTextArea tArea, APIAccessor AI, StockHashTable SHT, ArrayList<String> ticker) {


        JFrame frame = new JFrame(); //This is the frame in which the JTextArea and buttons/JComboBox go.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(); //This panel holds everything.
//        frame.setIconImage(image2);


        JTextField highlight = new JTextField("Highlight",10); //Here user can enter keystrokes that they wish to highlight/search for.
        highlight.setEditable(true);


        JButton highlightButton = new JButton("Highlight"); //This is the button which starts the search for, and highlights if found, the users entered text.


        highlightButton.addActionListener(e -> {

            String highlightText = highlight.getText();
            String tAreaText = tArea.getText();
            String pattern = "\\b" + highlightText + "\\b";
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(tAreaText);


            Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(randoColorCreator()); //Each piece of text user wants to highlight will be different color
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                try {
                    tArea.getHighlighter().addHighlight(start, end, painter);
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
            }


            tArea.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {

                }

                @Override
                public void removeUpdate(DocumentEvent e) {

                }

                @Override
                public void changedUpdate(DocumentEvent e) {

                }
            });




        });
        panel.add(highlightButton);

        //There are 7 rows
        JButton stocksButton = new JButton("Remove Selected Stock");
        stocksButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ticker.remove(stocks.getSelectedIndex());
                filterText();
            }
        });





        panel.add(stocks);
        panel.add(stocksButton, BorderLayout.SOUTH);





        JButton doneButton = new JButton("Finish and Save");
        doneButton.addActionListener(e -> {
            try {
                writeHashTableToFile(SHT, new File("SMHSave.txt"));
                System.out.println("New data written to file");

                new MainMenu(SHT);
            } catch(IOException p){
                System.err.println("Error writing to file: " + p.getMessage());

            }
            frame.dispose();
        });
        panel.add(doneButton);







        panel.add(highlight);

        panel.add(highlightButton);
        tArea.setBounds(0, 500, 700, 700);
        tArea.setEditable(false);
        tArea.setLineWrap(true);
        tArea.setWrapStyleWord(true);
        tArea.setBorder(BorderFactory.createLineBorder(new Color(71, 250, 255)));
        tArea.setCaretColor(Color.white);



        JScrollPane scrollPane = new JScrollPane(tArea);
        panel.add(scrollPane);



        JButton calcButton = new JButton("View Graphs and Calculations");
        calcButton.addActionListener(e -> {


            if(ticker.size() == 1) {


                String[] metrics = {"Date", "Open:", "Close:", "High:", "Low:", "Volume:"};

                //RESOLVED ISSUE: filteredLines is null unless filterText() is called. This is an issue when entering a single stock.

                String[] lines = new String[ticker.size() * 7];

                //The following may be the dumbest code ever written. Basically, if the user had only entered one stock and thus had not used filterText() by removing a stock, the format would be wrong here
                //This is because originally filterText() would actually remove all stocks BUT the selected one, whereas now it removes simply the selected stock.
                //So, these next five lines of code add a new blank element in the stocks dropdown and selects it so that filterText() will correctly format the text in tArea. And then ofc removes the new element.

                stocks.insertItemAt("  ", 1);
                stocks.setSelectedIndex(1);
                filterText();
                stocks.remove(1);
                lines = filteredLines.toArray(lines);

                int metricArrLen = ((lines.length + 1 / ticker.size())) / 8;

                double[] openPrice = new double[metricArrLen];
                double[] closePrice = new double[metricArrLen];
                double[] highPrice = new double[metricArrLen];
                double[] lowPrice = new double[metricArrLen];
                double[] volume = new double[metricArrLen];
                String[] dates = new String[metricArrLen];

                for (String m : metrics) {
                    int metCounter = 0;
                    for (String line : lines) {
                        if (line.startsWith(m)) {
                            if (m.startsWith("Date")) {
                                String temp = line.replace("Date: ", "");
                                dates[metCounter] = temp;

                            }
                            if (m.startsWith("Open:")) {
                                String temp = line.replace("Open: ", "");
                                openPrice[metCounter] = Double.parseDouble(temp);
                            }
                            if (m.startsWith("Close:")) {
                                String temp = line.replace("Close:", "");
                                closePrice[metCounter] = Double.parseDouble(temp);
                            }
                            if (m.startsWith("High:")) {
                                String temp = line.replace("High:", "");
                                highPrice[metCounter] = Double.parseDouble(temp);
                            }
                            if (m.startsWith("Low:")) {
                                String temp = line.replace("Low:", "");
                                lowPrice[metCounter] = Double.parseDouble(temp);
                            }
                            if (m.startsWith("Volume:")) {
                                String temp = line.replace("Volume:", "");
                                volume[metCounter] = Double.parseDouble(temp);
                            }
                            metCounter++;
                        }
                    }


                }

                double overAllPrcntChange = ((closePrice[0] - closePrice[closePrice.length - 1]) / Math.abs(closePrice[closePrice.length - 1])) * 100.0; //perfect
                double overAllMnyChange = (closePrice[0] - closePrice[closePrice.length - 1]); //perfect

                double[] DailyFluxPercent = new double[metricArrLen];
                double[] DailyFluxMny = new double[metricArrLen];
                double sum4Price = 0;
                int sum4Vol = 0;
                double[] totalPrice = new double[metricArrLen];
                for (int i = 0; i < closePrice.length; i++) {
                    Double dailyFluct = ((closePrice[i] - openPrice[i]) / Math.abs(openPrice[i])) * 100;
                    DailyFluxPercent[i] = Double.isFinite(dailyFluct) ? dailyFluct : 0.0;
                    dailyFluct = closePrice[i] - openPrice[i];
                    DailyFluxMny[i] = dailyFluct;
                    sum4Vol += volume[i];
                    sum4Price = sum4Price + closePrice[i];
                }

                double sum4Perc = 0;
                double sum4Mny = 0;

                for (int i = 0; i < DailyFluxPercent.length; i++) {
                    sum4Perc += DailyFluxPercent[i];
                    sum4Mny += DailyFluxMny[i];
                }

                double avgVlume = sum4Vol / volume.length;
                double avgFluxMny = sum4Mny / DailyFluxMny.length;
                double avgFluxPerc = sum4Perc / DailyFluxPercent.length;
                VolatilityCalculator VC = new VolatilityCalculator();
                double volatility = VC.calculateLogReturns(closePrice);

                System.out.println("price len: " + closePrice.length);
                DecimalFormat df = new DecimalFormat("#.##########");
                for (int i = 0; i < metricArrLen; i++) {
                    //System.out.println("\""+ dates[i]+"\""+","+closePrice[i]+","+openPrice[i]+","+highPrice[i]+","+lowPrice[i]+","+df.format(volume[i]));
                }

                String calcs = "Average price: " + sum4Price / closePrice.length + "\nAverage Volume: " + avgVlume + "\nAverage Flux Money: " + avgFluxMny + "\n" +
                        "Average Flux Percent: " + avgFluxPerc + "\nOverall money: " + overAllMnyChange +
                        "\nOverall percent: " + overAllPrcntChange + "%" + "\nVolatility: " + volatility + "\nARIMA Forecast: ";

                GraphFrame GF = new GraphFrame(openPrice, closePrice, highPrice, lowPrice, volume, dates, calcs);
                GF.setVisible(true);

                System.out.println();
                System.out.println("Average Volume: " + avgVlume);
                System.out.println("Average Flux Money: " + avgFluxMny);
                System.out.println("Average Flux Percent: " + avgFluxPerc);
                System.out.println("Overall money: " + overAllMnyChange);
                System.out.println("Overall percent: " + overAllPrcntChange + "%");


                System.out.println(new VolatilityCalculator().calculateLogReturns(closePrice));


            }
        });
        panel.add(calcButton);


        frame.setContentPane(panel);
        frame.setSize(900, 400);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }






    private static void writeHashTableToFile (StockHashTable centralTable, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (LinkedList<StockHashTable.Entry<String, String>> list : centralTable.hashTable) {
                if (list != null) {
                    for (StockHashTable.Entry<String, String> entry : list) {
                        writer.write(entry.key + " " + entry.value + "\n");

                    }
                }
            }
        }
    }
    private void filterText() {
        String selectedType = (String) stocks.getSelectedItem();
        String[] lines = tArea.getText().split("\n");


        boolean foundType = false;
        int counter = 0;
        for (String line : lines) {

            /**
             * This is really rudimentary, but basically if it finds the symbol the user wishes to remove it doesn't add it, or the next six lines of info pertaining to it, into filteredlines.
             * Then it replaces the text in tArea with filteredlines, practically deleting the info the user requested.
             */

            if (line.startsWith("Symbol:") && (!line.contains(selectedType))) {
                filteredLines.add(lines[counter]);
                filteredLines.add(lines[counter+1]);
                filteredLines.add(lines[counter+2]);
                filteredLines.add(lines[counter+3]);
                filteredLines.add(lines[counter+4]);
                filteredLines.add(lines[counter+5]);
                filteredLines.add(lines[counter+6]);


                foundType = true;
            } else if (foundType) {
                if (line.trim().isEmpty()) {
                    // If an empty line is encountered, it indicates the end of the current car info block
                    filteredLines.add(line);
                    foundType = false;
                }
            }
            counter++;
        }

        StringBuilder filteredText = new StringBuilder();
        for (String line : filteredLines) {
            filteredText.append(line).append("\n");
        }

        tArea.setText(filteredText.toString());
    }

    public Color randoColorCreator(){
        int red = ThreadLocalRandom.current().nextInt(000, 255 + 1);
        int green = ThreadLocalRandom.current().nextInt(000, 255 + 1);
        int blue = ThreadLocalRandom.current().nextInt(000, 255 + 1);
        return new Color(red, green, blue);

    }








}