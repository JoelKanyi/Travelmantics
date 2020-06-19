package com.example.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

public class DealActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText txtTitle,txtPrice,txtDescription;
    ProgressDialog progressDialog;
    private static final int PICTURE_CODE = 42;
    TravelDeal deal;
    private Button btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait...\n"+"Uploading Image");

        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        txtTitle = findViewById(R.id.txtTitle);
        txtPrice = findViewById(R.id.txtPrice);
        txtDescription = findViewById(R.id.txtDescription);
        btnUpload = findViewById(R.id.btnUploadImage);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(intent.createChooser(intent,"Insert picture"),PICTURE_CODE);
            }
        });

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

       if (FirebaseUtil.isAdmin){
           menu.findItem(R.id.save_menu).setVisible(true);
           menu.findItem(R.id.delete_deal).setVisible(true);
           enableEditText(true);
       }

       else {
           menu.findItem(R.id.save_menu).setVisible(false);
            menu.findItem(R.id.delete_deal).setVisible(false);
           enableEditText(false);
       }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressDialog.show();
        if (requestCode==PICTURE_CODE && resultCode== RESULT_OK){
            Uri file = data.getData();
            final StorageReference ref = FirebaseUtil.mStorageRef.child(file.getLastPathSegment());
            UploadTask uploadTask;

            uploadTask = ref.putFile(file);

            final Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL

                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        progressDialog.cancel();
                        Uri downloadUri = task.getResult();
                        Toast.makeText(getApplicationContext(),""+downloadUri,Toast.LENGTH_LONG).show();
                        deal.setImage_url(downloadUri.toString());
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                }
            });

        }
    }

    public void clean(){
        txtTitle.setText("");
        txtPrice.setText("");
        txtDescription.setText("");
    }

    public void enableEditText(boolean isEnabled){
        txtTitle.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        btnUpload.setEnabled(isEnabled);
    }
}