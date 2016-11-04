package io.github.zishell.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by zishell on 2015/8/16.
 */
public class FormatUtils {
    /**
     * format the long timestamp to date
     *
     * @param timestamp
     * @return
     */
    public static String tsToDate(final long timestamp) {
        return new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss").format(timestamp);
    }

    /**
     * format like yyyy-MM-dd HH:mm:ss
     *
     * @param dataStr
     * @return
     */
    public static long strToTimstamp(final String dataStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(dataStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }


}
