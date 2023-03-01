package Classes;

import Interfaces.Material;
import org.apache.commons.numbers.complex.Complex;

public class BasicMaterial implements Material {

    double cp;         // Heat Capacity
    double cond;       // Conductivity
    double density;    // Density
    Complex n;         // Refractive index

    public void setProperties(double cp, double conduct, double density, Complex n){
        this.cp = cp;
        this.cond = conduct;
        this.density = density;
        this.n = n;
    }

    @Override
    public Complex n(double T) {
        return n;
    }

    @Override
    public double[] thermal(double T) {
        return new double[]{cond/cp/density, cp, density};
    }
}
