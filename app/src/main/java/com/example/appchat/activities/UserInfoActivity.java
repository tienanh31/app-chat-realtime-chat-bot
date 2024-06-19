package com.example.appchat.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.appchat.R;

public class UserInfoActivity extends AppCompatActivity {

    private ImageView avatarImageView;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText emailEditText;
    private Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);



        // Bạn có thể thiết lập các dữ liệu mặc định cho người dùng ở đây
        // hoặc lấy dữ liệu từ cơ sở dữ liệu và hiển thị lên giao diện
        loadUserInfo();

        updateButton.setOnClickListener(v -> {
            // Xử lý sự kiện khi người dùng nhấn nút Update Info
            updateUserInfo();
        });
    }

    private void loadUserInfo() {
        // Giả sử bạn lấy thông tin người dùng từ cơ sở dữ liệu
        // Sau đó thiết lập dữ liệu này vào các View
        // avatarImageView.setImageResource(...);
        usernameEditText.setText("John Doe");
        passwordEditText.setText("password123");
        emailEditText.setText("john.doe@example.com");
    }

    private void updateUserInfo() {
        // Lấy dữ liệu từ các EditText
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String email = emailEditText.getText().toString();

        // Cập nhật thông tin người dùng vào cơ sở dữ liệu hoặc xử lý các yêu cầu cập nhật
        // ...
    }
}
