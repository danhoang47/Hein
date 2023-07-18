package com.hein.account;


import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadFileAsyncTask extends AsyncTask<Uri, Integer, Void> {

    AppCompatActivity contextParent;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    ImageView image;

    public UploadFileAsyncTask(AppCompatActivity contextParent) {
        this.contextParent = contextParent;
    }

    @Override
    protected Void doInBackground(Uri... selectedImages) {
        Uri selectedImage = selectedImages[0];
        StorageReference fileRef = storageRef.child("images/" + selectedImage.getLastPathSegment() + ".jpg");
        UploadTask uploadTask = fileRef.putFile(selectedImage);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("MSG", "Upload success");
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return fileRef.getDownloadUrl();
            }
        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // WARNING: Example code to load image after download into ImageView
//                Glide.with(contextParent)
//                        .load(uri)
//                        .into(image);
                Log.i("MSG", uri.toString());
                    AccountDetail.user.setAvatar(uri.toString());
                Glide.with(contextParent).load(uri.toString()).into(AccountDetail.avatar);
                // TODO: set image into view
            }
        });;

        return null;
    }
}
