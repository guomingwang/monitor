package com.ratection.gammamonitor.support;

import com.google.common.base.Strings;

public class HexUtil {

    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String intInverseToHexString(int data) {
        String hexString = Integer.toHexString(data);

        if (hexString.length() % 2 != 0) {
            return "0" + hexString;
        }
        return inverseHex(hexString);
    }

    public static String inverseHex(String hexString) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hexString.length() / 2; i ++) {
            result.insert(0, hexString.substring(i * 2, i * 2 + 2));
        }
        return result.toString();
    }

    public static String intToHexString(int data) {
        String hexString = Integer.toHexString(data);

        if (hexString.length() % 2 != 0) {
            return "0" + hexString;
        }
        return hexString;
    }

    public static char[] hexToCharArray(String hexString) {

        char[] chars = new char[hexString.length() / 2];
        for (int i = 0; i < chars.length; i ++) {
            chars[i] = (char) (0xff & HexUtil.hexToInt(hexString.substring(i * 2, i * 2 + 2)));
        }

        return chars;
    }

    public static float hexInverseToFloat(String hexString) {
        String inversedHexString = inverseHex(hexString);

        return Float.intBitsToFloat(Integer.valueOf(inversedHexString.trim(), 16));
    }

    public static String floatToInverseHex(Float floatData) {
        String hexString = Integer.toHexString(Float.floatToIntBits(floatData));

        if (hexString.length() % 2 != 0) {
            return "0" + hexString;
        }
        return inverseHex(hexString);
    }

    public static byte[] hexToByteArray(String hex) {
        if (Strings.isNullOrEmpty(hex)) {
            return new byte[0];
        }

        int byteLen = hex.length() / 2; //每两个字符描述一个字节
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            int intVal = Integer.decode("0x" + hex.substring(i * 2, i * 2 + 2));
            ret[i] = (byte) intVal;
        }
        return ret;
    }

    public static int hexToInt(String hex) {
        if (Strings.isNullOrEmpty(hex)) {
            return 0;
        }

        return Integer.parseInt(hex, 16);
    }

    public static String bytesToHexFun1(byte[] bytes) {

        // 一个byte为8位，可用两个十六进制位标识
        char[] buf = new char[bytes.length * 2];
        int a = 0;
        int index = 0;
        for(byte b : bytes) { // 使用除与取余进行转换
            if (b < 0) {
                a = 256 + b;
            } else {
                a = b;
            }

            buf[index++] = HEX_CHAR[a / 16];
            buf[index++] = HEX_CHAR[a % 16];
        }

        return new String(buf);
    }
}
