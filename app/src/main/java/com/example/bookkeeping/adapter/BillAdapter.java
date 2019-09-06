package com.example.bookkeeping.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookkeeping.R;
import com.example.bookkeeping.datepicker.DateFormatUtils;
import com.example.bookkeeping.entity.Bill;
import com.example.bookkeeping.ui.edit.EditFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import lombok.Setter;

public class BillAdapter extends RecyclerView.Adapter {
    private List<Bill> billList;
    public BillAdapter(List<Bill> billList){
        this.billList = billList;
    }
    private View isRemoveStateBtn;
    public interface OnremoveListnner{
        void  ondelete(int index,View removeBtn);
    }
    @Setter
    private OnremoveListnner onremoveListnner;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.history_bill_item,parent,false);
        ViewHolder holder = new ViewHolder (view);
        holder.billView.setOnClickListener (v -> {
            int postion = holder.getAdapterPosition ();
            Bill bill = billList.get (postion);
            EditFragment.bill = bill;
            BottomNavigationView navigation = view.getRootView ().findViewById (R.id.nav_view);
            navigation.setSelectedItemId(navigation.getMenu().getItem(1).getItemId());
        });
        holder.billView.setOnLongClickListener (v -> {
            if(isRemoveStateBtn!=null&&isRemoveStateBtn.getVisibility ()!=View.GONE){
                isRemoveStateBtn.setVisibility (View.GONE);
            }
            if (onremoveListnner!=null){
                isRemoveStateBtn = holder.removeBtn;
                onremoveListnner.ondelete(holder.getAdapterPosition (),isRemoveStateBtn);
            }
            return true;
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Bill bill = billList.get (position);
        ViewHolder viewHolder = (ViewHolder) holder;
        String consumptionDate = DateFormatUtils.fomart (bill.getConsumptionTime (),"MM月dd日");
        viewHolder.consumptionDate.setText (consumptionDate);
        if(position>0){
            int before = position - 1;
            String beforeTime = DateFormatUtils.fomart (billList.get (before).getConsumptionTime (),"MM月dd日");
            if(beforeTime.equals (consumptionDate)){
                viewHolder.consumptionDate.setVisibility (View.GONE);
            }
        }
        viewHolder.amount.setText (bill.getAmount ().toString ());
        viewHolder.expenditureImg.setImageResource (bill.getExpenditure ().getImageId ());
        String remark = bill.getExpenditure ().getName ();
        if (bill.getRemark ()!=null&&!bill.getRemark ().equals ("")){
            remark += "-"+bill.getRemark ();
        }
        viewHolder.remark.setText (remark);
        viewHolder.amount.setText (bill.getAmount ().toString ());
        viewHolder.payMethod.setText (bill.getPayMethod ().getName ());
        String consumptionTime =DateFormatUtils.getDayofWeek(bill.getConsumptionTime ()) + DateFormatUtils.fomart (bill.getConsumptionTime (),"HH:mm");
        viewHolder.consumptionTime.setText (consumptionTime);
        viewHolder.removeBtn.setVisibility (View.GONE);
    }

    @Override
    public int getItemCount() {
        return billList.size ();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView consumptionDate;
        ImageView expenditureImg;
        TextView remark;
        TextView amount;
        TextView payMethod;
        TextView consumptionTime;
        View billView;
        Button removeBtn;
        public ViewHolder(@NonNull View itemView) {
            super (itemView);
            billView = itemView;
            consumptionDate = itemView.findViewById (R.id.bill_item_consumptionDate);
            expenditureImg = itemView.findViewById (R.id.bill_item_expenditureImg);
            remark = itemView.findViewById (R.id.bill_item_remark);
            amount = itemView.findViewById (R.id.bill_item_amount);
            payMethod = itemView.findViewById (R.id.bill_item_payMethod);
            consumptionTime = itemView.findViewById (R.id.bill_item_consumptionTime);
            removeBtn = itemView.findViewById (R.id.custom_remove_btn);
        }
    }
}
