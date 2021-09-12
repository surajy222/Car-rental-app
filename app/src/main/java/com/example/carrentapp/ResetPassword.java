package com.example.carrentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private static final String TAG = "Reset Password";

    TextInputLayout email;
    MaterialButton send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        send = findViewById(R.id.reset);

        email = findViewById(R.id.username);

        send.setOnClickListener(v -> ResetPass());
    }

    private void ResetPass() {
        if (email.getEditText().getText().toString().trim().equals("")) {
            return;
        }
        String email = this.email.getEditText().getText().toString();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Email has sent. Check your inbox", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Email sent.");
            }
        });
    }
}