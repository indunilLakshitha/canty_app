package com.af.canty;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PKCS12Attribute;

public class Admin extends AppCompatActivity {
Button view;
EditText nic;
TextView address;
    FirebaseDatabase database;
    DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        view=findViewById(R.id.btn_view_address);
        nic=findViewById(R.id.txt_nic);
        address=findViewById(R.id.txt_address);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n=nic.getText().toString();
                database  = FirebaseDatabase.getInstance();
                ref= database.getReference("details").child(n);

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot_variable : snapshot.getChildren()) {

                            if(postSnapshot_variable.getKey().equals("address")){
                                address.setText(postSnapshot_variable.getValue().toString());
                            }



                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


    }
}
