package com.example.busfare_splitterv2.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.busfare_splitterv2.R;
import com.example.busfare_splitterv2.network.ApiClient;
import com.example.busfare_splitterv2.network.ApiService;
import com.example.busfare_splitterv2.network.UserLoginRequest;
import com.example.busfare_splitterv2.network.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    TextView tvSignUp;
    private Button btnLogin;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        apiService = ApiClient.getApiService();

        // Auto skip login if already authenticated
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        if (prefs.getString("jwt_token", null) != null) {
            startActivity(new Intent(this, TripListActivity.class));
            finish();
            return;
        }


        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });


        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        UserLoginRequest request = new UserLoginRequest(email, password);
        Call<UserResponse> call = apiService.login(request);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccessToken();

                    if (token != null && !token.isEmpty()) {
                        saveToken(token);
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, TripListActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid server response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveToken(String token) {
        SharedPreferences sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE);
        sharedPref.edit().putString("jwt_token", token).apply();
    }

    public static String getToken(SharedPreferences sharedPref) {
        return sharedPref.getString("jwt_token", null);
    }
}
