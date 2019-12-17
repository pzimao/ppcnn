package cn.edu.uestc.test;

import java.util.Random;

public class Test_12_15 {
    public static void main(String[] args) {
        // 一个桶
        boolean[][] buckets = new boolean[1000][];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new boolean[100000000];
        }
        // 随机生成10000个号码
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            long phoneNumber = random.nextLong() % 100000000000L;
            buckets[(int) (phoneNumber / 100000000L)][(int) (phoneNumber % 100000000L)] = true;
        }
        // 按顺序打印
        for (int i = 0; i < buckets.length; i++) {
            for (int j = 0; j < buckets[i].length; j++) {
                if (buckets[i][j]) {
                    System.out.println(String.format("%011d", i));
                }
            }
        }
    }
}
