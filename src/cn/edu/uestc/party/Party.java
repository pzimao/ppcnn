package cn.edu.uestc.party;

import cn.edu.uestc.conv.Matrix;
import cn.edu.uestc.paillier.Paillier;

import java.io.*;
import java.math.BigInteger;
import java.util.Random;

public abstract class Party {
    public Paillier paillier;
    private Random random;

    public Party() {
        random = new Random();
    }

    public BigInteger encrypt(BigInteger value) {
        return this.paillier.encrypt(value, random);
    }

    public Matrix encrypt(Matrix matrix) {
        Matrix outMatrix = new Matrix(matrix.getHeight(), matrix.getWidth());
        for (int i = 0; i < matrix.getHeight(); i++) {
            for (int j = 0; j < matrix.getWidth(); j++) {
                outMatrix.setValue(i, j, encrypt(matrix.getValue(i, j)));
            }
        }
        return outMatrix;
    }

    public Matrix[] encrypt(Matrix[] matrices) {
        Matrix[] outMatrices = new Matrix[matrices.length];
        for (int i = 0; i < outMatrices.length; i++) {
            outMatrices[i] = encrypt(matrices[i]);
        }
        return outMatrices;
    }
}
