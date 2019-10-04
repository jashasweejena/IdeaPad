package com.jashasweejena.ideapad.adapters;

import androidx.recyclerview.widget.RecyclerView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;

public abstract class RealmRecyclerViewAdapter<T extends RealmObject> extends RecyclerView.Adapter {

    private RealmBaseAdapter<T> realmBaseAdapter;

    T getItem(int position) {
        return realmBaseAdapter.getItem(position);
    }

    public void setRealmBaseAdapter(RealmBaseAdapter<T> realmBaseAdapter) {
        this.realmBaseAdapter = realmBaseAdapter;
    }
}
