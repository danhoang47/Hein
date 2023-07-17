package com.hein.shoppingcart.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hein.R;
import com.hein.entity.Booking;
import com.hein.entity.Product;
import com.hein.home.OnProductClickListener;
import com.hein.ordered_product.OrderedProductAdapter;
import com.hein.shoppingcart.CartViewModel;

import java.util.List;
import java.util.stream.IntStream;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Booking> orders;
    private Context context;
    FirebaseFirestore db;
    CartViewModel cartViewModel;
    private OnProductClickListener onProductClickListener;

    public CartAdapter(
            Context context,
            List<Booking> orders,
            CartViewModel cartViewModel,
            OnProductClickListener onProductClickListener) {
        this.orders = orders;
        this.context = context;
        this.cartViewModel = cartViewModel;
        this.onProductClickListener = onProductClickListener;
        db = FirebaseFirestore.getInstance();
        notifyViewModel(orders, false);

    }

    public void setOrderProducts(List<Booking> orders){
        this.orders = orders;
    }
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_cart_item, parent,false);
        return new CartAdapter.CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        Booking order = orders.get(position);

        Log.i("MSG", "Cart Item Id: " + order.getId());

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
                        holder.productOrderSizeAndColor.setText(order.getSize() + ", " + order.getColor());
                    }
                });

        holder.cartItemDeleteBtn.setOnClickListener(v -> {
            deleteItemFromCart(order, position);
        });

        holder.cartItemDecreaseBtn.setOnClickListener(v -> {
            varyItemQuantity("decrease", order, position);
        });

        holder.cartItemIncreaseBtn.setOnClickListener(v -> {
            varyItemQuantity("increase", order, position);
        });

        holder.cartCardItem.setOnClickListener(v -> {

        });
    }

    private void varyItemQuantity(String type, Booking order, int position) {
        int price = (int) (order.getTotalPrice() / order.getQuantity());
        if (type.equals("increase")) {
            db.collection("Product")
                    .document(order.getProductId())
                    .get()
                    .addOnSuccessListener(task -> {
                        Product product = task.toObject(Product.class);
                        int quantityOfSize = product.getSizes().get(order.getSize());
                        if (quantityOfSize > order.getQuantity()) {
                            order.setQuantity(order.getQuantity() + 1);
                        } else {
                            Toast.makeText(context, "Item quantity limit is reached", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            if (order.getQuantity() == 1) {
                deleteItemFromCart(order, position);
                return;
            } else {
                order.setQuantity(order.getQuantity() - 1);
            }
        }

        order.setTotalPrice(order.getQuantity() * price);

        orders.set(position, order);
        setOrderProducts(orders);

        notifyViewModel(orders, true);
        notifyItemChanged(position, order);
    }

    private void deleteItemFromCart(Booking order, int position) {
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("Confirm discard changes")
                .setMessage("Are you sure you want to discard all change to Shopping Cart")
                .setPositiveButton("Yes", (dialog, which) -> {
                    cartViewModel.deletedOrders.getValue().add(order.getId());
                    orders.remove(position);
                    setOrderProducts(orders);

                    notifyViewModel(orders, true);
                    notifyDataSetChanged();
                })
                .setNegativeButton("No", null)
                .show();


    }

    private void notifyViewModel(List<Booking> orders, boolean status) {
        int totalPrice = orders.stream().mapToInt(order -> (int)(order.getTotalPrice())).sum();

        cartViewModel.orders.postValue(orders);
        cartViewModel.setIsCartItemChanged(status);
        cartViewModel.setTotalPrice(totalPrice);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder{

        private TextView productName,
                productOrderSizeAndColor,
                productQuantity,
                productTotalPrice;
        private ImageView productImage, cartItemDeleteBtn, cartItemIncreaseBtn, cartItemDecreaseBtn;
        CardView cartCardItem;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.eachCartItemName);
            productOrderSizeAndColor = itemView.findViewById(R.id.cart_item_size_and_color);
            productTotalPrice = itemView.findViewById(R.id.eachCartItemPriceTv);
            productImage = itemView.findViewById(R.id.eachCartItemIV);
            productQuantity = itemView.findViewById(R.id.eachCartItemQuantityTV);
            cartItemDeleteBtn = itemView.findViewById(R.id.eachCartItemDeleteBtn);
            cartItemIncreaseBtn = itemView.findViewById(R.id.eachCartItemAddQuantityBtn);
            cartItemDecreaseBtn = itemView.findViewById(R.id.eachCartItemMinusQuantityBtn);
            cartCardItem = itemView.findViewById(R.id.cart_item_card_view);
        }
    }
}
