package Exp2_Cipher.part2;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class MonoalphabeticHillClimbToFiles {
   // Common English frequency order used for initialization
   private static final String EN_ORDER = "ETAOINSHRDLCUMWFGYPBVKJXQZ";

   // Apply substitution map to ciphertext (map maps 'A'..'Z' -> plaintext letter)
   static String applyKey(String ct, char[] map) {
      StringBuilder sb = new StringBuilder();
      for (char ch : ct.toCharArray()) {
         if (Character.isUpperCase(ch))
            sb.append(map[ch - 'A']);
         else if (Character.isLowerCase(ch))
            sb.append(Character.toLowerCase(map[Character.toUpperCase(ch) - 'A']));
         else
            sb.append(ch);
      }
      return sb.toString();
   }

   // Initialize mapping by frequency: most frequent cipher letter -> most frequent
   // English letter
   static char[] freqInit(String ct) {
      int[] f = new int[26];
      for (char c : ct.toUpperCase().toCharArray())
         if (c >= 'A' && c <= 'Z')
            f[c - 'A']++;
      Integer[] idx = new Integer[26];
      for (int i = 0; i < 26; i++)
         idx[i] = i;
      Arrays.sort(idx, (a, b) -> Integer.compare(f[b], f[a])); // descending by frequency
      char[] map = new char[26];
      for (int i = 0; i < 26; i++)
         map[idx[i]] = EN_ORDER.charAt(i);
      return map;
   }

   public static void main(String[] args) throws Exception {
      Scanner sc = new Scanner(System.in);
      System.out.print("Enter ciphertext: ");
      String ct = sc.nextLine();

      // output directory (matches pattern you used earlier)
      String outDir = "Exp2_Cipher/pairs/a_to_b_mono";
      FileUtils.ensureDir(outDir);
      FileUtils.write(outDir + "/ciphertext.txt", ct);

      // initialization
      char[] cur = freqInit(ct);
      String curPt = applyKey(ct, cur);
      int curScore = EnglishScorer.score(curPt); // lower is better (chi-square style)
      String bestPt = curPt;
      int bestScore = curScore;
      char[] bestMap = cur.clone();

      // hill-climb parameters
      Random rnd = new Random(12345); // deterministic seed; change for randomness
      int iterations = 40000;
      StringBuilder prog = new StringBuilder();
      prog.append("Initial score=").append(curScore).append("\n");
      FileUtils.write(outDir + "/program_output.txt", prog.toString());

      // hill-climb loop: propose random swap, accept if score improves
      for (int it = 0; it < iterations; it++) {
         int a = rnd.nextInt(26), b = rnd.nextInt(26);
         if (a == b)
            continue;
         char[] cand = cur.clone();
         char tmp = cand[a];
         cand[a] = cand[b];
         cand[b] = tmp;
         String candPt = applyKey(ct, cand);
         int candScore = EnglishScorer.score(candPt);
         if (candScore < curScore) {
            cur = cand;
            curScore = candScore;
            curPt = candPt;
            if (curScore < bestScore) {
               bestScore = curScore;
               bestPt = curPt;
               bestMap = cur.clone();
            }
         }
         // periodic logging (append to program_output.txt)
         if (it % 5000 == 0) {
            String s = "iter=" + it + " bestScore=" + bestScore + " curScore=" + curScore + "\n";
            FileUtils.append(outDir + "/program_output.txt", s);
         }
      }

      // prepare mapping text (A->X etc.)
      StringBuilder mapTxt = new StringBuilder();
      for (int i = 0; i < 26; i++)
         mapTxt.append((char) ('A' + i)).append("->").append(bestMap[i]).append("\n");

      // write output files
      FileUtils.write(outDir + "/program_output.txt",
            "Final bestScore=" + bestScore + "\n(see appended logs above if any)\n");
      // also append mapping progress summary to program_output (so marker has quick
      // view)
      FileUtils.append(outDir + "/program_output.txt", "Final mapping:\n" + mapTxt.toString());

      FileUtils.write(outDir + "/decrypted_plaintext.txt", bestPt == null ? "" : bestPt);
      FileUtils.write(outDir + "/recovered_key.txt", mapTxt.toString());
      FileUtils.write(outDir + "/attack_log.txt",
            "Hill-climb substitution attack\n" +
                  "Iterations=" + iterations + " seed=12345\n" +
                  "Initialization: frequency-based mapping\n" +
                  "Acceptance: greedy (only accept swaps that improve score)\n" +
                  "Score metric: EnglishScorer (lower is better)\n");
      FileUtils.write(outDir + "/metadata.txt",
            "Cipher: Monoalphabetic (substitution)\nSender: memberA\nReceiver: memberB\n");

      System.out.println("Done. Files written to: " + outDir);
      System.out.println("Best score=" + bestScore);
   }
}