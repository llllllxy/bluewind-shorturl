package com.bluewind.shorturl.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

/**
 * @author liuxingyu01
 * @date 2022-06-19 20:58
 * @description 生成AK和SK
 **/
public class GenerateAkAndSk {
    /**
     * 16进制常量池
     */
    public static final String[] POOL = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};


    /**
     * 生成字符串
     *
     * @return 生成的32位长度的16进制字符串
     */
    public static String generateAk() {
        return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
    }


    /**
     * 生成字符串
     *
     * @return 生成的16位长度的16进制字符串
     */
    public static String generateSk() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 32; i++) {
            sb.append(POOL[random.nextInt(POOL.length)]);
        }
        byte[] by = sb.toString().getBytes(StandardCharsets.UTF_8);
        String safeBase64Str = Base64.getEncoder().encodeToString(by);
        safeBase64Str = safeBase64Str.replace('+', '_');
        safeBase64Str = safeBase64Str.replace('/', '_');
        safeBase64Str = safeBase64Str.replace("=", "");
        return safeBase64Str;
    }


    /**
     * 测试一下
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        System.out.println("ak = " + generateAk());
        System.out.println("sk = " + generateSk());
    }
}
