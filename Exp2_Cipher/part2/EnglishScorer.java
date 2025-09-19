package Exp2_Cipher.part2;

public class EnglishScorer {
   private static final double[] ENGLISH_FREQ = {
         8.167, 1.492, 2.782, 4.253, 12.702, 2.228, 2.015, 6.094,
         6.966, 0.153, 0.772, 4.025, 2.406, 6.749, 7.507, 1.929,
         0.095, 5.987, 6.327, 9.056, 2.758, 0.978, 2.360, 0.150,
         1.974, 0.074
   };

   public static int score(String text) {
      text = text.toUpperCase();
      int[] counts = new int[26];
      int total = 0;
      for (char c : text.toCharArray()) {
         if (c >= 'A' && c <= 'Z') {
            counts[c - 'A']++;
            total++;
         }
      }
      if (total == 0)
         return Integer.MAX_VALUE;
      double chi2 = 0;
      for (int i = 0; i < 26; i++) {
         double expected = ENGLISH_FREQ[i] * total / 100.0;
         double diff = counts[i] - expected;
         chi2 += diff * diff / expected;
      }
      return (int) chi2;
   }

}
