package com.petrolpark.destroy.test;

import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public class CrudeOilGenerationTest {

    public static void main(String args[]) {

        RandomSource random = RandomSource.create(5252525252l);
        PerlinNoise noise = PerlinNoise.create(random, -2, 1d);

        BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
    
        for (int x = 0; x < 100; x += 1d) {
            for (int y = 0; y < 100; y += 1d) {
                bi.setRGB(x, y, Color.getHSBColor(0f, 0f, getNoise(noise, x, y)).getRGB());
            };
        };
        try {
            ImageIO.write(bi, "PNG", new File("src/main/java/com/petrolpark/destroy/test/crude_oil_noise_test.png"));
        } catch (IOException ie) {

        }; 
        
    };

    private static float getNoise(PerlinNoise noise, int x, int y) {
        float value = (float)(noise.getValue(x * 0.7, y * 0.7, 0));
        return value < 0.3d ? 0f : value;
    };
};
