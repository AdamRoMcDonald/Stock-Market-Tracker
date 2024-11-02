package org.example;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RescaleImage {
    public Image getRescaledImage(Image image, double scale) {
        double width = image.getWidth(null) / scale;
        double height = image.getHeight(null) / scale;
        BufferedImage resizedImage = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, 0, 0, (int) width, (int) height, null);
        g2.dispose();
        return resizedImage;
    }



}
