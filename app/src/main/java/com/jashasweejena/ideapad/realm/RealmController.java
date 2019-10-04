package com.jashasweejena.ideapad.realm;

import com.jashasweejena.ideapad.model.Idea;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmController  {

    private static RealmController instance;
    private final Realm realm;


    private RealmController() {
        // When a new instance of RealmController is created, a new instance of Realm is created too
        realm = Realm.getDefaultInstance();
    }

    public static RealmController getInstance() {

        if(instance == null)
        {
            instance = new RealmController();
        }

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    public void refresh() {

        realm.refresh();

    }

    //Clear all objects of Idea.class
    public void clearAll() {

        realm.beginTransaction();
        realm.clear(Idea.class);
        realm.commitTransaction();

    }

    //Return an arraylist consisting of all the objects of Idea.class
    public RealmResults<Idea> getAllBooks() {

        return realm.where(Idea.class).findAll();
    }

    public boolean isEmpty() {

        return !realm.allObjects(Idea.class).isEmpty();

    }
}
