package org.example;

import javax.swing.*;

public class TextAreaRowGrouping {
    public String[][] groupStocks(JTextArea tArea ) {
        // Create the frame
        JFrame frame = new JFrame("Row Grouping Example");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String[][] rowGroups;

        // Create the text area with sample text

        frame.add(new JScrollPane(tArea)); // Add a scroll pane to handle overflow

        // Make the frame visible
        frame.setVisible(true);

        // Group rows and store them in a 2D array
        int rowCount = tArea.getLineCount();
        int groupSize = 7; // Two rows per group
        rowGroups = new String[rowCount / groupSize][groupSize];

            try {



                for (int i = 0; i < rowCount; i += groupSize) {
                    int groupIndex = i / groupSize;
                    for (int j = 0; j < groupSize; j++) {
                        int rowIndex = i + j;
                        if (rowIndex < rowCount) {
                            int startOffset = tArea.getLineStartOffset(rowIndex);
                            int endOffset = tArea.getLineEndOffset(rowIndex);
                            rowGroups[groupIndex][j] = tArea.getText(startOffset, endOffset - startOffset);
                        }
                    }
                }

                return rowGroups;

            } catch (Exception e) {
                e.printStackTrace();
            }
        return rowGroups;
    }
}
