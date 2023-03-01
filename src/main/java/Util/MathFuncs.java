package Util;
import org.apache.commons.numbers.complex.Complex;

public class MathFuncs {

    public static double[] linspace(double start, double end, int count){
        double[] array = new double[count];
        double step = (end - start)/count;
        for(int i = 0; i < count; i++){
            array[i] = i*step + start;
        }
        return array;
    }

    public static Complex[][] multiplyMatrices(Complex[][] A, Complex[][] B) {
        int n = A.length;
        int m = A[0].length;
        int p = B[0].length;
        Complex[][] C = new Complex[n][p];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < p; j++) {
                C[i][j] = Complex.ZERO;
                for (int k = 0; k < m; k++) {
                    C[i][j] = C[i][j].add(A[i][k].multiply(B[k][j]));
                }
            }
        }
        return C;

    }

    public static Complex[] multiplyMatrixVector(Complex[][] matrix, Complex[] vector) {
        int numRows = matrix.length;
        int numCols = matrix[0].length;
        if (numCols != vector.length) {
            throw new IllegalArgumentException("Matrix and vector dimensions do not match");
        }
        Complex[] result = new Complex[numRows];
        for (int i = 0; i < numRows; i++) {
            result[i] = Complex.ZERO;
            for (int j = 0; j < numCols; j++) {
                result[i] = result[i].add(matrix[i][j].multiply(vector[j]));
            }
        }
        return result;
    }

    public static Complex[] addArrays(Complex[] array1, Complex[] array2) {
        if (array1.length != array2.length) {
            throw new IllegalArgumentException("Array dimensions do not match");
        }
        Complex[] result = new Complex[array1.length];
        for (int i = 0; i < array1.length; i++) {
            result[i] = array1[i].add(array2[i]);
        }
        return result;
    }

    public static double[] absComplexArray(Complex[] array) {
        double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].abs();
        }
        return result;
    }

    public static double[] multiplyArrays(double[] array1, double[] array2) {
        if (array1.length != array2.length) {
            throw new IllegalArgumentException("Array dimensions do not match");
        }
        double[] result = new double[array1.length];
        for (int i = 0; i < array1.length; i++) {
            result[i] = array1[i] * array2[i];
        }
        return result;
    }

    public static double[][] multiplyArrays(double[][] arr1, double[][] arr2) {
        if (arr1.length != arr2.length || arr1[0].length != arr2[0].length) {
            throw new IllegalArgumentException("Arrays must have the same dimensions");
        }

        int rows = arr1.length;
        int cols = arr1[0].length;

        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = arr1[i][j] * arr2[i][j];
            }
        }

        return result;
    }


    public static Complex[] multiplyByScalar(Complex[] array, double scalar) {
        Complex[] result = new Complex[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].multiply(scalar);
        }
        return result;
    }

    public static Complex[] indexToPermittivity(Complex[] index){
        Complex[] permittivity = new Complex[index.length];
        for (int i = 0; i < index.length; i++) {
            permittivity[i] = Complex.ofCartesian(index[i].real() * index[i].real() + index[i].imag() * index[i].imag(),
                   2 * index[i].real() * index[i].imag());
        }
        return permittivity;
    }


    public static double[] getRealValues(Complex[] array) {
        double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].getReal();
        }
        return result;
    }

    public static double[] getImagValues(Complex[] array) {
        double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].getImaginary();
        }
        return result;
    }
}


