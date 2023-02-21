package com.mordechay.yemotapp.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.DataTransfer;

import java.util.Objects;


public class loginToServerActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar prgLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_to_server);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        MaterialToolbar mtb = findViewById(R.id.login_to_server_mtb);
        setSupportActionBar(mtb);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        prgLogin = findViewById(R.id.login_to_server_progress);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
            } else {
                loginButton.setVisibility(View.GONE);
                prgLogin.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        DataTransfer.setUsername(user.getEmail());
                                        DataTransfer.setUid(user.getUid());
                                    }else{
                                        Toast.makeText(loginToServerActivity.this, "שגיאה חמורה בהתחברות לאפליקציה.", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    Intent intent = new Intent(loginToServerActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(loginToServerActivity.this, "האימות נכשל. הסיבה:  " + Objects.requireNonNull(task.getException()).getLocalizedMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    loginButton.setVisibility(View.VISIBLE);
                                    prgLogin.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });
    }




}