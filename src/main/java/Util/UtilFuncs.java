package Util;
import com.github.sh0nk.matplotlib4j.builder.ContourBuilder;
import com.github.sh0nk.matplotlib4j.builder.PlotBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Arrays;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;


public class UtilFuncs {

    public static List<List<Double>> convertToList(double[][] arr) {
        List<List<Double>> list = new ArrayList<>();
        for (double[] row : arr) {
            List<Double> innerList = new ArrayList<>();
            for (double val : row) {
                innerList.add(Double.valueOf(val));
            }
            list.add(innerList);
        }
        return list;
    }

    public static void exportDataToCSV(double[][] data, double[] firstRow, double[] firstColumn, String filename) {
        try {
            FileWriter writer = new FileWriter(filename);

            // Write the first row as the first vector
            writer.append(",");
            for (int i = 0; i < firstRow.length; i++) {
                writer.append(Double.toString(firstRow[i]));
                writer.append(",");
            }
            writer.append("\n");

            // Write each row of data with the first column as the second vector
            for (int i = 0; i < data.length; i++) {
                writer.append(Double.toString(firstColumn [i]));
                writer.append(",");
                for (int j = 0; j < data[0].length; j++) {
                    writer.append(Double.toString(data[i][j]));
                    writer.append(",");
                }
                writer.append("\n");
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void plotArrays(double[] xData, double[] yData, String title, String xAxisLabel, String yAxisLabel) {
        // Create a dataset from the x and y data
        XYDataset dataset = createDataset(xData, yData);

        // Create the chart object
        JFreeChart chart = ChartFactory.createScatterPlot(
                title,           // chart title
                xAxisLabel,      // x axis label
                yAxisLabel,      // y axis label
                dataset,         // data
                PlotOrientation.VERTICAL, // orientation
                true,            // include legend
                true,            // tooltips
                false            // urls
        );

        // Customize the plot
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setRangeGridlinePaint(Color.BLACK);

        // Customize the x axis
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setAutoRangeIncludesZero(false);
        domainAxis.setTickLabelFont(domainAxis.getTickLabelFont().deriveFont(12f));

        // Customize the y axis
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(false);
        rangeAxis.setTickLabelFont(rangeAxis.getTickLabelFont().deriveFont(12f));

        // Customize the data points
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));

        // Create the chart panel and display it
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 500));
        JFrame frame = new JFrame("Scatter Plot");
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }

    // Helper method to create a dataset from the x and y data
    private static XYDataset createDataset(double[] xData, double[] yData) {
        XYSeries series = new XYSeries("Data");
        for (int i = 0; i < xData.length; i++) {
            series.add(xData[i], yData[i]);
        }
        return new XYSeriesCollection(series);
    }

}
