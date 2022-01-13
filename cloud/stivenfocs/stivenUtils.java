package cloud.stivenfocs;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class stivenUtils {

    public static boolean isdigit(String string) {
        int intValue;

        if(string == null || string.equals("")) {
            return false;
        }

        try {
            intValue = Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {}
        return false;
    }

    public static List<String> colorlist(List<String> uncoloredList) {
        List<String> coloredList = new ArrayList<>();
        for(String line : uncoloredList) {
            coloredList.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return coloredList;
    }

}
