package Classes;
import Interfaces.EFieldInputAmplitude;
import Interfaces.Material;
import Util.MathFuncs;
import org.apache.commons.numbers.complex.Complex;

import java.util.Arrays;
import java.util.LinkedList;

public class Simulation {

    public static double[] T0;    // Initial temperature
    public static double[] x0;    // x = 0 boundary
    public static double[] xMax;  // x = Nx-1 boundary

    public static int Nx;         // x steps count
    public static double xSpan;   // x range
    public static double dx;      // x step
    public static int Nt;         // time steps count
    public static double tSpan;   // time range
    public static double dt;      // time step
    public static double wl;      // wavelength

    public static Material backgroundMaterial;            // Default material
    LinkedList<Layer> layers;               // Material layers
    EFieldInputAmplitude EInputFunction;    // Input field amplitude (laser pulse temporal profile)

    double[] x;             // x array
    double[] t;             // time array
    Material[] materials;   // material distribution

    double[][] T;       // Temperature data
    // double[][] v;    // Velocity data
    Complex[][] E;      // E field data
    Complex[][] n;      // Refractive index data
    double[][] Q;       // Heating data

    double[] transmission;      // Optical transmission
    double[] reflection;        // Optical reflection

    boolean complete;       // Is the simulation complete

    public Simulation(){
        layers = new LinkedList<Layer>();
        wl = 1e-6;
        complete = false;
    }

    public void processSetup() throws IncorrectSimulationSetup {
        if (dx == 0) {
            throw new IncorrectSimulationSetup("lacking space step");
        }
        if (xSpan == 0) {
            throw new IncorrectSimulationSetup("lacking space span");
        }
        if (dt == 0) {
            throw new IncorrectSimulationSetup("lacking time step");
        }
        if (tSpan == 0) {
            throw new IncorrectSimulationSetup("lacking time step");
        }

        Nx = (int)(xSpan/dx);
        Nt = (int)(tSpan/dt);

        if (T0 == null) {
            throw new IncorrectSimulationSetup("lacking initial temperature");
        }else if (T0.length != Nx) {
            throw new IncorrectSimulationSetup("Initial temperature data length does not match simulation length");
        }
        if (x0 == null) {
            throw new IncorrectSimulationSetup("lacking x = 0 boundary");
        } else if (x0.length != Nt) {
            throw new IncorrectSimulationSetup("x = 0 boundary data length does not match simulation length");
        }
        if (xMax == null) {
            throw new IncorrectSimulationSetup("lacking x = max boundary");
        }else if (xMax.length != Nt) {
            throw new IncorrectSimulationSetup("x = max boundary data length does not match simulation length");
        }
        if (backgroundMaterial == null) {
            throw new IncorrectSimulationSetup("lacking background material");
        }
        if (EInputFunction == null) {
            throw new IncorrectSimulationSetup("lacking E-field input");
        }

        // Initialize arrays

        x = MathFuncs.linspace(0, xSpan, Nx);
        t = MathFuncs.linspace(0, tSpan, Nt);
        materials = new Material[Nx];

        T = new double[Nt][Nx];
        E = new Complex[Nt][Nx];
        n = new Complex[Nt][Nx];
        Q = new double[Nt][Nx];

        transmission = new double[Nt];
        reflection = new double[Nt];

        // Fill in initial/boundary values

        for(int i = 0; i < Nx; i++){
            T[0][i] = T0[i];
        }
        for(int i = 0; i < Nt; i++){
            T[i][0] = x0[i];
            T[i][Nx-1] = xMax[i];
            E[i][0] = Complex.ofCartesian(EInputFunction.E(i*dt), 0);
        }

        // setup geometry
        materials = new Material[Nx];
        Arrays.fill(materials, backgroundMaterial);
        for (Layer layer : layers) {
            for (int i = 0; i < Nx; i++){
                if((x[i] >= layer.startPosition) & (x[i] < layer.endPosition)){
                    materials[i] = layer.material;
                }
            }
        }

    }

    public void run() throws IncorrectSimulationSetup {
        try {processSetup();}
        catch (IncorrectSimulationSetup ex) {throw ex;}

        for(int i = 0; i < Nx; i++){
            n[0][i] = materials[i].n(T[0][i]);
        }
        for(int i = 1; i < Nx; i++){
            E[0][i] = Complex.ofCartesian(0,0);
        }

        for(int i = 1; i < Nt; i++){

            for(int j = 0; j < Nx; j++){
                n[i][j] = materials[j].n(T[i-1][j]);
            }

            TMSolver opticsSolver = new TMSolver();
            opticsSolver.setWl(wl);
            opticsSolver.setInputE(E[i][0]);
            opticsSolver.defineGeometryWithEvenSteps(dx, n[i]);
            opticsSolver.solve();

            E[i] = opticsSolver.getE();
            Q[i] = opticsSolver.getQ();
            transmission[i] = opticsSolver.getTransmission();
            reflection[i] = opticsSolver.getReflection();

            for(int j = 1; j < Nx-1; j++){
                double[] thermalProp = materials[j].thermal(T[i-1][j]);
                T[i][j] = heatEQStep(T[i-1][j], T[i-1][j+1], T[i][j-1], Q[i][j],
                        thermalProp[0], thermalProp[1], thermalProp[2]);
                }
        }

        complete = true;
    }

    private double heatEQStep(double TPrevious, double TRight, double TLeft, double Q, double diff, double capacity, double density){
        return TPrevious + ((diff*dt/dx/dx) * (TRight - (2*TPrevious) + TLeft)) + (Q/capacity/density);
    }

    // --------------------------------
    //  Parameter input methods
    // --------------------------------

    public void setSteps(double dx, double dt){
        this.dx = dx;
        this.dt = dt;
    }

    public void setRanges(double xSpan, double tSpan){
        this.xSpan = xSpan;
        this.tSpan = tSpan;
    }

    public void setT0(double[] T0){
        this.T0 = T0;
    }

    public void setX0(double[] x0){
        this.x0 = x0;
    }

    public void setXMax(double[] xMax){
        this.xMax = xMax;
    }

    public void setWavelength(double wavelength){
        this.wl = wavelength;
    }

    public void setBackgroundMaterial(Material backgroundMaterial){
        this.backgroundMaterial = backgroundMaterial;
    }

    public void insertLayer(Layer layer){
        layers.add(layer);
    }

    public void setFieldInput(EFieldInputAmplitude EInputFunction){
        this.EInputFunction = EInputFunction;
    }

    // --------------------------------
    //  Results extraction
    // --------------------------------

    public double[][] getTemperature(){
        return T;
    }

    public Complex[][] getEField(){
        return E;
    }

    public Complex[][] getRefractiveIndex(){
        return n;
    }

    public double[][] getHeatingPower(){
        return Q;
    }

    public double[] getTransmission(){
        return transmission;
    }

    public double[] getReflection(){
        return reflection;
    }

    public double[][] getIntensity(){
        if (complete) {
            double[][] abs = new double[Nt][Nx];
            for (int i = 0; i < Nt; i++) {
                for (int j = 0; j < Nx; j++) {
                    abs[i][j] = E[i][j].abs() * E[i][j].abs();
                }
            }
            return abs;
        }else {
            return null;
        }
    }

    public double[][] getRealE(){
        if (complete) {
            double[][] real = new double[Nt][Nx];
            for (int i = 0; i < Nt; i++) {
                for (int j = 0; j < Nx; j++) {
                    real[i][j] = E[i][j].real();
                }
            }
            return real;
        }else {
            return null;
        }
    }

    public double[][] getImagE(){
        if (complete) {
            double[][] imag = new double[Nt][Nx];
            for (int i = 0; i < Nt; i++) {
                for (int j = 0; j < Nx; j++) {
                    imag[i][j] = E[i][j].imag();
                }
            }
            return imag;
        }else {
            return null;
        }
    }

    public double[][] getRealIndex(){
        if (complete) {
            double[][] real = new double[Nt][Nx];
            for (int i = 0; i < Nt; i++) {
                for (int j = 0; j < Nx; j++) {
                    real[i][j] = n[i][j].real();
                }
            }
            return real;
        }else {
            return null;
        }
    }

    public double[][] getImagIndex(){
        if (complete) {
            double[][] imag = new double[Nt][Nx];
            for (int i = 0; i < Nt; i++) {
                for (int j = 0; j < Nx; j++) {
                    imag[i][j] = n[i][j].imag();
                }
            }
            return imag;
        }else {
            return null;
        }
    }

    public double[][] getHeatCapacity(){
        double[][] cp = new double[Nt][Nx];
        for (int i = 0; i < Nt; i++){
            for (int j = 0; j < Nx; j++){
                cp[i][j] = materials[j].thermal(T[i][j])[1];
            }
        }
        return cp;
    }

    public double[][] getDiffusivity(){
        double[][] diff = new double[Nt][Nx];
        for (int i = 0; i < Nt; i++){
            for (int j = 0; j < Nx; j++){
                diff[i][j] = materials[j].thermal(T[i][j])[0];
            }
        }
        return diff;
    }

    public double[][] getDensity(){
        double[][] density = new double[Nt][Nx];
        for (int i = 0; i < Nt; i++){
            for (int j = 0; j < Nx; j++){
                density[i][j] = materials[j].thermal(T[i][j])[2];
            }
        }
        return density;
    }

    public double[] getX(){
        return x;
    }

    public double[] gett(){
        return t;
    }
}


