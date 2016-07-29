package controllers.oms.gateOutOrder.zto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtil {

    /**
     * 发送POST 请求
     * 
     * @param url
     *            请求地址
     * @param charset
     *            编码格式
     * @param params
     *            请求参数
     * @return 响应
     * @throws IOException
     */
    public static String post(String url, String charset,
            Map<String, Object> params) throws IOException {
        HttpURLConnection conn = null;
        OutputStreamWriter out = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer result = new StringBuffer();
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Accept-Charset", charset);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            out = new OutputStreamWriter(conn.getOutputStream(), charset);
            out.write(buildQuery(params, charset));
            out.flush();
            inputStream = conn.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);
            String tempLine = null;
            while ((tempLine = reader.readLine()) != null) {
                result.append(tempLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return result.toString();
    }

    /**
     * 将map转换为请求字符串
     * <p>
     * data=xxx&msg_type=xxx
     * </p>
     * 
     * @param params
     * @param charset
     * @return
     * @throws IOException
     */
    public static String buildQuery(Map<String, Object> params, String charset)
            throws IOException {
        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuffer data = new StringBuffer();
        boolean flag = false;

        for (Entry<String, Object> entry : params.entrySet()) {
            if (flag) {
                data.append("&");
            } else {
                flag = true;
            }
            data.append(entry.getKey())
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue().toString(),
                            charset));
        }

        return data.toString();

    }

}
