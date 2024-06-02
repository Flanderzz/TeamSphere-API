package com.YipYapTimeAPI.YipYapTimeAPI.utils;

import java.util.Random;

public class UniqueIDGenerator {

    private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ID_LENGTH = 9;
    private static final Random random = new Random();

    public static String generateInviteId() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ID_LENGTH; i++) {
            // can be an issue with random not being random if
            // there are multiple services, possible duplicates
            // potential use of uuid v4 or regex instead of this
            int randomIndex = random.nextInt(CHARS.length());
            sb.append(CHARS.charAt(randomIndex));
        }
        return sb.toString();
    }
}
