package Exp2_Cipher.part1;

import java.util.*;

public class HillCipher {
   static int mod = 26;

   static int[][] getKeyMatrix(String key, int n) {
      int[][] keyMatrix = new int[n][n];
      key = key.toUpperCase().replaceAll("[^A-Z]", "");
      int k = 0;
      for (int i = 0; i < n; i++)
         for (int j = 0; j < n; j++)
            keyMatrix[i][j] = (key.charAt(k++) - 'A') % 26;
      return keyMatrix;
   }

   static String encrypt(String text, int[][] keyMatrix, int n) {
      text = text.toUpperCase().replaceAll("[^A-Z]", "");
      while (text.length() % n != 0)
         text += "X";

      StringBuilder result = new StringBuilder();
      for (int i = 0; i < text.length(); i += n) {
         for (int row = 0; row < n; row++) {
            int sum = 0;
            for (int col = 0; col < n; col++) {
               sum += keyMatrix[row][col] * (text.charAt(i + col) - 'A');
            }
            result.append((char) ((sum % 26) + 'A'));
         }
      }
      return result.toString();
   }

   public static void main(String[] args) {
      Scanner sc = new Scanner(System.in);
      System.out.print("Enter key (length should be n*n): ");
      String key = sc.nextLine();
      System.out.print("Enter n (matrix size): ");
      int n = sc.nextInt();
      sc.nextLine();
      System.out.print("Enter text: ");
      String text = sc.nextLine();

      int[][] keyMatrix = getKeyMatrix(key, n);
      String encrypted = encrypt(text, keyMatrix, n);

      System.out.println("Encrypted: " + encrypted);
   }
}