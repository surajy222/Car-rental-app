package com.example.carrentapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {
    private static final String TAG = "ERROR";
    TextInputLayout username, password;
    ImageView image;
    TextView sloganText;
    Button go, new_user, new_user1;
    FirebaseAuth mAuth;
    private Object email;


    //carrentapp-c3973
//CarRentApp
    //private DataSnapshot dataSnapshot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().setTitle("Sign In");
        mAuth = FirebaseAuth.getInstance();
        new_user = findViewById(R.id.new_user);
        new_user1 = findViewById( R.id.new_user1 );
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        go = findViewById(R.id.login);
        findViewById(R.id.forget).setOnClickListener(v -> startActivity(new Intent(SignIn.this,ResetPassword.class)));
        new_user.setOnClickListener(v -> openSignUp());
        new_user1.setOnClickListener(v -> openadmin_signup());
        go.setOnClickListener(view -> {
            String user = username.getEditText().getText().toString().trim();
            String pass = password.getEditText().getText().toString().trim();
            GoToHome(user, pass);
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String email = bundle.getString("email");
            String pass = bundle.getString("pass");
            if (email != null && pass != null) {
                setFields(email, pass);
            }
        }
        password.getEditText().setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                String user = username.getEditText().getText().toString().trim();
                String pass = password.getEditText().getText().toString().trim();
                GoToHome(user, pass);
                return true;
            }
            return false;
        });
    }




    private void setFields(String email, String pass) {
        username.getEditText().setText(email);
        password.getEditText().setText(pass);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences("AutoLogin", MODE_PRIVATE);
        String userName = sharedPreferences.getString("UserName", "");
        String password = sharedPreferences.getString("Password", "");
        if (!userName.isEmpty() && !password.isEmpty()) {
            setFields(userName, password);
            GoToHome(userName, password);

        }

    }

    private void GoToHome(String UserName, String Password) {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please Wait logging in..");
        pd.show();
        SharedPreferences sharedPreferences = getSharedPreferences("AutoLogin", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!UserName.isEmpty() && !Password.isEmpty()) {
            if (UserName.equals("admin") && Password.equals("admin")) {
                pd.dismiss();
                editor.putString("UserName", UserName);
                editor.putString("Password", Password);
                startActivity(new Intent(SignIn.this, AdminDashboard.class));
                finish();
                Toast.makeText(getApplicationContext(), "Welcome " + UserName, Toast.LENGTH_SHORT).show();
            } else {
                editor.putString("UserName", UserName);
                editor.putString("Password", Password);
                mAuth.signInWithEmailAndPassword(UserName, Password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {


                        isAdmin(UserName,pd);


                    }
                }).addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }
        editor.apply();


    }

    private void openSignUp() {
        startActivity(new Intent(SignIn.this, SignUp.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
    private void openadmin_signup() {
        startActivity(new Intent(SignIn.this, admin_signup.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));

    }

    private boolean validatePassword() {
        String val = password.getEditText().getText().toString();
        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }


    private boolean validateUsername() {

        String val = username.getEditText().getText().toString();
        if (val.isEmpty()) {
            username.setError("Field cannot be empty");
            return false;
        } else {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }

    }
    public  void isAdmin(String email, ProgressDialog pd){

        Query query = FirebaseDatabase.getInstance().getReference().child("admin");
        query.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean checkEmail = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User modal = dataSnapshot.getValue(User.class);
                    if(modal != null) {
                        String Email = modal.Email.trim();
                        if (Email.equals( email )) {
                            checkEmail = true;

                            startActivity( new Intent( SignIn.this, licence_admin.class ) );
                            pd.dismiss();
                            finish();
                            Toast.makeText(getApplicationContext(), "Welcome " + Email, Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                if (!checkEmail){
                    Query query1 = FirebaseDatabase.getInstance().getReference().child("User");
                    query1.addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                User modal = dataSnapshot.getValue(User.class);
                                if(modal!= null){
                                    String Email = modal.Email.trim();
                                    if(Email.equals( email )){

                                        startActivity(new Intent(SignIn.this, MapsActivity.class));
                                        pd.dismiss();
                                        finish();
                                        Toast.makeText(getApplicationContext(), "Welcome " + Email, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "onCancelled :" + error.getMessage());
                        }
                    } );
                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled :" + error.getMessage());
            }

        });
    }



    public void loginUser(View view) {
        //Validate Login Info
        if (!validateUsername() | !validatePassword()) {
            return;
        } else {
//            isUser();
        }
    }

//
//    private void isUser() {
//
//        final String userEnteredUsername = username.getEditText().getText().toString().trim();
//        final String userEnteredPassword = password.getEditText().getText().toString().trim();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
//        Query checkUser = reference.orderByChild("username").equalTo(userEnteredUsername);
//        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    username.setError(null);
//                    username.setErrorEnabled(false);
//                    String passwordFromDB = dataSnapshot.child(userEnteredUsername).child("password").getValue(String.class);
//                    if (passwordFromDB.equals(userEnteredPassword)) {
//                        username.setError(null);
//                        username.setErrorEnabled(false);
//                        String nameFromDB = dataSnapshot.child(userEnteredUsername).child("name").getValue(String.class);
//                        String usernameFromDB = dataSnapshot.child(userEnteredUsername).child("username").getValue(String.class);
//                        String phoneNoFromDB = dataSnapshot.child(userEnteredUsername).child("phoneNo").getValue(String.class);
//                        String emailFromDB = dataSnapshot.child(userEnteredUsername).child("email").getValue(String.class);
//                        String stateFromDB = dataSnapshot.child(userEnteredUsername).child("state").getValue(String.class);
//                        String cityFromDB = dataSnapshot.child(userEnteredUsername).child("city").getValue(String.class);
//                        String streetFromDB = dataSnapshot.child(userEnteredUsername).child("street").getValue(String.class);
//                        String ageFromDB = dataSnapshot.child(userEnteredUsername).child("age").getValue(String.class);
//                        String codeFromDB = dataSnapshot.child(userEnteredUsername).child("code").getValue(String.class);
//
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        intent.putExtra("name", nameFromDB);
//                        intent.putExtra("username", usernameFromDB);
//                        intent.putExtra("email", emailFromDB);
//                        intent.putExtra("phoneNo", phoneNoFromDB);
//                        intent.putExtra("password", passwordFromDB);
//                        intent.putExtra("state", stateFromDB);
//                        intent.putExtra("city", cityFromDB);
//                        intent.putExtra("street", streetFromDB);
//                        intent.putExtra("age", ageFromDB);
//                        intent.putExtra("code", codeFromDB);
//                        startActivity(intent);
//
//                    } else {
//
//                        password.setError("Wrong Password");
//                        password.requestFocus();
//                    }
//                } else {
//
//                    username.setError("No such User exist");
//                    username.requestFocus();
//                }
//
//
//            }
//
//
//
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}