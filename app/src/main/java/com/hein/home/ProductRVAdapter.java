package com.hein.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hein.activities.DetailedActivity;
import com.hein.entity.Product;
import com.hein.R;

import java.util.List;

public class ProductRVAdapter extends RecyclerView.Adapter<ProductRVAdapter.ViewHolder> {
    private List<Product> products;
    private LayoutInflater mInflater;
    private Context context;
    FirebaseFirestore db;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    private OnProductClickListener onProductClickListener;
    ProductRVAdapter(Context context, List<Product> data, OnProductClickListener onProductClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.products = data;
        this.context = context;
        this.onProductClickListener = onProductClickListener;
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.productType.setText(product.getType());
        holder.productName.setText(product.getName());
        holder.productPrice.setText(product.getPrice() + "$");

        String imagePath = product.getImages().get(0);

        if (imagePath.contains("https://")) {
            Glide.with(context)
                    .load(imagePath)
                    .into(holder.productImage);
        } else {
            int index = imagePath.indexOf("images/");
            String pathString = imagePath.substring(index);
            StorageReference imageRef = storageRef.child(pathString);

            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(context)
                        .load(uri)
                        .into(holder.productImage);
            });

        }

        Glide
                .with(context)
                .load(product.getImages().get(0))
                .centerCrop()
                .placeholder(R.drawable.baseline_downloading_24)
                .into(holder.productImage);

        holder.productCardWrapper.setOnClickListener(view -> {
            onProductClickListener.onProductClick(product.getId());
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productType;
        TextView productName;
        TextView productPrice;
        CardView productCardWrapper;
        ViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productType = itemView.findViewById(R.id.productType);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productCardWrapper = itemView.findViewById(R.id.product_card);
        }
    }

    Product getItem(int id) {
        return products.get(id);
    }

    public void setProducts(List<Product> products) { this.products = products; }
}
