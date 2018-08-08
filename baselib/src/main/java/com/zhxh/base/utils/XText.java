package com.zhxh.base.utils;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhxh on 2018/8/8
 */
public class XText {
    private static Paint paint = new Paint();
    /**
     * 获取字体宽度
     *
     * @param str      字体
     * @param fontSize 字体sp
     * @return 像素
     */
    public static int getTextWeight(String str, int fontSize) {
        paint.setTextSize(fontSize);
        return (int) paint.measureText(str);
    }

    public static SpannableString getSpanColorReg(String sourceStr, String regularExpression, int color) {
        SpannableString sp = new SpannableString(sourceStr);
        Pattern p = Pattern.compile(regularExpression);
        Matcher m = p.matcher(sp);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            // 设置高亮样式
            sp.setSpan(new ForegroundColorSpan(color), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return sp;
    }

    public static SpannableString getSpanSizeReg(String srcStr, String regularExpression, int size) {
        SpannableString resultSpan = new SpannableString(srcStr);
        Pattern p = Pattern.compile(regularExpression);
        Matcher m = p.matcher(srcStr);

        while (m.find() && !regularExpression.equals("")) {
            resultSpan.setSpan(new AbsoluteSizeSpan(size, true), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return resultSpan;
    }


}
