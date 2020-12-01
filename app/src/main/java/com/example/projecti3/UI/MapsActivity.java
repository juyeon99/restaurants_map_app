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
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.projecti3.Model.PassingSearch;
import com.example.projecti3.Model.comeFromForRD;
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
import com.google.maps.android.clustering.ClusterItem;
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
// https://www.youtube.com/watch?v=iWYsBDCGhGw SearchView
// https://youtu.be/CTvzoVtKoJ8 Filtering search

/**
 *    MapsActivity --> Displays the user's current location as a default, and shows the map with the pegs for each restaurants
 *                     Able to go to restaurant lists by clicking the button
 *                     Clusters the pegs if there's too many pegs at the similar area
 *                     If the user search the restaurant, it would show the restaurant pegs that contains the word the user typed and satisfies the searching criteria.
 */

public class MapsActivity extends AppCompatActivity {
    Restaurant restaurant = new Restaurant();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 10;
    private FusedLocationProviderClient client;
    SupportMapFragment supportMapFragment;
    GoogleMap gMap;
    Button goToList;
    ArrayList<Restaurant> restaurantArrayList = new ArrayList<>();
    List<Inspection> list;
    private ClusterManager<MyItem> clusterManager;
    LocationRequest locationRequest;
    Marker userLocationMarker;
    private MarkerClusterRenderer renderer;
    private List<MyItem> myItemList=new ArrayList<>();

    SingletonRestaurantManager manager;
    List<Restaurant> sortedRestaurantList;
    PassingSearch passingSearch = PassingSearch.getInstance();

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
        manager = SingletonRestaurantManager.getInstance();
        sortedRestaurantList = manager.sortAlphabetically();

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

        //iteration 3 search
        SearchView searchView = findViewById(R.id.SearchMap);
        searchView.setQuery(passingSearch.getSearchValue(),false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query){
                passingSearch.setSearchValue(query);
                MapsActivity.this.renderer.getFilter().filter(passingSearch.getSearchValue());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //although in the email, prof Jack said, he will assume to see the result after press enter,
                //it is nicer to see the changes while user types
                passingSearch.setSearchValue(newText);
                MapsActivity.this.renderer.getFilter().filter(passingSearch.getSearchValue());
                return false;
            }
        });
        String constraint = "";
        String[] sortedInput = constraint.toString().split(",", -1);
//        Toast.makeText(getApplicationContext(), ""+sortedInput.length, Toast.LENGTH_SHORT).show();

//        searchView.setQuery(passingSearch.getSearchValue(),false);
//        if (!passingSearch.getSearchValue().isEmpty()) {
//            Toast.makeText(getApplicationContext(), "Click the search bar and press enter to show the searched result.", Toast.LENGTH_SHORT).show();
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
                            MarkerOptions options = new MarkerOptions().position(currentLatLng).title(getString(R.string.curLocation));
                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            options.title(getString(R.string.currentLocation));
                            moveCamera(currentLatLng, DEFAULT_ZOOM);
                            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            gMap.setMyLocationEnabled(true);
                            displayRestaurants();
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
                        level = getApplicationContext().getResources().getString(R.string.lowHazard);
                    } else if (orderedList.get(0).getHazardLevel().equals("Moderate")) {
                        options.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.orange_map_marker));
                        level = getApplicationContext().getResources().getString(R.string.moderateHazard);
                    } else if (orderedList.get(0).getHazardLevel().equals("High")) {
                        options.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.red_map_marker));
                        level = getApplicationContext().getResources().getString(R.string.highHazard);
                    }
                } else {
                    options.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.orange_map_marker));
                    level = getApplicationContext().getResources().getString(R.string.noHazard);
                }

                String address = getApplicationContext().getResources().getString(R.string.addressRecent);
                String whatHazard = getApplicationContext().getResources().getString(R.string.whatHazard);
                String mostRecent = getApplicationContext().getResources().getString(R.string.mostRecent);
                String snippet = address  + ": " + res.getAddress() + "\n"
                        + whatHazard  +" (" + mostRecent + "): " + level ;
                options.snippet(snippet);

                options.title(res.getName());

                //this tag is the index of a shorted restaurant list
                //gMap.addMarker(options).setTag(i);

                MyItem myItem = new MyItem(res.getLatitude(), res.getLongitude(), res.getName(), snippet, i, res.getFavStatus(), res.getLatestHazard(), res.getLatestNumIssues());
                myItemList.add(myItem);
              // clusterManager.addItem(myItem);
            }
            clusterManager.addItems(myItemList);
            gMap.setOnCameraIdleListener(clusterManager);
            gMap.setOnMarkerClickListener(clusterManager);
            clusterManager.cluster();
            renderer=new MarkerClusterRenderer(MapsActivity.this, gMap, clusterManager);
            clusterManager.setRenderer(renderer);
            //show the pop-up info when click

            gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    if(marker.getTitle().equals("This is where you are.")){
                        Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //this index is of a shorted restaurant list
                    int index = (int) marker.getTag();
                    //simple test
                    //Toast.makeText(getApplicationContext(), ""+marker.getTag(), Toast.LENGTH_SHORT).show();
                    Intent intent = RestaurantDetailsUI.makeDetailIntent(MapsActivity.this,index );
                    comeFromForRD.getInstance().setSearchValue("Map");
                    startActivity(intent);
                }
            });

            gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if(marker.getTitle().equals(getString(R.string.currentLocation))){
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
                    comeFromForRD.getInstance().setSearchValue("Map");
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

            if (!passingSearch.isEmpty()) {
                MapsActivity.this.renderer.getFilter().filter(passingSearch.getSearchValue());
            }

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
                                String name = getApplicationContext().getResources().getString(R.string.name);
                                String address = getApplicationContext().getResources().getString(R.string.addressRecent);
                                String clickPeg = getApplicationContext().getResources().getString(R.string.clickPeg);
                                LatLng currentLatLng = new LatLng(latitude,longitude);
                                moveCamera(currentLatLng, 20);
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.name) + " " + list.get(index).getName() + "\n" +
                                                getString(R.string.address) + ": " + list.get(index).getAddress() + "\n" +
                                                getString(R.string.clickPeg),
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
