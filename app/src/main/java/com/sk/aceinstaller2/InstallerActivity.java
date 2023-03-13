package com.sk.aceinstaller2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sk.aceinstaller2.util.Utils;

import java.io.File;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

import lib.folderpicker.FolderPicker;

public class InstallerActivity extends Activity {

    private File root;
    private Context mContext;
    private ArrayList<File> fileList = new ArrayList<File>();
    private LinearLayout view;
    private RelativeLayout select_all;
    private Button chooseFolder;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ApkFile> apkList;
    private Button installSelected;
    public CheckBox selectAll;

    private SharedPreferences appSettings;
    private int FOLDERPICKER_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installer);
        mContext = this;

        if (!Utils.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }


        view = (LinearLayout) findViewById(R.id.view);
        select_all = (RelativeLayout) findViewById(R.id.select_all);
        chooseFolder = (Button) findViewById(R.id.chooseButton);
        installSelected = (Button) findViewById(R.id.installSelected);
        selectAll = (CheckBox) findViewById(R.id.chkSelected);




        selectAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(selectAll.isChecked())
                {
                    ((ApkViewDataAdapter) mAdapter).selectAll();
                }
                if(!selectAll.isChecked())
                {
                    ((ApkViewDataAdapter) mAdapter).deselectAll();
                }
            }
        });

        apkList = new ArrayList<ApkFile>();

        this.showApkList();

        appSettings = getSharedPreferences("ACE_INSTALLER", MODE_PRIVATE);
    }

    private void showApkList() {

      //  root = Environment.getExternalStoragePublicDirectory("AceInstaller/Erudex");
        root = new File(Utils.getStoragepathString(InstallerActivity.this).concat("/AceInstaller/apps"));
        fileList = Utils.getApkFiles(root);

        if(fileList.size() < 1) {
            Toast.makeText(mContext, "Couldn't find any apk's in default folder.", Toast.LENGTH_SHORT).show();

            TextView textView = new TextView(InstallerActivity.this);
            textView.setText("No Apk's Found in this Location");
            textView.setPadding(5, 5, 5, 5);
            view.addView(textView);

            select_all.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < fileList.size(); i++) {
                ApkFile apk = new ApkFile(fileList.get(i).getName(), false);
                String filePath = root.toString() + "/" + fileList.get(i).getName();
                PackageInfo packageInfo = mContext.getPackageManager().getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);

                apk.setPackageName(packageInfo.packageName);
                if(packageInfo != null) {
                    ApplicationInfo appInfo = packageInfo.applicationInfo;
                    if (Build.VERSION.SDK_INT >= 8) {
                        appInfo.sourceDir = filePath;
                        appInfo.publicSourceDir = filePath;
                    }
                    Drawable icon = appInfo.loadIcon(mContext.getPackageManager());
                    apk.setIcon(icon);
                    if(appInstalledOrNot(appInfo.packageName)) {
                        apk.setInstalled(true);
                    } else {
                        apk.setInstalled(false);
                    }
                }

                apkList.add(apk);
            }

            mRecyclerView = (RecyclerView) findViewById(R.id.apk_recycler);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mAdapter = new ApkViewDataAdapter(apkList);
            mRecyclerView.setAdapter(mAdapter);
            installSelected.setVisibility(View.VISIBLE);

        }

    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    public void installSelected(View view)
    {
        installSelected.setEnabled(false);

        List<ApkFile> apkList = ((ApkViewDataAdapter) mAdapter).getSelectedApkList();

        for (int i = 0; i < apkList.size(); i++) {
            File directory = new File(Utils.getStoragepathString(InstallerActivity.this).concat("/AceInstaller/apps"));;
            File file = new File(directory, apkList.get(i).getName());
            Uri fileUri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            if (Build.VERSION.SDK_INT >= 24) {
                fileUri = FileProvider.getUriForFile(this, "com.sk.aceinstaller2" + ".provider", file);
            }
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //dont forget add this line
            startActivity(intent);
        }
        // Make sure you only run addShortcut() once, not to create duplicate shortcuts.
        if(!appSettings.getBoolean("apps_shortcut", false)) {
            addAppsShortcut(mContext);
        }
    }

    public void addAppsShortcut(Context context)
    {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context))
        {
            ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(context, "#1")
                    .setIntent(new Intent(context, AppListActivity.class).setAction(Intent.ACTION_MAIN)) // !!! intent's action must be set on oreo
                    .setShortLabel("Preloaded Applications")
                    .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_shortcut)) // Need to change the icon when kishore gives the icon - SarojK
                    .build();
            ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, null);

            SharedPreferences.Editor prefEditor = appSettings.edit();
            prefEditor.putBoolean("apps_shortcut", true);
            prefEditor.commit();
        }
        else
        {
            // Shortcut is not supported by your launcher
            Toast.makeText(context, "Couldn't add shortcut as it is not supported.", Toast.LENGTH_SHORT).show();
        }
    }
}