package Exp2_Cipher.part2;

import java.util.Scanner;

public class HillBruteforceToFiles {
   static final int MOD = 26;

   static int det2(int[][] m) {
      return (m[0][0] * m[1][1] - m[0][1] * m[1][0]) % MOD;
   }

   static int modInv(int a, int m) {
      a %= m;
      if (a < 0)
         a += m;
      for (int x = 1; x < m; x++)
         if ((a * x) % m == 1)
            return x;
      return -1;
   }

   static int[][] adj2(int[][] m) {
      int[][] adj = new int[2][2];
      adj[0][0] = m[1][1];
      adj[0][1] = -m[0][1];
      adj[1][0] = -m[1][0];
      adj[1][1] = m[0][0];
      for (int i = 0; i < 2; i++)
         for (int j = 0; j < 2; j++) {
            adj[i][j] %= MOD;
            if (adj[i][j] < 0)
               adj[i][j] += MOD;
         }
      return adj;
   }

   static int[][] inverseMatrix(int[][] m) {
      int d = det2(m);
      d %= MOD;
      if (d < 0)
         d += MOD;
      int invDet = modInv(d, MOD);
      if (invDet == -1)
         return null;
      int[][] adj = adj2(m);
      int[][] inv = new int[2][2];
      for (int i = 0; i < 2; i++)
         for (int j = 0; j < 2; j++)
            inv[i][j] = (adj[i][j] * invDet) % MOD;
      return inv;
   }

   static int[] mult(int[][] m, int[] v) {
      int[] out = new int[2];
      for (int i = 0; i < 2; i++) {
         int sum = m[i][0] * v[0] + m[i][1] * v[1];
         sum %= MOD;
         if (sum < 0)
            sum += MOD;
         out[i] = sum;
      }
      return out;
   }

   static String decryptWithInv(int[][] inv, String ct) {
      String s = ct.toUpperCase().replaceAll("[^A-Z]", "");
      while (s.length() % 2 != 0)
         s += 'X';
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < s.length(); i += 2) {
         int[] vec = new int[] { s.charAt(i) - 'A', s.charAt(i + 1) - 'A' };
         int[] res = mult(inv, vec);
         sb.append((char) ('A' + res[0])).append((char) ('A' + res[1]));
      }
      return sb.toString();
   }

   public static void main(String[] args) throws Exception {
      Scanner sc = new Scanner(System.in);
      System.out.print("Enter ciphertext: ");
      String ct = sc.nextLine();

      String outDir = "Exp2_Cipher/pairs/a_to_b_hill_2x2";
      FileUtils.ensureDir(outDir);
      FileUtils.write(outDir + "/ciphertext.txt", ct);

      int total = (int) Math.pow(26, 4); // 26^4
      StringBuilder progOut = new StringBuilder();
      String bestPlain = null;
      int bestScore = Integer.MAX_VALUE;
      String bestKeyDesc = null;
      int tried = 0, invertibleCount = 0;

      for (int a = 0; a < 26; a++)
         for (int b = 0; b < 26; b++)
            for (int c = 0; c < 26; c++)
               for (int d = 0; d < 26; d++) {
                  tried++;
                  int[][] K = { { a, b }, { c, d } };
                  int det = det2(K);
                  det %= MOD;
                  if (det < 0)
                     det += MOD;
                  int g = gcd(det, MOD);
                  if (det == 0 || g != 1)
                     continue;
                  invertibleCount++;
                  int[][] inv = inverseMatrix(K);
                  if (inv == null)
                     continue;
                  String pt = decryptWithInv(inv, ct);
                  int score = EnglishScorer.score(pt);
                  // keep lower chi2
                  if (score < bestScore) {
                     bestScore = score;
                     bestPlain = pt;
                     StringBuilder kd = new StringBuilder();
                     kd.append(String.format("[%d %d;%d %d] (letters: %c%c%c%c)", a, b, c, d,
                           (char) ('A' + a), (char) ('A' + b), (char) ('A' + c), (char) ('A' + d)));
                     bestKeyDesc = kd.toString();
                  }
                  // optionally log some periodic progress
                  if (tried % 50000 == 0) {
                     String p = "Tried " + tried + " / " + total + " (invertible so far " + invertibleCount + ")\n";
                     System.out.print(p);
                     FileUtils.append(outDir + "/program_output.txt", p);
                  }
               }

      progOut.append("Total matrices considered (26^4): ").append(total).append("\n");
      progOut.append("Best score (lower is better): ").append(bestScore).append("\n");
      progOut.append("Best key (numeric & letters): ").append(bestKeyDesc).append("\n");

      FileUtils.write(outDir + "/program_output.txt", progOut.toString());
      FileUtils.write(outDir + "/decrypted_plaintext.txt", bestPlain == null ? "" : bestPlain);
      FileUtils.write(outDir + "/recovered_key.txt", bestKeyDesc == null ? "" : bestKeyDesc);
      FileUtils.write(outDir + "/attack_log.txt",
            "Tried total matrices: " + total + "\nNote: skipped non-invertible matrices (gcd(det,26)!=1)\n" +
                  "Invertible matrices tried logged in program_output.txt (progress updates)\n");
      FileUtils.write(outDir + "/metadata.txt", "Cipher: Hill (2x2 brute force)\nSender: memberA\nReceiver: memberB\n");
      System.out.println("Done. Files in: " + outDir);
   }

   static int gcd(int a, int b) {
      return b == 0 ? a : gcd(b, a % b);
   }
}
