package com.hein.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hein.home.filter.Classification;
import com.hein.R;

import java.util.List;

public class ClassificationRVAdapter extends RecyclerView.Adapter<ClassificationRVAdapter.ViewHolder> {
    private List<Classification> classifications;
    private LayoutInflater mInflater;

    ClassificationRVAdapter(Context context, List<Classification> data) {
        this.mInflater = LayoutInflater.from(context);
        this.classifications = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.classification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Classification classification = classifications.get(position);
        holder.classifiIcon.setCompoundDrawablesWithIntrinsicBounds(classification.getDrawableIcon(), null, null, null);
        holder.classifiTitle.setText(classification.getTitle());
    }

    @Override
    public int getItemCount() {
        return classifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView classifiIcon;
        TextView classifiTitle;
        ViewHolder(View itemView) {
            super(itemView);
            classifiIcon = itemView.findViewById(R.id.classificationIcon);
            classifiTitle = itemView.findViewById(R.id.classificationTitle);
        }
    }

    Classification getItem(int id) {
        return classifications.get(id);
    }

}
