package Exp3_RSA;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;

public class Sender {
   private static KeyPair rsaKeyPair;
   private static PublicKey receiverPublicKey;

   public static void main(String[] args) {
      try {
         // Generate RSA keys
         KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
         keyGen.initialize(2048);
         rsaKeyPair = keyGen.generateKeyPair();

         Scanner sc = new Scanner(System.in);

         System.out.println("----------- Sender Service Started -----------");

         // Register with Third Party
         Socket tpSocket = new Socket("localhost", 4444);
         PrintWriter out = new PrintWriter(tpSocket.getOutputStream(), true);
         BufferedReader in = new BufferedReader(new InputStreamReader(tpSocket.getInputStream()));

         System.out.print("Enter your name: ");
         String name = sc.nextLine();

         String pubKeyString = Base64.getEncoder().encodeToString(rsaKeyPair.getPublic().getEncoded());
         out.println("REGISTER:" + name + ":" + pubKeyString);
         System.out.println(in.readLine());
         tpSocket.close();

         System.out.print("Enter receiver's name: ");
         String receiverName = sc.nextLine();

         // Get receiver's public key
         tpSocket = new Socket("localhost", 4444);
         out = new PrintWriter(tpSocket.getOutputStream(), true);
         in = new BufferedReader(new InputStreamReader(tpSocket.getInputStream()));

         out.println("GETKEY:" + receiverName);
         String response = in.readLine();
         if (response.startsWith("KEY")) {
            String[] parts = response.split(":");
            String pubKeyStr = parts[2];
            byte[] keyBytes = Base64.getDecoder().decode(pubKeyStr);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            receiverPublicKey = kf.generatePublic(new X509EncodedKeySpec(keyBytes));
            System.out.println("Successfully retrieved [" + receiverName + "]'s public key.");
         } else {
            System.out.println("Receiver not found.");
            return;
         }
         tpSocket.close();

         // Generate symmetric AES key
         KeyGenerator kg = KeyGenerator.getInstance("AES");
         kg.init(128);
         SecretKey aesKey = kg.generateKey();

         // Encrypt AES key with receiver's public RSA key
         Cipher rsaCipher = Cipher.getInstance("RSA");
         rsaCipher.init(Cipher.ENCRYPT_MODE, receiverPublicKey);
         byte[] encryptedAesKey = rsaCipher.doFinal(aesKey.getEncoded());
         String encryptedAesKeyStr = Base64.getEncoder().encodeToString(encryptedAesKey);

         System.out.println("----------- Encrypting and Sending AES Key -----------");

         // Connect to receiver and send encrypted AES key
         Socket receiverSocket = new Socket("localhost", 3333);
         out = new PrintWriter(receiverSocket.getOutputStream(), true);
         in = new BufferedReader(new InputStreamReader(receiverSocket.getInputStream()));

         out.println("KEY:" + encryptedAesKeyStr);
         System.out.println("AES key sent to receiver!");
         System.out.println("------------------------------------------------");

         // Encrypt message with AES
         System.out.println("Enter the message to send:");
         String message = sc.nextLine();

         Cipher aesCipher = Cipher.getInstance("AES");
         aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
         byte[] encryptedMsg = aesCipher.doFinal(message.getBytes());
         String encryptedMsgStr = Base64.getEncoder().encodeToString(encryptedMsg);

         out.println("MSG:" + encryptedMsgStr);
         System.out.println("Encrypting message...");
         System.out.println("Message sent to receiver!");
         System.out.println("------------------------------------------------");

         receiverSocket.close();
         sc.close();

      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
