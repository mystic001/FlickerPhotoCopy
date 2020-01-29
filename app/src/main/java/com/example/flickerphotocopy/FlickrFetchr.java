package com.example.flickerphotocopy;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class FlickrFetchr {
    public static final String API_KEY = "217ee655cb5a6602f6e442b62c51eadc";
    public static final String TAG = "FlickrFetchr";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final String FETCH_PHOTOS = "flickr.photos.getRecent";
    public static final Uri ENDPOINT = Uri.parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();

    public String buildUrl (String method , String query){
        Uri.Builder uriBuilder = ENDPOINT.buildUpon().appendQueryParameter("method",method);
        if (method == SEARCH_METHOD){
            uriBuilder.appendQueryParameter("text",query);
        }
        return uriBuilder.build().toString();
    }


    public List<GalleryItem> fetchRecentPhotos() {
        String url = buildUrl(FETCH_PHOTOS, null);
        return fetchPhotoUrl(url);
     }

     public List<GalleryItem> fetchSearchPhotos(String query){
        String url = buildUrl(SEARCH_METHOD, query);
        return fetchPhotoUrl(url);

     }

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }

            int bytesRead = 0 ;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchPhotoUrl(String url) {
         List<GalleryItem> Items = new ArrayList<>();

        try {
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(Items, jsonBody);
            Log.i(TAG, "RECEIVED JSON :" + jsonString);


        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch photos url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Items;
    }



    public void parseItems(List<GalleryItem> galleryItems, JSONObject jsonObject) throws JSONException{

        JSONObject photoObject = jsonObject.getJSONObject("photos");
        JSONArray jsonArray = photoObject.getJSONArray("photo");


        for(int i = 0 ; i<jsonArray.length(); i++){
           photoObject = jsonArray.getJSONObject(i);

           GalleryItem item = new GalleryItem();
           item.setId(photoObject.getString("id"));

           item.setCaption(photoObject.getString("title"));

            if (!photoObject.has("url_s")) {
                continue;
            }
           item.setUrl(photoObject.getString("url_s"));
           item.setOwner(photoObject.getString("owner"));
           Log.i("The setOwner is",item.getOwner());
           Log.i("The id is",item.getId());
           galleryItems.add(item);
        }
    }
}


