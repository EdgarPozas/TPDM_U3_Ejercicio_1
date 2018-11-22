package com.example.edgar.tpdm_u3_ejercicio_1_edgar_efren_pozas_bogarin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Main3Activity extends AppCompatActivity {

    private EditText no_mesa,cantidad_platillo,cantidad_bebida;
    private Spinner sp_platillos,sp_bebidas;
    private Button agregar_platillo,agregar_bebida,agregar_todo;
    private ListView lista;
    private ArrayList<Platillo> platillos;
    private ArrayList<Bebida> bebidas;

    private ArrayList<Platillo> platillos_selec;
    private ArrayList<Bebida> bebidas_selec;
    private ArrayList<String> bebidas_lista,platillos_lista;

    private DatabaseReference db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        db=FirebaseDatabase.getInstance().getReference();

        no_mesa=findViewById(R.id.no_mesa);
        cantidad_platillo=findViewById(R.id.cantidad_platillo);
        cantidad_bebida=findViewById(R.id.cantidad_bebidas);
        sp_platillos=findViewById(R.id.spinner_platillo);
        sp_bebidas=findViewById(R.id.spinner_bebidas);
        agregar_platillo=findViewById(R.id.agregar_platillo);
        agregar_bebida=findViewById(R.id.agregar_bebida);
        agregar_todo=findViewById(R.id.guardar_platillo);
        lista=findViewById(R.id.lista_levantar_platillos);

        platillos_selec=new ArrayList<>();
        bebidas_selec=new ArrayList<>();
        bebidas_lista=new ArrayList<>();
        platillos_lista=new ArrayList<>();

        agregar_platillo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validar_campos(new EditText[]{cantidad_platillo})||sp_platillos.getSelectedItem() ==null ){
                    mensaje("llene todos los campos");
                    return;
                }
                int cantidad=Integer.parseInt(cantidad_platillo.getText().toString());
                int index=sp_platillos.getSelectedItemPosition();
                for (int i=0;i<cantidad;i++){
                    Platillo p=platillos.get(index);
                    platillos_selec.add(p);
                }
                actualizar_lista();
            }
        });
        agregar_bebida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validar_campos(new EditText[]{cantidad_bebida})||sp_bebidas.getSelectedItem() ==null ){
                    mensaje("llene todos los campos");
                    return;
                }
                int cantidad=Integer.parseInt(cantidad_bebida.getText().toString());
                int index=sp_bebidas.getSelectedItemPosition();
                for (int i=0;i<cantidad;i++){
                    Bebida p=bebidas.get(index);
                    bebidas_selec.add(p);
                }
                actualizar_lista();
            }
        });
        agregar_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validar_campos(new EditText[]{no_mesa})||(platillos_selec.size()==0&&bebidas_selec.size()==0)){
                    mensaje("llene todos los campos");
                    return;
                }
                String id=Miscelaneo.generar_cadena(10);

                Map<String,Object> datos=new HashMap<String,Object>();
                datos.put("id_comanda",id);
                datos.put("fecha",new Date().toString());
                datos.put("estatus","N");
                datos.put("total",0);
                datos.put("no_mesa",no_mesa.getText().toString());

                db.child("comanda").child(id).setValue(datos);

                float total=0;
                Map<String,Object> datos2=new HashMap<String,Object>();
                for (String d:bebidas_lista) {
                    String id_ = d.split(",")[0];
                    String nombre_ = d.split(",")[1];
                    String precio_=d.split(",")[2];
                    String cantidad_=d.split(",")[3];
                    String total_=d.split(",")[4];
                    datos2.put("id_comanda",id);
                    datos2.put("id",id_);
                    datos2.put("nombre",nombre_);
                    datos2.put("precio",precio_);
                    datos2.put("cantidad",cantidad_);
                    datos2.put("total",total_);
                    total+=Float.parseFloat(total_);
                    db.child("comanda").child(id).child("bebidas").child(id_).setValue(datos2);
                }
                datos2=new HashMap<String,Object>();
                for (String d:platillos_lista) {
                    String id_ = d.split(",")[0];
                    String nombre_ = d.split(",")[1];
                    String precio_=d.split(",")[2];
                    String cantidad_=d.split(",")[3];
                    String total_=d.split(",")[4];
                    datos2.put("id_comanda",id);
                    datos2.put("id",id_);
                    datos2.put("nombre",nombre_);
                    datos2.put("precio",precio_);
                    datos2.put("cantidad",cantidad_);
                    datos2.put("total",total_);
                    total+=Float.parseFloat(total_);
                    db.child("comanda").child(id).child("platillos").child(id_).setValue(datos2);
                }
                db.child("comanda").child(id).child("total").setValue(total);

                mensaje("Guardado correctamente");
            }
        });

        db.child("platillo").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot datos) {
                if(datos==null){
                    mensaje("No hay platillos");
                    return;
                }
                platillos=new ArrayList<Platillo>();
                for (final DataSnapshot dato:datos.getChildren()) {
                    dato.getRef().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot atributos) {
                            Platillo b=atributos.getValue(Platillo.class);
                            if(b==null) {
                                return;
                            }
                            platillos.add(b);
                            cargar_combos();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        db.child("bebidas").addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot datos) {
                if(datos==null){
                    mensaje("No hay bebidas");
                    return;
                }
                bebidas=new ArrayList<Bebida>();
                for (final DataSnapshot dato:datos.getChildren()) {
                    dato.getRef().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot atributos) {
                            Bebida b=atributos.getValue(Bebida.class);
                            if(b==null) {
                                return;
                            }
                            bebidas.add(b);
                            cargar_combos();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void cargar_combos(){
        ArrayList<String> txts=new ArrayList<String>();
        for (Platillo b:platillos){
            txts.add(b.getNombre());
        }
        ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,txts);
        sp_platillos.setAdapter(adapter);

        txts=new ArrayList<String>();
        for (Bebida b:bebidas){
            txts.add(b.getNombre());
        }
        adapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,txts);
        sp_bebidas.setAdapter(adapter);
    }

    private void actualizar_lista(){
        ArrayList<String> txts=new ArrayList<String>();
        ArrayList<String> ids=new ArrayList<String>();

        for (Platillo p:platillos_selec){
            boolean existe=false;
            for (String id:ids){
                if(p.getId()==id) {
                    existe = true;
                    break;
                }
            }
            if(existe)
                continue;

            int cantidad=0;
            for (Platillo p2:platillos_selec) {
                if(p.getId().equals(p2.getId()))
                    cantidad++;
            }
            float total=Float.parseFloat(p.getPrecio())*cantidad;
            txts.add(p.getNombre()+"    "+cantidad);
            ids.add(p.getId());
            platillos_lista.add(p.getId()+","+p.getNombre()+","+p.getPrecio()+","+cantidad+","+total);
        }

        for (Bebida p:bebidas_selec){
            boolean existe=false;
            for (String id:ids){
                if(p.getId()==id) {
                    existe = true;
                    break;
                }
            }
            if(existe)
                continue;
            int cantidad=0;
            for (Bebida p2:bebidas_selec) {
                if(p.getId().equals(p2.getId()))
                    cantidad++;
            }
            float total=Float.parseFloat(p.getPrecio())*cantidad;
            txts.add(p.getNombre()+"    "+cantidad);
            ids.add(p.getId());
            bebidas_lista.add(p.getId()+","+p.getNombre()+","+p.getPrecio()+","+cantidad+","+total);
        }

        ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,txts);
        lista.setAdapter(adapter);
    }
    private void mensaje(String mensaje){
        Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show();
    }

    private boolean validar_campos(EditText[] txts){
        for (EditText e:txts){
            if(e.getText().toString().isEmpty())
                return false;
        }
        return true;
    }
}
