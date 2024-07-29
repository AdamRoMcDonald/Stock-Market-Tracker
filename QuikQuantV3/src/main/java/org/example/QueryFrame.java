package org.example;
import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.ZonedDateTime;

public class QueryFrame extends JFrame {
    ArrayList<String> symbols = new ArrayList<>();


    public QueryFrame(StockHashTable table, BufferedImage icon) {
        setTitle("Query Frame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setIconImage(icon);
        setBackground(new Color(33, 49, 101));

        JTextArea textArea = new JTextArea(5, 15); // Set rows and columns for JTextArea
        textArea.setEditable(false);

        JButton remove = new JButton("Remove Item Selected in Dropbox");
        JButton enterButton = new JButton("Enter");
        JTextField symField = new JTextField("Type Symbol", 10);

        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setPreferredSize(new Dimension(75, comboBox.getPreferredSize().height));
        JPanel toppanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.add(symField);

        enterButton.addActionListener(e ->{
            textArea.append(symField.getText()+'\n');
            comboBox.addItem(symField.getText());
            symbols.add(symField.getText());
            symField.setText("");
        });

        inputPanel.add(enterButton);
        inputPanel.setBackground(new Color(33, 49, 101));
        toppanel.add(inputPanel, BorderLayout.NORTH);
        toppanel.setBackground(new Color(33, 49, 101));
        toppanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        toppanel.add(comboBox, BorderLayout.EAST);

        remove.addActionListener(l -> {
            String selected = comboBox.getSelectedItem().toString();
            textArea.setText(textArea.getText().replaceAll(selected + '\n', ""));
            comboBox.removeItem(selected);
            symbols.remove(selected);
        });

        // Panel for dropdown and remove button
        JPanel dropboxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Left-align components
        dropboxPanel.setBackground(new Color(33, 49,101));
        dropboxPanel.add(comboBox);
        dropboxPanel.add(remove);

        JPanel bottompanel = new JPanel();
        bottompanel.setBackground(new Color(33, 49,101));
        bottompanel.setPreferredSize(new Dimension(400, 100));
        JDateChooser dateChooser1 = new JDateChooser();
        JDateChooser dateChooser2 = new JDateChooser();
        bottompanel.add(dateChooser1);
        bottompanel.add(dateChooser2);
        JButton showDateButton = new JButton("Enter Dates and Complete Request");
        JLabel dateLabel = new JLabel("Selected Date: ");
        bottompanel.add(showDateButton);
        bottompanel.add(dateLabel);

        showDateButton.addActionListener(e -> {
            String startDate = dateChooser1.getDate().toString();
            String endDate = dateChooser2.getDate().toString();
            ArrayList<String> dates = formatDates(startDate, endDate);
            try {
                System.out.println("Start Date: " + dates.get(2)+ " End date: "+dates.get(3));
                long startTime = System.nanoTime();
                new APIAccessor().infoLoader(symbols, dates.get(2), dates.get(3), table, icon);
                long endTime = System.nanoTime();

                long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
                System.out.println("Time spent retrieving info: " + duration/1000000);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }


        });

        add(toppanel, BorderLayout.NORTH);
        add(bottompanel, BorderLayout.SOUTH);
        add(dropboxPanel, BorderLayout.CENTER); // Add dropbox and remove button panel to the southwest corner

        pack(); // Sizes the frame to fit its contents
        setVisible(true);
    }


    public ArrayList<String> formatDates(String startDate, String endDate) {
        ArrayList<String> dates = new ArrayList<>();
        dates.add(startDate);
        dates.add(endDate);
        for(int i = 0; i < 2 ; i++) {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dates.get(i), inputFormatter);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = zonedDateTime.format(outputFormatter);
            dates.add(formattedDate);
        }
        return dates;

    }

}
