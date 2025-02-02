package com.example.ecommerce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Map<String, Object>> orderList;

    public OrderAdapter(Context context, List<Map<String, Object>> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        Map<String, Object> order = orderList.get(position);

        holder.orderIdTextView.setText("Order ID: " + order.get("orderId"));
        holder.subtotalTextView.setText("Total: " + order.get("subtotal"));
        holder.nameView.setText("Items Ordered: " + order.get("items"));
        holder.statusTextView.setText("Status: " + order.get("status"));
        holder.orderDateTextView.setText("Order Date: " + order.get("date"));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderIdTextView;
        TextView subtotalTextView;
        TextView statusTextView;
        TextView orderDateTextView;
        TextView nameView;

        public OrderViewHolder(View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            subtotalTextView = itemView.findViewById(R.id.subtotalTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            orderDateTextView = itemView.findViewById(R.id.orderDateTextView);
            nameView = itemView.findViewById(R.id.nameView);
        }
    }
}
