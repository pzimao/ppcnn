package cn.edu.uestc.test;

import cn.edu.uestc.paillier.Paillier;

import java.math.BigInteger;

public class Test {
    public static void main(String[] str) throws Exception {
        Paillier paillier = new Paillier();

        BigInteger m1 = new BigInteger("-2");
        BigInteger m2 = new BigInteger("-2");
        BigInteger em1 = paillier.encrypt(m1);
        BigInteger em2 = paillier.encrypt(m2);
        System.out.println(paillier.decrypt(em1).toString());
        System.out.println(paillier.decrypt(em2).toString());

        /*
         * test homomorphic properties -> D(E(m1)*E(m2) mod n^2) = (m1 + m2) mod
         * n
         */
        // m1+m2,求明文数值的和
//        BigInteger sum_m1m2 = m1.add(m2).mod(paillier.n);
//        System.out.println("original sum: " + sum_m1m2.toString());
        // em1+em2，求密文数值的乘
        BigInteger product_em1em2 = em1.multiply(em2).mod(paillier.nsquare);
        System.out.println("encrypted sum: " + product_em1em2.toString());
        System.out.println("decrypted sum: " + paillier.decrypt(product_em1em2).toString());

        /* test homomorphic properties -> D(E(m1)^m2 mod n^2) = (m1*m2) mod n */
        // m1*m2,求明文数值的乘
//        BigInteger prod_m1m2 = m1.multiply(m2).mod(paillier.n);
//        System.out.println("original product: " + prod_m1m2.toString());
        // em1的m2次方，再mod paillier.nsquare
        BigInteger expo_em1m2 = em1.modPow(m2, paillier.nsquare);
//        System.out.println("encrypted product: " + expo_em1m2.toString());
        System.out.println("em1: " + em1);
        System.out.println("em2: " + em1);
        System.out.println("decrypted product: " + paillier.decrypt(expo_em1m2).toString());
        if (1 == 1) {
            return;
        }
        //sum test
        System.out.println("--------------------------------");
        Paillier p = new Paillier();
        BigInteger t1 = new BigInteger("21");
        System.out.println(t1.toString());
        BigInteger t2 = new BigInteger("50");
        System.out.println(t2.toString());
        BigInteger t3 = new BigInteger("50");
        System.out.println(t3.toString());
        BigInteger et1 = p.encrypt(t1);
        System.out.println(et1.toString());
        BigInteger et2 = p.encrypt(t2);
        System.out.println(et2.toString());
        BigInteger et3 = p.encrypt(t3);
        System.out.println(et3.toString());
        BigInteger sum = new BigInteger("1");
        sum = p.add(sum, et1);
        System.out.println("decrypted sum: " + p.decrypt(sum).toString());
        sum = p.add(sum, et2);
        sum = p.add(sum, et3);
        System.out.println("sum: " + sum.toString());
        System.out.println("decrypted sum: " + p.decrypt(sum).toString());
        System.out.println("--------------------------------");
    }
}
