package com.example.carrentapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class CarDetailsForUserAdapter extends RecyclerView.Adapter<CarDetailsForUserAdapter.ViewHolder> {
    Context context;

    ArrayList<CarDetailModal> carDetailModals;
    ArrayList<String> DBRef;

    public CarDetailsForUserAdapter(Context context, ArrayList<CarDetailModal> carDetailModals, ArrayList<String> DBRef) {
        this.context = context;

        this.carDetailModals = carDetailModals;
        this.DBRef = DBRef;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.setModel(context, carDetailModals.get(position), DBRef.get(position));

        holder.bindData();
    }

    @Override
    public int getItemCount() {
        return carDetailModals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout root;
        public ImageView CarImage;
        public TextView txtTitle, txtDesc, txt_ac, txt_airbags, txt_passenger, txtCarRent;
        public MaterialButton editBtn;
        public CarDetailModal modal;
        Context context;
        String DatabaseRef;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.list_root);
            txtTitle = itemView.findViewById(R.id.list_title);
            txtDesc = itemView.findViewById(R.id.list_desc);
            CarImage = itemView.findViewById(R.id.carImage);
            txt_ac = itemView.findViewById(R.id.ac_text);
            txt_airbags = itemView.findViewById(R.id.Airbags_text);
            txt_passenger = itemView.findViewById(R.id.passengerText);
            editBtn = itemView.findViewById(R.id.edit);
            txtCarRent = itemView.findViewById(R.id.carRent);

        }

        private void setModel(Context context, CarDetailModal modal, String DatabaseReference) {
            this.modal = modal;
            this.context = context;
            DatabaseRef = DatabaseReference;
        }


        private void bindData() {
            txtTitle.setText(modal.CarBrand);
            txtDesc.setText(modal.ModelName);
            txt_ac.setText(modal.ACOptions);
            txt_airbags.setText(modal.NoOfAirbags);
            txt_passenger.setText(modal.NoOfPassengers);
            txtCarRent.setText(modal.CarRate);
            Glide.with(context)
                    .load(modal.Images.get("ImgLink0"))
                    .into(CarImage);
            SharedPreferences sharedPreferences = context.getSharedPreferences("AutoLogin", Context.MODE_PRIVATE);
            String userName = sharedPreferences.getString("UserName", "");
            if (!userName.isEmpty())
                if (userName.equals("admin"))
                    editBtn.setVisibility(View.VISIBLE);
                else
                    editBtn.setVisibility(View.GONE);
            root.setOnClickListener(view ->
                    context.startActivity(new Intent(context, BookingActivity.class).putExtra("CarDataModal", modal).putExtra("DatabaseReferenceKey", DatabaseRef).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK))
            );

        }


    }
}