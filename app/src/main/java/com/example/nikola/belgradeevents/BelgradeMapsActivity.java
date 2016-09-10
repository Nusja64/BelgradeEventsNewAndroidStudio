package com.example.nikola.belgradeevents;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


import com.example.nikola.belgradeevents.adapter.DataAdapter;
import com.example.nikola.belgradeevents.model.Event;
import com.example.nikola.belgradeevents.model.Image;
import com.example.nikola.belgradeevents.model.Location;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.zip.Inflater;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.PlaybackStateCompat.CustomAction;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class BelgradeMapsActivity extends AppCompatActivity implements DataList,OnMapReadyCallback, GoogleMap.OnMarkerClickListener,GoogleMap.InfoWindowAdapter {

    private GoogleMap mMap;
    LocationManager locationManager;
    DataAdapter adapter;
    RecyclerView card_recycler_view;
    ImageView btn_cancel;

    private final static LatLng LOCATION_BELGRADE = new LatLng(44.8180937,20.4431559);
    public final static String tag = "error";

    private NetworkClass nc = new NetworkClass(this);
    private BrokerSQLite db = new BrokerSQLite(this);
  //  private HashMap<MarkerOptions, Event> eventMarkerMap;

    //public static int EventIDClicked = 0;

    ArrayList<Image> images = new ArrayList<>();
    LinearLayout myGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(this);

        initToolbar();


        card_recycler_view= (RecyclerView) findViewById(R.id.card_recycler_view);
        btn_cancel = (ImageView) findViewById(R.id.btn_cancel);
        listener();
        if (isOnline())
        {

            new AsyncGetData().execute();
        }
        else
        {
            Toast.makeText(BelgradeMapsActivity.this, "NO NETWORK. Can not show markers".toUpperCase(), Toast.LENGTH_LONG)
                    .show();
            setInfoOffline();
        }


    }

    private void listener() {
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card_recycler_view.setVisibility(View.INVISIBLE);
                images.clear();
                btn_cancel.setVisibility(View.INVISIBLE);
            }
        });
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);
        toolbar.setBackgroundResource(R.drawable.my_borders);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("Events");
        title.setTextSize(24);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:

                setInfoExit();

                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setInfoExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //BelgradeMapsActivity.this.finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void setInfoOffline() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Working offline?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.open();
                        try {
                            ArrayList<Event> events = db.select_event();
                            if (events.size()>0)
                                setMarkers(events);
                            else{
                                Toast.makeText(BelgradeMapsActivity.this,"No data in db for markers!Please sett network".toUpperCase(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_BELGRADE, 11.25f);
        mMap.animateCamera(update);


        if (ContextCompat.checkSelfPermission(BelgradeMapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
}
            else
            {

            }

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {


            locationManager = (LocationManager) BelgradeMapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
            if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BelgradeMapsActivity.this);
                builder.setTitle("GPS is not found");  // GPS not found
                builder.setMessage("Turn on GPS?"); // Want to enable?
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BelgradeMapsActivity.this.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
                builder.setNegativeButton("NO", null);
                builder.create().show();
                return false;

            }
                else
            {
                android.location.Location location = mMap.getMyLocation();
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,16.0f));
                return true;
            }


            }
        });



       // mMap.setInfoWindowAdapter(new);

    }

    private void setMarkers(ArrayList<Event> events) {

        Event event;

        for (int i = 0; i < events.size(); i++) {

            event = new Event();
            event = events.get(i);
            MarkerOptions customMarker = new MarkerOptions();

          //  eventMarkerMap = new HashMap<MarkerOptions, Event>();

            // TODO Auto-generated method stub
            ///check and set tags
            String tags="";

            if (event.getTags().size() >= 1) {
                tags = event.getTags().get(0);
            }

            for (String element : event.getTags()) {

                if (!element.equalsIgnoreCase(tags))
                    tags += ", " +element;

            }

            //cneck date
            String endDate = "";
            if (event.getEnding_at().isEmpty() || event.getEnding_at().equalsIgnoreCase("null") || event.getEnding_at().equalsIgnoreCase("TIMESTAMP"))
                endDate="/";
            else endDate = event.getEnding_at().substring(0, 10);

            String startDate = "";
            if (event.getStarting_at().isEmpty() || event.getStarting_at().equalsIgnoreCase("null") || event.getStarting_at().equalsIgnoreCase("TIMESTAMP"))
                startDate="/";
            else startDate = event.getStarting_at().substring(0, 10);


            //set color of marker

            float hue = 0f;

            if (event.Is_trending()==1)
            {
                hue =  (float) BitmapDescriptorFactory.HUE_YELLOW;
            }
            else
            {
                hue =  (float) BitmapDescriptorFactory.HUE_GREEN;
            }



            customMarker = new MarkerOptions().position(new LatLng(event.getLocation().getLat(),event.getLocation().getLng())).
                    snippet("Start date : "+startDate+"\n"+"End date : "+endDate+"\n"+"Tag : "+tags).
                    icon(BitmapDescriptorFactory.defaultMarker(hue)).
                    title(event.getName());


           mMap.addMarker(customMarker);

//            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//
//                @Override
//                public View getInfoWindow(Marker arg0) {
//                    return null;
//                }
//
//                @Override
//                public View getInfoContents(Marker marker) {
//
//                    Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.
//
//                    LinearLayout info = new LinearLayout(context);
//                    info.setOrientation(LinearLayout.VERTICAL);
//
//                    TextView title = new TextView(context);
//                    title.setTextColor(Color.BLACK);
//                    title.setGravity(Gravity.CENTER);
//                    title.setTypeface(null, Typeface.BOLD);
//                    title.setText(marker.getTitle());
//
//                    TextView snippet = new TextView(context);
//                    snippet.setTextColor(Color.GRAY);
//                    snippet.setText(marker.getSnippet());
//
//                    info.addView(title);
//                    info.addView(snippet);
//
//                    return info;
//                }
//            });
//            mMap.addMarker(customMarker).showInfoWindow();
        }
    }


    private void moveToCurrentLocation(LatLng currentLocation)
    {

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLocation, 16.25f);
        mMap.animateCamera(update, 4000, null);

    }

    @Override
    public void sendDataToActivity(Image image) {
        Intent zoom = new Intent(BelgradeMapsActivity.this, ZoomImageActivity.class);
                                        zoom.putExtra("comment",image.getComment());
                                        zoom.putExtra("image",image.getImageUrl().toString());
                                        startActivity(zoom);
    }

//    private void setMarkerListener(MarkerOptions marker) {
//        // TODO Auto-generated method stuerb
//
//        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//
//                //Toast.makeText(BelgradeMapsActivity.this, "Testirnje klika", Toast.LENGTH_SHORT).show();
//                if (isOnline())
//                {
//
//                    // Event eventInfo = eventMarkerMap.get(marker);
//                    AsyncGetPicture getPicture = new AsyncGetPicture();
//                    getPicture.lat = marker.getPosition().latitude;
//                    getPicture.lng = marker.getPosition().longitude;
//                    //	getPicture.eventID = EventIDClicked;
//                    getPicture.execute();
//                    //	map.addMarker(marker.setIcon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_CYAN)));
//                }
//                else
//                {
//                    Toast.makeText(BelgradeMapsActivity.this, "NO NETWORK. Can not show markers".toUpperCase(), Toast.LENGTH_LONG)
//                            .show();
//                }
//
//            }
//        });
//
//    }

    @SuppressLint("NewApi") private class AsyncGetPicture extends AsyncTask<Void, Void, Void> {

        JSONArray resJsonArrayPicture;
        URL newurl;
        Bitmap bitmap;

        double lng;
        double lat;
        int eventID;
        boolean IsOK = false;
        Dialog statusDialogPicture = new Dialog(BelgradeMapsActivity.this);

        @Override
        protected void onPreExecute()
        {

            BelgradeMapsActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run()
                {
                    statusDialogPicture.setTitle(getString(R.string.fetchingDatafromServerImg));
                    statusDialogPicture.setContentView(R.layout.circular_progress_bar);
                    statusDialogPicture.show();
                }
            });

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {

                db.open();
                eventID = db.getEventID(lng,lat);
                resJsonArrayPicture = nc.searchRequestForImages(eventID);

                IsOK= parseJsonForImages(resJsonArrayPicture);
                //	if(resJsonArrayPicture.equals(null)||resJsonArrayPicture.isNull(0))
                //	IsOK = false;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            return null;
        }

        private boolean parseJsonForImages(JSONArray jsonArray) {

            images = new ArrayList<Image>();
            try
            {
                if (jsonArray == null || jsonArray.toString().equalsIgnoreCase("null"))
                {
                    return false;
                }

                else
                {

                    for (int i = 0; i < jsonArray.length(); i++)
                    {

                        Image image = new Image();

                        JSONObject obj = jsonArray.getJSONObject(i);

                        image.setImageUrl(obj.getString("image"));
                        image.setComment(obj.getString("description"));
                        images.add(image);
                        //Log.i(urlImages, "testing");




                    }

                }
            }
            catch (Exception e)
            {
                Log.e(tag, "Error witjh parsing JSOn to model class!", e);
                return false;
            }
            return true;

        }


        @SuppressLint({ "ShowToast", "NewApi" }) @Override
        protected void onPostExecute(Void result)
        {
            statusDialogPicture.cancel();
            if(IsOK)
            {

                card_recycler_view.setVisibility(View.VISIBLE);
                btn_cancel.setVisibility(View.VISIBLE);
                card_recycler_view.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BelgradeMapsActivity.this, LinearLayoutManager.HORIZONTAL, false);
                card_recycler_view.setLayoutManager(layoutManager);
             //   RelativeLayout.LayoutParams HParams = new RelativeLayout.LayoutParams(
               //         LayoutParams.WRAP_CONTENT, 80);
               // card_recycler_view.setLayoutParams(HParams);

                adapter = new DataAdapter(BelgradeMapsActivity.this,getApplicationContext(),images);
                card_recycler_view.setAdapter(adapter);
                adapter.notifyDataSetChanged();
//
//
// RelativeLayout.LayoutParams HParams = new RelativeLayout.LayoutParams(
//                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//
//                HParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//
//                cvImages.setLayoutParams(HParams);

//              for (final Image image : images) {
//
//
//                    Thread thread = new Thread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            // TODO Auto-generated method stub
//                            try {
//                               // newurl = new URL(image.getImageUrl());
//                               // bitmap = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
//                                //myGallery = (LinearLayout) findViewById(R.id.llMyGallery);
//
//                               // Picasso.with(this).load(image.getImageUrl()).resize(120, 60).into(viewHolder.img_android);
//
////                                final ImageView btnTag = new ImageView(BelgradeMapsActivity.this);
////
////                                LayoutParams params = new LayoutParams(
////                                        (int) getResources().getDimension(R.dimen.imageview_height),
////                                        (int) getResources().getDimension(R.dimen.imageview_height)
////                                );
////
////                                params.setMargins((int) getResources().getDimension(R.dimen.margins), (int) getResources().getDimension(R.dimen.margins), (int) getResources().getDimension(R.dimen.margins), (int) getResources().getDimension(R.dimen.margins));
////                                btnTag.setLayoutParams(params);
////                                btnTag.setScaleType(ImageView.ScaleType.CENTER_CROP);
////                                btnTag.setImageBitmap(bitmap);
//
//
////                                runOnUiThread(new Runnable() {
////
////                                    @Override
////                                    public void run() {
//
////                                        //	myGallery.clearFocus();
////                                        myGallery.setGravity(Gravity.BOTTOM);
////                                        myGallery.addView(btnTag);
////                                        //   	btnTag.destroyDrawingCache();
//
//
//
//
////                                    }
////                                });
//
////                                btnTag.setOnClickListener(new View.OnClickListener() {
////
////                                    @Override
////                                    public void onClick(View v) {
////                                        // TODO Auto-generated method stub
//////											db.open();
//////											Location  loc = db.selectLocation(eventID);
//////											MarkerOptions customMarker = new MarkerOptions().position(new LatLng(loc.getLat(),loc.getLng())).
//////
//////													icon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_CYAN));
//////													map.addMarker(customMarker);
////
////                                        Intent zoom = new Intent(BelgradeMapsActivity.this, ZoomImageActivity.class);
////                                        zoom.putExtra("comment",image.getComment());
////                                        zoom.putExtra("image",image.getImageUrl().toString());
////                                        startActivity(zoom);
////
////
////
////                                    }
////                                });
//
//                            } catch (Exception e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }) ;
//
//                    thread.start();



            //fimages    }

            }
            else
            {
                card_recycler_view.setVisibility(View.INVISIBLE);
                btn_cancel.setVisibility(View.INVISIBLE);
                Toast.makeText(BelgradeMapsActivity.this,"There are no images!".toUpperCase(), Toast.LENGTH_LONG).show();
            }

            super.onPostExecute(result);
        }

    }



    private class AsyncGetData extends AsyncTask<Void, Void, Void> {

        JSONArray resJsonArray;

        boolean IsOK = false;
        Dialog statusDialog = new Dialog(BelgradeMapsActivity.this);

        @Override
        protected void onPreExecute()
        {
            BelgradeMapsActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run()
                {
                    statusDialog.setTitle(getString(R.string.fetchingDatafromServer));
                    statusDialog.setContentView(R.layout.circular_progress_bar);
                    statusDialog.show();
                }
            });

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                resJsonArray = nc.searchRequest();
                IsOK = parseJsonToModle(resJsonArray);





            }
            catch (Exception e)
            {
                Log.e("error", "error", e);

            }
            return null;
        }


        private boolean parseJsonToModle(JSONArray jsArray) {


            try
            {
                if (jsArray == null || jsArray.toString().equalsIgnoreCase("null"))
                {
                    return false;
                }

                else
                {
                    Event event;
                    Location location;
                    for (int i = 0; i < jsArray.length(); i++)
                    {
                        int isTre = 0;
                        event = new Event();
                        location = new Location();

                        JSONObject obj = jsArray.getJSONObject(i);

                        event.setEventID(obj.getInt("id"));
                        event.setName(obj.getString("name"));
                        event.setStarting_at(obj.getString("starting_at"));
                        event.setEnding_at(obj.getString("ending_at"));
                        isTre = obj.getBoolean("is_trending")? 1 : 0;
                        event.setIs_trending(isTre);
                        JSONObject jsonLocation = obj.getJSONObject("location");
                        location.setLat(jsonLocation.getDouble("lat"));
                        location.setLng(jsonLocation.getDouble("lng"));
                        event.setLocation(location);
                        ArrayList<String> tags = new ArrayList<String>();


                        JSONArray jsArrayTags = obj.getJSONArray("tags");
                        //String tag="";
                        for (int j = 0; j < jsArrayTags.length(); j++)
                        {
                            String tag="";
                            tag = (String) jsArrayTags.get(j);
                            tags.add(tag);
                        }

                        event.setTags(tags);

                        insertDataInDB(event);



                    }

                }
            }
            catch (Exception e)
            {
                Log.e(tag, "Error witjh parsing JSOn to model class!", e);
                return false;
            }
            return true;

        }


        private void insertDataInDB(Event event) {
            // TODO Auto-generated method stub
            db.open();
            try {
                db.Delete_Event(event);
                db.Event_I(event);
                db.Location_I(event);
                //db.Tag_I_U(event);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }

        @SuppressLint("ShowToast") @Override
        protected void onPostExecute(Void result)
        {
            statusDialog.cancel();
            if(IsOK)
            {
                db.open();
                ArrayList<Event> events = new ArrayList<Event>();
                try {
                    events = db.select_event();
                    if (events.size()==0)
                    {
                        ///nlist is empty
                    }
                    else
                    {
                        setMarkers(events);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e(tag,"Error set select event from db", e);
                }


            }
            else
            {
                Toast.makeText(BelgradeMapsActivity.this,"Something wrong!Can not load google events and markers!".toUpperCase(), Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(result);
        }
    }

    protected boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())
        {
            return true;
        }
        else
        {
            Log.d("tag", "No network available!");
            return false;
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        card_recycler_view.setVisibility(View.INVISIBLE);
        btn_cancel.setVisibility(View.INVISIBLE);
        images.clear();
        images = new ArrayList<>();
        //images.clear();
        moveToCurrentLocation(marker.getPosition());
        mMap.setInfoWindowAdapter(this);
        marker.showInfoWindow();

            if (isOnline())
            {

                // Event eventInfo = eventMarkerMap.get(marker);
                AsyncGetPicture getPicture = new AsyncGetPicture();
                getPicture.lat = marker.getPosition().latitude;
                getPicture.lng = marker.getPosition().longitude;
                //	getPicture.eventID = EventIDClicked;
                getPicture.execute();
                //	map.addMarker(marker.setIcon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_CYAN)));
            }
            else
            {
                Toast.makeText(BelgradeMapsActivity.this, "NO NETWORK. Can not show markers".toUpperCase(), Toast.LENGTH_LONG)
                        .show();
            }



        return true;
    }


    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

     //   Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(this);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        title.setText(marker.getTitle());

        TextView snippet = new TextView(this);
        snippet.setTextColor(Color.GRAY);
        snippet.setText(marker.getSnippet());

        info.addView(title);
        info.addView(snippet);

        return info;
    }




}
