package com.example.nearbyplacefinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private HashMap<String, String>  getNearbyPlace(JSONObject googleplacesJsonData){

        HashMap<String, String> googlemapPlaces = new HashMap<>();

        String nameOfPlace = "-NA-";
        String vicinity = "=NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";



            try {
                if(!googleplacesJsonData.isNull("name"))
                {
                    nameOfPlace = googleplacesJsonData.getString("name");
                }
                if(!googleplacesJsonData.isNull("vicinity"))
                {
                    vicinity = googleplacesJsonData.getString("vicinity");
                }
                latitude = googleplacesJsonData.getJSONObject("geometry").getJSONObject("location").getString("lat");
                latitude = googleplacesJsonData.getJSONObject("geometry").getJSONObject("location").getString("lng");
                reference = googleplacesJsonData.getString("reference");

                googlemapPlaces.put("placename", nameOfPlace);
                googlemapPlaces.put("vicinity", vicinity);
                googlemapPlaces.put("latitude", latitude);
                googlemapPlaces.put("longitude", longitude);
                googlemapPlaces.put("reference", reference);



            } catch (JSONException e) {
                e.printStackTrace();
            }
        return googlemapPlaces;
    }


    private List<HashMap<String, String>> getNearbyPlaces(JSONArray jsonArray)
    {
        List<HashMap<String, String>>  jsonobjectlist = new ArrayList<>();
        HashMap<String, String>  singlejsonobject;
        int count = jsonArray.length();

        for(int i=0;i<count;i++)
        {
            try {
                singlejsonobject = getNearbyPlace((JSONObject) jsonArray.get(i));
                jsonobjectlist.add(singlejsonobject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonobjectlist;
    }

    public List<HashMap<String, String>> parse(String jsonData)
    {
        JSONObject jsonObject;
        JSONArray jsonArray = null;
        try {
             jsonObject = new JSONObject(jsonData);
             jsonArray = jsonObject.getJSONArray("result");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getNearbyPlaces(jsonArray);
    }
}
