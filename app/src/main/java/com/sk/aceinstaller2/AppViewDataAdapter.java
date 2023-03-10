package com.sk.aceinstaller2;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AppViewDataAdapter extends RecyclerView.Adapter<AppViewDataAdapter.ViewHolder> {

    private List<AppFile> appList;
    private Context mContext;

    public AppViewDataAdapter(List<AppFile> apps, Context context) {
        this.appList = apps;
        this.mContext = context;
    }

    // Create new views
    @Override
    public AppViewDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.app_file_item, null);

        // create ViewHolder
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        final int pos = position;
        viewHolder.appIcon.setImageDrawable(appList.get(position).getIcon());
        viewHolder.appName.setText(appList.get(position).getName());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApp(appList.get(pos));
            }
        });
    }

    private void openApp(AppFile app) {
        Intent LaunchIntent = mContext.getPackageManager().getLaunchIntentForPackage(app.getPackageName());
        mContext.startActivity( LaunchIntent );
    }

    // Return the size arraylist
    @Override
    public int getItemCount() {
        return appList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView appIcon;
        public TextView appName;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            appIcon = (ImageView) itemLayoutView.findViewById(R.id.appIcon);
            appName = (TextView) itemLayoutView.findViewById(R.id.appName);
        }
    }
}