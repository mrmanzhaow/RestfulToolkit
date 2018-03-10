package com.zhaow.restful.common;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class RequestHelper {

    public static String get(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        CloseableHttpResponse response = null;
        CloseableHttpClient  httpClient = HttpClients.createDefault();
        HttpGet httpMethod = new HttpGet(url);
        String result = null;
        try {
            response = httpClient.execute(httpMethod);
            HttpEntity entity = response.getEntity();

            result = toString(entity);
            // System.out.println(response.getStatusLine().getStatusCode());
        } catch (ClientProtocolException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            release(response, httpClient);
        }

        return result != null ? result : "";
    }


    public static String post(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        List<BasicNameValuePair> params = new ArrayList<>();
        // params.add(new BasicNameValuePair("parameter2", "23456"));

        String result = null;

        CloseableHttpResponse response = null;
        CloseableHttpClient  httpClient = HttpClients.createDefault();
        try {
            HttpEntity httpEntity;
            httpEntity = new UrlEncodedFormEntity(params);
            ////////////////////////////////////

            HttpPost httpMethod = new HttpPost(url);
            httpMethod.setEntity(httpEntity);
            response = httpClient.execute(httpMethod);

            HttpEntity entity = response.getEntity();
            result = toString(entity);
            // System.out.println(response.getStatusLine().getStatusCode());
        } catch (UnsupportedEncodingException e) {
            result = "URL: " + url + "\n\n" + e.toString();
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            result = "URL: " + url + "\n\n" + e.toString();
            e.printStackTrace();
        } catch (IOException e) {
            result = "URL: " + url + "\n\n" + e.toString();
            e.printStackTrace();
        } finally {
            release(response, httpClient);
        }

        return result;
    }

    @NotNull
    private static String toString(HttpEntity entity) {
        String result = null;
        try {
            result = EntityUtils.toString(entity, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result != null ? result : "";
    }


    public static String postRequestBodyWithJson(String url, String json) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        CloseableHttpResponse response = null;
        CloseableHttpClient  httpClient = HttpClients.createDefault();

        HttpPost postMethod = new HttpPost(url);

        String result = null;
        try {
            StringEntity httpEntity = new StringEntity(json);

            httpEntity.setContentType("application/json");                       //设置请求头数据传输格式
            httpEntity.setContentEncoding("UTF-8");

            postMethod.addHeader("Content-type","application/json; charset=utf-8");
            postMethod.setHeader("Accept", "application/json");
//            postMethod.setEntity(new StringEntity(parameters, Charset.forName("UTF-8")));
            postMethod.setEntity(httpEntity);                                          //设置post请求实体

            response = httpClient.execute(postMethod);

            result = toString(httpEntity);
//            System.out.println("the request body is:" + result);            //打印出请求实体
//            System.out.println(response.getStatusLine().getStatusCode());                          //打印http请求返回码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            release(response, httpClient);
        }

        return result;
    }

    public static String getResponseContentAsString(@NotNull HttpResponse response) throws IOException {
        return EntityUtils.toString(response.getEntity());
    }


    private static void release(CloseableHttpResponse response, CloseableHttpClient httpClient) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) { }
        }
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) { }
        }
    }

}
