package ru.mirea.zhirkova.mireaproject;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.List;

public class PlacesFragment extends Fragment {

    private MapView mapView;
    private final List<GeoPoint> placePoints = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(
                requireContext().getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext().getApplicationContext())
        );

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View root = inflater.inflate(R.layout.fragment_places, container, false);

        mapView = root.findViewById(R.id.map_places);
        Button buttonShowAll = root.findViewById(R.id.button_show_all_places);

        setupMap();
        addPlacesMarkers();
        addScaleBar();

        buttonShowAll.setOnClickListener(v -> showAllPlaces());

        return root;
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // Кнопки масштабирования
        mapView.setBuiltInZoomControls(true);

        // Масштабирование двумя пальцами
        mapView.setMultiTouchControls(true);

        // Округление масштаба
        mapView.setZoomRounding(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(12.0);
        mapController.setCenter(new GeoPoint(55.755864, 37.617698));
    }

    private void addPlacesMarkers() {
        placePoints.clear();

        addPlaceMarker(
                "Кофемания",
                "Москва, ул. Большая Никитская, 13",
                "Кофейня с десертами и завтраками.",
                new GeoPoint(55.757760, 37.601650)
        );

        addPlaceMarker(
                "РТУ МИРЭА",
                "Москва, ул. Стромынка, 20",
                "Учебный корпус РТУ МИРЭА.",
                new GeoPoint(55.794229, 37.700772)
        );

        addPlaceMarker(
                "Парк Горького",
                "Москва, ул. Крымский Вал, 9",
                "Популярное место для прогулок и отдыха.",
                new GeoPoint(55.729828, 37.601066)
        );

        addPlaceMarker(
                "Красная площадь",
                "Москва, Красная площадь",
                "Одна из главных достопримечательностей Москвы.",
                new GeoPoint(55.752023, 37.617499)
        );

        mapView.invalidate();
    }

    private void addPlaceMarker(
            String name,
            String address,
            String description,
            GeoPoint point
    ) {
        Marker marker = new Marker(mapView);

        marker.setPosition(point);
        marker.setTitle(name);
        marker.setSnippet(address + "\n" + description);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        marker.setOnMarkerClickListener((clickedMarker, clickedMapView) -> {
            clickedMarker.showInfoWindow();

            new AlertDialog.Builder(requireContext())
                    .setTitle(name)
                    .setMessage(
                            "Адрес: " + address + "\n\n" +
                                    "Описание: " + description
                    )
                    .setPositiveButton("OK", null)
                    .show();

            return true;
        });

        mapView.getOverlays().add(marker);
        placePoints.add(point);
    }

    private void addScaleBar() {
        Context context = requireContext().getApplicationContext();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);

        mapView.getOverlays().add(scaleBarOverlay);
    }

    private void showAllPlaces() {
        if (placePoints.isEmpty() || mapView == null) {
            return;
        }

        double north = placePoints.get(0).getLatitude();
        double south = placePoints.get(0).getLatitude();
        double east = placePoints.get(0).getLongitude();
        double west = placePoints.get(0).getLongitude();

        for (GeoPoint point : placePoints) {
            north = Math.max(north, point.getLatitude());
            south = Math.min(south, point.getLatitude());
            east = Math.max(east, point.getLongitude());
            west = Math.min(west, point.getLongitude());
        }

        BoundingBox boundingBox = new BoundingBox(
                north + 0.01,
                east + 0.01,
                south - 0.01,
                west - 0.01
        );

        mapView.zoomToBoundingBox(boundingBox, true);
    }

    @Override
    public void onResume() {
        super.onResume();

        Configuration.getInstance().load(
                requireContext().getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext().getApplicationContext())
        );

        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Configuration.getInstance().save(
                requireContext().getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext().getApplicationContext())
        );

        if (mapView != null) {
            mapView.onPause();
        }
    }
}