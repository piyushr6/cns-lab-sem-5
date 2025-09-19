package Exp2_Cipher.part1;

import java.util.Scanner;

public class Caeser {
   public static String encrypt(String text, int shift) {
      StringBuilder result = new StringBuilder();
      shift = shift % 26;
      for (char c : text.toCharArray()) {
         if (Character.isUpperCase(c)) {
            result.append((char) ((c - 'A' + shift) % 26 + 'A'));
         } else if (Character.isLowerCase(c)) {
            result.append((char) ((c - 'a' + shift) % 26 + 'a'));
         } else {
            result.append(c);
         }
      }
      return result.toString();
   }

   public static String decrypt(String text, int shift) {
      return encrypt(text, 26 - (shift % 26));
   }

   public static void main(String[] args) {
      Scanner sc = new Scanner(System.in);
      System.out.print("Enter text: ");
      String text = sc.nextLine();
      System.out.print("Enter shift: ");
      int shift = sc.nextInt();

      String encrypted = encrypt(text, shift);
      String decrypted = decrypt(encrypted, shift);

      System.out.println("Encrypted: " + encrypted);
      System.out.println("Decrypted: " + decrypted);
   }
}
