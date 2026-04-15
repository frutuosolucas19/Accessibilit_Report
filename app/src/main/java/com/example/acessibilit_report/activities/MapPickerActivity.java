package com.example.acessibilit_report.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.acessibilit_report.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapPickerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Marker marcadorAtual;
    private Double latitudeSelecionada;
    private Double longitudeSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.map_picker_titulo));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button btnConfirmar = findViewById(R.id.btnConfirmarLocal);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        btnConfirmar.setOnClickListener(v -> {
            if (latitudeSelecionada == null || longitudeSelecionada == null) {
                Toast.makeText(this, "Toque no mapa para marcar um local", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent result = new Intent();
            result.putExtra("latitude", latitudeSelecionada);
            result.putExtra("longitude", longitudeSelecionada);
            setResult(RESULT_OK, result);
            finish();
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        LatLng brasil = new LatLng(-15.7801, -47.9292);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brasil, 4f));

        googleMap.setOnMapClickListener(latLng -> {
            if (marcadorAtual != null) marcadorAtual.remove();
            marcadorAtual = googleMap.addMarker(
                    new MarkerOptions().position(latLng).title("Local da denúncia")
            );
            latitudeSelecionada = latLng.latitude;
            longitudeSelecionada = latLng.longitude;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
