package com.thanos.romidownloader.interfaces;

public interface VideoDownloader {

    String createDirectory();

    String getVideoId(String link);

    void DownloadVideo();
}
