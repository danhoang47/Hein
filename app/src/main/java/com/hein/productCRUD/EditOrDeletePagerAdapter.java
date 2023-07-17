package com.hein.productCRUD;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.hein.R;

import java.util.ArrayList;

public class EditOrDeletePagerAdapter extends PagerAdapter {
    private Context context;
    ArrayList<Uri> ImageUrls;
    LayoutInflater layoutInflater;

    public EditOrDeletePagerAdapter(Context context, ArrayList<Uri> imageUrls) {
        this.context = context;
        ImageUrls = imageUrls;
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return ImageUrls.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view=layoutInflater.inflate(R.layout.showimageslayout,container,false);
        ImageView imageView=view.findViewById(R.id.UploadImage);

        if(ImageUrls.get(position).toString().contains("http") || ImageUrls.get(position).toString().contains("https")) {
            Glide.with(context).load(ImageUrls.get(position)).placeholder(com.denzcoskun.imageslider.R.drawable.loading).into(imageView);
        } else {
            imageView.setImageURI(ImageUrls.get(position));
        }

        Log.i("imageview adapter del or update", ImageUrls.get(position).toString());

        container.addView(view);

        return view ;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view== object;
    }

    @Override
    public void destroyItem(@NonNull View container, int position, @NonNull Object object) {
        ((RelativeLayout)object).removeView(container);
    }
}
