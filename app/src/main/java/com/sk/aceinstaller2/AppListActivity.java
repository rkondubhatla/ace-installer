package com.sk.aceinstaller2;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sk.aceinstaller2.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends AppCompatActivity {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<AppFile> appList;
    private TextView txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        mContext = this;

        if (!Utils.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }

        txtMessage = (TextView) findViewById(R.id.txtMessage);
        txtMessage.setVisibility(View.INVISIBLE);

        appList = new ArrayList<AppFile>();

        this.showAppList();
    }

    private void showAppList() {
        final PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;

        String[] packageNames = getResources().getStringArray(R.array.packageNames);

        for(int i = 0; i < packageNames.length; i++) {
            if(appInstalledOrNot(packageNames[i])) {
                try {
                    ai = pm.getApplicationInfo( packageNames[i], 0);
                    AppFile app = new AppFile();

                    app.setPackageName(packageNames[i]);
                    app.setName(pm.getApplicationLabel(ai).toString());
                    Drawable icon = ai.loadIcon(mContext.getPackageManager());
                    app.setIcon(icon);

                    appList.add(app);
                } catch (final PackageManager.NameNotFoundException e) {
                    // do nothing
                }
            }
        }

        if(appList.size() < 1) {
            txtMessage.setVisibility(View.VISIBLE);
        }

        final int columns = getResources().getInteger(R.integer.gallery_columns);

        mRecyclerView = (RecyclerView) findViewById(R.id.app_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, columns));
        mAdapter = new AppViewDataAdapter(appList, mContext);
        mRecyclerView.setAdapter(mAdapter);
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            String test = "0";
        }

        return false;
    }
}
