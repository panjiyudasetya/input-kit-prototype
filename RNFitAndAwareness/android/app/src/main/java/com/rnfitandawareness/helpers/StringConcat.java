package com.rnfitandawareness.helpers;

import android.text.TextUtils;

/**
 * Created by panjiyudasetya on 5/29/17.
 */

public class StringConcat {

    public static String concat(String src, String target) {
        if (TextUtils.isEmpty(src)) return src + target;
        else return TextUtils.isEmpty(target) ? src : (src + (", " + target));
    }
}
