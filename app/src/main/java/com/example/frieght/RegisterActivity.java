package com.example.frieght;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frieght.databinding.ActivityRegisterBinding;
import com.example.frieght.utils.DialogUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
//    TextView binding.enterUsername, binding.enterEmail, binding.enterPassword, editConfirmPassword, editLogin;
//    ProgressBar prBar;
//    private Button signup;
//    private TextView to_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
        binding.takeToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signUp() {
        String name = binding.enterUsername.getText().toString().trim();
        String email = binding.enterEmail.getText().toString().trim();
        String password = binding.enterPassword.getText().toString().trim();
        //String confirmPassword = editConfirmPassword.getText().toString().trim();


        if (name.isEmpty()) {
            binding.enterUsername.setError("Full Name is Required!");
            binding.enterUsername.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            binding.enterEmail.setError("Email Address is Required!");
            binding.enterEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            binding.enterPassword.setError("Password is Required!");
            binding.enterPassword.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.enterEmail.setError("Please provide valid email!");
            binding.enterEmail.requestFocus();
            return;
        }


        if (password.length() < 6) {
            binding.enterPassword.setError("Password length must be greater than 6");
            binding.enterPassword.requestFocus();
            return;
        }


//        prBar.setVisibility(View.VISIBLE);
        DialogUtil.showSimpleProgressDialog(this);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name).build();
                                user.updateProfile(profileUpdates);
                                DialogUtil.closeProgressDialog();
                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            //  Toast.makeText(buyer_signup.this, -5.+"Failed to registered. Try Again!", Toast.LENGTH_LONG).show();
//                            prBar.setVisibility(View.GONE);
                            DialogUtil.closeProgressDialog();
                            Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });



























/*

        signup=findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,EmailVeri.class);
                startActivity(intent);
            }
        });
        to_login=findViewById(R.id.take_to_login);
        to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Login.class);
                startActivity(intent);
            }
        });



 */

    }
}