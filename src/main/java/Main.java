import Classes.Basic2PhaseMaterial;
import Classes.BasicMaterial;
import Classes.Layer;
import Classes.Simulation;
import Interfaces.EFieldInputAmplitude;
import Classes.*;
import Util.MathFuncs;
import Util.UtilFuncs;
import Util.XYZChart;
import com.github.sh0nk.matplotlib4j.NumpyUtils;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import com.github.sh0nk.matplotlib4j.builder.ContourBuilder;
import com.github.sh0nk.matplotlib4j.builder.PColorBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.numbers.complex.Complex;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        //create simulation
        Simulation sim = new Simulation();

        // define materials
        BasicMaterial air = new BasicMaterial();
        air.setProperties(1e3, 2e-2, 1.3, Complex.ofCartesian(1,0));

        Basic2PhaseMaterial material1 = new Basic2PhaseMaterial();
        material1.setPhase1Material(1.3e3, 100, 2.7e3, Complex.ofCartesian(3, 0.05));
        material1.setPhase2Material(1.3e3, 100, 2.7e3, Complex.ofCartesian(1, 0));
        material1.setPhaseTransition(900, 20, 1e5);

        Basic2PhaseMaterial material2 = new Basic2PhaseMaterial();
        material2.setPhase1Material(1.3e3, 10, 2.7e3, Complex.ofCartesian(2, 0.05));
        material2.setPhase2Material(1.3e3, 10, 2.7e3, Complex.ofCartesian(1, 0));
        material2.setPhaseTransition(600, 20, 1e5);

        // define parameters

        sim.setRanges(5e-6, 1e-9);     // space and time simulation range
        sim.setSteps(10e-9, 0.5e-12);        // space and time resolution
        sim.setWavelength(1e-6);                    // light wavelength
        double[] T0 = new double[(int)(sim.xSpan/sim.dx)];
        double[] x0 = new double[(int)(sim.tSpan/sim.dt)];
        double[] xMax = new double[(int)(sim.tSpan/sim.dt)];
        Arrays.fill(T0, 300);
        Arrays.fill(x0, 300);
        Arrays.fill(xMax, 300);
        sim.setT0(T0);          // initial temperature
        sim.setX0(x0);          // x = 0 boundary temperature
        sim.setXMax(xMax);      // x = Nx-1 boundary temperature
        sim.setBackgroundMaterial(air);

        // add Layers
        sim.insertLayer(new Layer(1e-6, 2e-6, material2));
        sim.insertLayer(new Layer(2e-6, 3e-6, material1));
        sim.insertLayer(new Layer(3e-6, 4e-6, material2));

        // set E-field input
        EFieldInputAmplitude exitationField = new EFieldInputAmplitude() {
            double t0 = 3e-10;
            double dt = 3e-10;
            double amp = 2e7;
            @Override
            public double E(double t) { return amp / 2 * Math.exp(- Math.pow((t-t0)/dt, 2)); }
        };
        sim.setFieldInput(exitationField);  // Incident field slow-varying amplitude

        try {
            sim.run();
        } catch (IncorrectSimulationSetup e) {
            throw new RuntimeException(e);
        }

        // ----------------
        // Plotting
        // ----------------

        //python-ish plotting
        /*
        List<Double> doublearrayt = Arrays.asList(ArrayUtils.toObject(sim.gett()));
        List<Double> doublearrayx = Arrays.asList(ArrayUtils.toObject(sim.getX()));
        List<List<Double>> data = UtilFuncs.convertToList(sim.getTemperature());
        Plot plt1 = Plot.create();
        PColorBuilder contour = plt1.pcolor().add(doublearrayx, doublearrayt, data);

        plt1.title("contour");
        plt1.legend().loc("upper right");
        try {
            plt1.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (PythonExecutionException e) {
            throw new RuntimeException(e);
        }
         */


        XYZChart temperaturePlot = new XYZChart();
        temperaturePlot.setxLabel("time");
        temperaturePlot.setyLabel("x");
        temperaturePlot.setTitle("Temperature");
        temperaturePlot.setData(sim.getTemperature(), sim.gett(), sim.getX());
        temperaturePlot.makeChart();

        XYZChart indexPlot = new XYZChart();
        indexPlot.setxLabel("time");
        indexPlot.setyLabel("x");
        indexPlot.setTitle("index");
        indexPlot.setData(sim.getRealIndex(), sim.gett(), sim.getX());
        indexPlot.makeChart();

        XYZChart EPlot = new XYZChart();
        EPlot.setxLabel("time");
        EPlot.setyLabel("x");
        EPlot.setTitle("abs E^2");
        EPlot.setData(sim.getIntensity(), sim.gett(), sim.getX());
        EPlot.makeChart();


    }
}