package Util;

import java.awt.Color;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Paint;
import javax.swing.JFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;


public class XYZChart {

    private static final int N = 1000;
    String xLabel;
    String yLabel;
    String title;
    double[][] array;
    double[] x;
    double[] y;
    double min;
    double max;


    public void makeChart() {
        JFrame f = new JFrame(title);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ChartPanel chartPanel = new ChartPanel(createChart(createDataset(array, x, y))) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(640, 480);
            }
        };
        chartPanel.setMouseZoomable(true, false);
        f.add(chartPanel);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    public JFreeChart createChart(XYDataset dataset) {
        NumberAxis xAxis = new NumberAxis(xLabel + " ps");
        NumberAxis yAxis = new NumberAxis(yLabel + " nm");
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        XYBlockRenderer r = new XYBlockRenderer();
        SpectrumPaintScale ps = new SpectrumPaintScale(min, max);
        r.setPaintScale(ps);
        r.setBlockHeight(10.0f);
        r.setBlockWidth(10.0f);
        plot.setRenderer(r);
        JFreeChart chart = new JFreeChart(title,
                JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        NumberAxis scaleAxis = new NumberAxis("Scale");
        scaleAxis.setAxisLinePaint(Color.white);
        scaleAxis.setTickMarkPaint(Color.white);
        PaintScaleLegend legend = new PaintScaleLegend(ps, scaleAxis);
        legend.setSubdivisionCount(128);
        legend.setAxisLocation(AxisLocation.TOP_OR_RIGHT);
        legend.setPadding(new RectangleInsets(10, 10, 10, 10));
        legend.setStripWidth(20);
        legend.setPosition(RectangleEdge.RIGHT);
        legend.setBackgroundPaint(Color.WHITE);
        chart.addSubtitle(legend);
        chart.setBackgroundPaint(Color.white);
        return chart;
    }

    private XYZDataset createDataset(double[][] array, double[] x, double[] y) {
        DefaultXYZDataset dataset = new DefaultXYZDataset();
        min = array[0][0];
        max = array[0][0];
        for (int i = 0; i < x.length; i++) {
            double[][] data = new double[3][y.length];
            for (int j = 0; j < y.length; j++) {
                if (array[i][j] < min){
                    min = array[i][j];
                }
                if (array[i][j] > max){
                    max = array[i][j];
                }
                data[0][j] = x[i]*1e12;
                data[1][j] = y[j]*1e9;
                data[2][j] = array[i][j];
            }
            dataset.addSeries("Series" + i, data);
        }
        return dataset;
    }

    public void setData(double[][] array, double[] x, double[] y){
        this.array = array;
        this.x = x;
        this.y = y;
    }
    public void setxLabel(String xlabel){
        this.xLabel = xlabel;
    }

    public void setyLabel(String ylabel){
        this.yLabel = ylabel;
    }

    public void setTitle(String title){
        this.title = title;
    }

    private static class SpectrumPaintScale implements PaintScale {

        private static final float H1 = 0f;
        private static final float H2 = 1f;
        private final double lowerBound;
        private final double upperBound;

        public SpectrumPaintScale(double lowerBound, double upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        @Override
        public double getLowerBound() {
            return lowerBound;
        }

        @Override
        public double getUpperBound() {
            return upperBound;
        }

        @Override
        public Paint getPaint(double value) {
            float scaledValue = (float) ((value - getLowerBound()) / (getUpperBound() - getLowerBound()));
            float scaledH = H1 + scaledValue * (H2 - H1);
            Color c1 = Color.red;
            Color c2 = Color.blue;
            return mix(c1, c2, scaledValue);
        }

        public static Color mix(Color a, Color b, double percent) {
            return new Color((int) (a.getRed() * percent + b.getRed() * (1.0 - percent)),
                    (int) (a.getGreen() * percent + b.getGreen() * (1.0 - percent)),
                    (int) (a.getBlue() * percent + b.getBlue() * (1.0 - percent)));
        }
    }
}