package model;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


/*
 * This static class handles all encryption and hashing.
 * 
 * CSD 460 Team 3
 * 
 * Ian Lewis
 * Robert Minkler
 * Kevin Ramirez
 * 
 */


public class SecurityPeer {
	

	private static final int passKeyLength = 512;
	private static final int passIterations = 100000;
	private static final String hashAlg = "PBKDF2WithHmacSHA256";

	// Return Hex String salt + password string. - separator
	public static String hashPassword(String password) {

		byte[] salt = new byte[16];

		// generate random salt
		SecureRandom random = new SecureRandom();
		random.nextBytes(salt);

		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, passIterations, passKeyLength);

		try {

			SecretKeyFactory factory = SecretKeyFactory.getInstance(hashAlg);

			byte[] hash = factory.generateSecret(spec).getEncoded();

			// return hex string with salt and hashed password
			return ("" + byteToHex(salt) + "-" + byteToHex(hash));

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}

		return null;
	}

	// Return Hex String salt + password string. - separator
	public static String hashPassword(String password, String salt) {

		// convert salt to byte array
		byte[] byteSalt = hexToByte(salt);

		KeySpec spec = new PBEKeySpec(password.toCharArray(), byteSalt, passIterations, passKeyLength);

		// hash the password with supplied salt
		try {

			SecretKeyFactory factory = SecretKeyFactory.getInstance(hashAlg);

			byte[] hash = factory.generateSecret(spec).getEncoded();

			// return hex string with salt and hashed password
			return ("" + salt + "-" + byteToHex(hash));

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}

		// if failed, return null
		return null;
	}
	
	// convert byte array to hex string
	private static String byteToHex(byte[] byteArray) {
		StringBuilder hex = new StringBuilder();

		// Iterating through each byte in the array
		for (byte i : byteArray) {
			hex.append(String.format("%02X", i));
		}

		return hex.toString();
	}

	// convert hex string to byte array Geeks for Geeks
	// https://www.geeksforgeeks.org/java/java-program-to-convert-hex-string-to-byte-array/
	private static byte[] hexToByte(String hexString) {

		// Initializing the byte array
		byte[] byteArray = new byte[hexString.length() / 2];

		// Iterate over each character
		for (int i = 0; i < byteArray.length; i++) {
			int index = i * 2;

			// Using parseInt() method of Integer class convert to byte
			int val = Integer.parseInt(hexString.substring(index, index + 2), 16);
			byteArray[i] = (byte) val;
		}

		return byteArray;
	}
}
