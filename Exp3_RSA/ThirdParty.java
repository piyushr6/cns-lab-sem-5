package Exp3_RSA;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ThirdParty {
   private static Map<String, String> publicKeyStore = new HashMap<>();

   public static void main(String[] args) {
      try (ServerSocket serverSocket = new ServerSocket(4444)) {
         System.out.println("----------- Third Party Service Started -----------");
         System.out.println("Listening on port 4444...");
         System.out.println("------------------------------------------------");

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
            String request = in.readLine();

            if (request.startsWith("REGISTER")) {
               String[] parts = request.split(":");
               String name = parts[1];
               String pubKey = parts[2];
               publicKeyStore.put(name, pubKey);
               out.println("REGISTERED " + name);
               System.out.println("[" + name + "] has been registered successfully.");

            } else if (request.startsWith("GETKEY")) {
               String[] parts = request.split(":");
               String name = parts[1];
               String key = publicKeyStore.get(name);
               if (key != null) {
                  out.println("KEY:" + name + ":" + key);
                  System.out.println("Public key requested for [" + name + "]");
               } else {
                  out.println("ERROR: No such user");
                  System.out.println("Key request failed: [" + name + "] not found.");
               }
            }

            socket.close();

         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }
}