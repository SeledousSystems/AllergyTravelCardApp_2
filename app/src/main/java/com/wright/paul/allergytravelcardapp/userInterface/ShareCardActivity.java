package com.wright.paul.allergytravelcardapp.userInterface;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.wright.paul.allergytravelcardapp.R;
import com.wright.paul.allergytravelcardapp.model.CardDrawable;
import com.wright.paul.allergytravelcardapp.model.CardManager;

public class ShareCardActivity extends AppCompatActivity {
    ImageView imageView;
    String language;
    String allergy;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardimage);
        language = getIntent().getStringExtra(CardManager.ls);
        allergy = getIntent().getStringExtra(CardManager.as);

        imageView = findViewById(R.id.imageView);

        //TODO get drawable
        //Drawable cardDrawable = new CardDrawable(this, language, allergy);
        //cardDrawable.draw(canvas);
//        Paint paint = new Paint();
//        canvas.drawPaint(paint);
//        paint.setColor(Color.BLACK);
//        paint.setTextSize(16);
//        canvas.drawText("sldljsdjlkdf", 0, 0, paint);
//
//        imageView.draw(canvas);

        //imageView.setImageDrawable(cardDrawable);

        int width = imageView.getMaxWidth();
        int height = imageView.getMaxHeight();
        Log.d("dilifhiufh", "width = " + width + " heigh = " + height);


        Bitmap bitmap = Bitmap.createBitmap(640, 350, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //paint.setColor(Color.BLACK);

        //add atc symbol



        //draw horizontal line


        //add language flag


        //add allergy flag


        //add Title
        Paint whitePaint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(whitePaint);

        //paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        canvas.drawText("Some Text", 10, 25, paint);

        //add card body



        //canvas.drawCircle(50, 50, 10, paint);


        //set bitmap to the image
        imageView.setImageBitmap(bitmap);


    }
}
