package Exp3_RSA;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.Base64;
import java.util.Scanner;

public class Receiver {
   private static KeyPair rsaKeyPair;
   private static SecretKeySpec aesKeySpec;

   public static void main(String[] args) {
      try {
         // Generate RSA keys
         KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
         keyGen.initialize(2048);
         rsaKeyPair = keyGen.generateKeyPair();

         Scanner sc = new Scanner(System.in);

         System.out.println("----------- Receiver Service Started -----------");

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

         System.out.println("Receiver is listening on port 3333...");
         System.out.println("Waiting for incoming messages...");

         // Listen for incoming messages
         ServerSocket serverSocket = new ServerSocket(3333);

         while (true) {
            Socket socket = serverSocket.accept();
            new Thread(new Handler(socket)).start();
         }

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   static class Handler implements Runnable {
      private Socket socket;

      Handler(Socket socket) {
         this.socket = socket;
      }

      public void run() {
         try (
               BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
               PrintWriter out = new PrintWriter(socket.getOutputStream(), true);) {
            // Receive AES key
            String keyLine = in.readLine();
            if (keyLine.startsWith("KEY:")) {
               System.out.println("----------- AES Key Received -----------");

               String encryptedAesKeyStr = keyLine.substring(4);
               byte[] encryptedAesKey = Base64.getDecoder().decode(encryptedAesKeyStr);

               Cipher rsaCipher = Cipher.getInstance("RSA");
               rsaCipher.init(Cipher.DECRYPT_MODE, rsaKeyPair.getPrivate());
               byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAesKey);
               aesKeySpec = new SecretKeySpec(aesKeyBytes, "AES");

               System.out.println("Decrypting AES key...");
               System.out.println("AES key decrypted successfully!");
               System.out.println("------------------------------------------------");

               out.println("AES Key received");

               // Receive message
               String msgLine = in.readLine();
               if (msgLine.startsWith("MSG:")) {
                  System.out.println("Encrypted message received.");
                  String encryptedMsgStr = msgLine.substring(4);
                  byte[] encryptedMsg = Base64.getDecoder().decode(encryptedMsgStr);

                  Cipher aesCipher = Cipher.getInstance("AES");
                  aesCipher.init(Cipher.DECRYPT_MODE, aesKeySpec);
                  byte[] decryptedMsg = aesCipher.doFinal(encryptedMsg);

                  System.out.println("Decrypting message...");
                  System.out.println("Message decrypted successfully!");
                  System.out.println(">>> " + new String(decryptedMsg));
                  System.out.println("------------------------------------------------");
               }
            }

            socket.close();

         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }
}
