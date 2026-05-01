package ru.mirea.zhirkovaei.yandexdriver;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingRouterType;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;

import ru.mirea.zhirkovaei.yandexdriver.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION_LOCATION = 100;

    private ActivityMainBinding binding;
    private MapView mapView;

    private MapObjectCollection mapObjects;
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession;

    private Point userLocationPoint;

    // Любимое заведение
    private final Point CAFE_LOCATION = new Point(55.757760, 37.601650);
    private final String CAFE_NAME = "Кофемания";
    private final String CAFE_ADDRESS = "Москва, ул. Большая Никитская, 13";
    private final String CAFE_DESCRIPTION = "Любимое заведение с кофе и десертами.";

    private final int[] colors = {
            0xFFFF0000,
            0xFF00FF00,
            0xFF00BBBB,
            0xFF0000FF
    };

    private final DrivingSession.DrivingRouteListener drivingRouteListener =
            new DrivingSession.DrivingRouteListener() {
                @Override
                public void onDrivingRoutes(@NonNull List<DrivingRoute> routes) {
                    mapObjects.clear();

                    for (int i = 0; i < routes.size(); i++) {
                        int color = colors[i % colors.length];

                        mapObjects.addPolyline(routes.get(i).getGeometry())
                                .setStrokeColor(color);
                    }

                    addCafeMarker();
                }

                @Override
                public void onDrivingRoutesError(@NonNull Error error) {
                    String errorMessage = "Неизвестная ошибка";

                    if (error instanceof RemoteError) {
                        errorMessage = "Ошибка сервера";
                    } else if (error instanceof NetworkError) {
                        errorMessage = "Ошибка сети";
                    }

                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                    addCafeMarker();
                }
            };

    private final MapObjectTapListener cafeTapListener = new MapObjectTapListener() {
        @Override
        public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
            Toast.makeText(
                    MainActivity.this,
                    CAFE_NAME + "\n" + CAFE_ADDRESS + "\n" + CAFE_DESCRIPTION,
                    Toast.LENGTH_LONG
            ).show();

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapKitFactory.initialize(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapView = binding.mapview;

        mapView.getMap().setRotateGesturesEnabled(false);

        mapObjects = mapView.getMap().getMapObjects().addCollection();

        drivingRouter = DirectionsFactory
                .getInstance()
                .createDrivingRouter(DrivingRouterType.COMBINED);

        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {

            buildRouteFromUserLocation();

        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    REQUEST_CODE_PERMISSION_LOCATION
            );
        }
    }

    private void buildRouteFromUserLocation() {
        userLocationPoint = getUserLocationPoint();

        Point screenCenter = new Point(
                (userLocationPoint.getLatitude() + CAFE_LOCATION.getLatitude()) / 2,
                (userLocationPoint.getLongitude() + CAFE_LOCATION.getLongitude()) / 2
        );

        mapView.getMap().move(
                new CameraPosition(
                        screenCenter,
                        12.0f,
                        0.0f,
                        0.0f
                ),
                new Animation(Animation.Type.SMOOTH, 0),
                null
        );

        submitRequest();
    }

    private Point getUserLocationPoint() {
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return new Point(55.751574, 37.573856);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location != null) {
            return new Point(location.getLatitude(), location.getLongitude());
        } else {
            Toast.makeText(
                    this,
                    "Не удалось получить координаты. Используется точка по умолчанию.",
                    Toast.LENGTH_LONG
            ).show();

            return new Point(55.751574, 37.573856);
        }
    }

    private void submitRequest() {
        DrivingOptions drivingOptions = new DrivingOptions();
        VehicleOptions vehicleOptions = new VehicleOptions();

        drivingOptions.setRoutesCount(4);

        ArrayList<RequestPoint> requestPoints = new ArrayList<>();

        requestPoints.add(
                new RequestPoint(
                        userLocationPoint,
                        RequestPointType.WAYPOINT,
                        null,
                        null,
                        null
                )
        );

        requestPoints.add(
                new RequestPoint(
                        CAFE_LOCATION,
                        RequestPointType.WAYPOINT,
                        null,
                        null,
                        null
                )
        );

        drivingSession = drivingRouter.requestRoutes(
                requestPoints,
                drivingOptions,
                vehicleOptions,
                drivingRouteListener
        );
    }

    private void addCafeMarker() {
        PlacemarkMapObject marker = mapObjects.addPlacemark(
                CAFE_LOCATION,
                ImageProvider.fromResource(this, android.R.drawable.star_big_on)
        );

        marker.addTapListener(cafeTapListener);
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

                buildRouteFromUserLocation();

            } else {
                Toast.makeText(
                        this,
                        "Разрешение на местоположение не выдано",
                        Toast.LENGTH_SHORT
                ).show();

                userLocationPoint = new Point(55.751574, 37.573856);
                submitRequest();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();

        super.onStop();
    }
}