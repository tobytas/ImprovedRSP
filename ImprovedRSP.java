import java.util.Random;
import java.util.Scanner;

import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import javax.xml.bind.DatatypeConverter;

public class ImprovedRSP {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final int KEY_BYTES_LENGTH = 32;

    public static void main(String[] args) throws NoSuchAlgorithmException, 
                                                  InvalidKeyException {
        if (args.length < 3 || args.length % 2 == 0) {
            System.out.println("Wrong number of parameters: must be >= 3 and odd");
            System.exit(1);
        }

        String[] arr = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            for (int j = 0; j < i; j++) {
                if (arr[j].equals(args[i])) {
                    System.out.println("Wrong parameter " + args[i] 
                        + ": duplicates are not allowed");
                    System.exit(2);
                }
            }
            arr[i] = args[i];
        }

        int comp = getCompChoice(arr);
        byte[] key = new byte[KEY_BYTES_LENGTH];
        getSecureKey(key);
        /*  
         *  hmac checked with: https://www.liavaag.org/English/SHA-Generator/HMAC/
         */
        byte[] hmac = getHmac(key, new byte[] {(byte) (comp + 1)}); 
        printByteArray("HMAC: ", hmac);

        int user = getUserChoice(arr);
        if (user == 0) {
            System.exit(0);
        }
        user--;

        System.out.println("comp: " + arr[comp]);
        System.out.println("user: " + arr[user]);

        if (user == comp) {
            System.out.println("draw");
        } else {
            int steps = comp > user ? comp - user : user - comp + 1;
            if (steps % 2 == 0) {
                System.out.println("comp wins");
            } else {
                System.out.println("user wins");
            }
        }
        printByteArray("key: ", key);
    }

    private static int getCompChoice(String[] arr) {
        Random random = new Random(System.currentTimeMillis());
        return random.nextInt(arr.length);
    }

    private static void getSecureKey(byte[] key) {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(key);
    }

    private static byte[] getHmac(byte[] key, byte[] input) throws NoSuchAlgorithmException, 
                                                                   InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec sks = new SecretKeySpec(key, HMAC_SHA256);
        mac.init(sks);
        byte[] hmac = mac.doFinal(input);
        return hmac;
    }
    
    private static int getUserChoice(String[] arr) {
        Scanner scanner = new Scanner(System.in);
        int user = -1;
        while (true) {
            showMenu(arr);
            try {
                user = Integer.valueOf(scanner.nextLine());
            } catch (Exception e) {

            }
            if (user >= 0 && user <= arr.length) {
                scanner.close();
                return user;
            }
            System.out.println("Enter number between 0 and " + arr.length);
        }
    }

    private static void printByteArray(String prefix, byte[] arr) {
        String hexString = DatatypeConverter.printHexBinary(arr);
        System.out.println(prefix + hexString);
    }

    private static void showMenu(String[] arr) {
        System.out.println("Chooses available:");
        for (int i = 0; i < arr.length; i++) {
            System.out.println((i + 1) + " - " + arr[i]);
        }
        System.out.println("0 - exit");
        System.out.print("Enter your choose: ");
    }
}
