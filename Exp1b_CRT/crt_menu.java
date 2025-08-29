import java.math.BigInteger;
import java.util.Scanner;

public class crt_menu {

   static Scanner sc = new Scanner(System.in);

   // Compute modular inverse using Extended Euclidean Algorithm
   static BigInteger modInverse(BigInteger a, BigInteger m) {
      return a.modInverse(m);
   }

   // Apply Chinese Remainder Theorem
   static BigInteger CRT(BigInteger[] c, BigInteger[] m, BigInteger M) {
      BigInteger result = BigInteger.ZERO;

      for (int i = 0; i < m.length; i++) {
         BigInteger Mi = M.divide(m[i]);
         BigInteger MiInverse = modInverse(Mi.mod(m[i]), m[i]);
         result = result.add(c[i].multiply(Mi).multiply(MiInverse));
      }
      return result.mod(M);
   }

   public static void main(String[] args) {
      System.out.print("Enter number of moduli (k): ");
      int k = sc.nextInt();

      BigInteger[] m = new BigInteger[k];
      BigInteger M = BigInteger.ONE;

      System.out.println("Enter " + k + " pairwise coprime moduli:");
      for (int i = 0; i < k; i++) {
         m[i] = sc.nextBigInteger();
         M = M.multiply(m[i]); // compute product of all moduli
      }

      while (true) {
         System.out.println("\n----- MENU -----");
         System.out.println("1. Addition");
         System.out.println("2. Subtraction");
         System.out.println("3. Multiplication");
         System.out.println("4. Division");
         System.out.println("5. Quit");
         System.out.print("Enter choice: ");

         int choice = sc.nextInt();
         if (choice == 5) {
            System.out.println("Exiting program...");
            break;
         }

         System.out.print("Enter first number A: ");
         BigInteger A = sc.nextBigInteger();
         System.out.print("Enter second number B: ");
         BigInteger B = sc.nextBigInteger();

         // Convert A and B into residues
         BigInteger[] a = new BigInteger[k];
         BigInteger[] b = new BigInteger[k];
         for (int i = 0; i < k; i++) {
            a[i] = A.mod(m[i]);
            b[i] = B.mod(m[i]);
         }

         // Perform operation
         BigInteger[] c = new BigInteger[k];
         for (int i = 0; i < k; i++) {
            switch (choice) {
               case 1:
                  c[i] = a[i].add(b[i]).mod(m[i]); // Addition
                  break;
               case 2:
                  c[i] = a[i].subtract(b[i]).mod(m[i]); // Subtraction
                  break;
               case 3:
                  c[i] = a[i].multiply(b[i]).mod(m[i]); // Multiplication
                  break;
               case 4:
                  if (b[i].equals(BigInteger.ZERO)) {
                     System.out.println("Division by zero not allowed for modulus " + m[i]);
                     c[i] = BigInteger.ZERO;
                  } else {
                     BigInteger bInv = b[i].modInverse(m[i]); // inverse of b[i]
                     c[i] = a[i].multiply(bInv).mod(m[i]); // Division
                  }
                  break;
            }
         }

         // Print residues
         System.out.print("Result residues (c1, c2, ... , ck): ");
         for (BigInteger value : c) {
            System.out.print(value + " ");
         }
         System.out.println();

         // Final result using CRT
         BigInteger C = CRT(c, m, M);
         System.out.println("Final result C (mod M): " + C);
      }
   }
}