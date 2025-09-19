package Exp2_Cipher.part2;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;

// import javax.swing.text.html.HTMLDocument.Iterator; // Removed incorrect import

public class PlayfairAttackToFiles {
   static char[][] makeMatrix(String key) {
      String alphabet = "ABCDEFGHIKLMNOPQRSTUVWXYZ"; // J->I
      LinkedHashSet<Character> set = new LinkedHashSet<>();
      for (char c : key.toUpperCase().toCharArray())
         if (Character.isLetter(c)) {
            char cc = (c == 'J') ? 'I' : c;
            set.add(cc);
         }
      for (char c : alphabet.toCharArray())
         set.add(c);
      char[][] m = new char[5][5];
      java.util.Iterator<Character> it = set.iterator();
      for (int i = 0; i < 5; i++)
         for (int j = 0; j < 5; j++)
            m[i][j] = it.next();
      return m;
   }

   static String prepare(String s) {
      s = s.toUpperCase().replaceAll("[^A-Z]", "").replace('J', 'I');
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < s.length(); i++) {
         char a = s.charAt(i);
         sb.append(a);
         if (i < s.length() - 1 && s.charAt(i) == s.charAt(i + 1))
            sb.append('X');
      }
      if (sb.length() % 2 == 1)
         sb.append('X');
      return sb.toString();
   }

   static int[] findPos(char[][] m, char c) {
      for (int i = 0; i < 5; i++)
         for (int j = 0; j < 5; j++)
            if (m[i][j] == c)
               return new int[] { i, j };
      return null;
   }

   static String decryptWithMatrix(String ct, char[][] m) {
      String s = prepare(ct);
      StringBuilder out = new StringBuilder();
      for (int i = 0; i < s.length(); i += 2) {
         char a = s.charAt(i), b = s.charAt(i + 1);
         int[] pa = findPos(m, a), pb = findPos(m, b);
         if (pa[0] == pb[0]) {
            out.append(m[pa[0]][(pa[1] + 4) % 5]);
            out.append(m[pb[0]][(pb[1] + 4) % 5]);
         } else if (pa[1] == pb[1]) {
            out.append(m[(pa[0] + 4) % 5][pa[1]]);
            out.append(m[(pb[0] + 4) % 5][pb[1]]);
         } else {
            out.append(m[pa[0]][pb[1]]);
            out.append(m[pb[0]][pa[1]]);
         }
      }
      return out.toString();
   }

   public static void main(String[] args) throws Exception {
      Scanner sc = new Scanner(System.in);
      System.out.print("Enter ciphertext: ");
      String ct = sc.nextLine();

      System.out
            .print("Enter keywords file path (one keyword per line), or press Enter to use wordlists/keywords.txt: ");
      String kwPath = sc.nextLine().trim();
      if (kwPath.isEmpty())
         kwPath = "wordlists/keywords.txt";

      String outDir = "Exp2_Cipher/pairs/a_to_b_playfair";
      FileUtils.ensureDir(outDir);
      FileUtils.write(outDir + "/ciphertext.txt", ct);

      List<String> keys;
      try {
         keys = Files.readAllLines(Paths.get(kwPath));
      } catch (Exception e) {
         keys = Arrays.asList("KEYWORD", "PLAYFAIR", "EXAMPLE", "KNOWLEDGE");
      }

      StringBuilder prog = new StringBuilder();
      String bestKey = null;
      String bestPlain = null;
      int bestScore = Integer.MAX_VALUE;
      int tried = 0;
      for (String k : keys) {
         if (k.trim().isEmpty())
            continue;
         tried++;
         char[][] m = makeMatrix(k);
         String pt = decryptWithMatrix(ct, m);
         int score = EnglishScorer.score(pt);
         prog.append("Key=").append(k).append(" score=").append(score).append(" pt=")
               .append(pt.substring(0, Math.min(60, pt.length()))).append("\n");
         if (score < bestScore) {
            bestScore = score;
            bestKey = k;
            bestPlain = pt;
         }
         if (tried % 200 == 0)
            FileUtils.append(outDir + "/program_output.txt", "Tried " + tried + " keys...\n");
      }

      FileUtils.write(outDir + "/program_output.txt", prog.toString());
      FileUtils.write(outDir + "/decrypted_plaintext.txt", bestPlain == null ? "" : bestPlain);
      FileUtils.write(outDir + "/recovered_key.txt", bestKey == null ? "" : bestKey);
      FileUtils.write(outDir + "/attack_log.txt", "Tried keywords file: " + kwPath + " (total tried: " + tried
            + ")\nSee program_output.txt for all candidates.\n");
      FileUtils.write(outDir + "/metadata.txt",
            "Cipher: Playfair (keyword list attack)\nSender: memberA\nReceiver: memberB\n");
      System.out.println("Done. Best key: " + bestKey + " score=" + bestScore);
   }
}
