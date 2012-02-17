package com.playground_soft.chord;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.playground_soft.chordlib.Document;

public class ImportThread extends Thread {

    public interface OnFinishHandler {
        public void onFinished();
    };

    private Handler mHandler;
    private DatabaseHelper mDbHelper;
    private Activity mActivity;
    private OnFinishHandler mOnFinishHandler;
    private ProgressDialog mProgressDialog;

    private static final int MESSAGE_TYPE_ERROR = 1;
    private static final int MESSAGE_TYPE_NORMAL = 0;

    private static final int MESSAGE_ARG_START = 1;
    private static final int MESSAGE_ARG_FINISHED = 0;

    public ImportThread(Activity activity, OnFinishHandler onFinishHandler) {

        this.mActivity = activity;
        this.mDbHelper = new DatabaseHelper(mActivity);
        this.mOnFinishHandler = onFinishHandler;
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                int arg = msg.arg1;
                if (msg.what == MESSAGE_TYPE_ERROR) {

                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            mActivity);
                    builder.setMessage((String) msg.obj)
                            .setCancelable(false)
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            dialog.dismiss();

                                        }
                                    });
                    builder.create().show();
                    return;
                }

                else if (msg.what == MESSAGE_TYPE_NORMAL) {
                    if (arg == MESSAGE_ARG_START) {
                        if (mProgressDialog == null) {
                            mProgressDialog = new ProgressDialog(mActivity);
                            mProgressDialog
                                    .setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            mProgressDialog.setMessage("Refreshing...");

                            mProgressDialog.setCancelable(false);
                            mProgressDialog.setIndeterminate(true);

                            mProgressDialog.show();
                        }
                    }

                    if (arg == MESSAGE_ARG_FINISHED) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;

                        if (mOnFinishHandler != null)
                            mOnFinishHandler.onFinished();
                    }
                }

            }
        };

    }

    @Override
    public void run() {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Message msg = new Message();
            msg.what = MESSAGE_TYPE_ERROR;
            msg.obj = "Storage Unavailable";
            mHandler.sendMessage(msg);
            return;
        }

        Message startMsg = new Message();
        startMsg.what = MESSAGE_TYPE_NORMAL;
        startMsg.arg1 = MESSAGE_ARG_START;
        mHandler.sendMessage(startMsg);

        File rootExtDir = Environment.getExternalStorageDirectory();
        File inDir = new File(rootExtDir.getAbsolutePath() + "/chordpro");
        File outDir = new File(rootExtDir.getAbsoluteFile()
                + "/Android/data/com.playground_soft.chord/cache/");

        mDbHelper.deleteAllSong();
        if (!inDir.exists()) {
            inDir.mkdirs();

            Message msg = new Message();
            msg.what = MESSAGE_TYPE_ERROR;

            msg.obj = "chordpro directory doesnot exist";
            mHandler.sendMessage(msg);
            return;
        }

        if (outDir.exists()) {
            for (File file : outDir.listFiles()) {
                file.delete();
            }
            outDir.delete();
        }

        if (!outDir.mkdirs()) {
            Message msg = new Message();
            msg.what = MESSAGE_TYPE_ERROR;

            msg.obj = "unable to create cache files";
            Log.i("file",
                    "Cannot create directory: " + outDir.getAbsolutePath());
            mHandler.sendMessage(msg);
            return;
        }

        File[] inFiles = inDir.listFiles();

        for (int i = 0; i < inFiles.length; i++) {

            try {
                File inFile = inFiles[i];
                File outFile = new File(outDir.getAbsoluteFile() + "/"
                        + inFile.getName());

                outFile.createNewFile();

                copyFile(inFile, outFile);

                Document doc = Document.createFromFile(outFile);
                mDbHelper.createOrUpdate(doc.title, doc.subtitle,
                        outFile.getName());
            } catch (IOException e) {
                Message msgErr = new Message();
                msgErr.what = MESSAGE_TYPE_ERROR;

                msgErr.obj = e.getMessage();
                mHandler.sendMessage(msgErr);
                return;
            }
        }

        Message msg = new Message();
        msg.what = MESSAGE_TYPE_NORMAL;
        msg.arg1 = MESSAGE_ARG_FINISHED;

        mHandler.sendMessage(msg);
        mDbHelper.close();
    }

    private void copyFile(File from, File to) throws IOException {
        FileReader reader = new FileReader(from);
        FileWriter writer = new FileWriter(to);

        final int bufferSize = 1024;
        char[] buffer = new char[bufferSize];
        int read = 0;
        while ((read = reader.read(buffer, 0, buffer.length)) != -1) {
            writer.write(buffer, 0, read);
        }

        reader.close();
        writer.close();
    }
}
