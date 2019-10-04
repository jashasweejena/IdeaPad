package com.jashasweejena.ideapad.activity;

import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.jashasweejena.ideapad.R;
import com.jashasweejena.ideapad.adapters.IdeaAdapter;
import com.jashasweejena.ideapad.adapters.RealmModelAdapter;
import com.jashasweejena.ideapad.app.Prefs;
import com.jashasweejena.ideapad.app.RecyclerTouchItemHelper;
import com.jashasweejena.ideapad.model.Idea;
import com.jashasweejena.ideapad.realm.RealmController;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements RecyclerTouchItemHelper.RecyclerTouchListener {

    private final static String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.coordinatorlayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private IdeaAdapter recyclerViewAdapter;
    private Realm realm;
    private RealmResults<Idea> listOfIdeas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        coordinatorLayout = findViewById(R.id.coordinatorlayout);
        setSupportActionBar(toolbar);
        realm = RealmController.getInstance().getRealm();
        setUpRecycler();
        listOfIdeas = RealmController.getInstance().getAllBooks();
        setRealmAdapter(listOfIdeas);
        handleIntent();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabFunction(null);
            }
        });
    }

    private void setRealmAdapter(RealmResults<Idea> listOfIdeas) {

        RealmModelAdapter<Idea> realmAdapter =
                new RealmModelAdapter<>(this, listOfIdeas, true);
        //Join the RecyclerView Adapter and the realmAdapter
        recyclerViewAdapter.setRealmBaseAdapter(realmAdapter);

        //Redraw the RecyclerView layout
        recyclerViewAdapter.notifyDataSetChanged();
    }

    public void setUpRecycler() {
        //Assign ItemTouchHelper to RecyclerView.
        ItemTouchHelper.SimpleCallback itemTouchHelper =
                new RecyclerTouchItemHelper(0, ItemTouchHelper.LEFT, this);
        recyclerViewAdapter = new IdeaAdapter(this);

        //Set up Vertical LinearLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(recyclerViewAdapter);

        runAnimation(recyclerView);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private void runAnimation(RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int deletedPosition) {
        if (viewHolder instanceof IdeaAdapter.IdeaViewHolder) {
            //Store the object to be हलाल so that you can resurrect it back, if you want.
            final Idea deletedIdea = RealmController.getInstance().getAllBooks().get(deletedPosition);

            //Store the fields of current idea object which
            //is gonna be deleted and then while restoring it
            //create a new Realm object and set these fields
            //to new object and pass the new object to restoreItem.
            final String deletedName = deletedIdea.getName();
            final String deletedDesc = deletedIdea.getDesc();
            final long deletedId = deletedIdea.getId();

            Log.d(TAG, "onSwiped: " + "Adapter position before deletion " + deletedPosition);
            Log.d(TAG, "onSwiped: " + "Name of Item before deletion " + deletedName);

            recyclerViewAdapter.removeItem(deletedPosition);

            if (RealmController.getInstance().getAllBooks().size() == 0) {
                Prefs.with(getApplicationContext()).setPreLoad(false);
            }

            //As item is removed, show SnackBar

            Snackbar snackbar = Snackbar.make(coordinatorLayout, deletedName + " Removed from cart", Snackbar.LENGTH_SHORT);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Create new Idea object with fields of old object and use it instead.
                    Idea newIdea = new Idea();
                    newIdea.setId(deletedId);
                    newIdea.setName(deletedName);
                    newIdea.setDesc(deletedDesc);

                    //Restore the deleted item.
                    recyclerViewAdapter.restoreItem(newIdea);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void fabFunction(@Nullable final String desc) {
        final View content = getLayoutInflater().inflate(R.layout.edit_idea, null, false);

        final EditText editName = content.findViewById(R.id.editName);
        final EditText editDesc = content.findViewById(R.id.editDesc);

        if (desc != null) {
            editDesc.setText(desc);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Mann mei ladoo phoota?")
                .setView(content)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Create a new Idea instance which will store information
                        //regarding the idea in respective fields and go into
                        //the realm database

                        if (desc == null) {
                            if (editName.getText() == null || editName.getText().toString().equals("") || editName.getText().toString().equals(" ")) {
                                Toast.makeText(MainActivity.this, "Name field cannot be left blank!", Toast.LENGTH_SHORT).show();
                            }
                            String name = editName.getText().toString();
                            String desc = editDesc.getText().toString();

                            addIdeaToRealm(name, desc);
                        } else {
                            if (editName.getText() == null || editName.getText().toString().equals("") || editName.getText().toString().equals(" ")) {
                                Toast.makeText(MainActivity.this, "Name field cannot be left blank!", Toast.LENGTH_SHORT).show();
                            }

                            String name = editName.getText().toString();
                            addIdeaToRealm(name, desc);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton(R.string.title_draw, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, CanvasActivity.class);
                        startActivity(intent);
                    }
                });


        AlertDialog dialog = builder.create();
        // get the center for the clipping circle

        final View view = dialog.getWindow().getDecorView();

        view.post(new Runnable() {
            @Override
            public void run() {
                final int centerX = view.getWidth() / 2;
                final int centerY = view.getHeight() / 2;
                // TODO Get startRadius from FAB
                // TODO Also translate animate FAB to center of screen?
                float startRadius = 20;
                float endRadius = view.getHeight();
                Animator animator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
                animator.setDuration(500);
                animator.start();
            }
        });

        dialog.show();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type.equals("text/plain")) {
            String sentText = intent.getStringExtra(Intent.EXTRA_TEXT);

            if (sentText != null) {
                fabFunction(sentText);
            }
        }
    }

    private void addIdeaToRealm(String name, String desc) {
        realm.beginTransaction();

        Idea idea = new Idea();
        idea.setId(System.currentTimeMillis() + RealmController.getInstance().getAllBooks().size() + 1);
        idea.setName(name);
        idea.setDesc(desc);

        realm.copyToRealm(idea);
        realm.commitTransaction();
    }
}