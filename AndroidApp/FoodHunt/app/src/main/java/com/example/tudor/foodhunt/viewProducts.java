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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Vector;

public class viewProducts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_products);

        final Button button = (Button) findViewById(R.id.button5);

        final TableLayout tableLayout = (TableLayout) findViewById(R.id.table);
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);

        final FoodHunt foodHunt = new FoodHunt();

        Vector<String> categories = foodHunt.getCategories(foodHunt.getProduseKaufland());
        categories.add(0,"Toate");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,categories);
        spinner.setAdapter(adapter);

        showProducts(tableLayout,foodHunt.getProduseKaufland());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showProducts(tableLayout,foodHunt.findAllCategory(parent.getItemAtPosition(position).toString(),foodHunt.getProduseKaufland()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showProducts(tableLayout,foodHunt.getProduseKaufland());
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
