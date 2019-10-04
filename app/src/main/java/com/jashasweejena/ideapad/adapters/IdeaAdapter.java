package com.jashasweejena.ideapad.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jashasweejena.ideapad.R;
import com.jashasweejena.ideapad.model.Idea;
import com.jashasweejena.ideapad.realm.RealmController;

import in.codeshuffle.typewriterview.TypeWriterView;
import io.realm.Realm;
import io.realm.RealmResults;

public class IdeaAdapter extends RealmRecyclerViewAdapter<Idea> {


    private final Context context;
    private Realm realm;

    public IdeaAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public IdeaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        realm = RealmController.getInstance().getRealm();

        return new IdeaViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.single_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        //Get an object of Idea at this very position
        final Idea idea = getItem(position);

        //cast the generic ViewHolder to a specific one
        final IdeaViewHolder ideaViewHolder = (IdeaViewHolder) holder;

        ideaViewHolder.name.setText(idea.getName());

        //If long pressed, launch the edit dialog
        ideaViewHolder.viewForeground.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                fabFunction(position);
                return false;

            }
        });

        //If single clicked, show the description
        ideaViewHolder.viewForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View showDesc = layoutInflater.inflate(R.layout.show_desc, null, false);

                TypeWriterView description = showDesc.findViewById(R.id.description);

                ImageView imageView = showDesc.findViewById(R.id.drawingImageView);

                description.setDelay(100);

                Idea idea = RealmController.getInstance().getAllBooks().get(position);

                String descriptionString = idea.getDesc();
                description.setText(descriptionString);

                byte[] drawingBytes = idea.getDrawing();

                if (drawingBytes != null) {

                    Bitmap drawing = BitmapFactory.decodeByteArray(drawingBytes, 0, drawingBytes.length);

                    if (drawing != null) {
                        imageView.setImageBitmap(drawing);
                    }


                }

                builder.setView(showDesc)
                        .setTitle("Description");

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {

        return RealmController.getInstance().getAllBooks().size();
    }

    public void removeItem(int position) {

        Realm r = RealmController.getInstance().getRealm();

        r.beginTransaction();

        RealmResults<Idea> results = realm.where(Idea.class).findAll();
        results.remove(position);

        r.commitTransaction();

        notifyItemRemoved(position);
    }

    public void restoreItem(Idea idea) {

        realm.beginTransaction();

        realm.copyToRealm(idea);

        realm.commitTransaction();

        notifyDataSetChanged();

    }

    public class IdeaViewHolder extends RecyclerView.ViewHolder {

        public CardView viewForeground;
        private TextView name;


        IdeaViewHolder(View itemView) {
            super(itemView);

            viewForeground = itemView.findViewById(R.id.card_idea);

            name = itemView.findViewById(R.id.name);
        }
    }

    private void fabFunction(final int position) {
        RealmController realmController = RealmController.getInstance();
        RealmResults<Idea> listOfIdeas = realmController.getAllBooks();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View content = LayoutInflater.from(context)
                .inflate(R.layout.edit_idea, null, false);

        final EditText editName = content.findViewById(R.id.editName);
        final EditText editDesc = content.findViewById(R.id.editDesc);

        editName.setText(listOfIdeas.get(position).getName());
        editDesc.setText(listOfIdeas.get(position).getDesc());

        builder.setView(content)
                .setTitle("Edit the idea")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        RealmResults<Idea> listOfIdeas = realm.where(Idea.class).findAll();

                        Idea idea = listOfIdeas.get(position);

                        realm.beginTransaction();

                        String name;
                        String desc;


                        name = editName.getText().toString();
                        desc = editDesc.getText().toString();

                        if (editName.getText() == null || editName.getText().toString().equals("") || editName.getText().toString().equals(" ")) {
                            Toast.makeText(context.getApplicationContext(), "Name field cannot be left blank!", Toast.LENGTH_SHORT).show();
                            realm.commitTransaction();
                        } else {

                            idea.setName(name);
                            idea.setDesc(desc);

                            realm.copyToRealm(idea);

                            realm.commitTransaction();

                            notifyDataSetChanged();
                        }

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
