package com.ucas.android.threads;

import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadImageUsingAsyncTaskActivity extends AppCompatActivity {
    private ImageView downloadedImg;
    private String downloadUrl = "https://images.unsplash.com/photo-1634233942057-b75723e58180?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80";
    public String TAG = "MainActivity";
    public int PERMISSION_WRITE_TO_STORAGE = 1;
    private String imageName = "asyncImage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloadedImg = (ImageView) findViewById(R.id.imageView);
        Button imageDownloaderBtn = findViewById(R.id.downloadButton);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_TO_STORAGE);
        imageDownloaderBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new saveImage().execute(downloadUrl);
            }

        });
    }

    private class saveImage extends AsyncTask<String, Integer, String> {

        private ProgressDialog progressDialog;
        private HttpURLConnection httpURLConnection;
        private InputStream inputStream;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(DownloadImageUsingAsyncTaskActivity.this);
            progressDialog.setTitle("Download Image");
            progressDialog.setMessage("DownLoading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                inputStream = httpURLConnection.getInputStream();
                OutputStream outputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + imageName);
                int lengthOfFile = httpURLConnection.getContentLength();
                int count = 0;
                byte date[] = new byte[1024];
                long total = 0;
                while ((count = inputStream.read(date)) != -1) {
                    total += count;
                    publishProgress((int) (total * 100 / lengthOfFile));
                    outputStream.write(date, 0, count);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                httpURLConnection.disconnect();
            }
            return imageName;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }


        @Override
        protected void onPostExecute(String bitmap) {
            super.onPostExecute(bitmap);
            Toast.makeText(DownloadImageUsingAsyncTaskActivity.this, "image " + bitmap + " downloaded ", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }

}