package com.example.attractions;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



import java.io.IOException;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    DatabaseHelper databaseHelper;
    Cursor userCursor;
    SQLiteDatabase db;
    MarkerOptions place1;
    MarkerOptions place2;
    private SearchView mapSearchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        mapSearchView = findViewById(R.id.mapSearch);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String location = mapSearchView.getQuery().toString();
                List<Address> addressList = null;

                if(location != null){

                    Geocoder geocoder = new Geocoder(MapActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    myMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        db = databaseHelper.open();//получаем данные из бд в виде курсора
        userCursor = db.rawQuery("select _id, Name,Address, History, Photo from " + DatabaseHelper.TABLE, null);
        userCursor.moveToFirst();

        String s2=userCursor.getString(2);
        String[] mass=s2.split(",");
        double l1 = Double.parseDouble(mass[0]);
        double l2=Double.parseDouble(mass[1]);
        String s3=userCursor.getString(1);
        String s4=userCursor.getString(3);
        mapFragment.getMapAsync(MapActivity.this);
        place2 = new MarkerOptions().position(new LatLng(l1, l2)).title(s3).snippet(s4);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        myMap = googleMap;

        db = databaseHelper.open();//получаем данные из бд в виде курсора
        userCursor = db.rawQuery("select _id, Name,Address, History, Photo from " + DatabaseHelper.TABLE, null);
        userCursor.moveToFirst();
//userCursor.getString(1);//
        int count =userCursor.getCount();
        for(int i=2;i<count;i++){
            userCursor = db.rawQuery("select _id, Name,Address, History from " + DatabaseHelper.TABLE + " where " +
                    DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(i)});
            userCursor.moveToFirst();

            String s2=userCursor.getString(2);
        String[] mass=s2.split(",");
        double l1 = Double.parseDouble(mass[0]);
        double l2=Double.parseDouble(mass[1]);
        String s3=userCursor.getString(1);
        String s4=userCursor.getString(3);
        place1 = new MarkerOptions().position(new LatLng(l1, l2)).title(s3).snippet(s4);
        myMap.addMarker(place1);

        }
        myMap.addMarker(place2);
        myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //int position = (int)(marker.getTag());
                String s1=marker.getTitle();
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("marker",s1);
                startActivity(intent);
                //Using position get Value from arraylist
                return false;
            }
        });
        boolean buildingsEnabled = myMap.isBuildingsEnabled();
        boolean indoorEnabled = myMap.isIndoorEnabled();

        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setCompassEnabled(true);


    }
}