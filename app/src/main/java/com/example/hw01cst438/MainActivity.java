package com.example.hw01cst438;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button register, login;
    EditText username, password, userId;
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        login = findViewById(R.id.buttonLogin);
        register = findViewById(R.id.buttonRegister);
        username = findViewById(R.id.editTextTextPersonName);
        password = findViewById(R.id.editTextTextPassword);
        userId = findViewById(R.id.editTextTextUserId);

        register.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 register();
             }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });
    }
    public void register() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(Integer.parseInt(userId.getText().toString()));
        userEntity.setPassword(password.getText().toString());
        userEntity.setName(username.getText().toString());


        if (validateInput(userEntity)) {
            UserDatabase userDatabase = UserDatabase.getUserDatabase(getApplicationContext());
            UserDao userDao = userDatabase.userDao();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    userDao.registerUser(userEntity);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "User Registered!", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }).start();
        } else {
            Toast.makeText(getApplicationContext(), "Field Missing", Toast.LENGTH_LONG).show();
        }

    }

    private Boolean validateInput (UserEntity userEntity) {
        if(userEntity.getName().isEmpty()|| userEntity.getPassword().isEmpty()) {
            return false;
        }
        return true;
    }

    public void login() {
        String user = username.getText().toString();
        String pass = password.getText().toString();
        UserDatabase userDatabase = UserDatabase.getUserDatabase(getApplicationContext());
        final UserDao userDao = userDatabase.userDao();
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserEntity userEntity = userDao.login(user, pass);
                if (userEntity != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "welcome it worked", Toast.LENGTH_LONG).show();
                        }
                    });
                    setContentView(R.layout.post);
                    textViewResult = findViewById(R.id.textViewPost);

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://jsonplaceholder.typicode.com/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

                    Call<List<Post>> call = jsonPlaceHolderApi.getPosts();

                    call.enqueue(new Callback<List<Post>>() {
                        @Override
                        public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {

                            if (!response.isSuccessful()) {
                                textViewResult.setText("Code: " + response.code());
                                return;
                            }

                            List<Post> posts = response.body();

                            for (Post post : posts) {
                                if (post.getUserId()==userEntity.getUserId()) {
                                    String content = "";
                                    content += "ID: " + post.getId() + "\n";
                                    content += "User Id:" + post.getUserId() + "\n";
                                    content += "Title" + post.getTitle() + "\n";
                                    content += "Text: " + post.getText() + "\n\n";

                                    textViewResult.append(content);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Post>> call, Throwable t) {
                            textViewResult.setText(t.getMessage());
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "bad credentials", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        }).start();


    }
}
