package com.leandrosnazareth.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class CreateTiles {
    public static void main(String[] args) throws Exception {
        int tileSize = 32;
        BufferedImage image = new BufferedImage(tileSize * 2, tileSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Bloco de pedra (primeiro tile)
        g2d.setColor(new Color(120, 120, 120)); // Cor base
        g2d.fillRect(0, 0, tileSize, tileSize);

        // Detalhes da pedra
        g2d.setColor(new Color(80, 80, 80));
        g2d.drawRect(0, 0, tileSize - 1, tileSize - 1); // Borda
        for (int i = 0; i < 4; i++) { // Algumas fissuras
            g2d.drawLine(8 * i, 5, 8 * i + 3, 25);
        }

        // Bloco vazio (segundo tile) - já é transparente

        g2d.dispose();
        ImageIO.write(image, "PNG", new File("src/main/resources/textures/tiles.png"));
        System.out.println("Texture created at: src/main/resources/textures/tiles.png");
    }
}