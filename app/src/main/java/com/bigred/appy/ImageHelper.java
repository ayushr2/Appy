package com.bigred.appy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


import com.microsoft.projectoxford.emotion.contract.FaceRectangle;

/**
 * Created by sanjitsingh on 9/16/17.
 */

public class ImageHelper {
    public static Bitmap drawRectonBitMap(Bitmap mBitmap, FaceRectangle faceRectangle, String status)
    {
        Bitmap bitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);


        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(12);


        canvas.drawRect(faceRectangle.left,
                faceRectangle.top,
                faceRectangle.left + faceRectangle.width,
                faceRectangle.top + faceRectangle.height,
                paint);
        int cX = faceRectangle.left+faceRectangle.width;
        int cY = faceRectangle.top+faceRectangle.height;

        drawTextOnBitmap(canvas,100,cX/2+cX/5, cY+100, Color.WHITE,status);

        return bitmap;

    }

    private static void drawTextOnBitmap(Canvas canvas, int textSize, int eX, int eY, int color, String status) {
        Paint tempTextPlain = new Paint();
        tempTextPlain.setAntiAlias(true);
        tempTextPlain.setStyle(Paint.Style.FILL);
        tempTextPlain.setColor(color);
        tempTextPlain.setTextSize(textSize);

        canvas.drawText(status,eX,eY,tempTextPlain);

    }
}
