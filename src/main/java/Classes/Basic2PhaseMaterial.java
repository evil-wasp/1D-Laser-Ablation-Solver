package Classes;

import Interfaces.Material;
import org.apache.commons.numbers.complex.Complex;
import org.apache.commons.math3.special.Erf;

public class Basic2PhaseMaterial implements Material {

    double phaseT;              // Transition temperature
    double transitionRange;     // Transition temperatures range
    double latentHeat;          // Latent heat

    // Phase 1 properties
    double cp1;         // Heat Capacity
    double cond1;       // Conductivity
    double density1;    // Density
    Complex n1;         // Refractive index

    // Phase 2 properties
    double cp2;         // Heat Capacity
    double cond2;       // Conductivity
    double density2;    // Density
    Complex n2;         // Refractive index

    public void Basic2PhaseMaterial(){

    }

    public void setPhase1Material(double cp, double conduct, double density, Complex n){
        cp1 = cp;
        cond1 = conduct;
        density1 = density;
        n1 = n;
    }

    public void setPhase2Material(double cp, double conduct, double density, Complex n){
        cp2 = cp;
        cond2 = conduct;
        density2 = density;
        n2 = n;
    }

    public void setPhaseTransition(double phaseT, double range, double latentHeat){
        this.phaseT = phaseT;
        this.transitionRange = range;
        this.latentHeat = latentHeat;
    }

    @Override
    public Complex n(double T) {
        double phaseFraction = (1 + Erf.erf((T - phaseT)/transitionRange))/2;
        Complex n = n1.add( n2.subtract(n1).multiply(phaseFraction));
        return n;
    }

    @Override
    public double[] thermal(double T) {
        double[] thermal = new double[3];

        double phaseFraction = (1 + Erf.erf((T - phaseT)/transitionRange))/2;


        //capacity
        thermal[1] = ((cp1) + ((cp2 - cp1) * phaseFraction))
                + latentHeat * (1/transitionRange/2/Math.sqrt(3.1415))
                    * Math.exp((0-1)*Math.pow((T-phaseT)/transitionRange, 2));
        //density
        thermal[2] = (density1) + ((density2 - density1) * phaseFraction);
        //diffusivity
        thermal[0] = ((cond1) + ((cond2 - cond1) * phaseFraction)) / thermal[1] / thermal[2];


        return thermal;
    }
}
