/*
 * EasyTravel
 * Copyright (C) 2011 Michael Hohl <http://www.hohl.co.at/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.co.hohl.utils.network;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

/**
 * Used for downloading a file from http.
 *
 * @author Michael Hohl
 */
public class Download extends Observable implements Runnable {
    /* Max size of download buffer. */
    private static final int MAX_BUFFER_SIZE = 1024;

    // These are the status codes.
    public static final int DOWNLOADING = 0;
    public static final int PAUSED = 1;
    public static final int COMPLETE = 2;
    public static final int CANCELLED = 3;
    public static final int ERROR = 4;

    /**
     * Url to download.
     */
    private final URL url;

    /**
     * Target file to download.
     */
    private final File targetFile;

    /**
     * Size of download in bytes.
     */
    private int size;

    /**
     * Bytes already downloaded.
     */
    private int downloaded;

    /**
     * Current status of the download.
     */
    private int status;

    /**
     * Downloads the passed url.
     *
     * @param url        url to download.
     * @param targetFile target file to store the download.
     */
    public Download(URL url, File targetFile) {
        this.url = url;
        this.targetFile = targetFile;
        size = -1;
        downloaded = 0;
        status = DOWNLOADING;

        // Begin the download.
        download();
    }

    // Get this downloads URL.
    public String getUrl() {
        return url.toString();
    }

    /**
     * @return the target file.
     */
    public File getTargetFile() {
        if (status == COMPLETE) {
            return targetFile;
        } else {
            return null;
        }
    }

    // Get this downloads size.
    public int getSize() {
        return size;
    }

    // Get this downloads progress.
    public float getProgress() {
        return ((float) downloaded / size) * 100;
    }

    // Get this downloads status.
    public int getStatus() {
        return status;
    }

    // Pause this download.
    public void pause() {
        status = PAUSED;
        stateChanged();
    }

    // Resume this download.
    public void resume() {
        status = DOWNLOADING;
        stateChanged();
        download();
    }

    // Cancel this download.
    public void cancel() {
        status = CANCELLED;
        stateChanged();
    }

    /**
     * Called when the download completed.
     *
     * @param downloadedFile the downloaded file.
     */
    public void onComplete(File downloadedFile) {
    }

    /**
     * Called when an error occurs on downloading.
     */
    public void onError() {
    }

    // Mark this download as having an error.
    private void error() {
        status = ERROR;
        stateChanged();
        onError();
    }

    // Start or resume downloading.
    private void download() {
        Thread thread = new Thread(this);
        thread.start();
    }

    // Get file name portion of URL.
    private String getFileName(URL url) {
        String fileName = url.getFile();
        return fileName.substring(fileName.lastIndexOf('/') + 1);
    }

    // Download file.
    public void run() {
        RandomAccessFile file = null;
        InputStream stream = null;

        try {
            // Open connection to URL.
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Specify what portion of file to download.
            connection.setRequestProperty("Range", "bytes=" + downloaded + "-");

            // Connect to server.
            connection.connect();

            // Make sure response code is in the 200 range.
            if (connection.getResponseCode() / 100 != 2) {
                error();
            }

            // Check for valid content length.
            int contentLength = connection.getContentLength();
            if (contentLength < 1) {
                error();
            }

            /* Set the size for this download if it hasn't been already set. */
            if (size == -1) {
                size = contentLength;
                stateChanged();
            }

            // Open file and seek to the end of it.
            file = new RandomAccessFile(targetFile, "rw");
            file.seek(downloaded);

            stream = connection.getInputStream();
            while (status == DOWNLOADING) {
                /* Size buffer according to how much of the file is left to download. */
                byte buffer[];
                if (size - downloaded > MAX_BUFFER_SIZE) {
                    buffer = new byte[MAX_BUFFER_SIZE];
                } else {
                    buffer = new byte[size - downloaded];
                }

                // Read from server into buffer.
                int read = stream.read(buffer);
                if (read == -1) {
                    break;
                }

                // Write buffer to file.
                file.write(buffer, 0, read);
                downloaded += read;
                stateChanged();
            }

            /* Change status to complete if this point was reached because downloading has finished. */
            if (status == DOWNLOADING) {
                status = COMPLETE;
                stateChanged();
                onComplete(targetFile);
            }
        } catch (Exception e) {
            error();
        } finally {
            // Close file.
            if (file != null) {
                try {
                    file.close();
                } catch (Exception e) {
                }
            }

            // Close connection to server.
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                }
            }
        }
    }

    // Notify observers that this downloads status has changed.
    private void stateChanged() {
        setChanged();
        notifyObservers();
    }
}