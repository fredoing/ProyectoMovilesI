package com.example.proyectomoviles.AgregarRestaurante;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyectomoviles.Objetos.Horario;
import com.example.proyectomoviles.R;
import com.example.proyectomoviles.Utils.AdaptadorListaHorarios;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgregarHorario extends AppCompatActivity {
    //Variables
    private static final List<String> dias = Arrays.asList("Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo");
    //Botones
    private Button btnAtras;
    private Button btnAgregar;
    private Button btnTerminar;
    //Spinner
    private Spinner spinnerDias;
    private Spinner spinnerHorasInicio;
    private Spinner spinnerMinutosInicio;
    private Spinner spinnerHorasFin;
    private Spinner spinnerMinutosFin;
    //Atributos
    private ArrayList<Horario> horarios;
    //Vistas
    private ListView listViewHorarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_horario);

        btnAtras = findViewById(R.id.btn_volverAgregarHorario);
        btnAgregar = findViewById(R.id.btn_NuevoHorario);
        btnTerminar = findViewById(R.id.btn_TerminarAgregarHorario);
        listViewHorarios = findViewById(R.id.listView_NuevosHorarios);
        spinnerDias = findViewById(R.id.spinner_dia);
        spinnerHorasInicio = findViewById(R.id.spinner_horaInicio);
        spinnerMinutosInicio = findViewById(R.id.spinner_minutoInicio);
        spinnerHorasFin = findViewById(R.id.spinner_horaFin);
        spinnerMinutosFin = findViewById(R.id.spinner_minutoFin);

        Intent intent = getIntent();
        horarios = intent.getParcelableArrayListExtra("Horarios");
        actualizarListaHorarios();

        llenarSpinnerHoras();
        llenarSpinnerMinutos();





        inicializarBotonAtras();
        inicializarBotonAgregar();
        inicializarBotonTerminar();

        registerForContextMenu(listViewHorarios);
    }

    private  void inicializarBotonAtras(){
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private  void inicializarBotonAgregar(){
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fechaInicio = spinnerHorasInicio.getSelectedItem()+":"+spinnerMinutosInicio.getSelectedItem();
                String fechafin = spinnerHorasFin.getSelectedItem()+":"+spinnerMinutosFin.getSelectedItem();
                String dia = String.valueOf(spinnerDias.getSelectedItem());



                Horario horario = new Horario(dia,fechaInicio,fechafin);

                if(horario.isHorarioValido()){
                    int i = 0;
                    boolean choqueHorarios = false;
                    while(i< horarios.size() && !choqueHorarios){
                        Horario anotherHorario = horarios.get(i);
                        choqueHorarios = horario.comprobarChoque(anotherHorario);
                        i++;
                    }
                    if(!choqueHorarios){
                        agregarHorario(horario);
                        actualizarListaHorarios();
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Choque de horarios",Toast.LENGTH_LONG).show();



                }
                else
                    Toast.makeText(getApplicationContext(),"Horario invalido",Toast.LENGTH_LONG).show();

            }
        });
    }


    private void inicializarBotonTerminar(){

        btnTerminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();

                intent.putParcelableArrayListExtra("Horarios",horarios);
                setResult(RESULT_OK,intent);
                finish();

            }
        });
    }

    private void llenarSpinnerHoras(){
        List<String> list = new ArrayList<String>();

        int i = 0;
        while(i<24){
            list.add(String.format("%02d",i));
            i++;
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHorasInicio.setAdapter(dataAdapter);
        spinnerHorasFin.setAdapter(dataAdapter);

    }

    private void llenarSpinnerMinutos(){
        List<String> list = new ArrayList<String>();

        int i = 0;
        while(i<60){
            list.add(String.format("%02d",i));
            i++;
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMinutosInicio.setAdapter(dataAdapter);
        spinnerMinutosFin.setAdapter(dataAdapter);
    }

    private void actualizarListaHorarios(){
        AdaptadorListaHorarios adaptadorListaHorarios = new AdaptadorListaHorarios(getApplicationContext(),horarios);
        listViewHorarios.setAdapter(adaptadorListaHorarios);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.listView_NuevosHorarios){
            getMenuInflater().inflate(R.menu.eliminar_menu,menu);
        }

    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.opcionEliminar:
                horarios.remove(info.position);
                actualizarListaHorarios();
                return true;
            default:
                return super.onContextItemSelected(item);
        }


    }

    private void agregarHorario(Horario horario){
       if(!horarios.isEmpty()){
           int i = 0;
           boolean horarioAgregado = false;
           while(i<horarios.size() && !horarioAgregado){

               if(dias.indexOf(horario.getDia()) < dias.indexOf(horarios.get(i).getDia())){
                   horarios.add(i,horario);
                   horarioAgregado = true;
               }
               else{
                   if(dias.indexOf(horario.getDia()) == dias.indexOf(horarios.get(i).getDia())){
                        if(horario.isHorarioAntes(horarios.get(i))){
                           horarios.add(i,horario);
                           horarioAgregado = true;
                       }

                   }

               }
            i++;
           }

           if(!horarioAgregado)
               horarios.add(horario);
       }
       else
           horarios.add(horario);

    }
}
