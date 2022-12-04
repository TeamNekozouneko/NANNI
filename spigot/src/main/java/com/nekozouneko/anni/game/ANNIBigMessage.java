package com.nekozouneko.anni.game;

import org.bukkit.Bukkit;

import java.util.*;

public class ANNIBigMessage {

    private static final String SQUARE = "▒";

    // Integer

    private static final String[] ONE = new String[] {
            "XXXXXXXXXX",
            "XXXXxxXXXX",
            "XXxxxxXXXX",
            "XXXXxxXXXX",
            "XXXXxxXXXX",
            "XXXXxxXXXX",
            "XXXXxxXXXX",
            "XXXXxxXXXX",
            "XXxxxxxxXX",
            "XXXXXXXXXX"
    };
    private static final String[] TWO = new String[] {
            "XXXXXXXXXX",
            "XXXxxxxXXX",
            "XXxXXXXxXX",
            "XXXXXXXxXX",
            "XXXXXXxXXX",
            "XXXXXxXXXX",
            "XXXXxXXXXX",
            "XXXxXXXXXX",
            "XXxxxxxxXX",
            "XXXXXXXXXX"
    };
    private static final String[] THREE = new String[] {
            "XXXXXXXXXX",
            "XXXxxxxXXX",
            "XXxXXXXxXX",
            "XXXXXXXxXX",
            "XXXxxxxXXX",
            "XXXXXXXxXX",
            "XXXXXXXxXX",
            "XXxXXXXxXX",
            "XXXxxxxXXX",
            "XXXXXXXXXX"
    };
    private static final String[] FOUR = new String[] {
            "XXXXXXXXXX",
            "XXXXXXxXXX",
            "XXXXXxxXXX",
            "XXXXxXxXXX",
            "XXXxXXxXXX",
            "XXxXXXxXXX",
            "XXxxxxxxXX",
            "XXXXXXxXXX",
            "XXXXXXxXXX",
            "XXXXXXXXXX"
    };
    private static final String[] FIVE = new String[] {
            "XXXXXXXXXX",
            "XXxxxxxxXX",
            "XXxXXXXXXX",
            "XXxXXXXXXX",
            "XXxxxxxXXX",
            "XXXXXXXxXX",
            "XXXXXXXxXX",
            "XXxXXXXxXX",
            "XXXxxxxXXX",
            "XXXXXXXXXX"
    };
    private static final String[] SIX = new String[] {
            "XXXXXXXXXX",
            "XXXxxxxXXX",
            "XXxXXXXXXX",
            "XXxXXXXXXX",
            "XXxxxxxXXX",
            "XXxXXXXxXX",
            "XXxXXXXxXX",
            "XXxXXXXxXX",
            "XXXxxxxXXX",
            "XXXXXXXXXX"
    };
    private static final String[] SEVEN = new String[] {
            "XXXXXXXXXX",
            "XXxxxxxxXX",
            "XXxXXXXxXX",
            "XXxXXXXxXX",
            "XXXXXXxXXX",
            "XXXXXXxXXX",
            "XXXXXxXXXX",
            "XXXXXxXXXX",
            "XXXXXxXXXX",
            "XXXXXXXXXX"
    };

    // Alphabet

    private static final String[] B = new String[] {
            "XXXXXXXXXX",
            "XXxxxxxXXX",
            "XXxXXXXxXX",
            "XXxXXXXxXX",
            "XXxxxxxXXX",
            "XXxXXXXxXX",
            "XXxXXXXxXX",
            "XXxXXXXxXX",
            "XXxxxxxXXX",
            "XXXXXXXXXX"
    };
    private static final String[] D = new String[] {
            "XXXXXXXXXX",
            "XXxxxxXXXX",
            "XXxXXXxXXX",
            "XXxXXXXxXX",
            "XXxXXXXxXX",
            "XXxXXXXxXX",
            "XXxXXXXxXX",
            "XXxXXXxXXX",
            "XXxxxxXXXX",
            "XXXXXXXXXX"
    };
    private static final String[] G = new String[] {
            "XXXXXXXXXX",
            "XXXxxxxXXX",
            "XXxXXXXxXX",
            "XXxXXXXXXX",
            "XXxXXxxxXX",
            "XXxXXXXxXX",
            "XXxXXXXxXX",
            "XXxXXXXxXX",
            "XXXxxxxxXX",
            "XXXXXXXXXX"
    };
    private static final String[] L = new String[] {
            "XXXXXXXXXX",
            "XXxXXXXXXX",
            "XXxXXXXXXX",
            "XXxXXXXXXX",
            "XXxXXXXXXX",
            "XXxXXXXXXX",
            "XXxXXXXXXX",
            "XXxXXXXXXX",
            "XXxxxxxxXX",
            "XXXXXXXXXX"
    };
    private static final String[] R = new String[] {
            "XXXXXXXXXX",
            "XXxxxxxXXX",
            "XXxXXXXxXX",
            "XXxXXXXxXX",
            "XXxxxxxXXX",
            "XXxXxXXXXX",
            "XXxXXxXXXX",
            "XXxXXXxXXX",
            "XXxXXXXxXX",
            "XXXXXXXXXX"
    };
    private static final String[] W = new String[] {
            "XXXXXXXXXX",
            "XXxXXXXxXX",
            "XXxXXXXxXX",
            "XXxXXXXxXX",
            "XXxXxxXxXX",
            "XXxXxxXxXX",
            "XXxxXXxxXX",
            "XXxxXXxxXX",
            "XXxXXXXxXX",
            "XXXXXXXXXX"
    };
    private static final String[] Y = new String[] {
            "XXXXXXXXXX",
            "XXxXXXXxXX",
            "XXxXXXXxXX",
            "XXxXXXXxXX",
            "XXXxXXxXXX",
            "XXXXxxXXXX",
            "XXXXxxXXXX",
            "XXXXxxXXXX",
            "XXXXxxXXXX",
            "XXXXXXXXXX"
    };

    private static final Map<Character, String[]> bigIcon = new HashMap<Character, String[]>() {{
            put('1',ONE);put('2',TWO);put('3',THREE);
            put('4',FOUR);put('5',FIVE);put('6',SIX);
            put('7',SEVEN);put('B',B);put('D',D);
            put('L',L);put('G',G);put('R',R);
            put('W',W);put('Y',Y);
    }};

    private ANNIBigMessage() {}

    public static List<String> createMessage(char bigChar, char teamColor, String... message) {
        String[] icon = bigIcon.get(bigChar);
        if (icon == null || icon.length == 0) return Collections.emptyList();
        if (message == null) return Arrays.asList(icon);

        List<String> bi = new ArrayList<>();

        for (String ic : icon) bi.add(ic.replace("X", "§7"+SQUARE+"§r").replace("x", "§"+teamColor+SQUARE));

        if (message.length <= 5) {
            for (int i = 0; i < bi.size(); i++) {
                if (i >= 4) {
                    try {
                        String ii = bi.get(i);
                        bi.set(i, ii + " " + message[i-4]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        bi.set(i, bi.get(i));
                    }
                }
            }
        }

        return bi;
    }

}
