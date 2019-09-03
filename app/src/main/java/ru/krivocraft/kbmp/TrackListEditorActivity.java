package ru.krivocraft.kbmp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import ru.krivocraft.kbmp.api.TrackListsStorageManager;
import ru.krivocraft.kbmp.constants.Constants;

public class TrackListEditorActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 2831;

    private TrackList source;
    private TrackList changed;

    private ImageView art;
    private final String TYPE_IMAGE = "image/*";
    private final String[] MIME_TYPES = new String[]{"image/jpeg", "image/png"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list_editor);
        source = TrackList.fromJson(getIntent().getStringExtra(Constants.Extras.EXTRA_TRACK_LIST));
        changed = TrackList.fromJson(getIntent().getStringExtra(Constants.Extras.EXTRA_TRACK_LIST));
        art = findViewById(R.id.track_list_editor_image);
        art.setClipToOutline(true);

        TrackListsStorageManager storageManager = new TrackListsStorageManager(TrackListEditorActivity.this);

        EditText title = findViewById(R.id.track_list_editor_edit_text);
        title.setText(source.getDisplayName());
        title.addTextChangedListener(new TextChangeSolver() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changed.setDisplayName(s.toString());
            }
        });

        Uri art = changed.getArt();
        if (!art.equals(Uri.EMPTY)) {
            try {
                Bitmap input = MediaStore.Images.Media.getBitmap(this.getContentResolver(), art);
                Bitmap bitmap = ThumbnailUtils.extractThumbnail(input, getSquareDimensions(input), getSquareDimensions(input));
                this.art.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.art.setImageDrawable(getDrawable(R.drawable.ic_icon));
        }

        Button pick = findViewById(R.id.track_list_editor_button_pick);
        pick.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType(TYPE_IMAGE);
            intent.putExtra(Intent.EXTRA_MIME_TYPES, MIME_TYPES);
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        });

        Button delete = findViewById(R.id.track_list_editor_button_delete);
        delete.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(TrackListEditorActivity.this)
                    .setTitle("Are you sure?")
                    .setMessage("Do you really want to delete " + source.getDisplayName() + "?")
                    .setPositiveButton("DELETE", (dialog12, which) -> storageManager.removeTrackList(source))
                    .setNegativeButton("CANCEL", (dialog1, which) -> dialog1.dismiss())
                    .create();
            dialog.show();
        });


        Button apply = findViewById(R.id.track_list_editor_button_apply);
        apply.setOnClickListener(v -> {
            storageManager.removeTrackList(source);
            storageManager.writeTrackList(changed);
            finish();
        });

        Button cancel = findViewById(R.id.track_list_editor_button_cancel);
        cancel.setOnClickListener(v -> showNotSavedPrompt());
    }

    private int getSquareDimensions(Bitmap bitmap) {
        return Math.min(bitmap.getHeight(), bitmap.getWidth());
    }

    private void showNotSavedPrompt() {
        if (!source.equals(changed)) {
            AlertDialog ad = new AlertDialog.Builder(this)
                    .setMessage("Do you really want to leave without saving?")
                    .setTitle("Wait!")
                    .setPositiveButton("Yes", (dialog, which) -> finish())
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .create();
            ad.show();
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == GALLERY_REQUEST_CODE) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap input = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        Bitmap bitmap = ThumbnailUtils.extractThumbnail(input, getSquareDimensions(input), getSquareDimensions(input));
                        art.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    changed.setArt(selectedImage);
                }
            }

    }
}
