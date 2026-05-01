package ru.mirea.zhirkovaei.osmmaps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import ru.mirea.zhirkovaei.osmmaps.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION_LOCATION = 100;

    private ActivityMainBinding binding;
    private MapView mapView;
    private MyLocationNewOverlay locationNewOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );

        Configuration.getInstance().setUserAgentValue(getPackageName());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapView = binding.mapView;

        setupMap();

        if (hasLocationPermission()) {
            addUserLocationLayer();
        } else {
            requestLocationPermission();
        }

        addCompass();
        addScaleBar();
        addMarkers();
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        mapView.setBuiltInZoomControls(true);

        mapView.setZoomRounding(true);

        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();

        mapController.setZoom(15.0);

        GeoPoint startPoint = new GeoPoint(55.794229, 37.700772);
        mapController.setCenter(startPoint);
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                REQUEST_CODE_PERMISSION_LOCATION
        );
    }

    private void addUserLocationLayer() {
        if (!hasLocationPermission()) {
            return;
        }

        locationNewOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(getApplicationContext()),
                mapView
        );

        locationNewOverlay.enableMyLocation();
        locationNewOverlay.enableFollowLocation();

        mapView.getOverlays().add(locationNewOverlay);
    }

    private void addCompass() {
        CompassOverlay compassOverlay = new CompassOverlay(
                getApplicationContext(),
                new InternalCompassOrientationProvider(getApplicationContext()),
                mapView
        );

        compassOverlay.enableCompass();

        mapView.getOverlays().add(compassOverlay);
    }

    private void addScaleBar() {
        final Context context = this.getApplicationContext();
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();

        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);

        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);

        mapView.getOverlays().add(scaleBarOverlay);
    }

    private void addMarkers() {
        addMarker(
                new GeoPoint(55.794229, 37.700772),
                "РТУ МИРЭА",
                "Кампус РТУ МИРЭА на Стромынке."
        );

        addMarker(
                new GeoPoint(55.752023, 37.617499),
                "Красная площадь",
                "Главная площадь Москвы рядом с Кремлём."
        );

        addMarker(
                new GeoPoint(55.729828, 37.601066),
                "Парк Горького",
                "Популярное место для прогулок, спорта и отдыха."
        );
    }

    private void addMarker(GeoPoint position, String title, String description) {
        Marker marker = new Marker(mapView);

        marker.setPosition(position);
        marker.setTitle(title);
        marker.setSnippet(description);

        marker.setIcon(
                ResourcesCompat.getDrawable(
                        getResources(),
                        org.osmdroid.library.R.drawable.osm_ic_follow_me_on,
                        null
                )
        );

        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                marker.showInfoWindow();

                Toast.makeText(
                        getApplicationContext(),
                        marker.getTitle() + "\n" + marker.getSnippet(),
                        Toast.LENGTH_LONG
                ).show();

                return true;
            }
        });

        mapView.getOverlays().add(marker);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                addUserLocationLayer();

            } else {
                Toast.makeText(
                        this,
                        "Разрешение на местоположение не выдано",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Configuration.getInstance().load(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );

        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        Configuration.getInstance().save(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );

        if (mapView != null) {
            mapView.onPause();
        }
    }
}