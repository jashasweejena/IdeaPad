package com.jashasweejena.ideapad.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.ajithvgiri.canvaslibrary.CanvasView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jashasweejena.ideapad.R;
import com.jashasweejena.ideapad.model.Idea;
import com.jashasweejena.ideapad.realm.RealmController;

import java.io.ByteArrayOutputStream;

import io.realm.Realm;

public class CanvasActivity extends AppCompatActivity {
    RelativeLayout parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        parentView = findViewById(R.id.parentView);
        CanvasView canvasView = new CanvasView(this);
        parentView.addView(canvasView);

        parentView.setDrawingCacheEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCanvas();
            }
        });
    }

    private void saveCanvas() {
        Bitmap bitmap = parentView.getDrawingCache();
        byte[] byteArray = convertBitmapToByteArray(bitmap);
        Realm realm = RealmController.getInstance().getRealm();

        realm.beginTransaction();
        Idea idea = new Idea();
        idea.setId(System.currentTimeMillis() + RealmController.getInstance().getAllBooks().size() + 1);
        idea.setDrawing(byteArray);
        realm.copyToRealm(idea);
        realm.commitTransaction();
        super.onBackPressed();

    }

    private byte[] convertBitmapToByteArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
