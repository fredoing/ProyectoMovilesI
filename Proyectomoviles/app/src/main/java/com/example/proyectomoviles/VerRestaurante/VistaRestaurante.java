package com.example.proyectomoviles.VerRestaurante;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectomoviles.Objetos.Restaurante;
import com.example.proyectomoviles.Objetos.Usuario;
import com.example.proyectomoviles.R;
import com.example.proyectomoviles.Utils.AdaptadorListaHorarios;
import com.example.proyectomoviles.Utils.GridImageAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class VistaRestaurante extends AppCompatActivity implements OnMapReadyCallback {
    //Variables
    private Restaurante restaurante;
    private Usuario usuario;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    //Views
    private ImageView imagePrincipal;
    private TextView txtNombreRestaurante;

    private TextView txtTipoComidaRestaurante;
    private TextView txtPrecioRestaurante;
    private TextView txtCalificacionRestaurante;
    private ListView listViewHorarios;
    private ListView listViewComentarios;
    private MapView mapViewUbicacion;
    //Buttons
    private Button btnBack;
    private Button btnCalificacion;
    private Button btnAgregarImagen;
    private Button btnAgregarComentario;
    //Spinner
    private Spinner spinnerCalificacion;
    //Request Codes
    private final int codigoImagen = 9002;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_restaurante);

        imagePrincipal = findViewById(R.id.img_vistaRestaurante_restaurante);
        txtNombreRestaurante = findViewById(R.id.txt_vistaRestaurante_NombreRestaurante);
        txtTipoComidaRestaurante = findViewById(R.id.txt_vistaRestaurante_TipoComida);
        txtPrecioRestaurante = findViewById(R.id.txt_vistaRestaurante_Precio);
        txtCalificacionRestaurante = findViewById(R.id.txt_vistaRestaurante_Calificacion);
        listViewHorarios = findViewById(R.id.listView_MostrarHorarios);
        mapViewUbicacion = findViewById(R.id.map_verRestaurante);
        btnBack = findViewById(R.id.btn_vistaRestaurante_back);
        btnCalificacion = findViewById(R.id.btn_AgregarCalificacion);
        btnAgregarImagen = findViewById(R.id.btn_VistaRestaurante_AgregarImagen);
        btnAgregarComentario = findViewById(R.id.btn_VistaRestaurante_AgregarComentario);
        spinnerCalificacion = findViewById(R.id.spinner_Calificar);
        listViewHorarios = findViewById(R.id.listView_MostrarHorarios);
        listViewComentarios = findViewById(R.id.listView_MostrarComentarios);


        Intent intent = getIntent();
        restaurante = intent.getParcelableExtra("Restaurante");
        usuario = intent.getParcelableExtra("Usuario");

        mostrarDatosRestaurante();
        inicializarBtnBack();
        inicializarBtnCalificar();
        inicializarBtnComentario();
        inicializarBtnImagen();
        inicializarListaHorarios();
        llenarSpinnerCalifcaciones();
        iniciarlizarMapa(savedInstanceState);




    }

    private void mostrarDatosRestaurante(){
        if(!restaurante.getImagenesURL().isEmpty()){
            imagePrincipal.setImageURI(Uri.parse(restaurante.getImagenesURL().get(0)));
        }
        txtNombreRestaurante.setText(restaurante.getNombre());
        txtTipoComidaRestaurante.setText(restaurante.getTipoDeComida());
        txtPrecioRestaurante.setText(restaurante.getPrecio());
        txtCalificacionRestaurante.setText(restaurante.getCalificacion());


    }
    private void inicializarBtnBack(){
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void inicializarBtnImagen(){
        btnAgregarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,codigoImagen);
            }
        });
    }

    private void inicializarBtnCalificar(){
        btnCalificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int califcacion = Integer.valueOf(String.valueOf(spinnerCalificacion.getSelectedItem()));
                restaurante.agregarCalificacion(califcacion,usuario.getCorreo());
                txtCalificacionRestaurante.setText(restaurante.getCalificacion());
            }
        });
    }

    private void inicializarBtnComentario(){
        btnAgregarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void inicializarListaHorarios(){
        AdaptadorListaHorarios adaptadorListaHorarios = new AdaptadorListaHorarios(getApplicationContext(),restaurante.getHorarios());
        ViewGroup.LayoutParams layoutParams = listViewHorarios.getLayoutParams();
        layoutParams.height = 100 * restaurante.getHorarios().size();
        listViewHorarios.setLayoutParams(layoutParams);
        listViewHorarios.setAdapter(adaptadorListaHorarios);
    }

    private void llenarSpinnerCalifcaciones(){
        List<String> list = new ArrayList<String>();

        int i = 0;
        while(i<6){
            list.add(String.valueOf(i));
            i++;
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCalificacion.setAdapter(dataAdapter);
    }


    private  void iniciarlizarMapa(Bundle savedInstanceState){
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapViewUbicacion.onCreate(mapViewBundle);
        mapViewUbicacion.getMapAsync(this);
    }

    private void actualizarListaImagenes(){

        if(!restaurante.getImagenesURL().isEmpty()){
            HorizontalScrollView horizontalScrollView = findViewById(R.id.horizontalView);
            GridView gridView = findViewById(R.id.gridView_VistaRestaurante);
            GridImageAdapter adapter = new GridImageAdapter(getApplicationContext(),R.layout.layout_grid_imageview,"",restaurante.getImagenesURL());

            DisplayMetrics dm = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(dm);
            float density = dm.density;
            int imageWidth = 200;
            int gridViewWidth = (int) (imageWidth * restaurante.getImagenesURL().size() * density);
            int itemWidth = (int) (imageWidth * density);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gridViewWidth, LinearLayout.LayoutParams.MATCH_PARENT);

            gridView.setLayoutParams(params);
            gridView.setNumColumns(restaurante.getImagenesURL().size());
            gridView.setHorizontalSpacing(2);
            gridView.setStretchMode(GridView.STRETCH_SPACING);
            gridView.setColumnWidth(itemWidth);
            gridView.setAdapter(adapter);
        }
    }

    private void actualizarComentarios(){

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapViewUbicacion.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapViewUbicacion.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapViewUbicacion.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapViewUbicacion.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng latLng = restaurante.getUbicacion();
        map.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude,latLng.longitude)).title(restaurante.getNombre()));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
    }

    @Override
    public void onPause() {
        mapViewUbicacion.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapViewUbicacion.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapViewUbicacion.onLowMemory();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == codigoImagen){
            Uri imageUri = data.getData();
            restaurante.agregarImagen(imageUri.toString());
            actualizarListaImagenes();
        }
    }
}