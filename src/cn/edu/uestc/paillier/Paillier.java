package cn.edu.uestc.paillier;

/**
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.Serializable;
import java.math.*;
import java.util.*;

/**
 * Paillier Cryptosystem <br>
 * <br>
 * References: <br>
 * [1] Pascal Paillier,
 * "Public-Key Cryptosystems Based on Composite Degree Residuosity Classes,"
 * EUROCRYPT'99. URL:
 * <a href="http://www.gemplus.com/smart/rd/publications/pdf/Pai99pai.pdf">http:
 * //www.gemplus.com/smart/rd/publications/pdf/Pai99pai.pdf</a><br>
 * <p>
 * [2] Paillier cryptosystem from Wikipedia. URL:
 * <a href="http://en.wikipedia.org/wiki/Paillier_cryptosystem">http://en.
 * wikipedia.org/wiki/Paillier_cryptosystem</a>
 *
 * @author Kun Liu (kunliu1@cs.umbc.edu)
 * @version 1.0
 */
public class Paillier implements Serializable {

    /**
     * p and q are two large primes. lambda = lcm(p-1, q-1) =
     * (p-1)*(q-1)/gcd(p-1, q-1).
     */
    private BigInteger p, q, lambda;
    /**
     * n = p*q, where p and q are two large primes.
     */
    public BigInteger n;

    // 最大值
    public BigInteger max;
    /**
     * nsquare = n*n
     */
    public BigInteger nsquare;
    /**
     * a random integer in Z*_{n^2} where gcd (L(g^lambda mod n^2), n) = 1.
     */
    private BigInteger g;
    /**
     * number of bits of modulus
     */
    private int bitLength;

    /**
     * u = (L(g^lambda mod n^2))^(-1) mod n.
     */
    private BigInteger u;

    /**
     * Constructs an instance of the Paillier cryptosystem.
     *
     * @param bitLengthVal number of bits of modulus
     * @param certainty    The probability that the new BigInteger represents a prime
     *                     number will exceed (1 - 2^(-certainty)). The execution time of
     *                     this constructor is proportional to the value of this
     *                     parameter.
     */
    public Paillier(int bitLengthVal, int certainty) {
        keyGenerate(bitLengthVal, certainty);
    }

    /**
     * Constructs an instance of the Paillier cryptosystem with 512 bits of
     * modulus and at least 1-2^(-64) certainty of primes generation.
     */
    public Paillier() {
        keyGenerate(512, 64);
    }

    /**
     * Sets up the public key and private key.
     *
     * @param bitLengthVal number of bits of modulus.
     * @param certainty    The probability that the new BigInteger represents a prime
     *                     number will exceed (1 - 2^(-certainty)). The execution time of
     *                     this constructor is proportional to the value of this
     *                     parameter.
     */
    public void keyGenerate(int bitLengthVal, int certainty) {
        bitLength = bitLengthVal;
        /*
         * Constructs two randomly generated positive BigIntegers that are
         * probably prime, with the specified bitLength and certainty.
         */
        p = new BigInteger(bitLength / 2, certainty, new Random());
        q = new BigInteger(bitLength / 2, certainty, new Random());

        n = p.multiply(q);
        nsquare = n.multiply(n);

        // todo 确定max的取值
        max = n.divide(new BigInteger("2"));

        // todo 为什么g直接取2？
        g = new BigInteger("2");
        lambda = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE))
                .divide(p.subtract(BigInteger.ONE).gcd(q.subtract(BigInteger.ONE)));
        /* check whether g is good. */
        if (g.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).gcd(n).intValue() != 1) {
            System.out.println("g is not good. Choose g again.");
            System.exit(1);
        }
        // 2019-12-17 添加，提前计算出u，减少解密开销。
        u = g.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).modInverse(n);
    }

    /**
     * Encrypts plaintext m. ciphertext c = g^m * r^n mod n^2. This function
     * explicitly requires random input r to help with encryption.
     *
     * @param m plaintext as a BigInteger
     * @param r random plaintext to help with encryption
     * @return ciphertext as a BigInteger
     */
    public BigInteger encrypt(BigInteger m, BigInteger r) {
        return g.modPow(m, nsquare).multiply(r.modPow(n, nsquare)).mod(nsquare);
    }

    /**
     * Encrypts plaintext m. ciphertext c = g^m * r^n mod n^2. This function
     * automatically generates random input r (to help with encryption).
     *
     * @param m plaintext as a BigInteger
     * @return ciphertext as a BigInteger
     */
    public BigInteger encrypt(BigInteger m) {
        BigInteger r = new BigInteger(bitLength, new Random());
        return g.modPow(m, nsquare).multiply(r.modPow(n, nsquare)).mod(nsquare);
    }

    /**
     * 2019-12-17 添加自定义方法，减少new Random()
      */
    public BigInteger encrypt(BigInteger m, Random rnd) {
        BigInteger r = new BigInteger(bitLength, rnd);
        return g.modPow(m, nsquare).multiply(r.modPow(n, nsquare)).mod(nsquare);
    }
    /**
     * Decrypts ciphertext c. plaintext m = L(c^lambda mod n^2) * u mod n, where
     * u = (L(g^lambda mod n^2))^(-1) mod n.
     *
     * @param c ciphertext as a BigInteger
     * @return plaintext as a BigInteger
     */
    public BigInteger decrypt(BigInteger c) {
        return c.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).multiply(u).mod(n);
    }

    /**
     * sum of (cipher) em1 and em2
     *
     * @param em1
     * @param em2
     * @return
     */
    public BigInteger add(BigInteger em1, BigInteger em2) {
        return em1.multiply(em2).mod(nsquare);
    }

    public BigInteger mul(BigInteger cipherValue, BigInteger plainValue) {
        return cipherValue.modPow(plainValue, nsquare);
    }
}
