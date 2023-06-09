package com.example.ecommerce_hvpp.adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerce_hvpp.R;
import com.example.ecommerce_hvpp.firebase.FirebaseHelper;
import com.example.ecommerce_hvpp.fragments.customer_fragments.OrderHistoryDetailFragment;
import com.example.ecommerce_hvpp.fragments.customer_fragments.OrderHistoryFragment;
import com.example.ecommerce_hvpp.model.OrderHistoryItem;
import com.example.ecommerce_hvpp.model.OrderHistorySubItem;
import com.example.ecommerce_hvpp.viewmodel.Customer.OrderHistoryViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.DataViewHolder>{
    private ArrayList<OrderHistoryItem> itemList;
    private OrderHistoryFragment parent;
    private OrderHistoryViewModel viewModel;

    public OrderHistoryAdapter(OrderHistoryFragment parent, ArrayList<OrderHistoryItem> listOrderHistory) {
        this.parent = parent;
        this.itemList = listOrderHistory;
    }
    public int getItemCount() {
        return itemList.size();
    }
    @NonNull
    @Override
    public OrderHistoryAdapter.DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.completed_order, parent, false);

        return new OrderHistoryAdapter.DataViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull OrderHistoryAdapter.DataViewHolder holder, int position) {
        OrderHistoryItem item = itemList.get(position);

        viewModel = new ViewModelProvider(parent).get(OrderHistoryViewModel.class);

        if (item.getQuantity_of_product() < 2){
            holder.quantity_tv.setText(Long.toString(item.getQuantity_of_product()) + " product");
        }
        else{
            holder.quantity_tv.setText(Long.toString(item.getQuantity_of_product()) + " products");
        }

        holder.day_of_order_tv.setText("Day order: " + getDate(item.getDayCreate_subItem()));
        holder.sum_of_order_tv.setText("$" + Double.toString(item.getSum_of_order()));
        Log.d(TAG, "id cua order: " + item.getID_of_Order());

        viewModel.getFirstItem(item.getID_of_Order()).observe((LifecycleOwner) holder.itemView.getContext(), Item -> {
            if (item.getID_of_Order().equals(Item.getOrderID())){
                holder.name_item_tv.setText(Item.getName_subItem());
                Log.d(TAG, item.getID_of_Order() + " + " + Item.getName_subItem());
                holder.price_item_tv.setText("$" + Double.toString(Item.getSum_subItem()));
                holder.quantity_item_tv.setText("Quantity:  " + Item.getQuantity_subItem());
                Glide.with(holder.itemView).load(Item.getImagePath_subItem()).fitCenter().into(holder.image_item);
            }
        });

        holder.more_detail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("order_id", item.getID_of_Order());
                parent.getNavController().navigate(R.id.navigate_to_orderhistorydetail, bundle);
            }
        });
        holder.review_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("order_id", item.getID_of_Order());
                parent.getNavController().navigate(R.id.navigate_to_orderhistorydetail, bundle);
            }
        });
    }
    /**
     * Data ViewHolder class.
     */
    public static class DataViewHolder extends RecyclerView.ViewHolder{
        private TextView quantity_tv, day_of_order_tv, sum_of_order_tv;
        private ImageView image_item;
        private TextView name_item_tv, quantity_item_tv, price_item_tv;
        private Button more_detail_btn, review_btn;
        public DataViewHolder(View itemView){
            super(itemView);

            more_detail_btn = (Button) itemView.findViewById(R.id.more_detail_btn);
            review_btn = (Button) itemView.findViewById(R.id.review_btn);

            quantity_tv = (TextView) itemView.findViewById(R.id.quantity_of_ordereditem_tiengviet);
            day_of_order_tv = (TextView) itemView.findViewById(R.id.day_of_order);
            sum_of_order_tv = (TextView) itemView.findViewById(R.id.sum_of_ordereditem_tiengviet);

            image_item = (ImageView) itemView.findViewById(R.id.image_of_1st_item_orderhistory);
            name_item_tv = (TextView) itemView.findViewById(R.id.name_of_ordereditem);
            quantity_item_tv = (TextView) itemView.findViewById(R.id.quantity_of_ordereditem);
            price_item_tv = (TextView) itemView.findViewById(R.id.total_money_ordereditem);

        }
    }
    public String getDate(long timeStamp){

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedTime = dateFormat.format(new Date(timeStamp));

        return formattedTime;
    }
}
