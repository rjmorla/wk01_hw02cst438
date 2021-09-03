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
    static HashMap<Integer, ArrayList<String>> credentialMap = preloadCredentials();

    private static HashMap<Integer, ArrayList<String>> preloadCredentials() {
        ArrayList<String> admin = new ArrayList<String>();
        admin.add("admin");
        admin.add("admin");
        admin.add("1");

        ArrayList<String> admin2 = new ArrayList<String>();
        admin2.add("admin2");
        admin2.add("admin2");
        admin2.add("2");

        ArrayList<String> admin3 = new ArrayList<String>();
        admin3.add("admin3");
        admin3.add("admin3");
        admin3.add("3");

        HashMap<Integer, ArrayList<String>> result = new HashMap<Integer, ArrayList<String>>();
        result.put(1, admin);
        result.put(2, admin2);
        result.put(3, admin3);
        return result;
    }

    public static ArrayList<String> searchForUser(String user, String pass) {
        Iterator i = credentialMap.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry mapElement = (Map.Entry) i.next();
            ArrayList<String> credential = (ArrayList<String>) mapElement.getValue();
            String username = credential.get(0);
            String pw = credential.get(1);
            String uId = credential.get(2);
            if (username.equals(user) && pw.equals(pass)) {
                return credential;
            } else if (username.equals(user)) {
                ArrayList<String> userOnly = new ArrayList<String>();
                userOnly.add(credential.get(0));
                return userOnly;
            }
        }
        return null;
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
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        boolean validUsername = false;
        boolean validPassword = false;
        if (user.isEmpty() && pass.isEmpty()) {
            Toast.makeText(this, "Please enter a username and password", Toast.LENGTH_LONG).show();
            username.setError("Please enter a username");
            password.setError("Please enter a password");
        } else if (user.isEmpty()){
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_LONG).show();
            username.setError("Please enter a username");
        } else if (pass.isEmpty()){
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_LONG).show();
            password.setError("Please enter a password");
        }
        // Find and get user credentials to compare with inputted text
        ArrayList<String> credential = searchForUser(user, pass);
        if (credential == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show();
            username.setError("The user was not found");
        }
        if (credential.get(0).equals(user)) {
            validUsername = true;
        }
        if (credential.size() < 3) {
            Toast.makeText(this, "Password is incorrect", Toast.LENGTH_LONG).show();
            password.setError("Password is incorrect");
        } else if (credential.get(1).equals(pass)) {
            validPassword = true;
        }
        if (validUsername && validPassword) {
            Toast.makeText(this, "Welcome " + credential.get(0) + "#" + credential.get(2), Toast.LENGTH_LONG).show();
            setContentView(R.layout.post);
            textViewResult = findViewById(R.id.textViewPost);
            textViewResult.append("Welcome " + credential.get(0) + " UserId#" + credential.get(2) + "\n\n");
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
                    }
                    List<Post> posts = response.body();
                    for (Post post : posts) {
                        if(post.getUserId() == Integer.parseInt(credential.get(2))) {
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
}
