package es.studium.pasitosapp;


import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, ActivityCompat.OnRequestPermissionsResultCallback

{

    private GoogleMap mapa;
    private final LatLng grupoStudium = new LatLng(37.39680717426016, -5.972389626774901);
    private double latitud;
    private double longitud;
    private LocationManager locManager;
    private Location loc;
    TextView txt_latitud;
    TextView txt_longitud;
    TextView txt_textolatitud;
    TextView txt_textolongitud;
    DatabaseHelper miBD;


    @Override
        protected void onCreate (Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);


            txt_textolatitud = findViewById(R.id.txt_textolatitud);
            txt_textolongitud = findViewById(R.id.txt_textolongitud);
            txt_latitud = findViewById(R.id.txt_latitud);
            txt_longitud = findViewById(R.id.txt_longitud);

            miBD = new DatabaseHelper(this);

            //guardarDatos();

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
            mapFragment.getMapAsync(this);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new
                        String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            }
            else
            {
                locationStart();
            }
        }
    private void locationStart()
    {
        LocationManager mlocManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled)
        {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    private void setLocation(Location loc)
    {
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0)
        {
            try
            {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

                if (!list.isEmpty())
                {
                    txt_latitud.setText("" +loc.getLatitude());
                    txt_longitud.setText("" +loc.getLongitude() );
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    public class Localizacion implements LocationListener
    {
        MainActivity mainActivity;
        public MainActivity getMainActivity()
        {
            return mainActivity;
        }
        public void setMainActivity(MainActivity mainActivity)
        {
            this.mainActivity = mainActivity;
        }
        @Override
        public void onLocationChanged(Location loc)
        {
            // Este método se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la detección de un cambio de ubicación

            latitud = loc.getLatitude();
            longitud = loc.getLongitude();
            this.mainActivity.setLocation(loc);
        }
        @Override
        public void onProviderDisabled(String provider)
        {
            // Este método se ejecuta cuando el GPS es desactivado
            //txtLatitudLongitud.setText("GPS Desactivado");
        }
        @Override
        public void onProviderEnabled(String provider)
        {
            // Este método se ejecuta cuando el GPS es activado
            //txtLatitudLongitud.setText("GPS Activado");
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            switch (status)
            {
                case 0:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case 1:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
                case 2:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
            }
        }
    }

        public void onMapReady (GoogleMap googleMap)
        {
            mapa = googleMap;
            mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            mapa.getUiSettings().setZoomControlsEnabled(false);
            mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(grupoStudium, 15));

           // mapa.addMarker(new MarkerOptions()
           //         .position(grupoStudium)
           //        .title("Grupo Studium")
           //         .snippet("Instituto Técnico Superior de Informática de Sevilla S.L")
           //         .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_compass))
           //         .anchor(0.5f,0.5f));
            mapa.setOnMapClickListener(this);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            {
            mapa.setMyLocationEnabled(true);
            mapa.getUiSettings().setCompassEnabled(true);
            }
        }
            public void moveCamera(View view)
            {
                mapa.moveCamera(CameraUpdateFactory.newLatLng(grupoStudium));
            }
            public void animateCamera(View view)
            {
                mapa.animateCamera(CameraUpdateFactory.newLatLng(grupoStudium));
            }

            public void addMarker(View view)
            {
                BatteryManager bm = (BatteryManager) this.getSystemService(BATTERY_SERVICE);
                int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

                //mapa.addMarker(new MarkerOptions().position(mapa.getCameraPosition().target));
                String mandarlatitud = txt_latitud.getText().toString();
                String mandarlongitud = txt_longitud.getText().toString();
                String mandarbateria = "" + batLevel;
                if (txt_latitud.length() != 0)
                {
                    agregar(mandarlatitud, mandarlongitud, mandarbateria);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Ingresa algo", Toast.LENGTH_SHORT).show();
                }
            }

            public void agregar(String mandarlatitud, String mandarlongitud, String mandarbateria)
            {
                boolean insertarData = miBD.addData(mandarlatitud, mandarlongitud, mandarbateria);
                if (insertarData == true)
                {
                    Toast.makeText(this, "Datos insertados correctamente", Toast.LENGTH_SHORT).show();
                } else
                    {
                    Toast.makeText(this, "Algo ha fallado...", Toast.LENGTH_SHORT).show();
                }
            }
            public void onMapClick(LatLng puntoPulsado)
            {

                mapa.addMarker(new MarkerOptions().position(puntoPulsado).icon(BitmapDescriptorFactory.fromResource(R.drawable.sandia)));

                latitud = puntoPulsado.latitude;
                longitud = puntoPulsado.longitude;
                LatLng ubicacion = new LatLng(latitud, longitud);

                BatteryManager bm = (BatteryManager) this.getSystemService(BATTERY_SERVICE);
                int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

                Toast.makeText(this, "Latitud: " + ubicacion.latitude + " " + "\nLongitud: " + ubicacion.longitude + " " +"\nBatería: " + batLevel, Toast.LENGTH_LONG).show();

            }

            public void guardarDatos()
            {
                BatteryManager bm = (BatteryManager) this.getSystemService(BATTERY_SERVICE);
                int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

                final Handler handler = new Handler();
                Timer timer = new Timer();
                TimerTask doAsynchronousTask = new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        handler.post(new Runnable()
                        {
                            public void run()
                            {
                                try
                                {
                                    String mandarlatitud = txt_latitud.getText().toString();
                                    String mandarlongitud = txt_longitud.getText().toString();
                                    String mandarbateria = "" + batLevel;
                                    if (txt_latitud.length() != 0)
                                    {
                                        agregar(mandarlatitud, mandarlongitud, mandarbateria);
                                    }
                                    else
                                    {
                                        Toast.makeText(MainActivity.this, "Ingresa algo", Toast.LENGTH_SHORT).show();
                                    }

                                }
                                catch (Exception e)
                                {

                                }
                            }
                        });
                    }
                };
                timer.schedule(doAsynchronousTask, 0, 10000);
            }
        }

