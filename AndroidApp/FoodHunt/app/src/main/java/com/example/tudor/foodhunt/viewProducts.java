package com.example.tudor.foodhunt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Vector;

public class viewProducts extends AppCompatActivity {

    String lastCategory;
    String lastStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_products);

        final Button button = (Button) findViewById(R.id.button5);
        final TableLayout tableLayout = (TableLayout) findViewById(R.id.table);
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        Spinner stores = (Spinner) findViewById(R.id.spinner);
        final FoodHunt foodHunt = new FoodHunt();
        final SearchView searchView = (SearchView)findViewById(R.id.searchView);

        Vector<String> categories = foodHunt.getCategories(foodHunt.getProduseKaufland());
        categories.add(0,"Toate");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,categories);
        spinner.setAdapter(adapter);

        categories = new Vector<>();
        categories.add("Toate");
        categories.add("Kaufland");
        categories.add("Carrefour");

        adapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,categories);
        stores.setAdapter(adapter);

        showProducts(tableLayout,foodHunt.getProduseKaufland());

        lastStore = "Toate";
        lastCategory = "Toate";
        lastCategory = "Toate";

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lastCategory = parent.getItemAtPosition(position).toString();
                showProducts(tableLayout,foodHunt.findAllCategory(parent.getItemAtPosition(position).toString(),foodHunt.getProduseKaufland()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                lastCategory = "Toate";
                switch (lastStore) {
                    case "Toate":
                        showProducts(tableLayout, foodHunt.getToateProdusele());
                        break;
                    case "Kaufland":
                        showProducts(tableLayout, foodHunt.getProduseKaufland());
                        break;
                    case "Carrefour":
                        showProducts(tableLayout, foodHunt.getProduseCarrefour());
                        break;
                }
            }
        });

        stores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lastStore = parent.getItemAtPosition(position).toString();
                switch (lastStore) {
                    case "Toate":
                        showProducts(tableLayout,foodHunt.findAllCategory(lastCategory,foodHunt.getToateProdusele()));
                        break;
                    case "Kaufland":
                        showProducts(tableLayout,foodHunt.findAllCategory(lastCategory,foodHunt.getProduseKaufland()));
                        break;
                    case "Carrefour":
                        showProducts(tableLayout,foodHunt.findAllCategory(lastCategory,foodHunt.getProduseCarrefour()));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                lastStore = "Toate";
                showProducts(tableLayout,foodHunt.findAllCategory(lastCategory,foodHunt.getProduseKaufland()));
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                switch (lastStore) {
                    case "Toate":
                        showProducts(tableLayout,foodHunt.searchProduct(searchView.getQuery().toString(),foodHunt.findAllCategory(lastCategory,foodHunt.getToateProdusele())));
                        break;
                    case "Kaufland":
                        showProducts(tableLayout,foodHunt.searchProduct(searchView.getQuery().toString(),foodHunt.findAllCategory(lastCategory,foodHunt.getProduseKaufland())));
                        break;
                    case "Carrefour":
                        showProducts(tableLayout,foodHunt.searchProduct(searchView.getQuery().toString(),foodHunt.findAllCategory(lastCategory,foodHunt.getProduseCarrefour())));
                        break;
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                switch (lastStore) {
                    case "Toate":
                        showProducts(tableLayout,foodHunt.searchProduct(searchView.getQuery().toString(),foodHunt.findAllCategory(lastCategory,foodHunt.getToateProdusele())));
                        break;
                    case "Kaufland":
                        showProducts(tableLayout,foodHunt.searchProduct(searchView.getQuery().toString(),foodHunt.findAllCategory(lastCategory,foodHunt.getProduseKaufland())));
                        break;
                    case "Carrefour":
                        showProducts(tableLayout,foodHunt.searchProduct(searchView.getQuery().toString(),foodHunt.findAllCategory(lastCategory,foodHunt.getProduseCarrefour())));
                        break;
                }
                return  true;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(viewProducts.this, User_Login.class);
                startActivity(intent);
            }
        });

    }

    void showProducts(TableLayout tableLayout,Vector<FoodHunt.Product> products){
        tableLayout.removeAllViews();
        for(FoodHunt.Product product : products) {

            final TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

            final TextView text = new TextView(this);
            text.setText(product.toString().replace("&quot",""));
            text.setTextSize(15);
            text.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

            tableRow.addView(text);

            tableLayout.addView(tableRow);
        }
    }
}
