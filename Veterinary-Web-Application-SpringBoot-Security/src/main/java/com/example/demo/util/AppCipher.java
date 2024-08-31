package com.example.demo.util;

import org.springframework.stereotype.Component;

@Component
public class AppCipher implements Cipher{
    private final int ALPHA_SIZE = 26;
    private final int ALPHA_START = 65;
    private final int NUMERIC_START = 48;
    private final int CHUNK_SIZE = 6;

    /*
    Cipher Algorithm:
    1. Takes text_to_be_cipher and secret text
    2. Calculates secret text and sums its value and modulus in range of 26 (Total Alphabet) adding 65 (A)
        Making sure it stays in range of 65-90 (A-Z)
    3. Converting Text Value into Binary Bits (String) of Chunk Size 6, Worldwide there is no 7-digit number character
    4. Converting each Number Text into 8 Bits Binary Bits (String) with corresponding range of number 0-9 (48-57 ASCII)
        Adding addition 0's to match 6 binary bits for final ciphering.
    5. Chunking 8 bits binary to 6 bits as 2^6 = 64 and mapping each 6 bits to (A-Z, a-z, 0-9, _, #) = 64

    Decipher Algorithm:
    Step 5 - 2
     */

    @Override
    public String cipher(String text, String secret) {
        if(text==null || text.length()==0){
            throw new RuntimeException("Text cannot be null or empty!");
        }
        int cipherSecretValue = cipherSecretValue(secret);
        int length = text.length();
        String textAppendedSecret = "";
        for(int i=0; i<length; i++){
            int cipherCharValue = cipherSecretValue+text.charAt(i);
            textAppendedSecret += (char) cipherCharValue;
        }
        String numericText = AppCipherUtils.getNumericText(textAppendedSecret);
        String numericBinary = AppCipherUtils.getNumericBinary(numericText);
        //Adding 0's for insufficient modulus
        numericBinary = padding(numericBinary.length()%CHUNK_SIZE) + numericBinary;
        String result = AppCipherUtils.generateCipherText(numericBinary);
        return result;
    }

    @Override
    public String decipher(String cipherText, String secret) {
        String numericBinary = AppCipherUtils.degenerateCipherTextToBinary(cipherText);
        String numericText = AppCipherUtils.degenerateCipherBinaryToNumber(numericBinary);
        String cipherTxt = AppCipherUtils.degenerateCipherNumberToText(numericText);
        int cipherSecretValue = cipherSecretValue(secret);
        String decipheredText = "";
        int length = cipherTxt.length();
        for(int i=0; i<length; i++){
            decipheredText += (char)(cipherTxt.charAt(i)-cipherSecretValue);
        }
        return decipheredText;
    }

    @Override
    public String hashCipher(String text, String secret) {
        return AppCipherUtils.hashCipher(text, secret);
    }

    @Override
    public boolean isHashCipherMatches(String text, String hashCipher, String secret) {
        return AppCipherUtils.isHashMatches(text, hashCipher, secret);
    }

    private int cipherSecretValue(String secret){
        int result = 0;
        if(secret==null){
            throw new RuntimeException("Secret cannot be null!");
        }
        int length = secret.length();
        if(length<2){
            throw new RuntimeException("Secret must contains more than 1 characters!");
        }
        for(int i=0; i<length; i++){
            result += (secret.charAt(i)%ALPHA_SIZE)+ALPHA_START;
        }
        return result;
    }

    private String padding(int insufficientValue){
        if(insufficientValue==2){
            return "00";
        }
        if(insufficientValue==4){
            return "0000";
        }
        return "";
    }
}
