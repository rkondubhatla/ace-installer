package com.sk.aceinstaller2;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sk.aceinstaller2.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;

import static android.os.Environment.getExternalStorageDirectory;

public class CopyActivity extends AppCompatActivity {

    private static Button copyButton;
    private Context mContext;
    private File dataRoot;
    private File apkRoot;

    private ArrayList<File> fileList = new ArrayList<File>();
    private ArrayList<File> apkFileList = new ArrayList<File>();
    private LinearLayout view;

    private TextView txtMessage =  null;
    private TextView countMessage = null;
    private ProgressBar mProgressBar =  null;
    private CopyWork task = null;
    private static final int MAX_PROGRESS = 100;
    private int total_file_count = 0;
    private int curr_file_count = 0;

    private TextView clockView;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy);
        mContext = this;

        if (!Utils.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }

        view = (LinearLayout) findViewById(R.id.view);

        txtMessage = (TextView) findViewById(R.id.txtMessage);
        countMessage = (TextView) findViewById(R.id.countMessage);
        countMessage.setVisibility(View.INVISIBLE);

        clockView = (TextView) findViewById(R.id.clockView);
        clockView.setVisibility(View.INVISIBLE);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        copyButton = (Button) findViewById(R.id.copyButton);
        // set an arbitrary max value for the progress bar
        mProgressBar.setMax(MAX_PROGRESS);

        dataRoot = new File(Utils.getStoragepathString(CopyActivity.this).concat("/AceInstaller/Erudex"));
        apkRoot = new File(Utils.getStoragepathString(CopyActivity.this).concat("/AceInstaller/apps"));

        if(!dataRoot.isDirectory()) {
            txtMessage.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            copyButton.setVisibility(View.GONE);

            TextView textView = new TextView(CopyActivity.this);
            textView.setText("Erudex folder is not found in this Location");
            textView.setPadding(5, 5, 5, 5);
            view.addView(textView);
        }

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
    }

    private void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            curr_file_count++;
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    private void start() {
        countMessage.setVisibility(View.VISIBLE);
        clockView.setVisibility(View.VISIBLE);
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);


        // instantiate a new async task
        task = new CopyWork();
        // start async task setting the progress to zero
        task.execute(0);
        // reset progress
        mProgressBar.setProgress(0);
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            clockView.setText("" + mins + ":"
                    + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }
    };

    class CopyWork extends AsyncTask<Integer, Integer, Integer> {

        // Method executed before the async task start. All things needed to be
        // setup before the async task must be done here. In this example we
        // simply display a message.
        @Override
        protected void onPreExecute() {
            txtMessage.setText("Executing Copying task...");
            countMessage.setText("Copying in progress : ");
            super.onPreExecute();
        }

        // overridden method onProgressUpdate(...) which updates the progress.
        @Override
        protected Integer doInBackground(Integer... params) {

            // get the initial parameters. For us, this is the initial bar progress = 0
            int progress = ((Integer[])params)[0];

            fileList = Utils.getfiles(dataRoot);
            apkFileList = Utils.getApkFiles(apkRoot);
            total_file_count = Utils.getFileCount(dataRoot) + Utils.getFileCount(apkRoot);

            int i = 0;
            do {
                String inputPath = dataRoot.toString()+"/";
                String fileName = fileList.get(i).getName();
                String outputPath = getExternalStorageDirectory().getAbsolutePath().toString().concat("/Erudex/");
                File f = new File(inputPath.concat(fileName));

                if(f.isDirectory()) {
                    copyRec(inputPath, fileName, outputPath);
                } else {
                    copyFile(inputPath, fileName, outputPath);
                }

                progress = (i * 100) / fileList.size();
                publishProgress(progress);
                i++;
            } while (i < fileList.size());

            int a = 0;
            do {
                String inputPath = apkRoot.toString()+"/";
                String fileName = apkFileList.get(a).getName();
                String outputPath = getExternalStorageDirectory().getAbsolutePath().toString().concat("/ErudexApps/");
                File f = new File(inputPath.concat(fileName));

                if(f.isDirectory()) {
                    copyRec(inputPath, fileName, outputPath);
                } else {
                    copyFile(inputPath, fileName, outputPath);
                }

                progress = (a * 100) / apkFileList.size();
                publishProgress(progress);
                a++;
            } while (a < apkFileList.size());

            return progress;
        }

        private void copyRec(String inputPath, String folderName, String outputPath) {
            File tempRoot = new File(inputPath.concat("/" + folderName));
            ArrayList<File> newFileList = new ArrayList<File>();

            newFileList = Utils.getfiles(tempRoot);

            int i = 0;
            do {
                String inPath = tempRoot.toString()+"/";
                String fileName = newFileList.get(i).getName();
                String outPath = outputPath.concat(folderName + "/");

                File f = new File(inPath.concat("/" + fileName));

                if(f.isDirectory()) {
                    copyRec(inPath, fileName, outPath);
                } else {
                    copyFile(inPath, fileName, outPath);
                }

                i++;
            } while (i < newFileList.size());
        }

        // Every time the progress is informed, we update the progress bar
        @Override
        protected void onProgressUpdate(Integer... values) {
            int progress = ((Integer[])values)[0];
            mProgressBar.setProgress(progress);
            countMessage.setText("Copying in progress : " + curr_file_count + "/" + total_file_count);
            super.onProgressUpdate(values);
        }

        // If the cancellation occurs, set the message informing so
        @Override
        protected void onCancelled(Integer result) {
            txtMessage.setText(MessageFormat.format
                    ("Copying task has been cancelled", result - 1));
            super.onCancelled(result);
        }

        // Method executed after the task is finished. If the task is cancelled this method is not
        // called. Here we display a finishing message and arrange the buttons.
        @Override
        protected void onPostExecute(Integer result) {
            txtMessage.setTextSize(20);
            txtMessage.setTextColor(Color.rgb(0, 200, 0));
            txtMessage.setText(MessageFormat.format
                    ("Copying task execution finished.", result - 1));
            super.onPostExecute(result);
            mProgressBar.setProgress(100);
            timeSwapBuff += timeInMilliseconds;
            customHandler.removeCallbacks(updateTimerThread);
            Toast.makeText(mContext, "Finished copying data to local storage from sdcard", Toast.LENGTH_SHORT).show();
        }
    }
}
