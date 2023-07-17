package com.hein;

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

import java.util.function.Consumer;

public class ConsumerAsyncTask<T> extends AsyncTask<T, Integer, Void> {

    AppCompatActivity contextParent;
    Consumer<T> callback;

    public ConsumerAsyncTask(AppCompatActivity contextParent, Consumer<T> callback) {
        this.contextParent = contextParent;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(T... params) {
        callback.accept(params[0]);
        return null;
    }
}
