package com.example.carrentapp;

import android.app.ProgressDialog;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;

public class CarViewAdapter extends FirebaseRecyclerAdapter<CarDetailModal, CarViewAdapter.ViewHolder> {
    Context context;
    ProgressDialog pd;
    String from = "", to = "";


    public CarViewAdapter(@NonNull FirebaseRecyclerOptions<CarDetailModal> options, Context context, ProgressDialog pd) {
        super(options);
        this.context = context;
        this.pd = pd;
    }

    public CarViewAdapter(FirebaseRecyclerOptions<CarDetailModal> options, Context context, ProgressDialog pd, String from, String to) {
        super(options);
        this.context = context;
        this.pd = pd;
        this.from = from;
        this.to = to;
    }


    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (pd.isShowing())
            pd.dismiss();
    }


    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull CarDetailModal model) {

        holder.setModel(context, model, getRef(position).getKey(), from, to);

        holder.bindData(position);


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car_list_item, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout root;
        public ImageView CarImage;
        public TextView txtTitle, txtDesc, txt_ac, txt_airbags, txt_passenger, txtCarRent;
        public MaterialButton editBtn;
        public CarDetailModal modal;
        Context context;
        String DatabaseReferenceKey;
        String from, to;

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

        private void setModel(Context context, CarDetailModal modal, String DatabaseReferenceKey, String from, String to) {
            this.modal = modal;
            this.context = context;
            this.DatabaseReferenceKey = DatabaseReferenceKey;
            this.from = from;
            this.to = to;
        }


        private void bindData(int position) {
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
            if (!userName.isEmpty())
                root.setOnClickListener(view -> next(userName.equals("admin")));
            editBtn.setOnClickListener(view -> EditData());


        }

        private void EditData() {

            context.startActivity(new Intent(context, AdminCarDetail.class)
                    .putExtra("CarDataModal", modal)
                    .putExtra("DatabaseReferenceKey", DatabaseReferenceKey)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }

        private void next(boolean isAdmin) {
            if (!isAdmin) {
                context.startActivity(new Intent(context, BookingActivity.class)
                        .putExtra("CarDataModal", modal)
                        .putExtra("DatabaseReferenceKey", DatabaseReferenceKey)
                        .putExtra("from", from)
                        .putExtra("to", to)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }

        }

    }
}