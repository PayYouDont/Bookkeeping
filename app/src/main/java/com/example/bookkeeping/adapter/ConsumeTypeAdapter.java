package com.example.bookkeeping.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookkeeping.R;
import com.example.bookkeeping.entity.Expenditure;

import java.util.List;

public class ConsumeTypeAdapter extends RecyclerView.Adapter {
    List<Expenditure> expenditures;

    public ConsumeTypeAdapter(List<Expenditure> expenditures) {
        this.expenditures = expenditures;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.edit_dropdown_menu__item,parent,false);
        ConsumeTypeAdapter.ViewHolder holder = new ConsumeTypeAdapter.ViewHolder (view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Expenditure expenditure = expenditures.get (position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.idText.setText (expenditure.getId ().toString ());
        viewHolder.imageView.setImageResource (expenditure.getImageId ());
        viewHolder.nameText.setText (expenditure.getName ());
    }

    @Override
    public int getItemCount() {
        return expenditures.size ();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView idText,nameText;
        ImageView imageView;
        View consumeTypeView;
        public ViewHolder(@NonNull View itemView) {
            super (itemView);
            consumeTypeView = itemView;
            idText = itemView.findViewById (R.id.dropdown_item_id);
            imageView = itemView.findViewById (R.id.dropdown_item_img);
            nameText = itemView.findViewById (R.id.dropdown_item_name);
        }
    }

}
