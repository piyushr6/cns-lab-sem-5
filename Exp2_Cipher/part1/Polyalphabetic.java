package Exp2_Cipher.part1;

import java.util.Scanner;

public class Polyalphabetic {
   public static String encrypt(String text, String key) {
      StringBuilder result = new StringBuilder();
      key = key.toUpperCase();
      text = text.toUpperCase();
      int j = 0;

      for (char c : text.toCharArray()) {
         if (c >= 'A' && c <= 'Z') {
            result.append((char) ((c - 'A' + key.charAt(j % key.length()) - 'A') % 26 + 'A'));
            j++;
         } else {
            result.append(c);
         }
      }
      return result.toString();
   }

   public static String decrypt(String text, String key) {
      StringBuilder result = new StringBuilder();
      key = key.toUpperCase();
      text = text.toUpperCase();
      int j = 0;

      for (char c : text.toCharArray()) {
         if (c >= 'A' && c <= 'Z') {
            result.append((char) ((c - 'A' - (key.charAt(j % key.length()) - 'A') + 26) % 26 + 'A'));
            j++;
         } else {
            result.append(c);
         }
      }
      return result.toString();
   }

   public static void main(String[] args) {
      Scanner sc = new Scanner(System.in);
      System.out.print("Enter text: ");
      String text = sc.nextLine();
      System.out.print("Enter key: ");
      String key = sc.nextLine();

      String encrypted = encrypt(text, key);
      String decrypted = decrypt(encrypted, key);

      System.out.println("Encrypted: " + encrypted);
      System.out.println("Decrypted: " + decrypted);
   }

}
