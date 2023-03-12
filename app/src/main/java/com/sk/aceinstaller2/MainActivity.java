package com.sk.aceinstaller2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sk.aceinstaller2.util.Utils;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static Button installerButton;
    private static Button contentCopyButton;
    private static Button appListButton;
    private Context mContext;
    final static int APP_STORAGE_ACCESS_REQUEST_CODE = 501;
    final static int APP_UNKNOWN_SERVICES_ACCESS_REQUEST_CODE = 502;
    private int STORAGE_PERMISSION_CODE=23;
    SharedPreferences sharedpreferences;
    public static final String shownPermissionPage = "shownPermissionPage";
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();




        if (!Utils.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }

        String[] permissionsArray = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.REQUEST_INSTALL_PACKAGES,
                Manifest.permission.INSTALL_SHORTCUT,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.UNINSTALL_SHORTCUT
        };

        ActivityCompat.requestPermissions(this, permissionsArray , STORAGE_PERMISSION_CODE);

        installerButton = (Button) findViewById(R.id.installerView);
        installerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                startActivityForResult(new Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,uri),APP_UNKNOWN_SERVICES_ACCESS_REQUEST_CODE);
           //     openInstaller(v);
            }
        });

        appListButton = (Button) findViewById(R.id.appListView);
        appListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppList(v);
            }
        });

        contentCopyButton = (Button) findViewById(R.id.contentDataView);
        contentCopyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCopier(v);
            }
        });
    }



    public void openInstaller(View view)
    {
        Intent intent = new Intent(getApplicationContext(), InstallerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(Build.VERSION.SDK_INT> 29) {
            if(!sharedpreferences.getBoolean(shownPermissionPage,false)) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("shownPermissionPage", true);
                editor.commit();
                try {
                    // write all the data entered by the user in SharedPreference and apply
                    Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                    startActivityForResult(intent, APP_STORAGE_ACCESS_REQUEST_CODE);
                } catch (Exception ex) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, APP_STORAGE_ACCESS_REQUEST_CODE);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_STORAGE_ACCESS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("shownPermissionPage", true);
                editor.commit();
            }
        } else if (requestCode == APP_UNKNOWN_SERVICES_ACCESS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                {
                    Intent intent = new Intent(getApplicationContext(), InstallerActivity.class);
                    startActivity(intent);
                }
            }
            else{
                Toast.makeText(MainActivity.this,"Need to enable the unknown sources installation",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void openAppList(View view) {
        Intent intent = new Intent(getApplicationContext(), AppListActivity.class);
        startActivity(intent);
    }

    public void openCopier(View view)
    {
        Intent intent = new Intent(getApplicationContext(), CopyActivity.class);
        startActivity(intent);
    }
}
