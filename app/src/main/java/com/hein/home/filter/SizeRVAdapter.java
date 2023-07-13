package com.hein.home.filter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.hein.R;

import java.util.List;

public class SizeRVAdapter extends RecyclerView.Adapter<SizeRVAdapter.ViewHolder> {
    private List<String> productSizes;
    private LayoutInflater mInflater;
    private Resources resources;
    private String packageName;
    private FilterViewModel filterViewModel;

    SizeRVAdapter(Context context, List<String> data, FilterViewModel filterViewModel) {
        this.mInflater = LayoutInflater.from(context);
        this.productSizes = data;
        this.resources = context.getResources();
        this.packageName = context.getPackageName();
        this.filterViewModel = filterViewModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.size_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String size = productSizes.get(position);
        holder.sizeBtn.setText(size);

        if (filterViewModel.getSizes().contains(size)) {
            holder.sizeBtn.setBackgroundTintList(getColorStateListByName("black"));
            holder.sizeBtn.setTextColor(getColorStateListByName("white"));
        }

        holder.sizeBtn.setOnClickListener(view -> onSizeSelect((MaterialButton) view, size));
    }

    public void onSizeSelect(MaterialButton button, String size) {
        if (filterViewModel.getSizes().contains(size)) {
            filterViewModel.getSizes().remove(size);
            button.setBackgroundTintList(getColorStateListByName("zircon"));
            button.setTextColor(getColorStateListByName("black"));
        } else {
            filterViewModel.getSizes().add(size);
            button.setBackgroundTintList(getColorStateListByName("black"));
            button.setTextColor(getColorStateListByName("white"));
        }
    }

    private ColorStateList getColorStateListByName(String colorName) {
        return resources.getColorStateList(
                resources.getIdentifier(colorName, "color", packageName),
                null
        );
    }

    @Override
    public int getItemCount() {
        return productSizes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button sizeBtn;
        ViewHolder(View itemView) {
            super(itemView);
            sizeBtn = itemView.findViewById(R.id.size_option_btn);
        }
    }

    String getItem(int id) {
        return productSizes.get(id);
    }
}
