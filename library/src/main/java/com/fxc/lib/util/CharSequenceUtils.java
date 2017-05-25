package com.fxc.lib.util;

/**
 * @author 风小灿
 * @date 2016-6-25
 */
public class CharSequenceUtils {
    /**
     * 字符串局部对比匹配
     * @param cs
     * @param ignoreCase
     * @param thisStart
     * @param substring
     * @param start
     * @param length
     * @return
     */
    public static boolean regionMatches(CharSequence cs, boolean ignoreCase, int thisStart, CharSequence substring, int start, int length) {
        if ((cs instanceof String) && (substring instanceof String)) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        }
        int index1 = thisStart;
        int index2 = start;
        int tmpLen = length;

        while (tmpLen-- > 0) {
            char c1 = cs.charAt(index1++);
            char c2 = substring.charAt(index2++);

            if (c1 == c2) {
                continue;
            }

            if (!(ignoreCase)) {
                return false;
            }

            if ((Character.toUpperCase(c1) != Character.toUpperCase(c2)) &&
                (Character.toLowerCase(c1) != Character.toLowerCase(c2))) {
                return false;
            }
        }

        return true;
    }
}