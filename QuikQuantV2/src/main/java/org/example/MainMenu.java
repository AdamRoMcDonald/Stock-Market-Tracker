package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;

public class MainMenu extends JFrame {

    private JButton useInf;
    private JButton viewSave;
    private JButton exit;
    private String APIKey;

    public MainMenu(StockHashTable table) throws IOException {
        ImageLoader imageLoader = new ImageLoader();
        BufferedImage logo = imageLoader.loadImage("QuikQuant.jpg");
        BufferedImage icon = imageLoader.loadImage("QQSmallest.PNG");

        // General frame setup
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Main Menu");
        setLayout(new BorderLayout());

        // Top panel setup
        add(topPanel(logo), BorderLayout.NORTH);

        // Buttons setup
        JButton[] buttons = optionButtons(table, icon);

        // Bottom panel setup
        add(bottomPanel(buttons), BorderLayout.SOUTH);

        // APIKey setup
        boolean isFirstLineBlank = isFirstLineBlank("userSettings.txt");
        if (isFirstLineBlank) {
            addApiKeyInput();
        } else {
            addUpdateButton();
        }

        pack();
        setVisible(true);
    }

    private JPanel topPanel(BufferedImage logo) {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.BLUE);
        topPanel.setPreferredSize(new Dimension(600, 300));
        JLabel logoLabel = new JLabel(new ImageIcon(new RescaleImage().getRescaledImage(logo, 3.6)));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(logoLabel);
        return topPanel;
    }

    private JButton[] optionButtons(StockHashTable table, BufferedImage icon) {
        useInf = createButton("Request Stock Info");
        useInf.addActionListener(e -> {
            try {
                if (APIKeyCheck()) {
                    new QueryFrame(table);
                    dispose();
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        viewSave = createButton("View Saved Content");
        viewSave.addActionListener(e -> {
            // Add action for viewing saved content
        });

        exit = createButton("Exit");
        exit.addActionListener(e -> System.exit(0));

        return new JButton[]{useInf, viewSave, exit};
    }

    private JPanel bottomPanel(JButton[] buttons) {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.LIGHT_GRAY);
        bottomPanel.setLayout(new GridLayout(2, 2));
        bottomPanel.setPreferredSize(new Dimension(600, 150));
        for (JButton button : buttons) {
            bottomPanel.add(button);
        }
        return bottomPanel;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        return button;
    }

    public boolean APIKeyCheck() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("userSettings.txt"))) {
            APIKey = reader.readLine();
        }
        if (APIKey == null || APIKey.trim().isEmpty()) {
            while (APIKey == null || APIKey.trim().isEmpty()) {
                APIKey = JOptionPane.showInputDialog("Please enter your API Key:");
                if (APIKey != null && !APIKey.trim().isEmpty()) {
                    saveAPIKey(APIKey);
                }
            }
            return APIKey != null;
        }
        return true;
    }

    public void saveAPIKey(String APIKey) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("userSettings.txt"))) {
            writer.write(APIKey);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save API Key.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isFirstLineBlank(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String firstLine = reader.readLine();
            return firstLine == null || firstLine.trim().isEmpty();
        } catch (IOException e) {
            return true; // Default to true if there's an issue reading the file
        }
    }

    private void addApiKeyInput() {
        JPanel apiKeyPanel = new JPanel(new FlowLayout());
        JTextField apiKeyField = new JTextField(15);
        JButton enterButton = new JButton("Enter API Key");

        enterButton.addActionListener(e -> {
            String apiKey = apiKeyField.getText();
            if (apiKey != null && !apiKey.trim().isEmpty()) {
                try {
                    saveAPIKey(apiKey);
                    APIKey = apiKey;
                    JOptionPane.showMessageDialog(this, "API Key saved successfully.");
                    remove(apiKeyPanel);
                    addUpdateButton();
                    revalidate();
                    repaint();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving API Key: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "API Key cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        apiKeyPanel.add(new JLabel("API Key:"));
        apiKeyPanel.add(apiKeyField);
        apiKeyPanel.add(enterButton);
        add(apiKeyPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void addUpdateButton() {
        JPanel updatePanel = new JPanel(new FlowLayout());
        JButton updateButton = new JButton("Update API Key");

        updateButton.addActionListener(e -> {
            remove(updatePanel);
            addApiKeyInput();
            revalidate();
            repaint();
        });

        updatePanel.add(updateButton);
        add(updatePanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
