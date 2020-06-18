package com.example.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DealActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText txtTitle,txtPrice,txtDescription;
    TravelDeal deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUtil.openFirebaseReference("traveldeals",this);

        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        txtTitle = findViewById(R.id.txtTitle);
        txtPrice = findViewById(R.id.txtPrice);
        txtDescription = findViewById(R.id.txtDescription);

        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");

        if (deal==null){
            deal = new TravelDeal();
        }

        this.deal = deal;

        txtTitle.setText(deal.getTitle());
        txtDescription.setText(deal.getDescription());
        txtPrice.setText(deal.getPrice());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu: {
                saveDeal();
                Toast.makeText(this, "Deal Saved", Toast.LENGTH_LONG).show();
                clean();
                backToList();
                return true;
            }
            case R.id.delete_deal: {
                deleteDeal();
                Toast.makeText(this, "Deal Deleted Successfully", Toast.LENGTH_LONG).show();
                backToList();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveDeal(){
        deal.setTitle(txtTitle.getText().toString());
        deal.setPrice(txtPrice.getText().toString());
        deal.setDescription(txtDescription.getText().toString());

        if (deal.getId()==null){
            mDatabaseReference.push().setValue(deal);

        }
        else
            mDatabaseReference.child(deal.getId()).setValue(deal);


    }

    public void deleteDeal(){
        if (deal==null){
            Toast.makeText(this,"Save a deal first",Toast.LENGTH_LONG).show();
        }
        else
            mDatabaseReference.child(deal.getId()).removeValue();
            backToList();
    }

    public void backToList(){
        startActivity(new Intent(this,ListMantics.class));
    }


    public void clean(){
        txtTitle.setText("");
        txtPrice.setText("");
        txtDescription.setText("");
    }
}