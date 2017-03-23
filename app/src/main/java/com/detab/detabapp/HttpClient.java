package com.detab.detabapp;

import android.preference.PreferenceActivity;

import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URLEncodedUtils;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by thiago on 21/03/2017.
 */


public class HttpClient
{
    public <T> T PostObject(final String url, final T object, final Class<T> objectClass)
    {
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        try
        {

            StringEntity stringEntity = new StringEntity(new GsonBuilder().create().toJson(object));
            httpPost.setEntity(stringEntity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Accept-Encoding", "gzip");

            HttpResponse httpResponse = defaultHttpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null)
            {
                InputStream inputStream = httpEntity.getContent();
                Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip"))
                {
                    inputStream = new GZIPInputStream(inputStream);
                }

                String resultString = convertStreamToString(inputStream);
                inputStream.close();
                return new GsonBuilder().create().fromJson(resultString, objectClass);

            }

        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClientProtocolException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public <T> T PostParams(String url, final List<NameValuePair> params, final Class<T> objectClass)
    {
        String paramString = URLEncodedUtils.format(params, "utf-8");
        url += "?" + paramString;
        return PostObject(url, null, objectClass);
    }

    private String convertStreamToString(InputStream inputStream)
    {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try
        {
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally
        {
            try
            {
                inputStream.close();
            } catch (IOException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return stringBuilder.toString();
    }

    public <T> T Get(String url, final Class<T> objectClass)
    {
        return Get(url, new ArrayList<NameValuePair>(), objectClass);
    }

    public <T> T Get(String url, List<NameValuePair> params, final Class<T> objectClass)
    {
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        String paramString = URLEncodedUtils.format(params, "utf-8");
        url += "?" + paramString;
        HttpGet httpGet = new HttpGet(url);
        try
        {

            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Accept-Encoding", "gzip");

            HttpResponse httpResponse = defaultHttpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null)
            {
                InputStream inputStream = httpEntity.getContent();
                Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip"))
                {
                    inputStream = new GZIPInputStream(inputStream);
                }

                String resultString = convertStreamToString(inputStream);
                inputStream.close();
                return new GsonBuilder().create().fromJson(resultString, objectClass);
            }

        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClientProtocolException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public boolean Delete(String url, final List<NameValuePair> params)
    {
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        String paramString = URLEncodedUtils.format(params, "utf-8");
        url += "?" + paramString;
        HttpDelete httpDelete = new HttpDelete(url);

        HttpResponse httpResponse = null;
        try
        {
            httpResponse = defaultHttpClient.execute(httpDelete);
            return httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT;
        } catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return false;
    }
}
