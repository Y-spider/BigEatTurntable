package top.chopper.utils;

import cn.hutool.json.JSONUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * FasterWhisper 工具类
 * 用于将 MultipartFile 上传到 Faster-Whisper 服务进行语音转写
 */
public class FasterWhisperUtil {

    // 默认服务 URL，可修改 服务器python接口
    private static String DEFAULT_URL = "http://118.195.164.246:15000/transcribe";

    private FasterWhisperUtil() {
        // 私有构造，防止实例化
    }

    /**
     * 使用默认 URL 调用 Faster-Whisper 服务
     *
     * @param file MultipartFile 文件
     * @return 转写结果 JSON
     * @throws IOException
     * @throws InterruptedException
     */
    public static String transcribe(MultipartFile file) throws IOException, InterruptedException {
        return transcribe(file, DEFAULT_URL);
    }

    /**
     * 调用 Faster-Whisper 服务
     *
     * @param file MultipartFile 文件
     * @param url 服务地址，例如 "http://127.0.0.1:15000/transcribe"
     * @return 转写结果 JSON
     * @throws IOException
     * @throws InterruptedException
     */
    public static String transcribe(MultipartFile file, String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String boundary = "----JavaMultipartBoundary" + System.currentTimeMillis();
        String LINE_FEED = "\r\n";

        // 获取文件类型
        String contentType = file.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // 构建 multipart/form-data 请求头
        StringBuilder sb = new StringBuilder();
        sb.append("--").append(boundary).append(LINE_FEED);
        sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                .append(file.getOriginalFilename()).append("\"").append(LINE_FEED);
        sb.append("Content-Type: ").append(contentType).append(LINE_FEED).append(LINE_FEED);

        byte[] headerBytes = sb.toString().getBytes();
        byte[] fileBytes = file.getBytes();
        byte[] footerBytes = (LINE_FEED + "--" + boundary + "--" + LINE_FEED).getBytes();

        // 合并 header + 文件 + footer
        byte[] body = new byte[headerBytes.length + fileBytes.length + footerBytes.length];
        System.arraycopy(headerBytes, 0, body, 0, headerBytes.length);
        System.arraycopy(fileBytes, 0, body, headerBytes.length, fileBytes.length);
        System.arraycopy(footerBytes, 0, body, headerBytes.length + fileBytes.length, footerBytes.length);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return JSONUtil.parseObj(response.body()).get("text").toString();
        } else {
            throw new IOException("Transcription failed with status: " + response.statusCode()
                    + ", body: " + response.body());
        }
    }
}
