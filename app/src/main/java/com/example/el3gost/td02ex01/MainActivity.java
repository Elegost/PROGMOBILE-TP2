package com.example.el3gost.td02ex01;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity
{
    private int TAKENPHOTO = 0;
    private ImageView image;
    private File photoFile;
    private Bitmap photoBmp;
    private Button btnSave;
    private Button btnSelectFileName;
    private String fileName;
    private TextView TextView_FileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = (ImageView)findViewById(R.id.imageView);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSelectFileName = (Button) findViewById(R.id.btnChooseFile);
        TextView_FileName = (TextView) findViewById(R.id.textViewFileName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void takePhoto(View view)
    {
        File photoStorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES); //on va chercher endroit de stockage externe
        photoFile = new File(photoStorage,  "imgTemp.jpg"); //Création du fichier temporaire dans lequelle on mettra la photo
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //intent qui va servir a prendre une photo
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile)); //on insere le chemin du fichier dans l'intent
        if (intent.resolveActivity(getPackageManager()) != null) { //Vérification du fait qu'il y est bien une activité de prise de photo
            startActivityForResult(intent, TAKENPHOTO); //Lancement de l'activité de prise de photo
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKENPHOTO) //si une photo a été prise
        {
            try
            {
                photoBmp = (Bitmap) data.getExtras().get("data"); //on récupere le bitmap
            }
            catch(NullPointerException ex)
            {
                photoBmp = BitmapFactory.decodeFile(photoFile.getAbsolutePath()); //on décode le bitmap
            }
            if(photoBmp != null)
            {
                image.setImageBitmap(photoBmp); //ON applique le bitmap sur l'ImageView
                btnSelectFileName.setEnabled(true);
            }
            else
            {
                Toast.makeText(this, "Image introuvable", Toast.LENGTH_LONG).show();
            }
        }
        else
            btnSave.setEnabled(false);
    }

    public void saveImageToGallery(View view)
    {
        if(fileName != null) {
            String uri = MediaStore.Images.Media.insertImage(getContentResolver(), photoBmp, fileName, "Description"); //on ajoute une image dans la galerie en lui passant un bitmap et d'autres informations
            if (uri == null)
                Toast.makeText(this, "Echec de sauvegarde de l'image", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "Ajout de l'image : " + fileName + " à la galerie", Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(this, "Veuillez entrer un nom de fichier", Toast.LENGTH_LONG).show();
    }

    public void selectFileName(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Veuillez saisir un nom de fichier :");
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Valider", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                fileName = input.getText().toString();
                TextView_FileName.setText(fileName);
                btnSave.setEnabled(true);
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
