package Exp2_Cipher.part1;

import java.util.LinkedHashSet;
import java.util.Scanner;

// import javax.swing.text.html.HTMLDocument.Iterator; // Removed incorrect import

public class Playfair {
   static String prepareText(String text) {
      text = text.toUpperCase().replaceAll("[^A-Z]", "").replace("J", "I");
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < text.length(); i++) {
         sb.append(text.charAt(i));
         if (i < text.length() - 1 && text.charAt(i) == text.charAt(i + 1)) {
            sb.append('X');
         }
      }
      if (sb.length() % 2 != 0)
         sb.append('X');
      return sb.toString();
   }

   static char[][] generateMatrix(String key) {
      String alphabet = "ABCDEFGHIKLMNOPQRSTUVWXYZ"; // J excluded
      LinkedHashSet<Character> set = new LinkedHashSet<>();
      for (char c : key.toUpperCase().toCharArray()) {
         if (c == 'J')
            c = 'I';
         set.add(c);
      }
      for (char c : alphabet.toCharArray())
         set.add(c);

      char[][] matrix = new char[5][5];

      java.util.Iterator<Character> it = set.iterator();
      for (int i = 0; i < 5; i++)
         for (int j = 0; j < 5; j++)
            matrix[i][j] = it.next();

      return matrix;
   }

   static int[] findPosition(char[][] matrix, char c) {
      if (c == 'J')
         c = 'I';
      for (int i = 0; i < 5; i++)
         for (int j = 0; j < 5; j++)
            if (matrix[i][j] == c)
               return new int[] { i, j };
      return null;
   }

   public static String encrypt(String text, String key) {
      char[][] matrix = generateMatrix(key);
      text = prepareText(text);
      StringBuilder result = new StringBuilder();

      for (int i = 0; i < text.length(); i += 2) {
         char a = text.charAt(i), b = text.charAt(i + 1);
         int[] posA = findPosition(matrix, a), posB = findPosition(matrix, b);

         if (posA[0] == posB[0]) { // same row
            result.append(matrix[posA[0]][(posA[1] + 1) % 5]);
            result.append(matrix[posB[0]][(posB[1] + 1) % 5]);
         } else if (posA[1] == posB[1]) { // same column
            result.append(matrix[(posA[0] + 1) % 5][posA[1]]);
            result.append(matrix[(posB[0] + 1) % 5][posB[1]]);
         } else { // rectangle
            result.append(matrix[posA[0]][posB[1]]);
            result.append(matrix[posB[0]][posA[1]]);
         }
      }
      return result.toString();
   }

   public static String decrypt(String text, String key) {
      char[][] matrix = generateMatrix(key);
      StringBuilder result = new StringBuilder();

      for (int i = 0; i < text.length(); i += 2) {
         char a = text.charAt(i), b = text.charAt(i + 1);
         int[] posA = findPosition(matrix, a), posB = findPosition(matrix, b);

         if (posA[0] == posB[0]) { // same row
            result.append(matrix[posA[0]][(posA[1] + 4) % 5]);
            result.append(matrix[posB[0]][(posB[1] + 4) % 5]);
         } else if (posA[1] == posB[1]) { // same column
            result.append(matrix[(posA[0] + 4) % 5][posA[1]]);
            result.append(matrix[(posB[0] + 4) % 5][posB[1]]);
         } else { // rectangle
            result.append(matrix[posA[0]][posB[1]]);
            result.append(matrix[posB[0]][posA[1]]);
         }
      }
      return result.toString();
   }

   public static void main(String[] args) {
      Scanner sc = new Scanner(System.in);
      System.out.print("Enter key: ");
      String key = sc.nextLine();
      System.out.print("Enter text: ");
      String text = sc.nextLine();

      String encrypted = encrypt(text, key);
      String decrypted = decrypt(encrypted, key);

      System.out.println("Encrypted: " + encrypted);
      System.out.println("Decrypted: " + decrypted);
   }
}