package com.hein.productCRUD;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hein.R;
import com.hein.entity.Booking;
import com.hein.entity.Product;
import com.hein.shoppingcart.ShoppingCartActivity;

import org.w3c.dom.Text;

import java.util.List;

public class ViewAllProductAdminAdapter extends BaseAdapter {
    Context context;
    String listNotiContent[];
    String listNotiTime[];

    List<Product> listProduct;

    LayoutInflater inflater;

    public ViewAllProductAdminAdapter(Context context, String[] listNotiContent, String[] listNotiTime) {
        this.context = context;
        this.listNotiContent = listNotiContent;
        this.listNotiTime = listNotiTime;
        this.inflater = LayoutInflater.from(context);
    }

    public ViewAllProductAdminAdapter(Context context, List<Product> listProduct) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listProduct = listProduct;

    }

    @Override
    public int getCount() {
        return listProduct.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.activities_product_card_admin, null);

        ImageView productImg = (ImageView) view.findViewById(R.id.product_admin_image);
        TextView productName = (TextView) view.findViewById(R.id.product_name_admin);
        TextView productPrice = (TextView) view.findViewById(R.id.product_price_admin) ;
        TextView productQuantity = (TextView) view.findViewById(R.id.product_quantity_left);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("productId", listProduct.get(i).getId().toString());
                Product product = listProduct.get(i);

                Intent intent = new Intent(context, EditOrDeleteProductActivity.class);
                intent.putExtra("selectedProduct", product);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });



        Glide.with(context).load(listProduct.get(i).getImages().get(0)).placeholder(com.denzcoskun.imageslider.R.drawable.loading).into(productImg);
        productName.setText(listProduct.get(i).getName() + "");
        productPrice.setText("Price: " + listProduct.get(i).getPrice() + "$");
        productQuantity.setText("Quantity: " + listProduct.get(i).getQuantity());


        return view;
    }
}
