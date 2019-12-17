package cn.edu.uestc.party;

import cn.edu.uestc.conv.ConvMode;
import cn.edu.uestc.conv.Matrix;
import cn.edu.uestc.paillier.Paillier;

import java.io.*;
import java.math.BigInteger;

public class Server2 extends Party {
    Server2() {
        super();
        File keyFile = new File("C:\\Users\\pzima\\Desktop\\ppcnn\\data\\paillier");
        // 获取解密密钥
        try {
            paillier = (Paillier) new ObjectInputStream(new FileInputStream(keyFile)).readObject();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("密钥交换出错.");
        }
    }

    public BigInteger decrypt(BigInteger input) {
        return paillier.decrypt(input);
    }

    public Matrix decrypt(Matrix matrix) {
        Matrix outMatrix = new Matrix(matrix.getHeight(), matrix.getWidth());
        for (int i = 0; i < matrix.getHeight(); i++) {
            for (int j = 0; j < matrix.getWidth(); j++) {
                BigInteger value = decrypt(matrix.getValue(i, j));
                if (value.compareTo(paillier.max) > 0) {
                    value = value.subtract(paillier.n);
                }
                outMatrix.setValue(i, j, value);
            }
        }
        return outMatrix;
    }
    public Matrix[] decrypt(Matrix[] matrices) {
        Matrix[] outMatrix = new Matrix[matrices.length];
        for (int i = 0; i < matrices.length; i++) {
            outMatrix[i] = decrypt(matrices[i]);
        }
        return outMatrix;
    }


    public Matrix relu(Matrix inputMatrix) {
        Matrix resultMatrix = new Matrix(inputMatrix.getHeight(), inputMatrix.getWidth());
        for (int i = 0; i < inputMatrix.getHeight(); i++) {
            for (int j = 0; j < inputMatrix.getWidth(); j++) {
                BigInteger value = decrypt(inputMatrix.getValue(i, j));
                if (value.compareTo(paillier.max) > -1) {
                    value = BigInteger.ZERO;
                }
                resultMatrix.setValue(i, j, encrypt(value));
            }
        }
        return resultMatrix;
    }

    public Matrix[] relu(Matrix[] matrices) {
        Matrix[] result = new Matrix[matrices.length];
        for (int i = 0; i < matrices.length; i++) {
            result[i] = relu(matrices[i]);
        }
        return result;
    }

    public Matrix[] maxPool(Matrix[] matrices, int kernelSize, int stride, ConvMode convMode) {
        Matrix[] resultMatrix = new Matrix[matrices.length];
        for (int i = 0; i < resultMatrix.length; i++) {
            resultMatrix[i] = maxPool(matrices[i], kernelSize, stride, convMode);
        }
        return resultMatrix;
    }

    public Matrix maxPool(Matrix matrix, int kernelSize, int stride, ConvMode convMode) {
        // 计算padding以及输出尺寸。
        int padding = 0;
        int outHeight = 0;
        int outWidth = 0;
        if (convMode == ConvMode.FULL) {
            padding = kernelSize - 1;
            outHeight = (matrix.getHeight() - kernelSize + 2 * padding) / stride + 1;
            outWidth = (matrix.getWidth() - kernelSize + 2 * padding) / stride + 1;
        } else if (convMode == ConvMode.SAME) {
            // 结果与输入的尺寸一致
            padding = ((matrix.getHeight() - 1) * stride - matrix.getHeight() + kernelSize) >> 1;
            outHeight = matrix.getHeight();
            outWidth = matrix.getWidth();
        } else {
            outHeight = (matrix.getHeight() - kernelSize) / stride + 1;
            outWidth = (matrix.getWidth() - kernelSize) / stride + 1;
        }
        BigInteger cipherZero = paillier.encrypt(BigInteger.ZERO);
        Matrix outMatrix = new Matrix(outHeight, outWidth);
        // 进行卷积计算
        for (int i = 0; i < outHeight; i++) { // y方向计步
            for (int j = 0; j < outWidth; j++) { // x方向计步
                BigInteger max = BigInteger.ZERO;
                for (int ii = 0; ii < kernelSize; ii++) {
                    for (int jj = 0; jj < kernelSize; jj++) {
                        BigInteger cipherValue = cipherZero;
                        int rowIndex = stride * i + ii - padding;
                        int colIndex = stride * j + jj - padding;
                        if (rowIndex >= 0 && rowIndex < matrix.getWidth() && colIndex >= 0 && colIndex < matrix.getHeight()) {
                            cipherValue = matrix.getValue(-padding + stride * i + ii, -padding + stride * j + jj);
                        }
                        BigInteger plainValue = decrypt(cipherValue);
                        if (plainValue.compareTo(max) > 0) {
                            max = plainValue;
                        }
                    }
                }
                outMatrix.setValue(i, j, paillier.encrypt(max));
            }
        }
        return outMatrix;
    }

    public Matrix divide(Matrix matrix, BigInteger num) {
        Matrix result = new Matrix(matrix.getHeight(), matrix.getWidth());
        for (int i = 0; i < result.getHeight(); i++) {
            for (int j = 0; j < result.getWidth(); j++) {
                BigInteger value = decrypt(matrix.getValue(i, j));
                result.setValue(i, j, encrypt(value.divide(num)));
            }
        }
        return result;
    }

    public Matrix[] divide(Matrix[] matrices, BigInteger scale) {
        Matrix[] result = new Matrix[matrices.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = divide(matrices[i], scale);
        }
        return result;
    }


    public double[] softmax(Matrix matrix) {
        double[] r = new double[matrix.getHeight()];
        for (int i = 0; i < r.length; i++) {
            r[i] = Double.valueOf(decrypt(matrix.getValue(i, 0)).toString()) / 255.0;
        }
        return softmax(r);
    }

    public double[] softmax(double[] r) {
        double sum = 0;
        for (int i = 0; i < r.length; i++) {
            r[i] = Math.exp(r[i]);
            sum += r[i];
        }
        for (int i = 0; i < r.length; i++) {
            r[i] /= sum;
        }
        return r;
    }
}
