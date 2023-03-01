package Classes;

import Util.MathFuncs;
import org.apache.commons.numbers.complex.Complex;

import java.util.Arrays;

public class TMSolver {

    double wl;  //wavelength
    double[] layerThicknesses;
    Complex[] layern;
    boolean geometryIsDefined;
    boolean isSolved;
    Complex inputE;
    Complex[] Ef;
    Complex[] Eb;
    double transmission;
    double reflection;

    public TMSolver(){
        wl = 1;
        inputE = Complex.ofCartesian(1,0);
        geometryIsDefined = false;
        isSolved = false;
    }

    public void solve(){
        if (!geometryIsDefined){
            return;
        }
        int Nx = layerThicknesses.length;
        Ef = new Complex[Nx];
        Eb = new Complex[Nx];
        Complex[][][] matricees = new Complex[Nx-1][2][2];


        // Get full transfer matrix to get reflection
        Complex[][] fullMatrix = new Complex[][]{{Complex.ofCartesian(1, 0), Complex.ofCartesian(0, 0)},
                {Complex.ofCartesian(0, 0), Complex.ofCartesian(1, 0)}};

        for (int i = 1; i < Nx; i++){
            matricees[i-1] = getTransferMatrix(layerThicknesses[i], layern[i-1], layern[i]);
            fullMatrix = MathFuncs.multiplyMatrices(fullMatrix, matricees[i-1]);
        }

        Complex complexReflection = fullMatrix[1][0].divide(fullMatrix[1][1]).multiply(-1);
        Complex complexTransmission = fullMatrix[0][0].subtract(fullMatrix[1][0].multiply(fullMatrix[0][1]).divide(fullMatrix[1][1]));
        transmission = complexTransmission.abs() * complexTransmission.abs();
        reflection = complexReflection.abs() * complexReflection.abs();

        Ef[0] = inputE;
        Eb[0] = complexReflection.multiply(Ef[0]);
        for(int i = 1; i < Nx; i++){
            Complex[] EfbOld = new Complex[] {Ef[i-1], Eb[i-1]};
            Complex[] EfbNew = MathFuncs.multiplyMatrixVector(matricees[i-1], EfbOld);
            Ef[i] = EfbNew[0];
            Eb[i] = EfbNew[1];
        }

        isSolved = true;

    }

    public Complex[][] getTransferMatrix(double thickness, Complex nLast, Complex nCurrent){
        Complex[][] matrix = new Complex[2][2];

        Complex k = nLast.multiply(6.2830).divide(wl);
        double a1 = 2 * nLast.real() / (nLast.real() + nCurrent.real());
        double a2 = (nLast.real() - nCurrent.real()) / (nLast.real() + nCurrent.real());

        matrix[0][0] = k.multiply(thickness).multiply(Complex.I).exp().divide(a1);
        matrix[0][1] = k.multiply(- thickness).multiply(Complex.I).exp().multiply(a2/a1);
        matrix[1][0] = k.multiply(thickness).multiply(Complex.I).exp().multiply(a2/a1);
        matrix[1][1] = k.multiply(- thickness).multiply(Complex.I).exp().divide(a1);

        return matrix;
    }

    // -------------------------------
    // Input parameters
    // -------------------------------

    public void setInputE(Complex E){
        this.inputE = E;
    }

    public boolean defineGeometry(double[] layerThicknesses, Complex[] layern){
        if(layerThicknesses.length == layern.length){
            this.layerThicknesses = layerThicknesses;
            this.layern = layern;
            geometryIsDefined = true;
        }
        return geometryIsDefined;
    }

    public boolean defineGeometryWithEvenSteps(double thickness, Complex[] layern){

        this.layerThicknesses = new double[layern.length];
        Arrays.fill(layerThicknesses, thickness);
        this.layern = layern;
        geometryIsDefined = true;

        return geometryIsDefined;
    }

    public void setWl(double wavelenghth){
        this.wl = wavelenghth;
    }

    // -------------------------------
    // Output results
    // -------------------------------

    public Complex[] getEf(){
        if (!isSolved){
            return null;
        }
        return Ef;
    }

    public Complex[] getEb(){
        if (!isSolved){
            return null;
        }
        return Eb;
    }

    public Complex[] getE(){
        if (!isSolved){
            return null;
        }
        return MathFuncs.addArrays(Ef, Eb);
    }

    public double[] getAbsE(){
        if (!isSolved){
            return null;
        }
        return MathFuncs.absComplexArray(MathFuncs.addArrays(Ef, Eb));
    }

    public double[] getQ(){
        if (!isSolved){
            return null;
        }
        double[] absE = MathFuncs.absComplexArray(MathFuncs.addArrays(Ef, Eb));
        double[] epsImag = MathFuncs.getImagValues(MathFuncs.indexToPermittivity(layern));

        return MathFuncs.multiplyArrays(absE, epsImag);
    }

    public double getReflection(){
        return reflection;
    }

    public double getTransmission(){
        return transmission;
    }
}
