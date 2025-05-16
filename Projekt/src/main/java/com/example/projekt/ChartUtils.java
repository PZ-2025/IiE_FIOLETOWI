package com.example.projekt;

import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Map;

public class ChartUtils {

    public static void saveChartAsPNG(Map<String, Integer> data, String title, String outputPath) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((key, value) -> dataset.addValue(value, "Dane", key));
        JFreeChart chart = ChartFactory.createBarChart(title, "", "", dataset);

        try {
            // Tworzenie obrazu z wykresu
            BufferedImage image = chart.createBufferedImage(500, 300);
            // Zapisanie obrazu jako plik PNG
            ImageIO.write(image, "png", new File(outputPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ImageView createChartImage(Map<String, Integer> data, String title) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((key, value) -> dataset.addValue(value, "Dane", key));
        JFreeChart chart = ChartFactory.createBarChart(title, "", "", dataset);

        BufferedImage bufferedImage = chart.createBufferedImage(500, 300);
        WritableImage fxImage = javafx.embed.swing.SwingFXUtils.toFXImage(bufferedImage, null);
        return new ImageView(fxImage);
    }
}
