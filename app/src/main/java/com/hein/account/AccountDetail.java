package com.hein.account;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hein.R;
import com.hein.entity.Booking;
import com.hein.entity.UserProfile;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

public class AccountDetail   extends AppCompatActivity {
    final Calendar myCalendar= Calendar.getInstance();
    ImageView backIcon;

   public static UserProfile user;
    EditText dobText;
    EditText nameText;
    EditText emailText;
    EditText jobText;
    EditText phoneText;
   static ImageView avatar;
    RadioButton male;
    RadioGroup gendergroup;
    String user_document;
    Button update;
    Button cancel;
    FirebaseFirestore dbroot;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbroot=FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_detail);
        backIcon = findViewById(R.id.backIcon);
        dobText=(EditText) findViewById(R.id.birth_date);
        avatar= findViewById(R.id.avatarImage);
        nameText= findViewById(R.id.edtName);
        jobText=findViewById(R.id.editJob);
        phoneText=findViewById(R.id.phoneNumb);
        gendergroup= findViewById(R.id.genderselect);
        update=findViewById(R.id.button);
        cancel=findViewById(R.id.cancelButton);
        male=findViewById(R.id.maleSelect);
        emailText=findViewById(R.id.edtEmail);
        user_document=getIntent().getStringExtra("userId");

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        dobText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AccountDetail.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        avatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 3);
        });
        fetchUser();

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountDetail.this,AccountActivity.class));
            }
        });
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountDetail.this,AccountActivity.class));
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("MSG", resultCode + "");

        if (resultCode == RESULT_OK && data != null) {
            // check if selected multiple files
                Uri selectedImage = data.getData();
                UploadFileAsyncTask uploadFileAsyncTask = new UploadFileAsyncTask(this);
                uploadFileAsyncTask.execute(selectedImage);


        }
    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    private void updateLabel(){
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat);
        dobText.setText(dateFormat.format(myCalendar.getTime()));
    }
    public void fetchUser(){
        user_document= "xnJNFIe8KoGheyJXrLWf";
        DocumentReference document =dbroot.collection("User").document(user_document);
        document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = new UserProfile(documentSnapshot.getString("name"),documentSnapshot.getString("email"),documentSnapshot.getString("dob"),documentSnapshot.getString("job"),documentSnapshot.getString("phone"),documentSnapshot.getString("gender"),documentSnapshot.getString("avatar"));
                RadioButton malebutton = findViewById(R.id.maleSelect);
                RadioButton femalebutton = findViewById(R.id.femaleSelect);
                nameText.setText(user.getName());
                emailText.setText(user.getEmail());
                Glide.with(getApplicationContext()).load(user.getAvatar()).into(avatar);
                dobText.setText(user.getDob());
                jobText.setText(user.getJob());
                phoneText.setText(user.getPhone());
                if(Objects.equals(user.getGender(), "Male")){
                    malebutton.setChecked(true);
                }else{
                    femalebutton.setChecked(true);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    public void updateUser(){
        user_document= "xnJNFIe8KoGheyJXrLWf";
        String gender;
        if(male.isChecked()) {
            gender = "Male";
        }else{gender= "Female";}
        UserProfile updateUser= new UserProfile(nameText.getText().toString(),emailText.getText().toString(),dobText.getText().toString(),jobText.getText().toString(),phoneText.getText().toString(),gender,user.getAvatar());
        Map<String, Object> update = updateUser.toMap();
        dbroot.collection("User").document(user_document).update(update).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "User updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
