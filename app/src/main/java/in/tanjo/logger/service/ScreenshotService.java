package in.tanjo.logger.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.tanjo.logger.R;

public class ScreenshotService extends Service {

  private WindowManager mWindowManager;
  private LayoutInflater mLayoutInflater;
  private View mView;

  @Bind(R.id.capture_button) Button mButton;

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    init();
    return super.onStartCommand(intent, flags, startId);
  }


  @Override
  public void onDestroy() {
    mWindowManager.removeViewImmediate(mView);
    super.onDestroy();
  }

  void init() {
    mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
    mLayoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    mView = mLayoutInflater.inflate(R.layout.screenshot_view, null, false);

    ButterKnife.bind(this, mView);

    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
        PixelFormat.TRANSLUCENT);
    params.gravity = Gravity.RIGHT | Gravity.TOP;
    mWindowManager.addView(mView, params);
  }

  @OnClick(R.id.capture_button)
  void onClickCapture(View v) {
    View rootView = v.getRootView();
    rootView.setDrawingCacheEnabled(true);
    Bitmap cache = rootView.getDrawingCache();
    if (cache == null) {
      return;
    }
    Bitmap bitmap = Bitmap.createBitmap(cache);
    rootView.setDrawingCacheEnabled(false);
    // --- 以下、要チェック ----
    final String SAVE_DIR = "/Pictures/Screenshots/";
    File file = new File(Environment.getExternalStorageDirectory().getPath() + SAVE_DIR);

    if (!file.exists())   {
      file.mkdirs();
    }

    Date date = new Date();
    SimpleDateFormat filenameDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    String fileName = "Screenshot_" +  filenameDate.format(date) + ".png";
    String attachName = file.getAbsolutePath() + "/" + fileName;

    OutputStream fout = null;

    try {
      fout = new FileOutputStream(attachName);
      bitmap.compress(Bitmap.CompressFormat.PNG, 100,fout);
      fout.flush();
      fout.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // save index
    ContentValues values = new ContentValues();
    ContentResolver contentResolver = getContentResolver();
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
    values.put(MediaStore.Images.Media.TITLE, fileName);
    values.put("_data", attachName);
    contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
  }
}
