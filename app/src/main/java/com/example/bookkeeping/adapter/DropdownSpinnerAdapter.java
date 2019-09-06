package com.example.bookkeeping.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookkeeping.R;
import com.example.bookkeeping.entity.Expenditure;

import org.litepal.LitePal;

import java.util.List;

public class DropdownSpinnerAdapter extends ArrayAdapter<Expenditure> {
    List<Expenditure> expenditures;

    public DropdownSpinnerAdapter(@NonNull Context context, int resource, List<Expenditure> expenditures) {
        super (context, resource, expenditures);
        this.expenditures = expenditures;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = View.inflate (getContext (), R.layout.edit_dropdown_menu,null);
        /*TextView name = view.findViewById (R.id.dropdown_item_name);
        TextView id = view.findViewById (R.id.dropdown_item_id);
        ImageView imageView = view.findViewById (R.id.dropdown_item_img);*/
        RecyclerView recyclerView = view.findViewById (R.id.dropdown_list);
        recyclerView.setLayoutManager (new GridLayoutManager (view.getContext (),4,GridLayoutManager.VERTICAL,false));
        List<Expenditure> expenditures = LitePal.findAll (Expenditure.class);
        ConsumeTypeAdapter consumeTypeAdapter = new ConsumeTypeAdapter (expenditures);
        recyclerView.setAdapter (consumeTypeAdapter);

        return view;
    }
}
