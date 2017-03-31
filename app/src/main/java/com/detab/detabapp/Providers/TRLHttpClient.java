package com.detab.detabapp.Providers;

//import org.apache.http.Header;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.HttpClientBuilder;

//import org.apache.http.client.methods.*;
//import org.apache.http.impl.client.*;

import com.detab.detabapp.Models.ReturnedObject;

import org.json.*;

import java.io.IOException;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by thiago on 21/03/2017.
 */


public class TRLHttpClient
{
    public ReturnedObject TGet(String url)
    {
        HttpClient client = new DefaultHttpClient();
        HttpGet getMethod = new HttpGet(url);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = null;
        try
        {
            responseBody = client.execute(getMethod, responseHandler);
            JSONObject obj = new JSONObject(responseBody);
            ReturnedObject result = new ReturnedObject();

            result.body = obj.get("body").toString();
            result.title = obj.get("title").toString();
            return result;
//            String pais = obj.get("country_name").toString();
//            String estado = obj.get("region_name").toString();
//            String cidade = obj.get("city").toString();
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
//    public void TGet(String url)
//    {
//        HttpClient client = HttpClientBuilder.create().build();
//        HttpGet request = new HttpGet(url);
//        HttpResponse response = client.execute(request);
//    }

   /* public <T> T PostObject(final String url, final T object, final Class<T> objectClass)
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


