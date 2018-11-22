package com.example.edgar.tpdm_u3_ejercicio_1_edgar_efren_pozas_bogarin;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;

public class Main4Activity extends AppCompatActivity {

    private EditText no_mesa;
    private TextView txt_total;
    private Button buscar,pagado;
    private ListView lista;

    private ArrayList<Platillo2> platillos;
    private ArrayList<Bebida2> bebidas;
    private ArrayList<String> bebidas_lista,platillos_lista;

    private DatabaseReference db;
    private String id_seleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        db=FirebaseDatabase.getInstance().getReference();

        no_mesa=findViewById(R.id.num_mesa_cobrar);
        txt_total=findViewById(R.id.total);
        buscar=findViewById(R.id.buscar_cobrar);
        pagado=findViewById(R.id.pagado_cobrar);
        lista=findViewById(R.id.lista_cobrar);

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validar_campos(new EditText[]{no_mesa})){
                    mensaje("llene todos los campos");
                    return;
                }
                db.child("comanda").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot comandas) {
                        if(comandas.getChildrenCount()==0){
                            mensaje("No hay comandas");
                            return;
                        }
                        platillos=new ArrayList<>();
                        bebidas=new ArrayList<>();
                        for (final DataSnapshot ds:comandas.getChildren()){
                            ds.getRef().child("no_mesa").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.getValue().toString().equals(no_mesa.getText().toString()))
                                        return;
                                    ds.getRef().child("estatus").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(!dataSnapshot.getValue().toString().equals("N"))
                                                return;
                                            ds.getRef().child("platillos").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.getChildrenCount()==0)
                                                        return;
                                                    for (final DataSnapshot ds1:dataSnapshot.getChildren()){

                                                        Platillo2 p=ds1.getValue(Platillo2.class);
                                                        id_seleccionado=p.getId_comanda();
                                                        if(p!=null){
                                                            platillos.add(p);
                                                        }
                                                    }
                                                    actualizar_lista();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            ds.getRef().child("bebidas").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.getChildrenCount()==0)
                                                        return;
                                                    for (final DataSnapshot ds1:dataSnapshot.getChildren()){
                                                        Bebida2 p=ds1.getValue(Bebida2.class);
                                                        id_seleccionado=p.getId_comanda();
                                                        if(p!=null){
                                                            bebidas.add(p);
                                                        }
                                                    }
                                                    actualizar_lista();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

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
        });
        pagado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validar_campos(new EditText[]{no_mesa})){
                    mensaje("llene todos los campos");
                    return;
                }
                if(id_seleccionado==null)
                    return;
                db.child("comanda").child(id_seleccionado).child("estatus").setValue("Y");
                id_seleccionado=null;
                mensaje("Estado cambiado");
            }
        });
    }

    private void actualizar_lista(){

        ArrayList<String> txts=new ArrayList<String>();
        float total=0;
        for (Platillo2 p:platillos){
            txts.add(p.getNombre()+"  :  "+p.getPrecio()+"    "+p.getCantidad());
            total+=Float.parseFloat(p.getTotal());
        }

        for (Bebida2 p:bebidas){
            txts.add(p.getNombre()+"  :  "+p.getPrecio()+"    "+p.getCantidad());
            total+=Float.parseFloat(p.getTotal());
        }
        txt_total.setText("Total:       "+total+"");

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
