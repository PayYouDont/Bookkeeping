package com.example.bookkeeping.entity;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.fragment.app.Fragment;

import com.example.bookkeeping.MainActivity;

public class BaseFragment extends Fragment {
    protected void setRootHeight(View root){
        if (root==null){
            return;
        }
        ViewTreeObserver vto = root.getViewTreeObserver ();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener () {
            @Override
            public void onGlobalLayout() {
                int height = root.getHeight ();
                ViewGroup.LayoutParams params = root.getLayoutParams ();
                params.height = height - MainActivity.navBarHeight;
                root.setLayoutParams (params);
                root.getViewTreeObserver ().removeOnGlobalLayoutListener (this);
            }
        });
    }
}
