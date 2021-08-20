package com.thanos.romidownloader.downloader;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.thanos.romidownloader.R;
import com.thanos.romidownloader.interfaces.VideoDownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import static android.content.Context.DOWNLOAD_SERVICE;

public class FbVideoDownloader implements VideoDownloader {

    private Context context;
    private String VideoURL;
    private long DownLoadID;
    private String VideoTitle;
    private ProgressDialog progressDialog;


    public FbVideoDownloader(Context context, String videoURL) {
        this.context = context;
        VideoURL = videoURL;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    public String createDirectory() {
                        String mBaseFolderPath = android.os.Environment
                        .getExternalStorageDirectory()
                        + File.separator
                        + "FacebookDownloader" + File.separator;
                if (!new File(mBaseFolderPath).exists()) {
                    new File(mBaseFolderPath).mkdir();
                }
//                String mFilePath = "file://" + mBaseFolderPath;

        File folder = new File(mBaseFolderPath);

//        File subFolder = null;
//        boolean success = true;
//        if (!folder.exists()) {
//            success = folder.mkdirs();
//        }
//        else {
//            boolean success1 = true;
//            subFolder = new File(folder.getPath()+File.separator+"Facebook Videos");
//            if(!subFolder.exists())
//            {
//                success1 = subFolder.mkdirs();
//            }
//        }
//        assert subFolder != null;
        return folder.getPath();
    }

    @Override
    public String getVideoId(String link) {
        return link;
    }

    @Override
    public void DownloadVideo() {
        new Data().execute(getVideoId(VideoURL));
    }

    @SuppressLint("StaticFieldLeak")
    private class Data extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute()
        {
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection;
            BufferedReader reader;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                String buffer = "No URL";
                String Line;
                while ((Line = reader.readLine()) != null)
                {
                    if(Line.contains("og:video:url"))
                    {
                        Line = Line.substring(Line.indexOf("og:video:url"));
                        if(Line.contains("og:title"))
                        {
                            VideoTitle = Line.substring(Line.indexOf("og:title"));
                            VideoTitle = VideoTitle.substring(ordinalIndexOf(VideoTitle,"\"",1)+1,ordinalIndexOf(VideoTitle,"\"",2));
                        }
                        Line = Line.substring(ordinalIndexOf(Line,"\"",1)+1,ordinalIndexOf(Line,"\"",2));
                        if(Line.contains("amp;")) {
                            Line = Line.replace("amp;", "");
                        }
                        if(!Line.contains("https"))
                        {
                            Line = Line.replace("http","https");
                        }
                        buffer=Line;
                        break;
                    }
                    else {
                        buffer = "No URL";
                    }
                }
                return buffer;
            } catch (IOException e) {
                return "No URL";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if(!s.contains("No URL")) {

//                String mFilePath = createDirectory();

                if(VideoTitle == null || VideoTitle.equals(""))
                {
                    VideoTitle = "fbVideo" + new Date().toString()+".mp4";
                }
                else {
                    VideoTitle = VideoTitle + ".mp4";
                }
//                File newFile = new File(mFilePath, VideoTitle);
                try {
//                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(s));
//                    request.allowScanningByMediaScanner();
//                    request.setDescription(VideoTitle)
//                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
//                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
//                            .setDestinationUri(Uri.fromFile(newFile))
//                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                            .setVisibleInDownloadsUi(true)
//                            .setTitle("Downloading");
//                    DownloadManager manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
//                    assert manager != null;
//                    DownLoadID = manager.enqueue(request);

                    Uri downloadUri = Uri.parse(s);
                    DownloadManager.Request req = new DownloadManager.Request(downloadUri);
                    req.addRequestHeader("Accept", "application/mp4");
                    req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,File.separator
                            + "FBDownloader" + File.separator+"facebook_" + VideoTitle);
                    req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    DownloadManager manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                    manager.enqueue(req);
                    showDownloadDialog();

                } catch (Exception e) {
//                    Looper.prepare();
                    Toast.makeText(context, "Video Can't be downloaded! Try Again", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                }

            }
            else {
//                Looper.prepare();
                Toast.makeText(context, "Wrong Video URL or Check Internet Connection", Toast.LENGTH_SHORT).show();
//                Looper.loop();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
//            Looper.prepare();
            Toast.makeText(context, "Video Can't be downloaded! Try Again", Toast.LENGTH_SHORT).show();
//            Looper.loop();
        }
    }

    private static int ordinalIndexOf(String str, String substr, int n) {
        int pos = -1;
        do {
            pos = str.indexOf(substr, pos + 1);
        } while (n-- > 0 && pos != -1);
        return pos;
    }

    private void showDownloadDialog() {
        final Dialog adDialog = new Dialog(context);
        adDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        adDialog.setCanceledOnTouchOutside(true);
        adDialog.setContentView(R.layout.dialog_download);
        adDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView _gotit = adDialog.findViewById(R.id._gotit);
        _gotit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adDialog.dismiss();
            }
        });


        adDialog.show();

    }
}
