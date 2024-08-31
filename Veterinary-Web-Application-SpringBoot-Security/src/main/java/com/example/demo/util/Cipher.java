package com.example.demo.util;

public interface Cipher {
    String cipher(String text, String secret);
    String decipher(String cipherText, String secret);
    String hashCipher(String text, String secret);
    boolean isHashCipherMatches(String text, String hashCipher, String secret);
}
