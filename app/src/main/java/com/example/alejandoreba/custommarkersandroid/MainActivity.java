package com.example.alejandoreba.custommarkersandroid;

import android.app.Activity;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;


public class MainActivity extends Activity {


    MapView mv;
    ItemizedIconOverlay<OverlayItem> items;
    ItemizedIconOverlay.OnItemGestureListener<OverlayItem> markerGestureListener;
    Map<String,Drawable> markersType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This line sets the user agent, a requirement to download OSM maps
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        //set map, zoom and center
        setContentView(R.layout.activity_main);
        mv = (MapView) findViewById(R.id.map1);

        mv.getController().setZoom(14);
        mv.setBuiltInZoomControls(true);
        mv.getController().setCenter(new GeoPoint(50.9319, -1.4011));


        //listener for single and long press tap up
        markerGestureListener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>()
        {
            public boolean onItemLongPress(int i, OverlayItem item)
            {
                Toast.makeText(MainActivity.this, item.getSnippet(), Toast.LENGTH_SHORT).show();
                return true;
            }

            public boolean onItemSingleTapUp(int i, OverlayItem item)
            {
                Toast.makeText(MainActivity.this, item.getSnippet(), Toast.LENGTH_LONG).show();
                return true;
            }
        };


        //initialize type of markers
        this.markersType = new HashMap<>();
        markersType.put("restaurant", getResources().getDrawable(R.drawable.restaurant));
        markersType.put("pub", getResources().getDrawable(R.drawable.pub));
        markersType.put("city", getResources().getDrawable(R.drawable.city));


        /*
        items = new ItemizedIconOverlay<OverlayItem>(this, new ArrayList<OverlayItem>(), markerGestureListener);
        OverlayItem fernhurst = new OverlayItem ("Fernhurst", "Village in West Sussex", new GeoPoint(51.05, -0.72));
        fernhurst.setMarker(getResources().getDrawable(R.drawable.marker));
        OverlayItem blackdown = new OverlayItem("Blackdown", "highest point in West Sussex", new GeoPoint(51.05, -0.6897));
        items.addItem(fernhurst);
        items.addItem(blackdown);

        */

        items = new ItemizedIconOverlay<OverlayItem>(this, new ArrayList<OverlayItem>(), markerGestureListener);
        try
        {
            FileReader fr = new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/poi.txt");
            BufferedReader reader = new BufferedReader(fr);
            String line = "";
            while((line = reader.readLine()) != null)
            {
                String[] components = line.split(",");
                if (components.length > 4){
                    String name = components[0];
                    String type = components[1];
                    String description = components[2];
                    double lon = Double.parseDouble(components[3]);
                    double lat = Double.parseDouble(components[4]);
                    OverlayItem newItem = new OverlayItem(name,type,description, new GeoPoint(lat,lon));
                    // method to set marker..
                    setMarker(newItem,type);
                    //newItem.setMarker(getResources().getDrawable(R.drawable.marker));
                    items.addItem(newItem);
                }
            }
            reader.close();
        }
        catch(IOException e)
        {
            new AlertDialog.Builder(this).setMessage("ERROR: " + e).
                    setPositiveButton("OK", null).show();

        }
        mv.getOverlays().add(items);


    }

    private void setMarker(OverlayItem item, String type){
        if (this.markersType.containsKey(type)){
            item.setMarker(this.markersType.get(type));
        }
    }

}
