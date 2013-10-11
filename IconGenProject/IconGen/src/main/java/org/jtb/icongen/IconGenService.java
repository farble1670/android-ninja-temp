package org.jtb.icongen;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.FileOutputStream;

public class IconGenService extends IntentService {
  private enum Size {
    MDPI(24, 24, "drawable-mdpi", DisplayMetrics.DENSITY_MEDIUM / DisplayMetrics.DENSITY_DEFAULT),
    HDPI(36, 36, "drawable-hdpi", DisplayMetrics.DENSITY_HIGH / DisplayMetrics.DENSITY_DEFAULT),
    XHDPI(48, 48, "drawable-xhdpi", DisplayMetrics.DENSITY_XHIGH / DisplayMetrics.DENSITY_DEFAULT),
    XXHDPI(72, 72, "drawable-xxhdpi", DisplayMetrics.DENSITY_XXHIGH / DisplayMetrics.DENSITY_DEFAULT);

    private final int w;
    private final int h;
    private final String folder;
    private final float scale;

    private Size(int w, int h, String folder, float scale) {
      this.w = w;
      this.h = h;
      this.folder = folder;
      this.scale = scale;
    }
  }

  private final Paint textPaint;

  public IconGenService() {
    super(IconGenService.class.getName());

    textPaint = new Paint();
    textPaint.setColor(Color.WHITE);
    textPaint.setAntiAlias(true);
    textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    textPaint.setTextAlign(Paint.Align.CENTER);
    textPaint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
    textPaint.setFakeBoldText(true);

  }

  @Override
  protected void onHandleIntent(Intent intent) {
    for (Size size : Size.values()) {
      for (int i = -99; i < 100; i++) {
        String resName;
        if (i < 0) {
          resName = String.format("ic_stat_minus_%d.png", i * -1);
        } else {
          resName = String.format("ic_stat_%d.png", i);
        }
        write(String.format("%d\u00B0", i), resName, size);
      }
    }

    for (Size size : Size.values()) {
      write("?\u00B0", "undefined.png", size);
    }
  }

  private void write(String text, String resName, Size size) {
    Bitmap b = Bitmap.createBitmap(size.w, size.h, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(b);

    textPaint.setTextSize(14 * size.scale);

    Rect bounds = new Rect();
    textPaint.getTextBounds(text, 0, text.length(), bounds);

    float x = c.getWidth() / 2f;
    float y = (c.getHeight() + bounds.height()) / 2f;

    c.drawText(text, x, y, textPaint);

    try {
      File dir = new File("/sdcard/res/" + size.folder);
      dir.mkdirs();

      FileOutputStream out = new FileOutputStream(new File(dir, resName));
      b.compress(Bitmap.CompressFormat.PNG, 90, out);
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
