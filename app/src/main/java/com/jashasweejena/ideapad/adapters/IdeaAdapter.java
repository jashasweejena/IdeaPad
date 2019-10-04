package com.jashasweejena.ideapad.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jashasweejena.ideapad.R;
import com.jashasweejena.ideapad.model.Idea;
import com.jashasweejena.ideapad.realm.RealmController;
import com.jashasweejena.ideapad.utils.DialogUtils;

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
        ideaViewHolder.viewForeground.setOnLongClickListener(v -> {
            fabFunction(position);
            return false;
        });

        //If single clicked, show the description
        ideaViewHolder.viewForeground.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View showDesc = layoutInflater.inflate(R.layout.show_desc, null, false);
            TypeWriterView description = showDesc.findViewById(R.id.description);
            ImageView imageView = showDesc.findViewById(R.id.drawingImageView);
            Idea idea1 = RealmController.getInstance().getAllBooks().get(position);
            String descriptionString = idea1.getDesc();

            description.setDelay(100);
            description.setText(descriptionString);

            byte[] drawingBytes = idea1.getDrawing();

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

    private void onEditComplete(Idea idea, String name, String desc) {
        realm.beginTransaction();
        idea.setName(name);
        idea.setDesc(desc);

        realm.copyToRealm(idea);
        realm.commitTransaction();
        notifyDataSetChanged();
    }

    private void fabFunction(final int position) {
        RealmController realmController = RealmController.getInstance();
        RealmResults<Idea> listOfIdeas = realmController.getAllBooks();

        Idea idea = listOfIdeas.get(position);
        DialogUtils.showIdeaDialog(context, R.string.title_edit_idea,
                idea.getName(), idea.getDesc(), (name, desc) -> onEditComplete(idea, name, desc),
                false);
    }
}
