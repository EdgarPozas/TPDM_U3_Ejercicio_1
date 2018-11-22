package com.example.edgar.tpdm_u3_ejercicio_1_edgar_efren_pozas_bogarin;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView lista;
    private DatabaseReference db;

    private ArrayList<Bebida> bebidas;
    private ArrayList<Platillo> platillos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db=FirebaseDatabase.getInstance().getReference();

        lista=findViewById(R.id.restaurante_lista);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Intent intent=new Intent(MainActivity.this,Main3Activity.class);
                    startActivity(intent);
                }else if(position==1){
                    Intent intent=new Intent(MainActivity.this,Main4Activity.class);
                    startActivity(intent);
                }else if(position==2){
                    exportar_csv();
                }else if(position==3){
                    System.exit(0);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.platillo){
            Intent intent=new Intent(MainActivity.this,Main2Activity.class);
            intent.putExtra("platillo",true);
            startActivity(intent);
        }else if(item.getItemId()==R.id.bebidas){
            Intent intent=new Intent(MainActivity.this,Main2Activity.class);
            intent.putExtra("platillo",false);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportar_csv() {
        db.child("bebidas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()==0)
                    return;
                bebidas=new ArrayList<>();
                for (final DataSnapshot ds:dataSnapshot.getChildren()){
                    Bebida b=ds.getValue(Bebida.class);
                    if(b==null)
                        return;
                    bebidas.add(b);
                }
                escribir("bebidas");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        db.child("platillo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()==0)
                    return;
                platillos=new ArrayList<>();
                for (final DataSnapshot ds:dataSnapshot.getChildren()){
                    Platillo b=ds.getValue(Platillo.class);
                    if(b==null)
                        return;
                    platillos.add(b);
                }
                escribir("platillo");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void escribir(String tipo){
        try {
            FileOutputStream outputStream = openFileOutput(tipo+".csv", Context.MODE_PRIVATE);
            String cad="";
            if(tipo.equals("platillo")){
                for (Platillo p:platillos){
                    cad+=p.getId()+","+p.getNombre()+","+p.getPrecio()+",";
                }
            }else {
                for (Bebida p:bebidas){
                    cad+=p.getId()+","+p.getNombre()+","+p.getPrecio()+",";
                }
            }
            outputStream.write(cad.getBytes());
            outputStream.close();
            Toast.makeText(this,"Archivo exportado",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
