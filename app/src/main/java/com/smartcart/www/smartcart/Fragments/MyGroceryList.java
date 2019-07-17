package com.smartcart.www.smartcart.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.smartcart.www.smartcart.ProjectAssistant.FileHelper;
import com.smartcart.www.smartcart.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyGroceryList extends Fragment implements AdapterView.OnItemClickListener {

    private EditText itemET;
    private Button btn;
    private ListView itemsList;

    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;

    public MyGroceryList() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_grocery_list, container, false);
        items = FileHelper.readData(getContext());


       return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewById(view);
        items = FileHelper.readData(getContext());

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items);
        itemsList.setAdapter(adapter);

        itemsList.setOnItemClickListener(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()){
                    case R.id.btnApproveGrocery:
                        String itemEntered = itemET.getText().toString();
                        adapter.add(itemEntered);
                        itemET.setText("");
                        FileHelper.writeData(items, getContext());
                        Toast.makeText(getContext(), "מוצר התווסף לרשימה", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });



    }

    private void findViewById(View view){
        itemET = view.findViewById(R.id.etInsertGrocery);
        btn = view.findViewById(R.id.btnApproveGrocery);
        itemsList = view.findViewById(R.id.lvGrocery);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        items.remove(position);
        adapter.notifyDataSetChanged();
        FileHelper.writeData(items, getContext());
        Toast.makeText(getContext(), "המוצר נמחק", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onDetach() {
        super.onDetach();
        adapter.clear();
        adapter.notifyDataSetChanged();
    }
}




