package com.example.pm1e2grupo4;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ActivityActualizar extends AppCompatActivity {

    private String id, nombre, telefono, latitud, longitud, foto, archivo;
    EditText txtNombre, txtTelefono;
    ImageView imgFoto2;
    Contacto contactoBuscado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar);

        Button btnVolver2 = (Button) findViewById(R.id.btnVolver2);
        txtNombre = (EditText) findViewById(R.id.txtNombreContacto2);
        txtTelefono = (EditText) findViewById(R.id.txtTelefonoContacto2);
        imgFoto2 = (ImageView) findViewById(R.id.imgFoto2);


        Intent intent = getIntent();
        id = intent.getStringExtra("idCont");
        buscarContacto(id);




        btnVolver2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pantallaVolver = new Intent(getApplicationContext(), ActivityListContactos.class);
                startActivity(pantallaVolver);
            }
        });
    }

    private void buscarContacto(String id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = RestApiMethods.ApiGetID + id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray ContactoArray = obj.getJSONArray("Contactos");

                    for (int i = 0; i < ContactoArray.length(); i++) {
                        //getting the json object of the particular index inside the array
                        JSONObject contactoObject = ContactoArray.getJSONObject(i);

                        //creating a hero object and giving them the values from json object
                        contactoBuscado = new Contacto(contactoObject.getString("ID"),
                                contactoObject.getString("NOMBRE"),
                                contactoObject.getString("TELEFONO"),
                                contactoObject.getString("LATITUD"),
                                contactoObject.getString("LONGITUD"),
                                contactoObject.getString("FOTO"),
                                contactoObject.getString("ARCHIVO"));

                    }

                    /*String[] obtencion = contactoBuscado.getFoto().split("\\[");
                    String[] obtencionBytes = obtencion[1].split("]");
                    byte[] foto = Base64.decode(obtencionBytes[0].getBytes(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
                    imgFoto2.setImageBitmap(bitmap);*/

                    txtNombre.setText(contactoBuscado.getNombre());
                    txtTelefono.setText(contactoBuscado.getTelefono());

                } catch (JSONException ex) {
                    Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error en Response", "onResponse: " +  error.getMessage().toString() );
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}