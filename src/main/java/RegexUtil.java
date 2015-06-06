import com.google.common.base.Strings;

import java.util.regex.Pattern;


public class RegexUtil {

    //非字母、数字和中文的正则表达式
    public static final Pattern NOT_ALPHA_NUM_CHINESE_PATTER = Pattern.compile("[^(a-zA-Z0-9\\u4e00-\\u9fa5)]");

    //非中文的正则表达式
    public static final Pattern NOT_CHINESE_PATTER = Pattern.compile("[^\\u4e00-\\u9fa5]");

    /**
     * 去除非字母、数字和中文的特殊符号
     * @param text
     * @return
     */
    public static String trimSpecialSymbol(String text)
    {
        if(Strings.isNullOrEmpty(text))
            return "";
        return NOT_ALPHA_NUM_CHINESE_PATTER.matcher(text).replaceAll("");
    }

    /**
     * 去除非中文的特殊符号
     * @param text
     * @return
     */
    public static String trimNotChineseSymbol(String text)
    {
        if(Strings.isNullOrEmpty(text))
            return "";
        return NOT_CHINESE_PATTER.matcher(text).replaceAll("");
    }

}
