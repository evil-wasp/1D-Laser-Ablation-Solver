import Classes.Basic2PhaseMaterial;
import Util.MathFuncs;
import Util.UtilFuncs;
import org.apache.commons.numbers.complex.Complex;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        Basic2PhaseMaterial material = new Basic2PhaseMaterial();
        material.setPhase1Material(2e3, 100, 2e3, Complex.ofCartesian(3, 0.05));
        material.setPhase2Material(1e3, 10, 1e3, Complex.ofCartesian(1, 0));
        material.setPhaseTransition(1000, 20, 5e5);

        double[] T = MathFuncs.linspace(300, 1200, 900);
        double[] cp = new double[T.length];
        double[] diff = new double[T.length];
        double[] dens = new double[T.length];
        for(int i = 0; i < T.length; i++){
            double[] thermal = material.thermal(T[i]);
            cp[i] = thermal[1];
            diff[i] = thermal[0];
            dens[i] = thermal[2];
        }

        UtilFuncs.plotArrays(T, cp, "cp", "T", "cp");
        UtilFuncs.plotArrays(T, diff, "diffusivity", "T", "diff");
        UtilFuncs.plotArrays(T, dens, "density", "T", "dens");

    }


}
