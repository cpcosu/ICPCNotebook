package tools;

import java.util.HashMap;

/**
 * METHOD LIST:
 * long discreteLog(long a, long b, long mod)
 * long[][] matMul(long[][] mat1, long[][] mat2, long mod)
 * long[][] modPow(long[][] mat, long p, long mod)
 * long modPow(long a, long p, long mod)
 * double[] gaussianElimination(double[][] coef, double[] solutions)
 */
public final class LinearAlgebra {

/**
 * Compute the discrete logarithm using Shank's Baby-Step
 * Giant-Step algorithm. That is, find an x such that
 * a^x = b % mod. The time complexity is O(sqrt(mod))
 * a should be a generator for mod
 * mod should be prime
 */
private static long discreteLog(long a, long b, long mod) {
    int n = (int) Math.sqrt(mod) + 1;
    // want to solve a^x=b (% mod). Let x = i-jn.
    // use meet-in-the-middle and solve a^i = a^(jn)b (% mod)
    // first compute all values of a^i in a map from a^i -> i
    HashMap<Long, Integer> seen = new HashMap<>();
    long LHS = 1;
    for (int i = 0; i < n; i++) {
        seen.put(LHS, i);
        LHS = (a * LHS) % mod;
    }
    
    long aToTheNth = modPow(a, n, mod);
    long RHS = b;
    for (int j = 0; j <= n; j++) {
        if (seen.containsKey(RHS)) {
            // i = seen.get(RHS). a^(i-jn)=b, i-jn is taken mod (mod - 1)
            int solution = seen.get(RHS) - j * n;
            while (solution < 0) {
                solution += (mod - 1);
            }
            return solution % (mod - 1);
        }
        RHS = (RHS * aToTheNth) % mod;
    }
    return -1;
}

/**
 * return mat1*mat2, each term % mod
 * requires that mat1[0].length = mat2.length
 **/
private static long[][] matMul(long[][] mat1, long[][] mat2, long mod) {
    long[][] result = new long[mat1.length][mat2[0].length];
    for (int i = 0; i < mat1.length; i++) {
        for (int j = 0; j < mat2[0].length; j++) {
            for (int k = 0; k < mat1[0].length; k++) {
                result[i][j] = (result[i][j] + mat1[i][k] * mat2[k][j]) % mod;
            }
        }
    }
    return result;
}

/**
 * return mat^p, each term % mod
 */
private static long[][] modPow(long[][] mat, long p, long mod) {
    if (p == 0) {
        long[][] id = new long[mat.length][mat.length];
        for (int i = 0; i < id.length; i++) {
            id[i][i] = 1;
        }
        return id;
    } else {
        long[][] half = modPow(mat, p / 2, mod);
        long[][] ans = matMul(half, half, mod);
        if (p % 2 == 1) {
            ans = matMul(ans, mat, mod);
        }
        return ans;
    }
}

/**
 * return a^p (% mod)
 */
private static long modPow(long a, long p, long mod) {
    if (p == 0) {
        return 1;
    }
    long half = modPow(a, p / 2, mod);
    long ans = (half * half) % mod;
    if (p % 2 == 1) {
        ans = (ans * a) % mod;
    }
    return ans;
}

// currently very messy and not well tested
private static double[] GaussianElim(double[][] coefficients, double[] solutions) {
    double[] values = new double[solutions.length];
    for (int curRow = 1; curRow < solutions.length; curRow++) {
        for (int i = 0; i < curRow; i++) {
            if (Math.abs(coefficients[curRow][i]) > 0.00001) { // nonzero
                double f = coefficients[curRow][i] / coefficients[i][i];
                // uncomment out and replace minus with appropriate loop
                //coefficients[curRow] = minus(coefficients[curRow], coefficients[i], f);
                solutions[curRow] = solutions[curRow] - solutions[i] * f;
                assert (Math.abs(coefficients[curRow][curRow - 1]) < 0.00001);
            }
        }
    }
    for (int row = values.length - 1; row >= 0; row--) {
        values[row] = solutions[row] / coefficients[row][row];
        for (int i = 0; i < row; i++) {
            solutions[i] -= coefficients[i][row] * values[row];
        }
    }
    return values;
}
}

