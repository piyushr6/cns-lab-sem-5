package Exp2_Cipher.part2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CaesarBruteforceToFiles {
   public static String caesar(String text, int shift) {
      StringBuilder sb = new StringBuilder();
      for (char ch : text.toCharArray()) {
         if (Character.isUpperCase(ch))
            sb.append((char) ('A' + (ch - 'A' + shift + 26) % 26));
         else if (Character.isLowerCase(ch))
            sb.append((char) ('a' + (ch - 'a' + shift + 26) % 26));
         else
            sb.append(ch);
      }
      return sb.toString();
   }

   public static void main(String[] args) throws Exception {
      Scanner sc = new Scanner(System.in);
      System.out.print("Enter ciphertext: ");
      String ct = sc.nextLine();

      String outDir = "Exp2_Cipher/pairs/a_to_b_caesar";
      FileUtils.ensureDir(outDir);
      FileUtils.write(outDir + "/ciphertext.txt", ct);

      Map<Integer, Integer> scoreMap = new HashMap<>();
      Map<Integer, String> cand = new HashMap<>();
      for (int s = 0; s < 26; s++) {
         String pt = caesar(ct, 26 - s);
         int score = EnglishScorer.score(pt);
         scoreMap.put(s, score);
         cand.put(s, pt);
      }
      List<Map.Entry<Integer, Integer>> ranked = new ArrayList<>(scoreMap.entrySet());
      ranked.sort(Map.Entry.comparingByValue());

      int bestShift = ranked.get(0).getKey();
      String bestPlain = cand.get(bestShift);

      StringBuilder programOutput = new StringBuilder();
      for (int i = 0; i < Math.min(5, ranked.size()); i++) {
         int shift = ranked.get(i).getKey();
         programOutput.append("Shift ").append(shift)
               .append(" -> ").append(cand.get(shift))
               .append(" [score=").append(ranked.get(i).getValue()).append("]\n");
      }

      FileUtils.write(outDir + "/program_output.txt", programOutput.toString());
      FileUtils.write(outDir + "/decrypted_plaintext.txt", bestPlain);
      FileUtils.write(outDir + "/recovered_key.txt", "Shift = " + bestShift);
      FileUtils.write(outDir + "/attack_log.txt", programOutput.toString());
      FileUtils.write(outDir + "/metadata.txt", "Cipher: Caesar\nSender: memberA\nReceiver: memberB");
   }
}