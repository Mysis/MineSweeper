package minesweeper.util;

public final class TimeConverter {

    //convert milliseconds into readable string (HH:MM:SS.LLL)
    public static String convertTimeMillisToString(long millis) {
        if (millis >= 3600000) { //if more than an hour has passed
            return String.format("%1$tH:%1$tM:%1$tS.%1$tL", millis);
        } else if (millis >= 60000) { //if more than a minute has passed
            return String.format("%1$tM:%1$tS.%1$tL", millis);
        } else if (millis >= 1000) { //if more than a seconds has passed
            return String.format("%1$tS.%1$tL", millis);
        } else { //if less than a second has passed
            return String.format("0.%1$tL", millis);
        }
    }
}
