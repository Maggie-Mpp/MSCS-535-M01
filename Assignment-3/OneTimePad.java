// OneTimePad.java
import java.security.SecureRandom;

public class OneTimePad {
  private static final SecureRandom RNG = new SecureRandom();

  public static void main(String[] args) {
    String plaintext = "MY NAME IS UNKNOWN";

    String key = generateKey(plaintext);
    String ciphertext = applyOTP(plaintext, key, true);
    String decrypted  = applyOTP(ciphertext, key, true);

    System.out.println("Plaintext : " + plaintext);
    System.out.println("Key       : " + key);
    System.out.println("Ciphertext: " + ciphertext);
    System.out.println("Decrypted : " + decrypted);
  }

  /** Generate a random A–Z key; non-letters mirrored as spaces to keep alignment. */
  private static String generateKey(String text) {
    StringBuilder sb = new StringBuilder(text.length());
    for (char ch : text.toUpperCase().toCharArray()) {
      if (ch >= 'A' && ch <= 'Z') {
        char k = (char) ('A' + RNG.nextInt(26));
        sb.append(k);
      } else {
        // keep non-letters as is (e.g., space), does not contribute to shift
        sb.append(ch);
      }
    }
    return sb.toString();
  }

  /**
   * XOR-like by modulo-26 addition/subtraction on letters.
   * If encrypt==true: C = (P + K) mod 26
   * If encrypt==false: P = (C - K) mod 26
   * (Here we use same method for both by flipping K sign via +/-.)
   */
  private static String applyOTP(String text, String key, boolean encrypt) {
    StringBuilder out = new StringBuilder(text.length());
    for (int i = 0; i < text.length(); i++) {
      char p = Character.toUpperCase(text.charAt(i));
      char k = Character.toUpperCase(key.charAt(i));

      if (p >= 'A' && p <= 'Z' && k >= 'A' && k <= 'Z') {
        int pv = p - 'A';
        int kv = k - 'A';
        int cv = (pv + kv) % 26; // encryption

        // For decryption we can just call this again with ciphertext as input and same key.
        // Because ( (P+K) + (26-K) ) % 26 = P. But to keep it simple, we’ll use the same op.
        // We'll do a trick: when encrypt==false, invert the key.
        if (!encrypt) {
          cv = ( (p - 'A') - kv ) % 26;
          if (cv < 0) cv += 26;
        }

        out.append((char) ('A' + cv));
      } else {
        // Non-letters pass through unchanged (spaces, punctuation)
        out.append(p);
      }
    }
    return out.toString();
  }
}
