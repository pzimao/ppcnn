package cn.edu.uestc.party;

import cn.edu.uestc.conv.CnnUtils;
import cn.edu.uestc.conv.ConvMode;
import cn.edu.uestc.conv.Matrix;

import java.io.File;
import java.math.BigInteger;

public class SecureCnn {

    public static Matrix operate() {
        Client client = new Client();
        Server1 server1 = new Server1();
        Server2 server2 = new Server2();
        // 开始CNN
        Matrix[] image = client.readImage(new File("C:\\Users\\pzima\\Desktop\\ppcnn\\data\\minist_7.txt"));

        Matrix[] conv1Result = server1.conv(image, 32, 3, 1, ConvMode.VALID);
        Matrix[] relu1Result = server2.relu(conv1Result);

        Matrix[] maxPool1Result = server2.maxPool(relu1Result, 2, 2, ConvMode.VALID);

        maxPool1Result = server2.divide(maxPool1Result, new BigInteger("100000"));
        Matrix[] conv2Result = server1.conv(maxPool1Result, 64, 5, 1, ConvMode.VALID);

        Matrix[] relu2Result = server2.relu(conv2Result);
        Matrix[] maxPool2Result = server2.maxPool(relu2Result, 2, 2, ConvMode.VALID);


        Matrix flattenResult = server1.flatten(maxPool2Result);
        flattenResult = server2.divide(flattenResult, new BigInteger("100000"));
        Matrix dense1Result = server1.dense(flattenResult, 500);
        dense1Result = server2.relu(dense1Result);

        System.out.println("500 dense");
        System.out.println(server2.decrypt(dense1Result));
        System.out.println("==");
        // -- 这以上都是对的

        dense1Result = server2.divide(dense1Result, new BigInteger("100000"));
        Matrix result = server1.dense(dense1Result, 10);
        result = server2.relu(result);

        System.out.println("test");
        System.out.println(server2.decrypt(result));
        System.out.println("==");


        result = server2.divide(result, new BigInteger("100000"));

        System.out.println("test2");
        System.out.println(server2.decrypt(result));
        System.out.println("==");

        System.out.println("layer_6");
        System.out.println(CnnUtils.a2s(server2.softmax(result)));
        System.out.println("==");
        return result;
    }

    public static void main(String[] args) {
        System.out.println(operate());
    }
}
