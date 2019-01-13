package com.wright.paul.allergytravelcardapp.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class CardDrawable extends Drawable {

    String language;
    String allergy;
    Context context;
    Paint bluePaint = new Paint();

    public CardDrawable(Context context, String language, String allergy) {
        this.language = language;
        this.allergy = allergy;
        this.context = context;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        setBounds(0, 0, 640, 320);
        bluePaint.setARGB(55, 82, 0, 115);

        //add atc symbol



        //draw horizontal line


        //add language flag


        //add allergy flag


        //add Title
        Paint paint = new Paint();
        canvas.drawPaint(paint);
        paint.setColor(Color.BLACK);
        paint.setTextSize(16);
        canvas.drawText("sldljsdjlkdf", 320, 20, paint);

        //add card body

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public void setAlpha(int i) {

    }
}
