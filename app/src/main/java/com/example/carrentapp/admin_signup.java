package com.example.carrentapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class admin_signup extends AppCompatActivity {

    private static final String TAG = "ERROR";
    TextInputLayout regname, regusername, regemail, regphoneNo, regPassword, regstate, regcity, regstreet, regage, regcode;
    Button reggo, regBtn;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    //    FirebaseDatabase rootNode;
//    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_admin_signup );
        getSupportActionBar().setTitle("Sign Up");
        mAuth = FirebaseAuth.getInstance();
        regname = findViewById(R.id.name);
        regusername = findViewById(R.id.username);
        regemail = findViewById(R.id.email);
        regphoneNo = findViewById(R.id.phoneNo);
        regPassword = findViewById(R.id.Password);
        regstate = findViewById(R.id.state);
        regcity = findViewById(R.id.city);
        regstreet = findViewById(R.id.street);
        regage = findViewById(R.id.age);
        regcode = findViewById(R.id.code);
        reggo = findViewById(R.id.go);
        regBtn = findViewById(R.id.regBtn);

        reggo.setOnClickListener(view -> {
            if (validateFields()) {
                String Name = regname.getEditText().getText().toString();
                String UserName = regusername.getEditText().getText().toString();
                String Email = regemail.getEditText().getText().toString();
                String PhoneNo = regphoneNo.getEditText().getText().toString();
                String Password = regPassword.getEditText().getText().toString();
                String State = regstate.getEditText().getText().toString();
                String City = regcity.getEditText().getText().toString();
                String Street = regstreet.getEditText().getText().toString();
                String age = regage.getEditText().getText().toString();
                String Postalcode = regcode.getEditText().getText().toString();
                mAuth.createUserWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                User user = new User(Name, UserName, Email, PhoneNo, Password, State, City, Street, age, Postalcode);
                                FirebaseDatabase.getInstance().getReference("admin")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        openactivity_signin(Email, Password);
                                        Toast.makeText(getApplicationContext(), "User Has Been Registered...", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Fail to Registered...", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            } else {
                                Toast.makeText(getApplicationContext(), "Fail Registered...", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                    Log.e(TAG, "checkregisterUser: " + e.getMessage());
                });


            }
        });

        regBtn.setOnClickListener(v -> startActivity(new Intent(admin_signup.this, SignIn.class)));


    }

    private void openactivity_signin(String email, String Password) {
        startActivity(new Intent(admin_signup.this, SignIn.class).putExtra("email", email).putExtra("pass", Password).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        //startActivity(new Intent( SignUp.this,SignIn.class));
    }

    private boolean validateFields() {
        return validateName() && validateUsername() && validateEmail() && validatePhoneNo() && validatePassword() && validateState() && validateCity() && validateStreet() && validateAge() && validatePostalCode();
    }

    private boolean validateUsername() {
        String val = regusername.getEditText().getText().toString().trim();
        String noWhiteSpace = "(?=\\S+$)";

        Pattern whitespace = Pattern.compile("\\s\\s");
        Matcher matcher = whitespace.matcher(val);

        if (val.isEmpty()) {
            regusername.setError("Field cannot be Empty");
            regusername.requestFocus();
            return false;
        } else if (val.length() >= 15) {
            regusername.setError("Username too long");
            regusername.requestFocus();
            return false;
        } else if (matcher.find()) {
            regusername.setError("White Spaces are not allowed ");
            regusername.requestFocus();
            return false;
        } else {
            regusername.setError(null);
            return true;
        }

    }

    private boolean validateName() {
        String Name = regname.getEditText().getText().toString();
        if (Name.isEmpty()) {
            regname.setError("Full Name is Required..");
            regname.requestFocus();
            return false;
        } else {
            regname.setError(null);
            return true;
        }
    }

    private boolean validatePhoneNo() {
        String PhoneNo = regphoneNo.getEditText().getText().toString().trim();
        if (PhoneNo.isEmpty()) {
            regphoneNo.setError("Phone No is Required..");
            regphoneNo.requestFocus();
            return false;
        } else if (PhoneNo.length() != 10) {
            regphoneNo.setError("Phone No must be of 10 digit..");
            regphoneNo.requestFocus();
            return false;
        } else {
            regphoneNo.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String Email = regemail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (Email.isEmpty()) {
            regemail.setError("Email is Required..");
            regemail.requestFocus();
            return false;
        } else if (!Email.matches(emailPattern)) {
            regemail.setError("Invalidate email.");
            regemail.requestFocus();
            return false;
        } else {
            regemail.setError(null);
            return true;
        }

    }

    private boolean validatePassword() {
        String Password = regPassword.getEditText().getText().toString().trim();
        if (Password.isEmpty()) {
            regPassword.setError("Password is Required..");
            regPassword.requestFocus();
            return false;
        } else if (Password.length() < 6) {
            regPassword.setError("Password cannot be less than 6");
            regPassword.requestFocus();
            return false;
        } else {
            regPassword.setError(null);
            return true;
        }
    }

    private boolean validateState() {
        String State = regstate.getEditText().getText().toString().trim();

        if (State.isEmpty()) {
            regstate.setError("State is Required..");
            regstate.requestFocus();
            return false;
        } else {
            regstate.setError(null);
            return true;
        }
    }

    private boolean validateCity() {
        String City = regcity.getEditText().getText().toString().trim();

        if (City.isEmpty()) {
            regcity.setError("City is Required..");
            regcity.requestFocus();
            return false;
        } else {
            regcity.setError(null);

            return true;
        }
    }

    private boolean validateStreet() {
        String Street = regstreet.getEditText().getText().toString();
        if (Street.isEmpty()) {
            regstreet.setError("Street is Required..");
            regstreet.requestFocus();
            return false;
        } else {
            regstreet.setError(null);

            return true;

        }
    }

    private boolean validateAge() {
        String age = regage.getEditText().getText().toString().trim();
        if (age.isEmpty()) {
            regage.setError("age is Required..");
            regage.requestFocus();
            return false;
        } else {
            regage.setError(null);
            return true;
        }
    }

    private boolean validatePostalCode() {
        String Postalcode = regcode.getEditText().getText().toString();
        if (Postalcode.isEmpty()) {
            regcode.setError("PostalCode is Required..");
            regcode.requestFocus();
            return false;
        } else {
            regcode.setError(null);
            return true;

        }
    }


//
//    public void registerUser(View view) {
//
//        if(!validateUsername()){
//            return;
//        }
//
//        rootNode = FirebaseDatabase.getInstance();
//        reference = rootNode.getReference("users");
//
//
//        String name = regname.getEditText().getText().toString();
//        String username = regusername.getEditText().getText().toString();
//        String email = regemail.getEditText().getText().toString();
//        String phoneNo = regphoneNo.getEditText().getText().toString();
//        String Password = regPassword.getEditText().getText().toString();
//        String state = regstate.getEditText().getText().toString();
//        String city = regcity.getEditText().getText().toString();
//        String street = regstreet.getEditText().getText().toString();
//        String age = regage.getEditText().getText().toString();
//        String code = regcode.getEditText().getText().toString();
//
//        UserHelperClass helperClass = new UserHelperClass(name, username, email, phoneNo, Password, state, city, street, age, code);
//
//        reference.child(username).setValue(helperClass);
//    }
//


}

