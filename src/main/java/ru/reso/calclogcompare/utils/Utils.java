package ru.reso.calclogcompare.utils;

import java.time.LocalTime;

public class Utils {

    public static boolean isBetween(LocalTime candidate, LocalTime start, LocalTime end) {
        return !candidate.isBefore(start) && !candidate.isAfter(end);  // Inclusive.
    }

}
