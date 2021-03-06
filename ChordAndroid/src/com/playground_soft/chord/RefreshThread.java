package com.playground_soft.chord;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.playground_soft.chord.db.DatabaseHelper;
import com.playground_soft.chordlib.Document;

public class RefreshThread extends Thread {

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

    public RefreshThread(Activity activity, OnFinishHandler onFinishHandler) {

        this.mActivity = activity;
        this.mDbHelper = new DatabaseHelper(mActivity);
        this.mOnFinishHandler = onFinishHandler;
        mHandler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {

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
                    return true;
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
                return true;
            }
        });
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

        File inDir = FileSystemUtils.EXTERNAL_DIR;
        File outDir = FileSystemUtils.INTERNAL_DIR;

        mDbHelper.deleteAllSong();
        if (!inDir.exists()) {
            inDir.mkdirs();

            Message msg = new Message();
            msg.what = MESSAGE_TYPE_NORMAL;
            msg.arg1 = MESSAGE_ARG_FINISHED;

            mHandler.sendMessage(msg);
            mDbHelper.close();
            return;
        }

        if (outDir.exists()) {

            deleteDir(outDir);
        }

        if (!outDir.mkdirs()) {
            Message msg = new Message();
            msg.what = MESSAGE_TYPE_ERROR;

            msg.obj = "unable to create cache files";
            Log.i("file",
                    "Cannot create directory: " + outDir.getAbsolutePath());
            mHandler.sendMessage(msg);
            mDbHelper.close();
            return;
        }

        try {
            copyFiles(inDir, outDir);
        } catch (IOException e) {
            Message msgErr = new Message();
            msgErr.what = MESSAGE_TYPE_ERROR;

            msgErr.obj = e.getMessage();
            mHandler.sendMessage(msgErr);
            return;
        }

        Message msg = new Message();
        msg.what = MESSAGE_TYPE_NORMAL;
        msg.arg1 = MESSAGE_ARG_FINISHED;

        mHandler.sendMessage(msg);
        mDbHelper.close();
    }

    private void deleteDir(File outDir) {
        for (File file : outDir.listFiles()) {
            if (file.isDirectory())
                deleteDir(file);
            else
                file.delete();
        }
        outDir.delete();
    }

    private void copyFiles(File inDir, File outDir) throws IOException {
        File[] inFiles = inDir.listFiles();

        for (int i = 0; i < inFiles.length; i++) {
            File inFile = inFiles[i];
            String inFileName = inFile.getName();
            File outFile = new File(outDir.getAbsoluteFile() + "/"
                    + inFile.getName());

            if (inFile.isDirectory()) {
                outFile.mkdirs();

                copyFiles(inFile, outFile);
                return;
            }

            if (!inFileName.endsWith(".crd") && !inFileName.endsWith(".cho")
                    && !inFileName.endsWith(".pro")
                    && !inFileName.endsWith(".chordpro")) {

                continue;
            }

            outFile.createNewFile();

            FileSystemUtils.copyFile(inFile, outFile);

            Document doc = Document.createFromFile(outFile);
            mDbHelper.createOrUpdate(doc.title, doc.subtitle,
                    outFile.getAbsolutePath());
        }
    }

}
