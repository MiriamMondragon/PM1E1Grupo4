package com.example.pm1e1grupo4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PETICION_ACCESO_PERMISOS = 100;

    ImageView imgFoto;
    EditText txtNombreContacto, txtTelefonoContacto;
    TextView txtLatitud, txtLongitud;

    Contacto contacto = new Contacto();
    byte[] byteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgFoto = (ImageView) findViewById(R.id.imgFoto);
        txtNombreContacto = (EditText) findViewById(R.id.txtNombreContacto);
        txtTelefonoContacto = (EditText) findViewById(R.id.txtTelefonoContacto);
        txtLatitud = (TextView) findViewById(R.id.txtLatitud);
        txtLongitud = (TextView) findViewById(R.id.txtLongitud);
        Button btnTomarFoto = (Button) findViewById(R.id.btnTomarFoto);
        Button btnSalvar = (Button) findViewById(R.id.btnSalvar);
        Button btnListarContactos = (Button) findViewById(R.id.btnListarContactos);

        //BOTON TOMAR FOTO
        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos();
            }
        });

        //BOTON SALVAR CONTACTO
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos();
            }
        });


        //BOTON LISTAR CONTACTOS
        btnListarContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getApplicationContext(), ActivityListContactos.class);
                //startActivity(intent);
            }
        });

    }

    //FUNCIONES REALCIONADAS A LA TOMA DE LA FOTOGRAFIA
    private void permisos() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PETICION_ACCESO_PERMISOS);
        } else {
            tomarFoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PETICION_ACCESO_PERMISOS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tomarFoto();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Se necesitan permisos de acceso", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            getBytes(data);
        }
    }

    private void getBytes(Intent data) {
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        imgFoto.setImageBitmap(photo);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();

        //SETEO DE DATOS EN EL OBJETO (FOTO BASE64 Y NOMBRE DEL ARCHIVO)
        String encode = Base64.encodeToString(byteArray, Base64.DEFAULT);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        contacto.setFoto(encode);
        contacto.setArchivo(currentDateTimeString);
        obtenerLocalizacion();
    }

    private void tomarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void obtenerLocalizacion() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        encontrarUbicacion(getApplicationContext(), lm);
    }

    public void encontrarUbicacion(Context contexto, LocationManager locationManager) {
        String location_context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) contexto.getSystemService(location_context);
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(provider, 1000, 0,
                    new LocationListener() {

                        public void onLocationChanged(Location location) {
                            String longitud = String.valueOf(location.getLongitude());
                            String latitud = String.valueOf(location.getLatitude());
                            contacto.setLongitud(longitud);
                            contacto.setLatitud(latitud);
                            txtLongitud.setText(longitud);
                            txtLatitud.setText(latitud);
                        }

                        public void onProviderDisabled(String provider) {
                        }

                        public void onProviderEnabled(String provider) {
                        }

                        public void onStatusChanged(String provider, int status,
                                                    Bundle extras) {
                        }
                    });
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                String longitud = String.valueOf(location.getLongitude());
                String latitud = String.valueOf(location.getLatitude());
                contacto.setLongitud(longitud);
                contacto.setLatitud(latitud);
                txtLongitud.setText(longitud);
                txtLatitud.setText(latitud);
            }
        }
    }

    //FUNCIONES RELACIONADAS AL GUARDADO DEL CONTACTO Y MENSAJES DE ERROR

    private void ClearScreen() {
        txtNombreContacto.setText("");
        txtTelefonoContacto.setText("");
        txtLatitud.setText("");
        txtLongitud.setText("");
    }

    private void mostrarDialogoVacios() {
        new AlertDialog.Builder(this)
                .setTitle("Alerta de Vacíos")
                .setMessage("No puede dejar ningún campo vacío")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private void mostrarDialogoNumeros() {
        new AlertDialog.Builder(this)
                .setTitle("Alerta de Números")
                .setMessage("No puede ingresar números en el campo de Nombre")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }
}