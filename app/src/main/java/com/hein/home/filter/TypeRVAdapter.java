package com.hein.home.filter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.hein.R;

import java.util.List;

public class TypeRVAdapter extends RecyclerView.Adapter<TypeRVAdapter.ViewHolder> {
    private List<String> typeOptions;
    private LayoutInflater mInflater;
    private Resources resources;
    private String packageName;
    FilterViewModel filterViewModel;


    TypeRVAdapter(Context context, List<String> data, FilterViewModel filterViewModel) {
        this.mInflater = LayoutInflater.from(context);
        this.typeOptions = data;
        this.resources = context.getResources();
        this.packageName = context.getPackageName();
        this.filterViewModel = filterViewModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.type_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String typeName = typeOptions.get(position);
        holder.typeSelectBtn.setText(typeName);

        if (filterViewModel.getTypes().contains(typeName)) {
            holder.typeSelectBtn.setBackgroundTintList(getColorStateListByName("black"));
            holder.typeSelectBtn.setTextColor(getColorStateListByName("white"));
            holder.typeSelectBtn.setStrokeColor(getColorStateListByName("black"));
        }

        holder.typeSelectBtn.setOnClickListener(view -> onTypeSelect((MaterialButton) view, typeName));

    }

    private void onTypeSelect(MaterialButton view, String typeName) {
        if (filterViewModel.getTypes().contains(typeName)) {
            filterViewModel.getTypes().remove(typeName);
            view.setBackgroundTintList(getColorStateListByName("white"));
            view.setTextColor(getColorStateListByName("black"));
            view.setStrokeColor(getColorStateListByName("gray"));
        } else {
            filterViewModel.getTypes().add(typeName);
            view.setBackgroundTintList(getColorStateListByName("black"));
            view.setTextColor(getColorStateListByName("white"));
            view.setStrokeColor(getColorStateListByName("black"));
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
        return typeOptions.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        MaterialButton typeSelectBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            typeSelectBtn = itemView.findViewById(R.id.type_filter_btn);
        }
    }
}
