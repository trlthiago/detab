package com.detab.detabapp.Providers;

import com.detab.detabapp.Models.BulkPotholeModel;
import com.detab.detabapp.Models.TRLPothole;
import com.google.gson.Gson;

import org.json.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by thiago on 21/03/2017.
 */

public class TRLHttpClient
{
    public List<TRLPothole> Get(String url)
    {
        HttpClient client = new DefaultHttpClient();
        HttpGet getMethod = new HttpGet(url);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = null;
        try
        {
            responseBody = client.execute(getMethod, responseHandler);

            JSONArray obj = new JSONArray(responseBody);

            List<TRLPothole> results = new ArrayList<TRLPothole>();
            int myJsonArraySize = obj.length();
            for (int i = 0; i < myJsonArraySize; i++)
            {
                JSONObject item = (JSONObject) obj.get(i);
                TRLPothole o = new TRLPothole(Double.parseDouble(item.get("Lat").toString()), Double.parseDouble(item.get("Lng").toString()), Double.parseDouble(item.get("Deep").toString()));
                results.add(o);
            }
            return results;
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        } catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void Post(String url, BulkPotholeModel potholes)
    {
        try
        {
            JSONArray array = new JSONArray();
            for (TRLPothole pothole : potholes.potholes)
            {
                JSONObject item = new JSONObject();
                item.put("Lat", pothole.Lat);
                item.put("Lng", pothole.Lng);
                item.put("Deep", pothole.Deep);
                array.put(item);
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("potholes", array);

            String json = jsonObject.toString();

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(json));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            new DefaultHttpClient().execute(httpPost);
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        } catch (ClientProtocolException e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
//    public void Get(String url)
//    {
//        HttpClient client = HttpClientBuilder.create().build();
//        HttpGet request = new HttpGet(url);
//        HttpResponse response = client.execute(request);
//    }
/*
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
    }*/


    /*public <T> T Get(String url, final Class<T> objectClass)
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
    }*/
}


