package Classes;

import Interfaces.Material;

public class Layer {
    Material material;
    double startPosition;
    double endPosition;

    public Layer(double startPosition, double endPosition, Material material){
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.material = material;
    }
}
