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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button login;
    EditText username, password;
    private TextView textViewResult;
    static HashMap<Integer, ArrayList<String>> credentials = loadCredentials();

    private static HashMap<Integer, ArrayList<String>> loadCredentials() {
        ArrayList<String> admin = new ArrayList<String>();
        admin.add("admin");
        admin.add("admin");
        admin.add("1");

        ArrayList<String> admin2 = new ArrayList<String>();
        admin2.add("admin2");
        admin2.add("admin2");
        admin2.add("2");
        HashMap<Integer, ArrayList<String>> result = new HashMap<Integer, ArrayList<String>>();

        result.put(1, admin);
        result.put(2, admin2);
        return result;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = findViewById(R.id.button);
        username = findViewById(R.id.editTextTextPersonName);
        password = findViewById(R.id.editTextTextPassword);

        login.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 login();
             }
        });
    }

    public void login() {
        Map<String, ArrayList<String>> multiValueMap = new HashMap<String, ArrayList<String>>();

        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        boolean valid_username = false;
        boolean valid_password = false;

        Iterator i = credentials.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry mapElement = (Map.Entry) i.next();
            ArrayList<String> credential = (ArrayList<String>) mapElement.getValue();
            String username = credential.get(0);
            String pw = credential.get(1);
            String uId = credential.get(2);

            if (username.equals(user) && pw.equals(pass)) {
                valid_username = true;
                valid_password = true;
            }
            if (valid_username && valid_password) {
                Toast.makeText(this, "welcome it worked", Toast.LENGTH_LONG).show();
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
                            if(post.getUserId() == Integer.parseInt(uId)) {
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
            }
        }
        if (user.isEmpty() && pass.isEmpty()) {
            Toast.makeText(this, "Please enter a username and password", Toast.LENGTH_LONG).show();
        } else if(user.isEmpty()){
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_LONG).show();
        } else if(pass.isEmpty()){
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_LONG).show();
        } else if (!valid_password) {
            Toast.makeText(this, "Incorrect password", Toast.LENGTH_LONG).show();
        } else if (!valid_username) {
            Toast.makeText(this, "Username not found", Toast.LENGTH_LONG).show();
        }
    }
}
