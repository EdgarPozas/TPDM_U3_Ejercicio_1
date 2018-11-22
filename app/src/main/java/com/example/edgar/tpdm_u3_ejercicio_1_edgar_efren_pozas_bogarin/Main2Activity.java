package com.example.edgar.tpdm_u3_ejercicio_1_edgar_efren_pozas_bogarin;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {

    private boolean platillo;
    private EditText nombre,precio;
    private Button agregar,borrar;
    private ListView lista;
    private ArrayList<Platillo> platillos;
    private ArrayList<Bebida> bebidas;
    private Platillo pla_selec;
    private Bebida bebida_selec;

    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        db=FirebaseDatabase.getInstance().getReference();

        platillo=getIntent().getExtras().getBoolean("platillo");

        platillos=new ArrayList<Platillo>();
        bebidas=new ArrayList<Bebida>();

        nombre=findViewById(R.id.nombre);
        precio=findViewById(R.id.precio);
        agregar=findViewById(R.id.agregar);
        borrar=findViewById(R.id.borrar);
        lista=findViewById(R.id.lista);

        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validar_campos(new EditText[]{nombre,precio})){
                    mensaje("llene todos los campos");
                    return;
                }
                if(pla_selec==null&&platillo){
                    mensaje("Seleccione un elemento para borrarlo");
                    return;
                }
                if(bebida_selec==null&&!platillo){
                    mensaje("Seleccione un elemento para borrarlo");
                    return;
                }
                db.child(platillo?"platillo":"bebidas").child(platillo?pla_selec.getId():bebida_selec.getId()).setValue(null);
                nombre.setText("");
                precio.setText("");
                pla_selec=null;
                bebida_selec=null;
                mensaje("Borrado correctamente");
            }
        });

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validar_campos(new EditText[]{nombre,precio})){
                    mensaje("llene todos los campos");
                    return;
                }
                Map<String,Object> datos=new HashMap<>();
                String id=Miscelaneo.generar_cadena(10);
                datos.put("id",id);
                datos.put("nombre",nombre.getText().toString());
                datos.put("precio",precio.getText().toString());
                db.child(platillo?"platillo":"bebidas").child(id).setValue(datos);
                mensaje("Guardado correctamente");
                nombre.setText("");
                precio.setText("");
            }
        });
        db.child(platillo?"platillo":"bebidas").addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot datos) {
                if(datos==null){
                    mensaje("No hay datos");
                    return;
                }
                platillos=new ArrayList<Platillo>();
                bebidas=new ArrayList<Bebida>();
                for (final DataSnapshot dato:datos.getChildren()) {
                    db.child(platillo?"platillo":"bebidas").child(dato.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot atributos) {
                            if(platillo) {
                                Platillo p = atributos.getValue(Platillo.class);
                                if (p == null) {
                                    actualizar_lista();
                                    return;
                                }
                                platillos.add(p);
                            }else{
                                Bebida b=atributos.getValue(Bebida.class);
                                if(b==null) {
                                    actualizar_lista();
                                    return;
                                }
                                bebidas.add(b);
                            }
                            actualizar_lista();
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
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(platillo) {
                    if (platillos.size() == 0)
                        return;
                    pla_selec = platillos.get(position);
                    nombre.setText(pla_selec.getNombre());
                    precio.setText(pla_selec.getPrecio());
                }else {
                    if (bebidas.size() == 0)
                        return;
                    bebida_selec = bebidas.get(position);
                    nombre.setText(bebida_selec.getNombre());
                    precio.setText(bebida_selec.getPrecio());
                }
            }
        });
    }

    private void actualizar_lista(){

        ArrayList<String> txts=new ArrayList<String>();
        if(platillo) {
            for (Platillo p : platillos) {
                txts.add(p.getNombre());
            }
        }else {
            for (Bebida p : bebidas) {
                txts.add(p.getNombre());
            }
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
