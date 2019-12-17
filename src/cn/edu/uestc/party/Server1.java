package cn.edu.uestc.party;

import cn.edu.uestc.conv.ConvMode;
import cn.edu.uestc.conv.Matrix;
import cn.edu.uestc.paillier.Paillier;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;

public class Server1 extends Party {
    BigInteger[] params;
    private int index = 0;

    Server1() {
        super();
        // 读取模型的参数
        File paramsFile = new File("C:\\Users\\pzima\\Desktop\\ppcnn\\params\\params.txt");
        try {
            List<String> lines = FileUtils.readLines(paramsFile, Charset.defaultCharset());
            params = new BigInteger[lines.size()];
            for (int i = 0; i < lines.size(); i++) {
                params[i] = new BigInteger(lines.get(i).trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 获取加密密钥
        File keyFile = new File("C:\\Users\\pzima\\Desktop\\ppcnn\\data\\paillier");
        try {
            paillier = (Paillier) new ObjectInputStream(new FileInputStream(keyFile)).readObject();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("密钥交换出错.");
        }
    }

    /**
     * 参数游标
     *
     * @return
     */
    private BigInteger nextParam() {
        return params[index++];
    }


    /**
     * 此时server1需要读取滤波器权重参数
     * 滤波器权重参数保存在一个矩阵数组，长度是: 输入通道数 * 滤波器数量
     * 保存形式：滤波器数行 * 通道数列
     * 滤波器优先、通道次优先、行、列
     *
     * @param matrices
     * @param filterNum
     * @param kernelSize
     * @param stride
     * @param convMode
     * @return
     */
    public Matrix[] conv(Matrix[] matrices, int filterNum, int kernelSize, int stride, ConvMode convMode) {

        Matrix[] kernelMatrix = new Matrix[filterNum * matrices.length];
        for (int filterIndex = 0; filterIndex < filterNum; filterIndex++) {
            for (int channelIndex = 0; channelIndex < matrices.length; channelIndex++) {
                Matrix kernel = new Matrix(kernelSize);
                kernelMatrix[channelIndex + filterIndex * matrices.length] = kernel;
                for (int i = 0; i < kernelSize; i++) {
                    for (int j = 0; j < kernelSize; j++) {
                        kernel.setValue(i, j, nextParam());
                    }
                }
            }
        }

        // 读取偏置参数
        Matrix bias = new Matrix(filterNum, 1);
        for (int i = 0; i < filterNum; i++) {
            bias.setValue(i, 0, nextParam());
        }

        return add(conv(matrices, kernelMatrix, stride, convMode), bias);
    }


    public Matrix[] add(Matrix[] matrices, Matrix bias) {
        // 同一通道下各个特征矩阵加起来
        // 输出的矩阵数量 = 滤波器的数量 = 偏执参数的数量
        Matrix[] midResult = new Matrix[bias.length()];
        // 输入的每个通道下矩阵的数量
        int inputChannelNum = matrices.length / bias.length();

        for (int i = 0; i < midResult.length; i++) {
            Matrix[] fms = new Matrix[inputChannelNum];
            for (int j = 0; j < fms.length; j++) {
                fms[j] = matrices[i * inputChannelNum + j];
            }
            midResult[i] = add(fms);
        }
        Matrix[] result = new Matrix[bias.length()];
        for (int i = 0; i < bias.length(); i++) {
            result[i] = add(midResult[i], bias.getValue(i, 0));
        }
        return result;
    }

//    public Matrix[] add(Matrix[] matrices, Matrix bias) {
//        // 同一通道下各个特征矩阵加起来
//        // 输出的矩阵数量 = 滤波器的数量 = 偏执参数的数量
//        Matrix[] midResult = new Matrix[bias.length()];
//        // 输入的每个通道下矩阵的数量
//        int inputChannelNum = matrices.length / bias.length();
//
//        for (int i = 0; i < midResult.length; i++) {
//            Matrix[] fms = new Matrix[inputChannelNum];
//            for (int j = 0; j < fms.length; j++) {
//                fms[j] = matrices[i * inputChannelNum + j];
//            }
//            midResult[i] = add(fms);
//        }
//        Matrix[] result = new Matrix[bias.length()];
//        for (int i = 0; i < bias.length(); i++) {
//            result[i] = CnnUtils.add(midResult[i], bias.getValue(i, 0));
//        }
//        return result;
//    }


    public Matrix dense(Matrix matrix, int neuronNum) {
        // 读取全连接层的权重参数
        Matrix weight = new Matrix(matrix.length() * neuronNum, 1);
        for (int i = 0; i < weight.getHeight(); i++) {
            weight.setValue(i, 0, nextParam());
        }

        // 读取偏置参数
        Matrix bias = new Matrix(neuronNum, 1);
        for (int i = 0; i < neuronNum; i++) {
            bias.setValue(i, 0, nextParam());
        }

        return add(dense(matrix, weight), bias);
    }

    public Matrix noising(Matrix matrix, int kernelSize) {
        // todo 加随机值 & 打乱顺序
        return matrix;
    }

    // ----------来自CnnUtils----------
    // 全连接
    public Matrix dense(Matrix inputMatrix, Matrix fcMatrix) {

        Matrix resultMatrix = new Matrix(fcMatrix.length() / inputMatrix.length(), 1);
        for (int i = 0; i < resultMatrix.getHeight(); i++) {
            // 计算 某个神经元的输出
            BigInteger value = encrypt(BigInteger.ZERO);
            for (int j = 0; j < inputMatrix.getHeight(); j++) {
                value = value.multiply(inputMatrix.getValue(j, 0).modPow(fcMatrix.getValue(resultMatrix.length() * j + i, 0), paillier.nsquare)).mod(paillier.nsquare);
            }
            resultMatrix.setValue(i, 0, value);
        }
        return resultMatrix;
    }

    public Matrix dense(Matrix inputMatrix, Matrix fcMatrix, Paillier paillier) {
        Matrix resultMatrix = new Matrix(fcMatrix.length() / inputMatrix.length(), 1);
        for (int i = 0; i < resultMatrix.getHeight(); i++) {
            // 计算 某个神经元的输出
            BigInteger value = paillier.encrypt(BigInteger.ZERO);
            for (int j = 0; j < inputMatrix.getHeight(); j++) {
                value = paillier.add(value, paillier.mul(inputMatrix.getValue(j, 0), fcMatrix.getValue(resultMatrix.length() * j + i, 0)));
            }
            resultMatrix.setValue(i, 0, value);
        }
        return resultMatrix;
    }

    // flatten
    public Matrix flatten(Matrix[] matrices) {
        // 把输入的矩阵列成1列
        Matrix resultMatrix = new Matrix(matrices.length * matrices[0].length(), 1);
        int cursor = 0;
        for (int i = 0; i < resultMatrix.length(); ) {
            for (Matrix matrix : matrices) {
                resultMatrix.setValue(i++, 0, matrix.getValue(cursor));
            }
            cursor++;
        }
        return resultMatrix;
    }

    public Matrix add(Matrix matrix, Matrix bias) {
        Matrix result = new Matrix(matrix.getHeight(), matrix.getWidth());
        for (int i = 0; i < matrix.getHeight(); i++) {
            result.setValue(i, 0, paillier.add(matrix.getValue(i, 0), encrypt(bias.getValue(i, 0))));
        }
        return result;
    }

    //    public Matrix add(Matrix[] matrices) {
//        return add(matrices);
//    }
    public Matrix add(Matrix[] matrices) {
        int outHeight = matrices[0].getHeight();
        int outWidth = matrices[0].getWidth();
        Matrix outMatrix = new Matrix(outHeight, outWidth);
        for (int i = 0; i < outHeight; i++) {
            for (int j = 0; j < outWidth; j++) {
                BigInteger value = encrypt(BigInteger.ZERO);
                for (Matrix matrix : matrices) {
                    value = paillier.add(value, matrix.getValue(i, j));
                }
                outMatrix.setValue(i, j, value);
            }
        }
        return outMatrix;
    }


    public Matrix add(Matrix matrix, BigInteger value) {
        BigInteger encryptRandomValue = paillier.encrypt(value);
        Matrix outMatrix = new Matrix(matrix.getHeight(), matrix.getWidth());
        for (int i = 0; i < matrix.getHeight(); i++) {
            for (int j = 0; j < matrix.getWidth(); j++) {
                outMatrix.setValue(i, j, paillier.add(matrix.getValue(i, j), encryptRandomValue));
            }
        }
        return outMatrix;
    }

    public Matrix[] conv(Matrix[] matrices, Matrix[] kernels, int stride, ConvMode convMode) {
        Matrix[] results = new Matrix[kernels.length];
        // 滤波器数量
        int filterNum = kernels.length / matrices.length;
        for (int fi = 0; fi < filterNum; fi++) {
            for (int i = 0; i < matrices.length; i++) {
                results[fi * matrices.length + i] = conv(matrices[i], kernels[fi * matrices.length + i], stride, convMode);
            }
        }
        return results;
    }

    public Matrix[] conv(Matrix matrix, Matrix[] kernels, int stride, ConvMode convMode) {
        Matrix[] results = new Matrix[kernels.length];
        for (int i = 0; i < results.length; i++) {
            results[i] = conv(matrix, kernels[i], stride, convMode);
        }
        return results;
    }

    public Matrix conv(Matrix matrix, Matrix kernel, int stride, ConvMode convMode) {
        // 计算padding以及输出尺寸。
        int padding = 0;
        int outHeight = 0;
        int outWidth = 0;
        if (convMode == ConvMode.FULL) {
            padding = kernel.getHeight() - 1;
            outHeight = (matrix.getHeight() - kernel.getHeight() + 2 * padding) / stride + 1;
            outWidth = (matrix.getWidth() - kernel.getWidth() + 2 * padding) / stride + 1;
        } else if (convMode == ConvMode.SAME) {
            // 结果与输入的尺寸一致
            padding = ((matrix.getHeight() - 1) * stride - matrix.getHeight() + kernel.getHeight()) >> 1;
            outHeight = matrix.getHeight();
            outWidth = matrix.getWidth();
        } else {
            outHeight = (matrix.getHeight() - kernel.getHeight()) / stride + 1;
            outWidth = (matrix.getWidth() - kernel.getWidth()) / stride + 1;
        }

        Matrix outMatrix = new Matrix(outHeight, outWidth);
        // 进行卷积计算
        for (int i = 0; i < outHeight; i++) { // y方向计步
            for (int j = 0; j < outWidth; j++) { // x方向计步
                BigInteger cipherSum = encrypt(BigInteger.ZERO);
                for (int ii = 0; ii < kernel.getHeight(); ii++) {
                    for (int jj = 0; jj < kernel.getWidth(); jj++) {
                        BigInteger cipherValue = encrypt(BigInteger.ZERO);
                        int rowIndex = stride * i + ii - padding;
                        int colIndex = stride * j + jj - padding;
                        if (rowIndex >= 0 && rowIndex < matrix.getWidth() && colIndex >= 0 && colIndex < matrix.getHeight()) {
                            cipherValue = matrix.getValue(-padding + stride * i + ii, -padding + stride * j + jj);
                        }
                        // 乘
                        BigInteger cipherProduct = paillier.mul(cipherValue, kernel.getValue(ii, jj));
                        // 累加
                        cipherSum = paillier.add(cipherSum, cipherProduct);
                    }
                }
                outMatrix.setValue(i, j, cipherSum);
            }
        }
        return outMatrix;
    }
}
