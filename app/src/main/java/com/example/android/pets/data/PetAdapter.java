package com.example.android.pets.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.R;

import static com.example.android.pets.data.PetContract.PetEntry;

public class PetAdapter extends CursorAdapter {

    public PetAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.pet_list_item, parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Get the TextViews we need to populate.
        TextView petNameTv = view.findViewById(R.id.pet_name_tv);
        TextView petBreedTv = view.findViewById(R.id.pet_breed_tv);
        // Get the data we need to populate with.
        String petNameString = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME));
        String petBreedString = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED));
        // Populate the TextViews with the data.
        petNameTv.setText(petNameString);
        petBreedTv.setText(petBreedString);
    }
}
