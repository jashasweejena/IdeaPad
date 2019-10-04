package com.jashasweejena.ideapad.utils;

/*
 * Copyright (C) 2019 Ritayan Chakraborty <ritayanout@gmail.com>
 *
 * This file is part of IdeaPad
 *
 * IdeaPad is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * IdeaPad is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with IdeaPad.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.jashasweejena.ideapad.R;
import com.jashasweejena.ideapad.activity.CanvasActivity;

import io.realm.Realm;

public class DialogUtils {

    /**
     * Shows a dialog to let the user add a new Idea or edit an existing idea. Starts a Realm
     * transaction immediately, and commits it if the Idea add or edit operation fails. Remember
     * that by the time {@code listener} is called, {@link Realm#beginTransaction()} has already
     * been called. Call {@link Realm#commitTransaction()} in {@code listener}
     *
     * @param context        Context
     * @param title          The title res to use as the dialog title
     * @param realm          The Realm instance
     * @param overriddenName The Idea name to pre fill in the dialog. Can be null.
     * @param overriddenDesc The Idea description to pre fill in the dialog. Can be null.
     * @param listener       The listener that is called when the Idea add or edit operation
     *                       completes
     * @param allowDraw      True to show a Draw button
     */
    public static void showIdeaDialog(Context context, @StringRes int title, Realm realm,
                                      @Nullable String overriddenName, @Nullable String overriddenDesc,
                                      OnIdeaDialogClosedListener listener, boolean allowDraw) {

        realm.beginTransaction();
        final View inflater = LayoutInflater.from(context)
                .inflate(R.layout.edit_idea, null, false);
        final EditText editName = inflater.findViewById(R.id.editName);
        final EditText editDesc = inflater.findViewById(R.id.editDesc);
        editName.setText(overriddenName);
        editDesc.setText(overriddenDesc);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setView(inflater)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    // Create a new Idea instance which will store information regarding the idea
                    // in respective fields and go into the realm database

                    String nameStr = editName.getText().toString();
                    if (nameStr.isEmpty() || nameStr.matches("\\s+")) {
                        Toast.makeText(context,
                                "Name field cannot be left blank!", Toast.LENGTH_SHORT)
                                .show();
                        realm.commitTransaction();
                        return;
                    }

                    String desc1 = editDesc.getText().toString();
                    if (desc1.isEmpty() || desc1.matches("\\s+")) {
                        Toast.makeText(context,
                                "Desc field cannot be left blank!", Toast.LENGTH_SHORT)
                                .show();
                        realm.commitTransaction();
                        return;
                    }
                    listener.onIdeaSet(nameStr, desc1);

                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());

        if (allowDraw)
            builder.setNeutralButton(R.string.title_draw, (dialog, which) -> {
                Intent intent = new Intent(context, CanvasActivity.class);
                context.startActivity(intent);
            });

        builder.setOnDismissListener(dialog -> realm.commitTransaction());

        AlertDialog dialog = builder.create();
        // get the center for the clipping circle

        final View view = dialog.getWindow().getDecorView();

        view.post(() -> {
            final int centerX = view.getWidth() / 2;
            final int centerY = view.getHeight() / 2;
            // TODO Get startRadius from FAB
            // TODO Also translate animate FAB to center of screen?
            float startRadius = 20;
            float endRadius = view.getHeight();
            Animator animator = ViewAnimationUtils
                    .createCircularReveal(view, centerX, centerY, startRadius, endRadius);
            animator.setDuration(500);
            animator.start();
        });

        dialog.show();
    }

    public interface OnIdeaDialogClosedListener {
        void onIdeaSet(String name, String desc);
    }
}
