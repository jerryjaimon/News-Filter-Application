package com.news.newsapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.news.newsapplication.Model.Adapter;
import com.news.newsapplication.Model.ApiClient;
import com.news.newsapplication.Model.Articles;
import com.news.newsapplication.Model.Headlines;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsView extends AppCompatActivity {
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText etQuery;
    Button btnSearch,btnAboutUs;
    Dialog dialog;
    final String API_KEY = "4ab12fe1f19a49efbc62a477b7a7d0ed";
    Adapter adapter;
    Articles art;
    List<Articles> articles = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsview);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        recyclerView = findViewById(R.id.recyclerView);

        dialog = new Dialog(NewsView.this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final String country = getCountry();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieveJson("",country,API_KEY);
            }
        });
        retrieveJson("",country,API_KEY);

    }

    public void retrieveJson(String query ,String country, String apiKey){


        swipeRefreshLayout.setRefreshing(true);
        Call<Headlines> call;
        call= ApiClient.getInstance().getApi().getHeadlines(country,apiKey);


        call.enqueue(new Callback<Headlines>() {
            @Override
            public void onResponse(Call<Headlines> call, Response<Headlines> response) {
                if (response.isSuccessful() && response.body().getArticles() != null){
                    swipeRefreshLayout.setRefreshing(false);
                    articles.clear();
                    articles = response.body().getArticles();
                    Integer flag = 0;
                    List <Articles> finalList = new ArrayList<>();
                    for (int i = 0; i < articles.size(); i++) {
                        art = articles.get(i);
                        if(art.getDescription().toLowerCase().contains("corona") || art.getDescription().toLowerCase().contains("covid")||art.getDescription().toLowerCase().contains("virus") ||
                                art.getTitle().toLowerCase().contains("corona") || art.getTitle().toLowerCase().contains("covid")||art.getTitle().toLowerCase().contains("virus") ) {
                            finalList.add(articles.get(i));
                        }
                    }
                    adapter = new Adapter(NewsView.this, finalList);
                    recyclerView.setAdapter(adapter);


                }
            }

            @Override
            public void onFailure(Call<Headlines> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(NewsView.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getCountry(){
        Locale locale = Locale.getDefault();
        String country = "in";
        return country.toLowerCase();
    }

}
