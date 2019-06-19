package com.e.firebasecrud;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.e.firebasecrud.model.Producto;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private List<Producto> listproduct = new ArrayList<Producto>();
    ArrayAdapter<Producto> arrayAdapterProducto;

    EditText nombP, descriP, precioP;
    ListView ListProdu;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Producto productoSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nombP = findViewById(R.id.txtNombre);
        descriP = findViewById(R.id.txtDescripcion);
        precioP = findViewById(R.id.txtPrecio);

        ListProdu = findViewById(R.id.ListProductos);
        inicializarFirebase();
        listarDatos();


        ListProdu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                productoSelected = (Producto) parent.getItemAtPosition(position);
                nombP.setText(productoSelected.getNombre());
                descriP.setText(productoSelected.getDescripcion());
                precioP.setText(productoSelected.getPrecio());

            }
        });


    }

    private void listarDatos() {

        databaseReference.child("Producto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listproduct.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Producto p = objSnapshot.getValue(Producto.class);
                    listproduct.add(p);

                    arrayAdapterProducto = new ArrayAdapter<Producto>(MainActivity.this,android.R.layout.simple_list_item_1,listproduct);
                    ListProdu.setAdapter(arrayAdapterProducto);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item){
        //switch
        String nombre = nombP.getText().toString();
        String descripcion = descriP.getText().toString();
        String precio = precioP.getText().toString();
        switch (item.getItemId()){
            case R.id.ic_add:{
                if (nombre.equals("") || descripcion.equals("") || precio.equals("")){
                    validacion();
                }else{
                    Producto p = new Producto();
                    p.setId(UUID.randomUUID().toString());
                    p.setNombre(nombre);
                    p.setDescripcion(descripcion);
                    p.setPrecio(precio);
                    databaseReference.child("Producto").child(p.getId()).setValue(p);
                    Toast.makeText(this,"Agregado", Toast.LENGTH_LONG).show();
                    limpiarCaja();

                }
                break;

            }
            case R.id.ic_save:{
                Producto p = new Producto();
                p.setId(productoSelected.getId());
                p.setNombre(nombP.getText().toString().trim());
                p.setDescripcion(descriP.getText().toString().trim());
                p.setPrecio(precioP.getText().toString().trim());
                databaseReference.child("Producto").child(p.getId()).setValue(p);
                Toast.makeText(this,"Actualizado", Toast.LENGTH_LONG).show();
                limpiarCaja();
                break;
            }
            case R.id.ic_delete:{
                Producto p = new Producto();
                p.setId(productoSelected.getId());
                databaseReference.child("Producto").child(p.getId()).removeValue();
                Toast.makeText(this,"Eliminado", Toast.LENGTH_LONG).show();
                limpiarCaja();
                break;
            }
            default:break;
        }
        return true;

    }

    private void limpiarCaja() {
        nombP.setText("");
        descriP.setText("");
        precioP.setText("");
    }

    private void validacion() {
        String nombre = nombP.getText().toString();
        String descripcion = descriP.getText().toString();
        String precio = precioP.getText().toString();
        if (nombre.equals("")){
            nombP.setError("Required");
        }else if (descripcion.equals("")){
            descriP.setError("Required");
        }else if (precio.equals("")){
            precioP.setError("Required");
        }

    }
}
