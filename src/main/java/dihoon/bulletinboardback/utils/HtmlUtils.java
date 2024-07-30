package dihoon.bulletinboardback.utils;

import org.apache.commons.text.StringEscapeUtils;

public class HtmlUtils {
    public static String encodeHtml(String html) {
        return StringEscapeUtils.escapeHtml4(html);
    }

    public static String decodeHtml(String html) {
        return StringEscapeUtils.unescapeHtml4(html);
    }
}
