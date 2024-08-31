package com.example.demo.util;

public class AppCipherUtils {
    private static final int CHUNK_SIZE = 6;
    private static final int BYTE_BINARY_CHUNK_SIZE = 8;
    private static String[] BYTE_BINARY = new String[]{
                "00110000", "00110001", "00110010", "00110011", "00110100",
                "00110101", "00110110", "00110111", "00111000", "00111001"
    };

    private static String[] ALPHA_BINARY = new String[]{
                "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P",
                "Q","R","S","T","U","V","W","X","Y","Z","a","b","c","d","e","f",
                "g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v",
                "w","x","y","z","0","1","2","3","4","5","6","7","8","9", "_", "#"
    };

    private static int ALPHA_LENGTH = 26;

    private static String getBinaryByte(int index){
        return BYTE_BINARY[index];
    }

    protected static String getNumericBinary(String number){
        StringBuffer stringBuffer = new StringBuffer();
        int length = number.length();
        for(int i=0; i<length; i++){
            stringBuffer.append(getBinaryByte(Character.getNumericValue(number.charAt(i))));
        }
        return stringBuffer.toString();
    }

    protected static String getNumericText(String appendedCipherSecretText){
        String numericText = "";
        int length = appendedCipherSecretText.length();
        int padding0 = 0;
        for(int i=0; i<length; i++){
            int val = appendedCipherSecretText.charAt(i);
            String stringVal = val+"";
            padding0 = CHUNK_SIZE - stringVal.length();
            numericText += paddingZeros(padding0)+val;
        }
        return numericText;
    }

    private static String paddingZeros(int times){
        switch (times){
            case 1:
                return "0";
            case 2:
                return "00";
            case 3:
                return "000";
            case 4:
                return "0000";
            case 5:
                return "00000";
        }
        return "";
    }

    public static String generateCipherText(String numericBinary){
        int loopTimes = numericBinary.length()/CHUNK_SIZE;
        String cipherText = "";
        for(int i=0; i<loopTimes; i++){
            int chuckOffset = i*CHUNK_SIZE;
            cipherText += getAlphaByte(
                    alphaByteIndex(numericBinary.substring(chuckOffset, chuckOffset+CHUNK_SIZE))
            );
        }
        return cipherText;
    }

    /**
     * It will return character from A-Z, a-z, 0-9, _ and # based on given index.
     * @param index
     * @return
     */
    private static String getAlphaByte(int index){
        return ALPHA_BINARY[index];
    }

    /**
     * Requires 6 bit binary string and will calculate its sum.
     * @param alphaByte6
     * @return
     */
    private static int alphaByteIndex(String alphaByte6){
        int result = 0;
        int j = 0;
        for(int i=5; i>=0; i--){
            result += getBinaryGetIntegerVal(alphaByte6.charAt(j), i);
            j++;
        }
        return result;
    }

    /**
     * It will return value based on binary position of 6 bits
     * @param number
     * @param position
     * @return
     */
    private static int getBinaryGetIntegerVal(char number, int position){
        if(number == '1'){
            switch (position){
                case 0:
                    return 1;
                case 1:
                    return 2;
                case 2:
                    return 4;
                case 3:
                    return 8;
                case 4:
                    return 16;
                case 5:
                    return 32;
            }
        }
        return 0;
    }

    /*
     * Decryption
     */
    protected static String degenerateCipherTextToBinary(String cipherText){
        String binaryNumeric = "";
        int length = cipherText.length();
        for(int i=0; i<length; i++){
            binaryNumeric += indexAlphaByte(getCharacterIndex(cipherText.charAt(i)));
        }
        return binaryNumeric;
    }

    protected static String degenerateCipherBinaryToNumber(String cipherBinary){
        int length = cipherBinary.length() / BYTE_BINARY_CHUNK_SIZE;
        String cipherNumber = "";
        for(int i=0; i<length; i++){
            int chunkOffset = i*BYTE_BINARY_CHUNK_SIZE;
            int chunkResult = chunk8ToNumber(cipherBinary.substring(chunkOffset, chunkOffset+BYTE_BINARY_CHUNK_SIZE));
            cipherNumber += chunkResult;
        }
        return cipherNumber;
    }

    protected static String degenerateCipherNumberToText(String cipherNumber){
        int length = cipherNumber.length() / CHUNK_SIZE;
        String cipherText = "";
        for(int i=0; i<length; i++){
            int chunkOffset = i * CHUNK_SIZE;
            cipherText += (char) Integer.parseInt(trimZeroToLeft(cipherNumber.substring(chunkOffset, chunkOffset + CHUNK_SIZE)));
        }
        return cipherText;
    }

    private static String indexAlphaByte(int index){
        String alphaByte6 = "";
        for(int i=5; i>=0; i--){
            String result = getIntegerValGetBinary(index, i);
            alphaByte6 += result;
            if(result.equals("1")){
                index -= Math.pow(2, i);
            }
        }
        return alphaByte6;
    }

    private static String getIntegerValGetBinary(Integer value, int position){
        int powerPositionValue = (int) Math.pow(2, position);
        if(value >= powerPositionValue){
            return "1";
        }
        return "0";
    }

    private static int getCharacterIndex(char c){
        int value = c;
        if(value==35){
            //#
            return 63;
        }
        if(value==95){
            //_
            return 62;
        }
        //A = 65 && Z = 90
        if(value >= 48 && value <= 57){
            return value + 4; //48 - 54 = -4, 48 = 0 && 9 = 57
        }
        if(value >= 65 && value <= 90){
            return value - 65;
        }
        if(value >= 97 && value <= 122){
            return value - 71; //97 - 26 = 71, 97 = a && z = 122
        }
        return -1;
    }

    private static int chunk8ToNumber(String chunk){
        if(chunk.charAt(4)=='1'){
            return 8 + (chunk.charAt(7) == '1' ? 1 : 0);
        }
        if(chunk.charAt(5)=='1'){
            return 4 + (chunk.charAt(6) == '1' ? 2 : 0) + (chunk.charAt(7) == '1' ? 1 : 0);
        }
        return (chunk.charAt(6) == '1' ? 2 : 0) + (chunk.charAt(7) == '1' ? 1 : 0);
    }

    private static String trimZeroToLeft(String number){
        int length = number.length();
        int start = 0;
        for(int i=0; i<length; i++){
            if(number.charAt(i)!='0'){
                start = i;
                break;
            }
        }
        return number.substring(start);
    }

    /*
    Hash
     */
    protected static String hashCipher(String text, String secret){
        if(text == null || secret == null || text.length() == 0 || secret.length() == 0){
            throw new RuntimeException("Text or secret size must be greater than 0!");
        }
        String prepared64 = prepared64Character(text, secret);
        return prepared64;
    }

    private static byte modulus(byte b){
        if(b < 0){
            return (byte)(b * 1);
        }
        return b;
    }

    private static String base64String(String text){
        byte[] bytes = text.getBytes();
        int length = bytes.length;
        for(int i=0; i<length; i++){
            if(bytes[i] < 0){
                bytes[i] = modulus(bytes[i]);
            }
            bytes[i] = (byte) (bytes[i] % 127);
            int mulValue = 2;
            while (bytes[i] < 33){
                if(mulValue == 2){
                    mulValue = 3;
                }else{
                    mulValue = 2;
                }
                bytes[i] *= 33 * mulValue;
            }
        }
        return new String(bytes);
    }

    private static String prepared64Character(String text, String secret){
        String firstHalf = hash32characters(text);
        String secondHalf = hash32characters(secret);
        String jumbled = "";
        for(int i=0; i<32; i++){
            jumbled += firstHalf.charAt(i) +""+ secondHalf.charAt(i);
        }
        return jumbled;
    }

    private static String hash32characters(String text){
        String result = text;
        int length = text.length();
        if(length>32){
            result = moreMake32(text);
        }
        if(length<32){
            result = lessMake32(text);
        }
        return hash(result);
    }

    private static String hashSecret = "tradeflow360";

    private static String hash(String text){
        String result = "";
        String firstHalf = text.substring(0, 16);
        String secondHalf = text.substring(16, 32);
        for(int i=0; i<500; i++){
            text = divideTwoIntervals(text, firstHalf, secondHalf);
            secondHalf = text.substring(16, 32);
            text = base64String(text);
        }
        result = text;
        return result;
    }

    private static String divideTwoIntervals(String text, String firstHalf, String secondHalf){
        String result = divideOneInterval(text.substring(0, 16), secondHalf)
                + divideOneInterval(text.substring(16, 32), firstHalf);
        return result;
    }

    private static String divideOneInterval(String text, String half){
        byte[] textBytes = text.getBytes();
        byte[] halfBytes = half.getBytes();
        int divValue = 2;
        for(int i=0; i<16; i++){
            if(divValue == 2){
                divValue = 3;
            }else {
                divValue = 2;
            }
            textBytes[i] = (byte) ((textBytes[i] + halfBytes[i]) / divValue);
        }
        return new String(textBytes);
    }

    private static String lessMake32(String text){
        int length = text.length();
        int padding = 0;
        while(length != 32){
            padding = (32 - length) > 12 ? 12 : 32 - length;
            text += hashSecret.substring(0, padding);
            length = text.length();
        }
        return text;
    }

    private static String moreMake32(String text){
        boolean flag = true;
        int index = 0;
        int length = text.length();
        while(length != 32){
            if(flag){
                index = text.length() / 2;
                flag = false;
            }else{
                index = text.length() / 3;
                flag = true;
            }
            text = text.substring(0, index-1)+text.substring(index, length);
            length = text.length();
        }
        return text;
    }

    protected static boolean isHashMatches(String rawText, String hashText, String secret){
        String hashRawText = hashCipher(rawText, secret);
        return hashRawText.equals(hashText) ? true : false;
    }

//    public static void main(String[] args) {
//        String password ="123456789";
//        String secret ="TR@D3F1OW36O";
//        String hashedPassword = AppCipherUtils.hashCipher(password, secret);
//        System.out.println(hashedPassword);
//
//    }
}
