package Interfaces;

import org.apache.commons.numbers.complex.Complex;

public interface Material {
    public Complex n(double T);
    public double[] thermal(double T); //  return: [diffusivity, capacity, density]
}
