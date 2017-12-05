package PasswordCracker;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Utility class for PasswordCracker.

public class PasswordCrackerUtil {
    private static final String PASSWORD_CHARS = "0123456789abcdefghijklmnopqrstuvwxyz";    // Possible Password symbol (NUMBER(0~9) + CHARACTER(A to Z))
    private static final int PASSWORD_LEN = 6;
    public static final long TOTAL_PASSWORD_RANGE_SIZE = (long) Math.pow(PASSWORD_CHARS.length(), PASSWORD_LEN);

    public static MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot use MD5 Library:" + e.getMessage());
        }
    }

    public static String encrypt(String password, MessageDigest messageDigest) {
        messageDigest.update(password.getBytes());
        byte[] hashedValue = messageDigest.digest();
        return byteToHexString(hashedValue);
    }

    public static String byteToHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                builder.append('0');
            }
            builder.append(hex);
        }
        return builder.toString();
    }

    // Tries i'th candidate (rangeBegin <= i < rangeEnd) and compares against encryptedPassword
    // If original password is found, return the password;
    // if not, return null.

    public static String findPasswordInRange(long rangeBegin, long rangeEnd, String encryptedPassword, TerminationChecker checker)
            throws IOException {
        /** COMPLETE **/
    	MessageDigest msg = getMessageDigest();
        int[] numArrayInBase36Begin = new int[PASSWORD_LEN];
        int[] numArrayInBase36End = new int[PASSWORD_LEN];
        transformDecToBase36(rangeBegin, numArrayInBase36Begin);     
        transformDecToBase36(Math.min(rangeEnd, TOTAL_PASSWORD_RANGE_SIZE) - 1, numArrayInBase36End);
        System.out.println("Range Begin : " + rangeBegin);
        for(int i = 0; i < numArrayInBase36Begin.length; i++){
        	System.out.print(numArrayInBase36Begin[i] + ", ");
        }
        System.out.println("\nRange End : ");
        for(int i = 0; i < numArrayInBase36End.length; i++){
        	System.out.println(numArrayInBase36End[i] + ", ");
        }
        System.out.println("");
        for(long i = rangeBegin, j = Math.min(rangeEnd, TOTAL_PASSWORD_RANGE_SIZE) - 1; i <= j; i++, j--){
            if(i % 100000 == 0 && checker.isTerminated()) {
                return null; 
            }
            String strBegin = transformIntoStr(numArrayInBase36Begin);
            String strEnd;
            if(numArrayInBase36End[0] != -1) {
                strEnd = transformIntoStr(numArrayInBase36End);            	
            } else {
            	strEnd = "";
            }
            String mdBegin = encrypt(strBegin, msg);
            String mdEnd = encrypt(strEnd, msg);
            if(mdBegin.equals(encryptedPassword)) {
                return strBegin;
            }
            if(mdEnd.equals(encryptedPassword)) {
                return strEnd;
            }
            getNextCandidateBegin(numArrayInBase36Begin);
            if(numArrayInBase36End[0] != -1) getNextCandidateEnd(numArrayInBase36End);
        }   
        return null;
    }

    /* ###  transformDecToBase36  ###
     * The transformDecToBase36 transforms decimal into numArray that is base 36 number system
     * If you don't understand, refer to the homework01 overview
    */

    private static void transformDecToBase36(long numInDec, int[] numArrayInBase36) {
        /** COMPLETE **/
    	int i = 0;
        do {
        	numArrayInBase36[i] = (int)(numInDec % 36); 
            numInDec /= 36;
            i++;
        } while (numInDec != 0 && i < PASSWORD_LEN);
        int n = numArrayInBase36.length;
        for(int j = 0; j < n / 2; j++){
        	int tmp = numArrayInBase36[j];
            numArrayInBase36[j] = numArrayInBase36[n - j - 1];
            numArrayInBase36[n - j - 1] = tmp;
        }
    }

    private static void getNextCandidateBegin(int[] candidateChars) {
        /** OPTIONAL **/
    	candidateChars[candidateChars.length - 1]++;
        for(int i = candidateChars.length - 1; i > 0; i--){
            if(candidateChars[i] > 35) {
                candidateChars[i] = 0;
                candidateChars[i - 1] ++;
            }
            else break;
        } 
    }
    
    private static void getNextCandidateEnd(int[] candidateChars) {
        /** OPTIONAL **/
    	candidateChars[candidateChars.length - 1]--;
        for(int i = candidateChars.length - 1; i > 0; i--){
            if(candidateChars[i] < 0) {
                candidateChars[i] = 35;
                candidateChars[i - 1] --;
            }
            else break;
        } 
        if(candidateChars[0] < 0) candidateChars[0] = -1; // TODO(aibeksmagulov): BUGS?
    }

    private static String transformIntoStr(int[] candidateChars) {
        char[] password = new char[candidateChars.length];
        for (int i = 0; i < password.length; i++) {
            password[i] = PASSWORD_CHARS.charAt(candidateChars[i]);
        }
        return new String(password);
    }
}
