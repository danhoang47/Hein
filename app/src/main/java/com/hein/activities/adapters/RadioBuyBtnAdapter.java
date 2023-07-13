package com.hein.activities.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hein.R;

import java.util.List;

public class RadioBuyBtnAdapter extends RecyclerView.Adapter<RadioBuyBtnAdapter.ViewHolder> {
    private static List<String> itemList;
    private RadioButtonClickListener radioButtonClickListener;

    public RadioBuyBtnAdapter(List<String> itemList, RadioButtonClickListener listener) {
        this.itemList = itemList;
        this.radioButtonClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_radio_button, parent, false);
        return new ViewHolder(view, radioButtonClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = itemList.get(position);
        holder.radioButton.setText(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;

        public ViewHolder(@NonNull View itemView, RadioButtonClickListener listener) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radio_button);

            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String selectedValue = itemList.get(position);
                        listener.onRadioButtonClicked(selectedValue);
                    }
                }
            });
        }
    }

    public interface RadioButtonClickListener {
        void onRadioButtonClicked(String selectedValue);
    }
}
