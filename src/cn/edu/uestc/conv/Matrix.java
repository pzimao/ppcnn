package cn.edu.uestc.conv;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.math.BigInteger;

public class Matrix {
    BigInteger[][] matrix;

    public Matrix(int height, int width) {
        matrix = new BigInteger[height][];
        for (int i = 0; i < height; i++) {
            matrix[i] = new BigInteger[width];
        }
    }

    public Matrix(int size) {
        matrix = new BigInteger[size][];
        for (int i = 0; i < size; i++) {
            matrix[i] = new BigInteger[size];
        }
    }

    public int getHeight() {
        return matrix.length;
    }

    public int getWidth() {
        if (matrix.length > 0) {
            return matrix[0].length;
        }
        return 0;
    }

    public void setValue(int rowIndex, int colIndex, BigInteger value) {
        this.matrix[rowIndex][colIndex] = value;
    }

    public BigInteger getValue(int rowIndex, int colIndex) {
        return this.matrix[rowIndex][colIndex];
    }

    public BigInteger getValue(int index) {
        return this.matrix[index / getWidth()][index % getWidth()];
    }

    public Matrix duplicate() {
        return new Matrix(getHeight(), getWidth());
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (BigInteger[] row : matrix) {
            for (BigInteger value : row) {
                sb.append(value + "\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public int length() {
        return this.getWidth() * this.getHeight();
    }

    public boolean check(Matrix targetMatrix) {
        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                if (this.getValue(i, j).compareTo(targetMatrix.getValue(i, j)) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    // 矩阵检查
    public static boolean check(Matrix srcMatrix, Matrix targetMatrix) {
        return srcMatrix.check(targetMatrix);
    }

    public static boolean check(Matrix[] srcMatrix, Matrix[] targetMatrix) {
        if (srcMatrix.length != targetMatrix.length) {
            return false;
        }
        for (int i = 0; i < srcMatrix.length; i++) {
            if (!check(srcMatrix[i], targetMatrix[i])) {
                return false;
            }
        }
        return true;
    }
}
