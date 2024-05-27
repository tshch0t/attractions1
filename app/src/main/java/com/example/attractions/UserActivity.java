package com.example.attractions;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UserActivity extends AppCompatActivity {

    Button exit;
    Bitmap Photo;
    TextView Text;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    double l1,l2;
    long userId=0;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Text = findViewById(R.id.Text);
        exit = findViewById(R.id.exit);

            exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.open();
        String search="";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getLong("id");
            search=extras.getString("marker");
        }
        // если 0, то добавление
        if (userId > 0) {
            // получаем элемент по id из бд
            userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                    DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
            userCursor.moveToFirst();
            String s2=userCursor.getString(1);
            String[] mass=s2.split(",");
            l1=Double.parseDouble(mass[0]);
            l2=Double.parseDouble(mass[1]);
            String s1=userCursor.getString(0)+"\n";
            s1+=userCursor.getString(2);
            //yearBox.setText(String.valueOf(userCursor.getInt(2)));
            Text.setText(s1);
            byte[] image = userCursor.getBlob(3);

            if(image!=null) {

                Photo = getImage(image);

                SpannableStringBuilder builder = new SpannableStringBuilder();

                builder.append(userCursor.getString(2))

                        .append("\n\r\n" +

                                "\n")

                        .append(" ", new ImageSpan(this, Photo), 0);

                Text.setText(builder);

            }
            userCursor.close();
        }
        else if (search.length()>0) {
            // получаем элемент по id из бд
            userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                    DatabaseHelper.COLUMN_NAME + "=?", new String[]{String.valueOf(search)});
            userCursor.moveToFirst();
            String s2=userCursor.getString(1);
            String[] mass=s2.split(",");
            l1=Double.parseDouble(mass[0]);
            l2=Double.parseDouble(mass[1]);
            String s1=userCursor.getString(0)+"\n";
            s1+=userCursor.getString(2);
            //yearBox.setText(String.valueOf(userCursor.getInt(2)));
            Text.setText(s1);
            byte[] image = userCursor.getBlob(3);

            if(image!=null) {

                Photo = getImage(image);

                SpannableStringBuilder builder = new SpannableStringBuilder();

                builder.append(userCursor.getString(2))

                        .append("\n\r\n" +

                                "\n")

                        .append(" ", new ImageSpan(this, Photo), 0);

                Text.setText(builder);

            }
            userCursor.close();
        }
    }

   
    public void onClickMap(View v) {


        String format = "geo:0,0?q=" + l1+ "," + l2 + "( Location title)";

        Uri uri = Uri.parse(format);


        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
    public static Bitmap getImage(byte[] image) {

        return BitmapFactory.decodeByteArray(image, 0, image.length);

    }

}