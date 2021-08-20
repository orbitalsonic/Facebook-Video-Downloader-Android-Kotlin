package com.thanos.romidownloader.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.Toast;
import com.io.tools.android.ramiloif.folderchooser.ChooseDirectoryDialog;
import com.thanos.romidownloader.R;
import com.thanos.romidownloader.adapter.SaveVideoAdapter;
import com.thanos.romidownloader.databinding.ActivityDownloadedVideosBinding;
import com.thanos.romidownloader.utils.VideoFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class DownloadedVideos extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, SaveVideoAdapter.OnItemClickListener {

    private File[] files;
    private Cursor videocursor;
    private File[] allFiles;
    private SaveVideoAdapter adapter;
    private ArrayList<VideoFile> videoFileArrayList = new ArrayList<>();

    ActivityDownloadedVideosBinding binding;

    private int PERMISSION_ALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_downloaded_videos);
        binding.saveSwipe.setOnRefreshListener(this::onRefresh);

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getApplicationContext());
        lbm.registerReceiver(receiver, new IntentFilter("savefile"));

        if (ContextCompat.checkSelfPermission(DownloadedVideos.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            init_phone_video_grid();

        } else {
            requestStoragePermission();
        }

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("isfinish", false)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                            init_phone_video_grid();
                        }
                    }
                }, 700);
            }
        }

    };


    private void init_phone_video_grid() {

        System.gc();
        String[] proj = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATE_ADDED};
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArgs = new String[]{"%" + getResources().getString(R.string.video_dir) + "%"};
        videocursor = getApplicationContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                proj, selection, selectionArgs, MediaStore.Video.Media.DATE_ADDED + " DESC");
//	        Toast.makeText(getApplicationContext(), String.valueOf(videocursor.getCount()), Toast.LENGTH_SHORT).show();
        videoFileArrayList = new ArrayList<>();
        if (videocursor.moveToFirst()) {
            do {
                VideoFile videoFile = new VideoFile();
                String data = videocursor.getString(videocursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String filename = videocursor.getString(videocursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                String filesize = videocursor.getString(videocursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                String fileduration = videocursor.getString(videocursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                String filedate = videocursor.getString(videocursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                videoFile.setFilename(filename);
                videoFile.setPath(data);
                videoFile.setTotalSize(filesize);
                videoFile.setAddedDate(filedate);
                videoFile.setDuration(fileduration);
                videoFileArrayList.add(videoFile);

                // do what ever you want here
            } while (videocursor.moveToNext());
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);

        //        binding.directory.setText("Directory: "+Environment.DIRECTORY_DOWNLOADS);

        Collections.sort(videoFileArrayList, new Comparator<VideoFile>() {
            public int compare(VideoFile example, VideoFile t1) {
                Integer v1 = (int) Long.parseLong(example.getAddedDate());
                Integer v2 = (int) Long.parseLong(t1.getAddedDate());
                return v2.compareTo(v1);
            }
        });

        adapter = new SaveVideoAdapter(allFiles, videoFileArrayList, this);
        binding.rvFile.setAdapter(adapter);

        binding.saveSwipe.setRefreshing(false);


    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setCancelable(false)
                    .setMessage("This permission is needed to show videos")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(DownloadedVideos.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_ALL);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_ALL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                init_phone_video_grid();


            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onRefresh() {

        binding.saveSwipe.setRefreshing(true);
        init_phone_video_grid();
    }

    @Override
    public void onItemClick(SaveVideoAdapter.SaveFileViewHolder holder, final int position, final VideoFile file) {
//        Toast.makeText(this, "Jhinga LaLa Huraaaa", Toast.LENGTH_SHORT).show();

        ChooseDirectoryDialog dialog =
                ChooseDirectoryDialog.builder(DownloadedVideos.this). // Context
                        titleText("Choose directory"). // The title will be shown
                        startDir(Environment.getExternalStorageDirectory().getAbsoluteFile()).// File from where to start
                        showNeverAskAgain(false). // Enable or disable 'Never ask again checkbox
                        neverAskAgainText("Never ask again"). // Text of never ask again check box(if enabled)
                        onPickListener(new ChooseDirectoryDialog.DirectoryChooseListener() {
                    @Override
                    public void onDirectoryPicked(ChooseDirectoryDialog.DialogResult result) {

                        String downloadPath = result.getPath();

                        File newFile = new File(new File(downloadPath), file.getFilename());
                        FileChannel outputChannel = null;
                        FileChannel inputChannel = null;
                        try {
                            outputChannel = new FileOutputStream(newFile).getChannel();
                            inputChannel = new FileInputStream(file.getPath()).getChannel();
                            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
                            inputChannel.close();

                            new File(file.getPath()).delete();
                            adapter.remove(position);

                            Toast.makeText(getApplicationContext(), "File Move To " + newFile.getPath(), Toast.LENGTH_SHORT).show();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (inputChannel != null) {
                                try {
                                    inputChannel.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (outputChannel != null) {
                                try {
                                    outputChannel.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }


                    }

                    @Override
                    public void onCancel() {
                        //                                resultTV.setText("operation canceled");
                    }
                }).build();
        dialog.show();

    }

}