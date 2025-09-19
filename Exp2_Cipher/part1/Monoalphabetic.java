package Exp2_Cipher.part1;

import java.util.Scanner;

public class Monoalphabetic {
   static String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

   public static String encrypt(String text, String key) {
      text = text.toUpperCase();
      StringBuilder result = new StringBuilder();
      for (char c : text.toCharArray()) {
         int index = ALPHABET.indexOf(c);
         if (index != -1)
            result.append(key.charAt(index));
         else
            result.append(c);
      }
      return result.toString();
   }

   public static String decrypt(String text, String key) {
      StringBuilder result = new StringBuilder();
      for (char c : text.toCharArray()) {
         int index = key.indexOf(c);
         if (index != -1)
            result.append(ALPHABET.charAt(index));
         else
            result.append(c);
      }
      return result.toString();
   }

   public static void main(String[] args) {
      String key = "QWERTYUIOPASDFGHJKLZXCVBNM"; // key must be permutation of alphabet
      Scanner sc = new Scanner(System.in);

      System.out.print("Enter text: ");
      String text = sc.nextLine();

      String encrypted = encrypt(text, key);
      String decrypted = decrypt(encrypted, key);

      System.out.println("Encrypted: " + encrypted);
      System.out.println("Decrypted: " + decrypted);
   }

}
