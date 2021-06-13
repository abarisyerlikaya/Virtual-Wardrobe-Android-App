package tr.edu.yildiz.sanaldolabim_18011063.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import tr.edu.yildiz.sanaldolabim_18011063.R;

import static android.widget.Toast.LENGTH_SHORT;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LatLng selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ok, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (selectedLocation == null)
            Toast.makeText(getApplicationContext(), "Lütfen bir lokasyon seçiniz!", LENGTH_SHORT).show();

        else {
            Intent intent = new Intent();
            intent.putExtra("latitude", selectedLocation.latitude);
            intent.putExtra("longitude", selectedLocation.longitude);
            setResult(RESULT_OK, intent);
            finish();
        }

        return true;
    }

    private void initialize() {
        setContentView(R.layout.activity_map);
        selectedLocation = null;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(latLng -> handleMapClick(latLng));

        // Default: Istanbul
        LatLng istanbul = new LatLng(41.015137, 28.979530);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(istanbul));
    }

    private void handleMapClick(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Konum"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        selectedLocation = latLng;
    }

}
