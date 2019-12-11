package com.example.naber;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {


    private View group_Fragment_View;
    private ListView listView;
    private ArrayAdapter<String> stringArrayAdapter;
    private ArrayList<String> list = new ArrayList<>();
    private DatabaseReference reference;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        group_Fragment_View = inflater.inflate(R.layout.fragment_chats, container, false);
        reference = FirebaseDatabase.getInstance().getReference().child("Gruplar");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();


                while (iterator.hasNext())
                {
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }

                list.clear();
                list.addAll(set);
                stringArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView = group_Fragment_View.findViewById(R.id.Message_View);
        stringArrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list);
        listView.setAdapter(stringArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String Grup_adi = parent.getItemAtPosition(position).toString();

                Intent Grup_chatt_intent = new Intent(getContext(),GroupChat_Activity.class);
                Grup_chatt_intent.putExtra("Grup_Name",Grup_adi);
                startActivity(Grup_chatt_intent);
            }
        });

        return group_Fragment_View;
    }

}
