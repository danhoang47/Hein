package com.hein.ordered_product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hein.R;
import com.hein.entity.Booking;
import com.hein.entity.Product;

import java.util.List;

public class OrderedProductAdapter extends RecyclerView.Adapter<OrderedProductAdapter.OrderedViewHolder> {
    private List<Booking> orders;
    private Context context;

    public OrderedProductAdapter(Context context, List<Booking> orders){
        this.orders = orders;
        this.context = context;
    }

    public void setOrderProducts(List<Booking> orders){
        this.orders = orders;
    }
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    @NonNull
    @Override
    public OrderedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent,false);
        return new OrderedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderedViewHolder holder, int position) {

        Booking order = orders.get(position);

        db.collection("Product")
                        .document(order.getProductId())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Product product = task.getResult().toObject(Product.class);
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

                                holder.productName.setText(order.getProductName());
                                holder.productQuantity.setText("x" + order.getQuantity());
                                holder.productTotalPrice.setText("$" + order.getTotalPrice());
                                holder.productOrderStatus.setText(getOrderStatus(order.getStatus()));
                                holder.productOrderSizeAndColor.setText(order.getSize() + ", " + order.getColor());
                            }
                        });

    }

    public String getOrderStatus(int statusCode) {
        switch(statusCode) {
            case 0:
                return "Accepted";
            case 1:
                return "Delivered";
            case 2:
                return "Arrived";
            case 3:
                return "Canceled";
            default:
                return null;
        }
    };

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class OrderedViewHolder extends RecyclerView.ViewHolder{

        private TextView productName,
                productOrderSizeAndColor,
                productTotalPrice,
                productQuantity,

        productOrderStatus;
        private ImageView productImage;
        public OrderedViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.order_item_name);
            productOrderStatus = itemView.findViewById(R.id.order_status);
            productOrderSizeAndColor = itemView.findViewById(R.id.order_size_and_color);
            productTotalPrice = itemView.findViewById(R.id.order_total_price);
            productImage = itemView.findViewById(R.id.order_image);
            productQuantity = itemView.findViewById(R.id.order_quantity);
        }
    }
}
