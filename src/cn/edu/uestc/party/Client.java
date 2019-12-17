package cn.edu.uestc.party;

import cn.edu.uestc.conv.Matrix;
import cn.edu.uestc.paillier.Paillier;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;

public class Client extends Party {
    Client() {
        super();
        File keyFile = new File("C:\\Users\\pzima\\Desktop\\ppcnn\\data\\paillier");
        // client角色负责生成密钥
        if (!keyFile.exists()) {
            try {
                Paillier paillier = new Paillier(64, 64);
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(keyFile));
                oos.writeObject(paillier);
                this.paillier = paillier;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                paillier = (Paillier) new ObjectInputStream(new FileInputStream(keyFile)).readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            paillier = (Paillier) new ObjectInputStream(new FileInputStream(keyFile)).readObject();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("密钥交换出错.");
        }
    }

    // todo 读取图象
    public Matrix[] readImage(File imageFile) {
        try {
            List<String> list = FileUtils.readLines(imageFile, Charset.defaultCharset());
            Matrix plainMatrix = new Matrix(list.size());
            for (int i = 0; i < list.size(); i++) {
                String[] values = list.get(i).split(" ");
                for (int j = 0; j < values.length; j++) {
                    plainMatrix.setValue(i, j, new BigInteger(values[j]));
                }
            }
            Matrix[] input = {plainMatrix};
            return encrypt(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
