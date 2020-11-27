package com.example.projecti3.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import com.example.projecti3.Model.Inspection;
import com.example.projecti3.Model.MarkerClusterRenderer;
import com.example.projecti3.Model.MyItem;
import com.example.projecti3.Model.Restaurant;
import com.example.projecti3.Model.SingletonRestaurantManager;
import com.example.projecti3.R;

// https://www.youtube.com/watch?v=p0PoKEPI65o current location
// https://www.youtube.com/playlist?list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt maps
// https://www.youtube.com/watch?v=26bl4r3VtGQ changing marker icons
// https://www.iconsdb.com/guacamole-green-icons/map-marker-2-icon.html map marker icons image downloaded
// https://www.youtube.com/watch?v=5tsxswfzZXc changing marker color
// https://www.youtube.com/watch?v=m6zcM6Q2qZU adding onClickListener for markers
// https://www.youtube.com/watch?v=4GYKOzgQDWI popup dialog
// https://www.youtube.com/watch?v=DhYofrJPzlI display popup
// https://milkissboy.tistory.com/36 clustered pegs
// https://developers.google.com/maps/documentation/android-sdk/utility/marker-clustering#introduction clustered pegs
// https://stackoverflow.com/questions/30963602/android-open-infowindow-on-cluster-marker overriding renderer
// https://www.youtube.com/watch?v=VUVv2Of7gBU&feature=share location update

/**
 *    MapsActivity --> Displays the user's current location as a default, and shows the map with the pegs for each restaurants
 *                     Able to go to restaurant lists by clicking the button
 *                     Clusters the pegs if there's too many pegs at the similar area
 */

public class MapsActivity extends AppCompatActivity {
    Restaurant restaurant = new Restaurant();
    Inspection inspection = new Inspection();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private FusedLocationProviderClient client;
    SupportMapFragment supportMapFragment;
    GoogleMap gMap;
    Button goToList;
    ArrayList<Restaurant> restaurantArrayList = new ArrayList<>();
    List<Inspection> list;
    private ClusterManager<MyItem> clusterManager;
    LocationRequest locationRequest;
    Marker userLocationMarker;

    private EditText searchText;
    SingletonRestaurantManager manager = SingletonRestaurantManager.getInstance();
    List<Restaurant> sortedRestaurantList = manager.sortAlphabetically();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        client = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        goToList = (Button) findViewById(R.id.btnShowList);
        goToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MapsActivity.this, RestaurantList.class);
                //pass the index of current restaurant
                //intent.putExtra("fromMap:",-1);
                MapsActivity.this.startActivityForResult(intent,1);
            }
        });

        for (Restaurant res : SingletonRestaurantManager.getInstance()) {
            restaurantArrayList.add(res);
        }

        list = restaurant.getAllInspectionList();

        // Check Permission
        if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // When the permission is granted, call the method to get the current location
            getCurrentLocation();
        } else {
            // When the permission is denied, request permission
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

        }
        //this is for when we use gps in the restaurant detail to call this map
        startGps(this.getIntent());
    }

    private void init() {
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute the method for searching
                    geoLocate();
                }
                return false;
            }
        });
    }

    private void geoLocate(){
        String searchString = searchText.getText().toString();

        List<Restaurant> restaurantLists = new ArrayList<>();

        for (Restaurant r : sortedRestaurantList) {
            if (r.getName().contains(searchString)) {
                List<Inspection> orderedList = r.getAllInspection();
                helperOrdering(orderedList);
                if (orderedList.get(0).getHazardLevel().equals("Low")
                        && orderedList.get(0).getViolationString().size() <= 5) {
                    restaurantLists.add(r);
                }
            }
        }

        if (restaurantLists.size() > 0) {
            displayRestaurants(restaurantLists);
        }

        // hide;


//        if(restaurantLists.size() > 0){
//            Restaurant restaurant = list.get(0);
//            Log.d(TAG, "Found a location");
//            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
//            moveCamera(new LatLng(restaurant.getLatitude(), restaurant.getLongitude()), DEFAULT_ZOOM);
//        }
    }

    private void getCurrentLocation() {
        // Initialize task location
        @SuppressLint("MissingPermission") Task<Location> task = client.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // When success
                if (location != null) {
                    // Sync map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {

                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            gMap = googleMap;
                            gMap.getUiSettings().setZoomControlsEnabled(true);
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions options = new MarkerOptions().position(currentLatLng).title("Current Location");
                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            options.title("This is where you are.");
                            // gMap.addMarker(options).showInfoWindow();
                            //moveCamera(currentLatLng, 5);
                            moveCamera(currentLatLng, 10);
                            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            gMap.setMyLocationEnabled(true);
                            //init();
                            displayRestaurants();
                            init();
                        }
                    });
                }
            }
        });

    }

    private void displayRestaurants() {

        try {
            SingletonRestaurantManager manager = SingletonRestaurantManager.getInstance();
            List<Restaurant> sortedRestaurantList = manager.sortAlphabetically();
            clusterManager = new ClusterManager<MyItem>(this, gMap);

            //TODO Optimize
            for (int i = 0; i < manager.getNumRestaurants(); i++) {
                Restaurant res = sortedRestaurantList.get(i);
                float latitude = res.getLatitude();
                float longitude = res.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
//            if(latitude != 0 && longitude != 0 && latLng != null || ) {
//
//            }
                //LatLng latLng = new LatLng(latitude, longitude);
                MarkerOptions options = new MarkerOptions().position(latLng);
                String recentDate = "" + res.getLatestInspectionDate(list, res.getTrackingNum());

                //get all the inspection of restaurant we are working on
                List<Inspection> orderedList = res.getAllInspection();
                //helps make the inspection listn in the order from latest to the oldest
                helperOrdering(orderedList);
                String level = "";
                if (orderedList.size() != 0) {
                    if (orderedList.get(0).getHazardLevel().equals("Low")) {
                        options.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.green_map_marker));
                        level = "Low";
                    } else if (orderedList.get(0).getHazardLevel().equals("Moderate")) {
                        options.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.orange_map_marker));
                        level = "Moderate";
                    } else if (orderedList.get(0).getHazardLevel().equals("High")) {
                        options.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.red_map_marker));
                        level = "High";
                    }
                } else {
                    options.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.orange_map_marker));
                    level = "None";
                }


                String snippet = "Address: " + res.getAddress() + "\n"
                        + "Hazard Level (most recent): "+level ;
                options.snippet(snippet);

                options.title(res.getName());

                //this tag is the index of a shorted restaurant list
                //gMap.addMarker(options).setTag(i);

                MyItem myItem = new MyItem(res.getLatitude(), res.getLongitude(), res.getName(), snippet,i);

                clusterManager.addItem(myItem);


            }
            gMap.setOnCameraIdleListener(clusterManager);
            gMap.setOnMarkerClickListener(clusterManager);
            clusterManager.cluster();
            clusterManager.setRenderer(new MarkerClusterRenderer(this, gMap, clusterManager));



            //show the pop-up info when click

            gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    if(marker.getTitle().equals("This is where you are.")){
                        Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //this index is of a shorted restaurant list
                    int index= (int) marker.getTag();
                    //simple test
                    //Toast.makeText(getApplicationContext(), ""+marker.getTag(), Toast.LENGTH_SHORT).show();
                    Intent intent = RestaurantDetailsUI.makeDetailIntent(MapsActivity.this,index );
                    startActivity(intent);
                }
            });



            gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if(marker.getTitle().equals("This is where you are.")){
                        return false;
                    }
                    gMap.setInfoWindowAdapter(new CustomInfo(MapsActivity.this));
                    return false;
                }
            });
            clusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<MyItem>() {
                public void onClusterItemInfoWindowClick(MyItem item) {
                    //this index is of a shorted restaurant list
                    int index= (int) item.getTag();
                    //simple test
                    //Toast.makeText(getApplicationContext(), ""+marker.getTag(), Toast.LENGTH_SHORT).show();
                    Intent intent = RestaurantDetailsUI.makeDetailIntent(MapsActivity.this,index );
                    startActivity(intent);
                }
            });

            clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {

                public boolean onClusterItemClick(MyItem item) {
                    //Toast.makeText(getApplicationContext(),"Gopod" ,Toast.LENGTH_LONG).show();
                    gMap.setInfoWindowAdapter(new CustomInfo(MapsActivity.this));
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private BitmapDescriptor bitmapDescriptorFromVector (Context context, int vectorResId) {
        Drawable drawable = ContextCompat.getDrawable(context, vectorResId);
        drawable.setBounds(0,0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // When the permission is granted, call the method to get the current location
                getCurrentLocation();
            }
        }
    }

    private void moveCamera (LatLng latLng, float zoom) {
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
    public static Intent makeIntent(Context context) {
        // switching activity from RestaurantList to MapsActivity
        return new Intent (context, MapsActivity.class);

    }
    //this part is for showing map when click the gpscoord
    private void startGps(Intent intent) {

        int index=getIntent().getIntExtra("fromRestaurantDetail",-1);
        if(index!=-1){
            SingletonRestaurantManager manager = SingletonRestaurantManager.getInstance();
            List<Restaurant> list = manager.sortAlphabetically();
            List<Inspection> inslist = list.get(index).getAllInspection();
            helperOrdering(inslist);

            float latitude=list.get(index).getLatitude();
            float longitude=list.get(index).getLongitude();
            LatLng latLng = new LatLng(latitude,longitude);
            //Toast.makeText(getApplicationContext(),"La:" +list.get(index).getLatitude()+"\n"+"lo: "+ list.get(index).getLongitude() ,Toast.LENGTH_LONG).show();
            // Initialize task location
            //credits from above getCurrentLocation()
            @SuppressLint("MissingPermission") Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // When success
                    if (location != null) {
                        // Sync map
                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                //move view to the gps of the clicked restaurant
                                gMap = googleMap;
                                gMap.getUiSettings().setZoomControlsEnabled(true);
                                LatLng currentLatLng = new LatLng(latitude,longitude);
                                moveCamera(currentLatLng, 20);
                                Toast.makeText(getApplicationContext(),
                                        "Name: "+list.get(index).getName()+"\n"+
                                                "Address: "+list.get(index).getAddress()+"\n"+
                                                "Click this peg for more information.",
                                        Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                }
            });
        }
        //simple test
        //Toast.makeText(getApplicationContext(),"index: "+index ,Toast.LENGTH_LONG).show();
    }
    //back button behavior
    //to exit the app
    //source from: https://stackoverflow.com/questions/21253303/exit-android-app-on-back-pressed
    @Override
    public void onBackPressed(){
        //when the calling activity is the restaurant list activity
        //we exit the app
        //otherwise we keep the original function
        if(getCallingActivity().getClassName().equals("com.example.projecti3.UI.RestaurantList")){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        finish();
    }

    private void helperOrdering(List<Inspection> orderedList) {
        Inspection temp;
        for (int i = 0; i < orderedList.size(); i++) {
            for (int j = i + 1; j < orderedList.size(); j++) {
                if (orderedList.get(i).getDate()<(orderedList.get(j).getDate())) {

                    temp = orderedList.get(i);
                    orderedList.set(i, orderedList.get(j));
                    orderedList.set(j, temp);
                }
            }
        }
    }
}