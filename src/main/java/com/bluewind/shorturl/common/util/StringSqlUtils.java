package com.bluewind.shorturl.common.util;

import java.util.*;

/**
 * @author liuxingyu01
 * @date 2022-06-21 20:04
 * @description SQL字符串工具
 **/
public class StringSqlUtils {
    /**
     * List 转化成特定 String 列如 sql 语句中 in('','') 字符串
     *
     * @param argSplitChar 包裹字段值的符号 如 '
     * @param compart      字段值间的分隔符 如 ,
     * @return 格式化之后的字符串
     */
    public static String listToSpString(List<?> list, String argSplitChar, String compart) {
        if (list == null || list.size() == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        if (argSplitChar == null)
            argSplitChar = "";
        if (compart == null)
            compart = ",";
        sb.append(argSplitChar).append(String.valueOf(list.get(0))).append(
                argSplitChar);
        for (int i = 1; i < list.size(); i++) {
            sb.append(compart).append(argSplitChar).append(
                    String.valueOf(list.get(i))).append(argSplitChar);
        }
        return sb.toString();
    }

    /**
     * Collection 转化成特定 String 列如 sql 语句中 in('','') 字符串
     *
     * @param col Collection对象
     * @param argSplitChar 包裹字段值的符号 如 '
     * @param compart      字段值间的分隔符 如 ,
     * @return 格式化之后的字符串
     */
    public static String CollectionToSpString(Collection<?> col, String argSplitChar, String compart) {
        if (col == null || col.size() == 0)
            return "";
        Iterator<?> it = col.iterator();
        List<Object> list = new ArrayList<Object>();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return listToSpString(list, argSplitChar, compart);
    }

    /**
     * 数组转化成特定 String 列如 sql 语句中 in('','') 字符串
     *
     * @param argStr 待转换的字符串数组
     * @param argSplitChar 包裹字段值的符号 如 '
     * @param compart      字段值间的分隔符 如 ,
     * @return 格式化之后的字符串
     */
    public static String arrayToSpString(String[] argStr, String argSplitChar,
                                         String compart) {
        return listToSpString(Arrays.asList(argStr), argSplitChar, compart);
    }

    /**
     * 字符串 转换成特定的字符串,参看 方法
     * @param argStr 待转换的字符串
     * @param argSplitChar 包裹字段值的符号 如 '
     * @param compart      字段值间的分隔符 如 ,
     * @author chenyzh
     */
    public static String muliStringToSpString(String argStr,
                                              String argSplitChar, String compart) {
        StringTokenizer st = new StringTokenizer(argStr, compart);
        String[] s = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            s[i] = st.nextToken();
            i++;
        }
        return listToSpString(Arrays.asList(s), argSplitChar, compart);
    }

    /**
     * 测试
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(arrayToSpString(new String[]{"a", "b", "c", "d",
                "e"}, "'", ","));
        String s = "1,2";
        System.out.println(muliStringToSpString(s, "*", ","));
    }
}
