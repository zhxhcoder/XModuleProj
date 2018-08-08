package com.zhxh.base.utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhxh on 2018/8/8
 */
public class StringUtils {

    private static final String floatRegex = "(-?\\d+)|(-?\\d+\\.\\d+)";
    private static final String emptyRegex = "\\s*";
    private static final String colorRegex = "#([0-9a-fA-F]{3}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8})";
    private static final String hanziRegex = "[\\u4e00-\\u9fa5]";
    private static final String emailRegex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
    private static final String zeroRegex = "[\\s0\\.]+";

    public static List<String> getRegexList(String input, String regex) {
        List<String> stringList = new ArrayList<>();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        while (m.find())
            stringList.add(m.group());

        return stringList;
    }


    public static boolean isFloatStr(String input) {
        return isRegexMatch(input, floatRegex);
    }

    public static boolean isColorStr(String input) {
        return isRegexMatch(input, colorRegex);
    }

    public static boolean isEmptyStr(String input) {
        return isRegexMatch(input, emptyRegex);
    }

    public static boolean isHanziStr(String input) {
        return isRegexMatch(input, hanziRegex);
    }

    public static boolean isEmailStr(String input) {
        return isRegexMatch(input, emailRegex);
    }

    public static boolean isZeroStr(String input) {
        return isRegexMatch(input, zeroRegex);
    }

    public static boolean isRegexMatch(String input, String regex) {

        if (TextUtils.isEmpty(input) || TextUtils.isEmpty(regex)) {
            return false;
        }
        if (!regex.startsWith("^")) {
            regex = "^" + regex;
        }
        if (!regex.endsWith("$")) {
            regex = regex + "$";
        }


        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);

        return m.find();

    }


}
