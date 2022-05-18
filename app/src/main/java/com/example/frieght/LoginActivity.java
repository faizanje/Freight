package com.example.frieght;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frieght.databinding.ActivityLoginBinding;
import com.example.frieght.utils.DialogUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    private FirebaseAuth mAuth =  FirebaseAuth.getInstance();
//    TextView binding.Email, binding.Password;
//    private Button signin;
//    private TextView forgotpass, take_to_signup;
//    ProgressBar prBar;

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginPage();
            }
        });

        binding.takeToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void loginPage() {

        String email = binding.Email.getText().toString().trim();
        String password = binding.Password.getText().toString().trim();

        if (email.isEmpty()) {
            binding.Email.setError("Email Address is Required!");
            binding.Email.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            binding.Password.setError("Password is Required!");
            binding.Password.requestFocus();
            return;
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.Email.setError("Please provide valid email!");
            binding.Email.requestFocus();
            return;
        }


        if (password.length() < 6) {
            binding.Password.setError("Password length must be greater than 6");
            binding.Password.requestFocus();
            return;
        }

        DialogUtil.showSimpleProgressDialog(this);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseDatabase.getInstance().getReference()
                            .child("buyers")
                            .child(user.getUid())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    finish();
                                }
                            });
                    //redirect to the buyer home
                    Log.i("Qasim", "onComplete: I am in Login ");


                } else {
                    DialogUtil.closeProgressDialog();
                    Toast.makeText(LoginActivity.this, task.getException().getMessage() + "", Toast.LENGTH_LONG).show();
                }


            }
        });


    }
}