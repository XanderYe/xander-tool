package cn.xanderye.util;

import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * http请求工具
 *
 * @author XanderYe
 * @date 2020/2/4
 */
public class HttpUtil {

    private static String baseUrl = "";
    /**
     * 是否使用代理
     */
    private static boolean enableProxy = false;
    private static String defaultProxyIp = "127.0.0.1";
    private static int defaultProxyPort = 8888;

    /**
     * 是否自动重定向
     */
    private static boolean redirect = true;

    /**
     * socket连接超时
     */
    private static int defaultSocketTimeout = 15000;

    /**
     * 请求超时
     */
    private static int defaultConnectTimeout = 30000;

    /**
     * 是否重试
     */
    private static boolean enableRetry = false;
    /**
     * 重试次数
     */
    private static int defaultRetryCount = 3;

    /**
     * 默认编码
     */
    private static final String CHARSET = "UTF-8";

    /**
     * 默认请求头
     */
    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36";

    /**
     * HttpClient对象
     */
    private static CloseableHttpClient httpClient;

    // 静态代码块初始化配置
    static {
        initHttpClient();
    }

    /**
     * 初始化配置
     * @param
     * @return void
     * @author XanderYe
     * @date 2021/5/24
     */
    private static void initHttpClient() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(defaultConnectTimeout)
                .setSocketTimeout(defaultSocketTimeout)
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .setRedirectsEnabled(redirect)
                .build();
        httpClient = custom().setDefaultRequestConfig(config).build();
    }

    /**
     * 创建httpClientBuilder
     *
     * @return org.apache.http.impl.client.HttpClientBuilder
     * @author XanderYe
     * @date 2020/2/14
     */
    private static HttpClientBuilder custom() {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        // 忽略证书
        httpClientBuilder.setSSLSocketFactory(ignoreCertificates());
        if (enableProxy) {
            // 使用代理
            httpClientBuilder.setProxy(new HttpHost(defaultProxyIp, defaultProxyPort));
        }
        if (enableRetry) {
            // 使用重试机制
            httpClientBuilder.setRetryHandler(retryHandler());
        }
        return httpClientBuilder;
    }

    /**
     * GET请求
     *
     * @param url
     * @param params
     * @return cn.xanderye.util.HttpUtil.ResEntity
     * @author XanderYe
     * @date 2020-03-15
     */
    public static ResEntity doGet(String url, Map<String, Object> params) throws IOException {
        return doGet(url, null, null, params);
    }


    /**
     * POST请求
     *
     * @param url
     * @param params
     * @return cn.xanderye.util.HttpUtil.ResEntity
     * @author XanderYe
     * @date 2020-03-15
     */
    public static ResEntity doPost(String url, Map<String, Object> params) throws IOException {
        return doPost(url, null, null, params);
    }

    /**
     * POST提交JSON请求
     * @param url
     * @param jsonString
     * @return cn.xanderye.util.HttpUtil.ResEntity
     * @author XanderYe
     * @date 2020/10/22
     */
    public static ResEntity doPostJSON(String url, String jsonString) throws IOException {
        return doPostJSON(url, null, null, jsonString);
    }

    /**
     * get请求基础方法
     *
     * @param url
     * @param headers
     * @param params
     * @return cn.xanderye.util.HttpUtil.ResEntity
     * @author XanderYe
     * @date 2020/2/4
     */
    public static ResEntity doGet(String url, Map<String, Object> headers, Map<String, Object> cookies, Map<String, Object> params) throws IOException {
        url = baseUrl + url;
        // 拼接参数
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> pairs = new ArrayList<>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String value = entry.getValue() == null ? null : (entry.getValue()).toString();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
            try {
                String parameters = EntityUtils.toString(new UrlEncodedFormEntity(pairs, CHARSET));
                String symbol = url.contains("?") ? "&" : "?";
                // 判断是否已带参数
                url += symbol + parameters;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        HttpGet httpGet = new HttpGet(url);
        // 添加headers
        addHeaders(httpGet, headers);
        // 添加cookies
        addCookies(httpGet, cookies);
        CloseableHttpResponse response = null;
        HttpEntity resultEntity = null;
        try {
            HttpClientContext httpClientContext = new HttpClientContext();
            response = httpClient.execute(httpGet, httpClientContext);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                resultEntity = response.getEntity();
                if (resultEntity != null) {
                    String res = EntityUtils.toString(resultEntity, CHARSET);
                    String cookieString = getCookieString(response);
                    ResEntity resEntity = new ResEntity();
                    resEntity.setResponse(res);
                    resEntity.setHeaders(getHeaders(response));
                    resEntity.setCookies(formatCookies(cookieString));
                    return resEntity;
                }
            } else {
                throw new IOException(MessageFormat.format("Request error with error code {0}.", statusCode));
            }
        } finally {
            try {
                if (resultEntity != null) {
                    EntityUtils.consume(resultEntity);
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResEntity();
    }

    /**
     * post请求基础方法
     *
     * @param url
     * @param headers
     * @param params
     * @return cn.xanderye.util.HttpUtil.ResEntity
     * @author XanderYe
     * @date 2020/2/4
     */
    public static ResEntity doPost(String url, Map<String, Object> headers, Map<String, Object> cookies, Map<String, Object> params) throws IOException {
        HttpPost httpPost = new HttpPost(baseUrl + url);
        // 拼接参数
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> pairs = new ArrayList<>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String value = entry.getValue() == null ? null : (entry.getValue()).toString();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 添加headers
        addHeaders(httpPost, headers);
        // 添加cookies
        addCookies(httpPost, cookies);
        CloseableHttpResponse response = null;
        HttpEntity resultEntity = null;
        try {
            HttpClientContext httpClientContext = new HttpClientContext();
            response = httpClient.execute(httpPost, httpClientContext);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                resultEntity = response.getEntity();
                if (resultEntity != null) {
                    String res = EntityUtils.toString(resultEntity, CHARSET);
                    String cookieString = getCookieString(response);
                    ResEntity resEntity = new ResEntity();
                    resEntity.setResponse(res);
                    resEntity.setHeaders(getHeaders(response));
                    resEntity.setCookies(formatCookies(cookieString));
                    return resEntity;
                }
            } else {
                throw new IOException(MessageFormat.format("Request error with error code {0}.", statusCode));
            }
        } finally {
            try {
                if (resultEntity != null) {
                    EntityUtils.consume(resultEntity);
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResEntity();
    }

    /**
     * POST提交JSON基础方法
     *
     * @param url
     * @param headers
     * @param json
     * @return cn.xanderye.util.HttpUtil.ResEntity
     * @author XanderYe
     * @date 2020/2/4
     */
    public static ResEntity doPostJSON(String url, Map<String, Object> headers, Map<String, Object> cookies, String json) throws IOException {
        HttpPost httpPost = new HttpPost(baseUrl + url);
        // 拼接参数
        if (json != null && !"".equals(json)) {
            StringEntity requestEntity = new StringEntity(json, CHARSET);
            requestEntity.setContentEncoding(CHARSET);
            requestEntity.setContentType("application/json");
            httpPost.setEntity(requestEntity);
        }
        // 添加headers
        addHeaders(httpPost, headers);
        // 添加cookies
        addCookies(httpPost, cookies);
        CloseableHttpResponse response = null;
        HttpEntity resultEntity = null;
        try {
            HttpClientContext httpClientContext = new HttpClientContext();
            response = httpClient.execute(httpPost, httpClientContext);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                resultEntity = response.getEntity();
                if (resultEntity != null) {
                    String res = EntityUtils.toString(resultEntity, CHARSET);
                    String cookieString = getCookieString(response);
                    ResEntity resEntity = new ResEntity();
                    resEntity.setResponse(res);
                    resEntity.setHeaders(getHeaders(response));
                    resEntity.setCookies(formatCookies(cookieString));
                    return resEntity;
                }
            } else {
                throw new IOException(MessageFormat.format("Request error with error code {0}.", statusCode));
            }
        } finally {
            try {
                if (resultEntity != null) {
                    EntityUtils.consume(resultEntity);
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResEntity();
    }

    /**
     * get下载基础方法
     *
     * @param url
     * @param headers
     * @param cookies
     * @param params
     * @return cn.xanderye.util.HttpUtil.ResEntity
     * @author XanderYe
     * @date 2020/2/4
     */
    public static ResEntity doDownload(String url, Map<String, Object> headers, Map<String, Object> cookies, Map<String, Object> params) throws IOException {
        url = baseUrl + url;
        // 拼接参数
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> pairs = new ArrayList<>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String value = entry.getValue() == null ? null : (entry.getValue()).toString();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
            try {
                String parameters = EntityUtils.toString(new UrlEncodedFormEntity(pairs, CHARSET));
                String symbol = url.contains("?") ? "&" : "?";
                // 判断是否已带参数
                url += symbol + parameters;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        HttpGet httpGet = new HttpGet(url);
        // 添加headers
        addHeaders(httpGet, headers);
        // 添加cookies
        addCookies(httpGet, cookies);
        CloseableHttpResponse response = null;
        HttpEntity resultEntity = null;
        try {
            HttpClientContext httpClientContext = new HttpClientContext();
            response = httpClient.execute(httpGet, httpClientContext);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                resultEntity = response.getEntity();
                if (resultEntity != null) {
                    byte[] bytes = EntityUtils.toByteArray(resultEntity);
                    String cookieString = getCookieString(response);
                    ResEntity resEntity = new ResEntity();
                    resEntity.setBytes(bytes);
                    resEntity.setHeaders(getHeaders(response));
                    resEntity.setCookies(formatCookies(cookieString));
                    return resEntity;
                }
            } else {
                throw new IOException(MessageFormat.format("Request error with error code {0}.", statusCode));
            }
        } finally {
            try {
                if (resultEntity != null) {
                    EntityUtils.consume(resultEntity);
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResEntity();
    }

    /**
     * post上传基础方法，注意Content-Type
     *
     * @param url
     * @param headers
     * @param cookies
     * @param bytes
     * @return cn.xanderye.util.HttpUtil.ResEntity
     * @author XanderYe
     * @date 2020/2/4
     */
    public static ResEntity doUpload(String url, Map<String, Object> headers, Map<String, Object> cookies, byte[] bytes) throws IOException {
        HttpPost httpPost = new HttpPost(baseUrl + url);
        // 拼接参数
        if (bytes != null && bytes.length > 0) {
            ByteArrayEntity requestEntity = new ByteArrayEntity(bytes);
            httpPost.setEntity(requestEntity);
        }
        // 添加headers
        addHeaders(httpPost, headers);
        // 添加cookies
        addCookies(httpPost, cookies);
        CloseableHttpResponse response = null;
        HttpEntity resultEntity = null;
        try {
            HttpClientContext httpClientContext = new HttpClientContext();
            response = httpClient.execute(httpPost, httpClientContext);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                resultEntity = response.getEntity();
                if (resultEntity != null) {
                    String res = EntityUtils.toString(resultEntity, CHARSET);
                    String cookieString = getCookieString(response);
                    ResEntity resEntity = new ResEntity();
                    resEntity.setResponse(res);
                    resEntity.setHeaders(getHeaders(response));
                    resEntity.setCookies(formatCookies(cookieString));
                    return resEntity;
                }
            } else {
                throw new IOException(MessageFormat.format("Request error with error code {0}.", statusCode));
            }
        } finally {
            try {
                if (resultEntity != null) {
                    EntityUtils.consume(resultEntity);
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResEntity();
    }

    /**
     * 添加cookie
     *
     * @param httpRequestBase
     * @return void
     * @author XanderYe
     * @date 2020-03-15
     */
    private static void addHeaders(HttpRequestBase httpRequestBase, Map<String, Object> headers) {
        // 设置默认UA
        httpRequestBase.setHeader("User-Agent", DEFAULT_USER_AGENT);
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue() == null ? "" : String.valueOf(entry.getValue());
                httpRequestBase.setHeader(key, value);
            }
        }
    }

    /**
     * 添加cookie
     *
     * @param cookies
     * @return void
     * @author XanderYe
     * @date 2020-03-15
     */
    private static void addCookies(HttpRequestBase httpRequestBase, Map<String, Object> cookies) {
        if (cookies != null && !cookies.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<String, Object> entry : cookies.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue() == null ? "" : String.valueOf(entry.getValue());
                stringBuilder.append(key).append("=").append(value).append("; ");
            }
            httpRequestBase.addHeader("Cookie", stringBuilder.toString());
        }
    }

    /**
     * 从请求头中获取cookie字符串
     * @param response
     * @return java.lang.String
     * @author XanderYe
     * @date 2021/1/26
     */
    private static String getCookieString(CloseableHttpResponse response) {
        Header[] headers = response.getHeaders("Set-Cookie");
        return Arrays.stream(headers).map(Header::getValue).collect(Collectors.joining("; "));
    }

    /**
     * 获取请求头
     * @param response
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author XanderYe
     * @date 2021/6/21
     */
    private static Map<String, Object> getHeaders(CloseableHttpResponse response) {
        Header[] headers = response.getAllHeaders();
        Map<String, Object> headersMap = new HashMap<>();
        for (Header header : headers) {
            headersMap.put(header.getName(), header.getValue());
        }
        return headersMap;
    }

    /**
     * 格式化请求头
     *
     * @param headerString
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author XanderYe
     * @date 2020/4/1
     */
    public static Map<String, Object> formatHeaders(String headerString) {
        if (headerString != null && !"".equals(headerString)) {
            String[] headers = headerString.split(";");
            if (headers.length > 0) {
                Map<String, Object> headerMap = new HashMap<>(16);
                for (String header : headers) {
                    int index = header.indexOf(":");
                    if (index > 0) {
                        String k = header.substring(0, index).trim();
                        String v = header.substring(index + 1).trim();
                        headerMap.put(k, v);
                    }
                }
                return headerMap;
            }
        }
        return null;
    }

    /**
     * 格式化cookie
     *
     * @param cookieString
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author XanderYe
     * @date 2020/4/1
     */
    public static Map<String, Object> formatCookies(String cookieString) {
        Map<String, Object> cookieMap = new HashMap<>(16);
        if (cookieString != null && !"".equals(cookieString)) {
            String[] cookies = cookieString.split(";");
            if (cookies.length > 0) {
                for (String parameter : cookies) {
                    int eqIndex = parameter.indexOf("=");
                    if (eqIndex > -1) {
                        String k = parameter.substring(0, eqIndex).trim();
                        String v = parameter.substring(eqIndex + 1).trim();
                        if (!"".equals(v)) {
                            cookieMap.put(k, v);
                        }
                    }
                }
            }
        }
        return cookieMap;
    }

    /**
     * 格式化请求体
     *
     * @param parameterString
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author XanderYe
     * @date 2020/4/1
     */
    public static Map<String, Object> formatParameters(String parameterString) {
        if (parameterString != null) {
            String[] parameters = parameterString.split("&");
            if (parameters.length > 0) {
                Map<String, Object> paramMap = new HashMap<>(16);
                for (String parameter : parameters) {
                    String[] value = parameter.split("=");
                    String k = value[0].trim();
                    String v = null;
                    if (value.length == 2) {
                        v = value[1].trim();
                    }
                    paramMap.put(k, v);
                }
                return paramMap;
            }
        }
        return null;
    }

    /**
     * 忽略证数配置
     *
     * @param
     * @return org.apache.http.conn.ssl.SSLConnectionSocketFactory
     * @author XanderYe
     * @date 2020/2/14
     */
    private static SSLConnectionSocketFactory ignoreCertificates() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build();
            return new SSLConnectionSocketFactory(sslContext);
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 重试配置
     * @return
     */
    private static HttpRequestRetryHandler retryHandler() {
        return (e, retryTimes, httpContext) -> {
            if (retryTimes > defaultRetryCount) {
                // 重试次数大于3次
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(httpContext);
            HttpRequest request = clientContext.getRequest();
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
            if (idempotent) {
                // 如果请求被认为是幂等的，则重试
                return true;
            }
            if (e instanceof NoHttpResponseException) {
                // NoHttpResponseException异常重试
                return true;
            }
            if (e instanceof ConnectTimeoutException) {
                // 连接超时重试
                return true;
            }
            if (e instanceof SocketTimeoutException) {
                // 响应超时
                return false;
            }
            if (e instanceof InterruptedIOException) {
                // 超时
                return false;
            }
            if (e instanceof UnknownHostException) {
                // 未知主机
                return false;
            }
            if (e instanceof SSLException) {
                // SSL异常
                return false;
            }
            return false;
        };
    }

    /**
     * 设置重定向
     * @param redirect
     * @return void
     * @author XanderYe
     * @date 2021/5/24
     */
    public static void setRedirect(boolean redirect) {
        HttpUtil.redirect = redirect;
        initHttpClient();
    }

    /**
     * 配置代理
     * @param useProxy
     * @param proxyIp
     * @param proxyPort
     * @return void
     * @author XanderYe
     * @date 2021/5/24
     */
    public static void setProxy(boolean useProxy, String proxyIp, Integer proxyPort) {
        HttpUtil.enableProxy = useProxy;
        if (null != proxyIp && !"".equals(proxyIp)) {
            HttpUtil.defaultProxyIp = proxyIp;
        }
        if (null != proxyPort) {
            HttpUtil.defaultProxyPort = proxyPort;
        }
        initHttpClient();
    }

    /**
     * 设置超时
     * @param socketTimeout
     * @param connectTimeout
     * @return void
     * @author XanderYe
     * @date 2021/5/24
     */
    public static void setTimeout(int socketTimeout, int connectTimeout) {
        defaultSocketTimeout = socketTimeout;
        defaultConnectTimeout = connectTimeout;
        initHttpClient();
    }

    /**
     * 设置重试机制
     * @param retryCount
     * @return void
     * @author XanderYe
     * @date 2021/6/24
     */
    public static void setRetry(int retryCount) {
        if (retryCount > 0) {
            defaultRetryCount = retryCount;
        }
        enableRetry = true;
        initHttpClient();
    }

    /**
     * 直接设置自定义httpClient
     * @param customHttpClient
     * @return void
     * @author XanderYe
     * @date 2021/5/24
     */
    public static void setHttpClient(CloseableHttpClient customHttpClient) {
        httpClient = customHttpClient;
    }

    public static void setBaseUrl(String base) {
        baseUrl = base;
        if ('/' != base.charAt(base.length() - 1)) {
            baseUrl += "/";
        }
    }

    public static class ResEntity {

        private byte[] bytes;

        private String response;

        private Map<String, Object> headers;

        private Map<String, Object> cookies;

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }

        public Map<String, Object> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, Object> headers) {
            this.headers = headers;
        }

        public Map<String, Object> getCookies() {
            return cookies;
        }

        public void setCookies(Map<String, Object> cookies) {
            this.cookies = cookies;
        }

        @Override
        public String toString() {
            return "ResEntity{" +
                    "bytes=" + Arrays.toString(bytes) +
                    ", response='" + response + '\'' +
                    ", headers=" + headers +
                    ", cookies=" + cookies +
                    '}';
        }
    }
}
