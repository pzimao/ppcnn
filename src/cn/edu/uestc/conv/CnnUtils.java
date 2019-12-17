package cn.edu.uestc.conv;

import cn.edu.uestc.paillier.Paillier;

import java.math.BigInteger;


public class CnnUtils {

//    // 卷积
//    public static Matrix conv(Matrix matrix, Matrix kernel, int stride, ConvMode convMode) {
//        // 计算padding以及输出尺寸。
//        int padding = 0;
//        int outHeight = 0;
//        int outWidth = 0;
//        if (convMode == ConvMode.FULL) {
//            padding = kernel.getHeight() - 1;
//            outHeight = (matrix.getHeight() - kernel.getHeight() + 2 * padding) / stride + 1;
//            outWidth = (matrix.getWidth() - kernel.getWidth() + 2 * padding) / stride + 1;
//        } else if (convMode == ConvMode.SAME) {
//            // 结果与输入的尺寸一致
//            padding = ((matrix.getHeight() - 1) * stride - matrix.getHeight() + kernel.getHeight()) >> 1;
//            outHeight = matrix.getHeight();
//            outWidth = matrix.getWidth();
//        } else {
//            outHeight = (matrix.getHeight() - kernel.getHeight()) / stride + 1;
//            outWidth = (matrix.getWidth() - kernel.getWidth()) / stride + 1;
//        }
//
//        Matrix outMatrix = new Matrix(outHeight, outWidth);
//        // 进行卷积计算
//        for (int i = 0; i < outHeight; i++) { // y方向计步
//            for (int j = 0; j < outWidth; j++) { // x方向计步
//                BigInteger sum = BigInteger.ZERO;
//                for (int ii = 0; ii < kernel.getHeight(); ii++) {
//                    for (int jj = 0; jj < kernel.getWidth(); jj++) {
//                        BigInteger value = BigInteger.ZERO;
//                        int rowIndex = stride * i + ii - padding;
//                        int colIndex = stride * j + jj - padding;
//                        if (rowIndex >= 0 && rowIndex < matrix.getWidth() && colIndex >= 0 && colIndex < matrix.getHeight()) {
//                            value = matrix.getValue(-padding + stride * i + ii, -padding + stride * j + jj);
//                        }
//                        // 乘
//                        BigInteger product = value.multiply(kernel.getValue(ii, jj));
//                        // 累加
//                        sum = sum.add(product);
//                    }
//                }
//                outMatrix.setValue(i, j, sum);
//            }
//        }
//        return outMatrix;
//    }
//
//
//    public static Matrix[] conv(Matrix[] matrices, Matrix[] kernels, int stride, ConvMode convMode) {
//        Matrix[] results = new Matrix[kernels.length];
//        // 滤波器数量
//        int filterNum = kernels.length / matrices.length;
//        for (int fi = 0; fi < filterNum; fi++) { // 滤波器
//            for (int i = 0; i < matrices.length; i++) { // 输入通道
//                results[fi * matrices.length + i] = conv(matrices[i], kernels[fi * matrices.length + i], stride, convMode);
//            }
//        }
//        return results;
//    }
//
//
//    // 矩阵加
//    public static Matrix add(Matrix matrix, BigInteger value) {
//        Matrix outMatrix = new Matrix(matrix.getHeight(), matrix.getWidth());
//        for (int i = 0; i < matrix.getHeight(); i++) {
//            for (int j = 0; j < matrix.getWidth(); j++) {
//                outMatrix.setValue(i, j, matrix.getValue(i, j).add(value));
//            }
//        }
//        return outMatrix;
//    }
//
//
//    public static Matrix[] secureConv(Matrix[] matrices, Matrix[] kernels, Matrix kernelBias, int stride, ConvMode
//            convMode, Paillier paillier) {
//        // 输出的大小是 kernels.length / matrices.length
//        Matrix[] resultMatrices = new Matrix[kernels.length / matrices.length];
//        for (int i = 0; i < resultMatrices.length; i++) {
//            Matrix[] aKernel = new Matrix[matrices.length];
//            for (int j = 0; j < matrices.length; j++) {
//                aKernel[j] = kernels[i * kernels.length / matrices.length + j];
//            }
//            resultMatrices[i] = relu(add(add(conv(matrices, aKernel, 1, convMode, paillier), paillier), kernelBias.getValue(i, 0), paillier), paillier);
//        }
//
//        return resultMatrices;
//    }
//
//
//    // softmax

//
//
//
    // 矩阵数组转字符串
    public static String ma2s(Matrix[] matrices) {
        String s = "";
        for (Matrix matrix : matrices) {
            s += matrix;
            s += "\n";
        }
        return s;
    }
//


    public static String a2s(double[] r) {
        String s = "";
        for (double value : r) {
            s += String.format("%5f", value);
            s += "\n";
        }
        return s;
    }
}

