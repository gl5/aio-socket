package io.github.mxd888.http.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class HttpUtils {
    /**
     * 解码URI中的参数
     *
     * @param paramStr http参数字符串： aa=111&bb=222
     * @param paramMap 参数存放Map
     */
    public static void decodeParamString(String paramStr, Map<String, String[]> paramMap) {
        if (StringUtils.isBlank(paramStr)) {
            return;
        }
        String[] uriParamStrArray = StringUtils.split(paramStr, "&");
        for (String param : uriParamStrArray) {
            int index = param.indexOf("=");
            if (index == -1) {
                continue;
            }
            try {
                String key = StringUtils.substring(param, 0, index);
                String value = URLDecoder.decode(StringUtils.substring(param, index + 1), "utf8");
                String[] values = paramMap.get(key);
                if (values == null) {
                    paramMap.put(key, new String[]{value});
                } else {
                    String[] newValue = new String[values.length + 1];
                    System.arraycopy(values, 0, newValue, 0, values.length);
                    newValue[values.length] = value;
                    paramMap.put(key, newValue);
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
