package com.example.pruebakotlin.Ejemplos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pruebakotlin.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class MapaJava extends AppCompatActivity implements OnMapReadyCallback,PermissionsListener, MapboxMap.OnMapClickListener {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private MarkerView markerView;
    private MarkerViewManager markerViewManager;
    private PermissionsManager permissionsManager;
    private com.mapbox.geojson.Point originPosition;
    private com.mapbox.geojson.Point destinationPosition;
    private LatLng originLocation;
    private NavigationMapRoute navigationMapRoute;
    private final static String TAG="MapaJava";
    private DirectionsRoute directionsRoute;
    private CarmenFeature home;
    private CarmenFeature work;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";
    LinearLayout BtnBuscador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_mapa);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        MaterialButton BtnNavigation = (MaterialButton)findViewById(R.id.BtnNavigation);
        FloatingActionButton BtnUbication=(FloatingActionButton)findViewById(R.id.BtnUbication);
        BtnBuscador=(LinearLayout)findViewById(R.id.BtnBuscador);

        BtnNavigation.setOnClickListener(v->{
            NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                    .directionsRoute(directionsRoute)
                    .shouldSimulateRoute(false)
                    .build();
            NavigationLauncher.startNavigation(MapaJava.this, options);
        });

        BtnUbication.setOnClickListener(v -> {
            setCameraPosition(originLocation);
        });


       /* mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                MapaJava.this.mapboxMap = mapboxMap;

                mapboxMap.setStyle(Style.TRAFFIC_DAY, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        addMarker();
                    }
                });
                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {
                        Toast.makeText(MapaJava.this, String.format("User clicked at: %s", point.toString()), Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
            }
        });*/
    }

    private void addMarker(LatLng location){
        markerViewManager = new MarkerViewManager(mapView, mapboxMap);
        ImageView imageView = new ImageView(MapaJava.this);
        imageView.setImageResource(R.mipmap.ic_marker);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
        imageView.setLayoutParams(params);
        markerView = new MarkerView(new LatLng(location.getLatitude(), location.getLongitude()), imageView);
        imageView.setOnClickListener(v -> {
            openDialog();
        });

        markerViewManager.addMarker(markerView);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        this.mapboxMap=mapboxMap;
        mapboxMap.addOnMapClickListener(this);
        mapboxMap.getUiSettings().setRotateGesturesEnabled(false);

        mapboxMap.setStyle(Style.TRAFFIC_DAY, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enabledLocation(style);

                initSearchFab();
                addUserLocations();

                // Add the symbol layer icon to map for future use
                style.addImage(symbolIconId, BitmapFactory.decodeResource(
                        MapaJava.this.getResources(), R.mipmap.ic_marker));

                // Create an empty GeoJSON source using the empty feature collection
                setUpSource(style);

                // Set up a new symbol layer for displaying the searched location's feature coordinates
                setupLayer(style);
            }
        });
    }



    private void initSearchFab(){

        BtnBuscador.setOnClickListener(v->{
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.mapbox_access_token))
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(Color.parseColor("#EEEEEE"))
                            .limit(10)
                            //.addInjectedFeature(home)
                            //.addInjectedFeature(work)
                            .build(PlaceOptions.MODE_CARDS))
                    .build(MapaJava.this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        });
    }
    private void addUserLocations() {
        home = CarmenFeature.builder().text("Mapbox SF Office")
                .geometry(Point.fromLngLat(-122.3964485, 37.7912561))
                .placeName("50 Beale St, San Francisco, CA")
                .id("mapbox-sf")
                .properties(new JsonObject())
                .build();

        work = CarmenFeature.builder().text("Mapbox DC Office")
                .placeName("740 15th Street NW, Washington DC")
                .geometry(Point.fromLngLat(-77.0338348, 38.899750))
                .id("mapbox-dc")
                .properties(new JsonObject())
                .build();
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enabledLocation(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

            originLocation=new LatLng(locationComponent.getLastKnownLocation());
            setCameraPosition(originLocation);

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }
    private void setCameraPosition(LatLng location){
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(),location.getLongitude()))
                .zoom(mapboxMap.getMaxZoomLevel()-8)
                .bearing(50.0)
                //.tilt(30.0)
                .build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000);
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        if (markerView!=null){
            markerViewManager.removeMarker(markerView);
        }

        addMarker(point);

        destinationPosition= com.mapbox.geojson.Point.fromLngLat(point.getLongitude(),point.getLatitude());
        originPosition=com.mapbox.geojson.Point.fromLngLat(originLocation.getLongitude(),originLocation.getLatitude());
        getRoute(originPosition,destinationPosition);
        return false;
    }

    private void getRoute(Point origin, Point destination) {

        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        directionsRoute = response.body().routes().get(0);

                        if(navigationMapRoute!=null){
                            navigationMapRoute.removeRoute();
                        }else {
                            navigationMapRoute= new NavigationMapRoute(null,mapView,mapboxMap);
                        }
                        navigationMapRoute.addRoute(directionsRoute);

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG, "ERROR"+t.getMessage());

                    }
                });
    }

    private void openDialog(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_detail);
        dialog.show();
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Requiere los permisos", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enabledLocation(style);
                }
            });
        } else {
            Toast.makeText(this, "Permisos no aceptados", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }

                    // Move map camera to the selected location
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 4000);
                }
            }
        }
    }

    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[] {0f, -8f})
        ));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


}