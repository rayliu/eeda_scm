package controllers.oms.gateOutOrder.zto;

import java.security.MessageDigest;

import sun.misc.BASE64Encoder;

/**
 * 生成摘要工具类
 */
public class DigestUtil {
    public static final String UTF8 = "UTF-8";

    public final static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    /**
     * base64
     * 
     * @param md5
     * @return
     * @throws Exception
     */
    public static String encryptBASE64(String data) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(data.getBytes(UTF8)).trim();
    }

    /**
     * MD5
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static String encryptMD5(String data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(data.getBytes(UTF8));
        byte[] b = md5.digest();
        // 把密文转换成十六进制的字符串形式
        int j = b.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = b[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }

    /**
     * 摘要生成
     * 
     * @param data
     *            请求数据
     * @param sign
     *            签名秘钥(key或者parternID)
     * @param charset
     *            编码格式
     * @return 摘要
     * @throws Exception
     */
    public static String digest(String partner, String dataTime, String data,
            String pass) throws Exception {
        return encryptMD5((partner + dataTime + data + pass));
    }

}