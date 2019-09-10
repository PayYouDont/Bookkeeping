package com.example.bookkeeping.ui.edit;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookkeeping.R;
import com.example.bookkeeping.adapter.ConsumeTypeAdapter;
import com.example.bookkeeping.entity.Expenditure;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = false)
public class MyDialog extends Dialog{
    private List<Expenditure> expenditureList;
    private TextView cancelTxt;
    @Setter
    private ConsumeTypeAdapter.OnSelectItemListener onSelectItemListener;
    public MyDialog(@NonNull Context context, List<Expenditure> expenditureList, ConsumeTypeAdapter.OnSelectItemListener onSelectItemListener) {
        super (context);
        this.expenditureList = expenditureList;
        this.onSelectItemListener = onSelectItemListener;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        View view = View.inflate (getContext (), R.layout.edit_dropdown_menu,null);
        RecyclerView recyclerView = view.findViewById (R.id.dropdown_list);
        recyclerView.setLayoutManager (new GridLayoutManager (view.getContext (),4,GridLayoutManager.VERTICAL,false));
        ConsumeTypeAdapter consumeTypeAdapter = new ConsumeTypeAdapter (expenditureList);
        recyclerView.setAdapter (consumeTypeAdapter);
        consumeTypeAdapter.setOnSelectItemListener (postion -> {
            this.onSelectItemListener.onSelect (postion);
            dismiss ();
        });
        setContentView (view);
        cancelTxt = findViewById (R.id.dropdown_cancel);
        setCancelTxt (cancelTxt);
        cancelTxt.setOnClickListener (v -> dismiss ());
        Window window = getWindow ();
        WindowManager.LayoutParams p = window.getAttributes ();
        WindowManager manager = getWindow ().getWindowManager ();
        Point size = new Point ();
        manager.getDefaultDisplay ().getSize (size);
        p.width = size.x;
        window.setAttributes (p);
        window.setGravity (Gravity.BOTTOM);
    }

}
