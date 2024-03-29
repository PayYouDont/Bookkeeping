package com.example.bookkeeping.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookkeeping.R;
import com.example.bookkeeping.adapter.BillAdapter;
import com.example.bookkeeping.entity.BaseFragment;
import com.example.bookkeeping.entity.Bill;
import com.example.bookkeeping.ui.EditFragment;

import org.litepal.LitePal;

import java.util.List;

public class HistoryFragment extends BaseFragment {
    private View root;
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate (R.layout.fragment_history, container, false);
        RecyclerView recyclerView = root.findViewById (R.id.bill_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager (root.getContext ());
        recyclerView.setLayoutManager (layoutManager);
        List<Bill> billList = LitePal.order ("consumptionTime desc").find (Bill.class);
        BillAdapter adapter = new BillAdapter (billList);
        adapter.setOnremoveListnner ((index,removeBtn) -> {
            removeBtn.setVisibility (View.VISIBLE);
            removeBtn.refreshDrawableState ();
            removeBtn.setOnClickListener (v -> {
                billList.get (index).delete ();
                billList.remove (index);
                adapter.notifyDataSetChanged ();
                removeBtn.setVisibility (View.GONE);
            });
        });
        adapter.setOnEditListenner (bill -> {
            FragmentTransaction transaction = getFragmentManager ().beginTransaction ();
            Fragment fragment = new EditFragment (bill);
            transaction.replace (R.id.nav_host_fragment,fragment).commit ();
        });
        recyclerView.setAdapter (adapter);
        setRootHeight (root);
        return root;
    }
}