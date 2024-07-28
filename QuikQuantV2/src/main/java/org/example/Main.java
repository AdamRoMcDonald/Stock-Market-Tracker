package org.example;


import java.awt.image.BufferedImage;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        StockHashTable table = new StockHashTable();



        new MainMenu(table);
    }
}