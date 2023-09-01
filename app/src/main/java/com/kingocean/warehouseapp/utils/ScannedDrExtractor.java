package com.kingocean.warehouseapp.utils;

import android.util.Log;

import com.kingocean.warehouseapp.data.model.ScannedDr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScannedDrExtractor {
    public static String extractValue(String text) {
//        String regex = "%[a-zA-Z]%([0-9]+)-?[0-9]*";
        String regex = "\\%([a-zA-Z])\\%([0-9]{8})-?([0-9]*)%";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(2);
        }

        return text; // Return null if no match is found
    }

    public static String extractContainerValue(String text) {
        // First pattern: \\%([a-zA-Z])\\%([0-9]{8})-?([0-9]*)
        String regex1 = "\\%([a-zA-Z])\\%([0-9]{8})-?([0-9]*)%";

        // Second pattern: ([0-9]{8})
        String regex2 = "([0-9]{8})";

        Pattern pattern1 = Pattern.compile(regex1);
        Pattern pattern2 = Pattern.compile(regex2);

        Matcher matcher1 = pattern1.matcher(text);
        Matcher matcher2 = pattern2.matcher(text);

        if (matcher1.find()) {
            return matcher1.group(2); // Extract from the second capturing group
        } else if (matcher2.find()) {
            return matcher2.group(1); // Extract from the first capturing group
        }

        return null; // Return null if no match is found
    }
    public static ScannedDr extractScannedDr(String text) {
//        String regex = "\\%([a-zA-Z])\\%([0-9]+)-?([0-9]*)\\%";
        String regex = "\\%([a-zA-Z])\\%([0-9]{8})-?([0-9]*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String drNumber = matcher.group(2);
            String unitSequence = matcher.group(3);
            Log.i("SCANNER", "extractScannedDr: " + drNumber + "-"+ unitSequence);

            return new ScannedDr(drNumber, unitSequence);
        }

        Log.i("SCANNER", "extractScannedDr: MOJON" );

        return null;
    }

    // New method to extract numeric sequence without the % and letter
    public static ScannedDr extractNumeric(String text) {
        String regex = "([0-9]{8})"; // Match 8-digit numeric sequence
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String drNumber = matcher.group(1);
            String unitSequence = ""; // Numeric sequence has no unit sequence in this case
            Log.i("SCANNER", "extractNumeric: " + drNumber);

            return new ScannedDr(drNumber, unitSequence);
        }

        Log.i("SCANNER", "extractNumeric: MOJON");

        return null;
    }

}
