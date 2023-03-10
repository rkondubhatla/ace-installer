package com.sk.aceinstaller2;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ApkViewDataAdapter extends RecyclerView.Adapter<ApkViewDataAdapter.ViewHolder> {

    private List<ApkFile> apkList;

    public ApkViewDataAdapter(List<ApkFile> apks) {
        this.apkList = apks;
    }

    // Create new views
    @Override
    public ApkViewDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.apk_file_item, null);

        // create ViewHolder
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        final int pos = position;
        viewHolder.apkIcon.setImageDrawable(apkList.get(position).getIcon());
        viewHolder.apkName.setText(apkList.get(position).getName());
        viewHolder.chkSelected.setChecked(apkList.get(position).isSelected());
        viewHolder.chkSelected.setTag(apkList.get(position));
        viewHolder.chkSelected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                ApkFile contact = (ApkFile) cb.getTag();

                contact.setSelected(cb.isChecked());
                apkList.get(pos).setSelected(cb.isChecked());
            }
        });

        viewHolder.chkSelected.setEnabled(!apkList.get(position).isInstalled());
        viewHolder.installedButton.setVisibility(apkList.get(position).installedButtonView());
    }

    // Return the size arraylist
    @Override
    public int getItemCount() {
        return apkList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView apkIcon;
        public TextView apkName;
        public CheckBox chkSelected;
        public Button installedButton;
        public boolean isInstalled = false;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            apkIcon = (ImageView) itemLayoutView.findViewById(R.id.apkIcon);
            apkName = (TextView) itemLayoutView.findViewById(R.id.apkName);
            chkSelected = (CheckBox) itemLayoutView.findViewById(R.id.chkSelected);
            installedButton = (Button) itemLayoutView.findViewById(R.id.installedButton);
        }
    }

    // method to access in activity after updating selection
    public List<ApkFile> getApkList() {
        return apkList;
    }

    public List<ApkFile> getSelectedApkList() {
        List<ApkFile> selectedApkList = new ArrayList<ApkFile>();

        for (int i = 0; i < apkList.size(); i++) {
            ApkFile apk = apkList.get(i);
            if (apk.isSelected() == true) {
                selectedApkList.add(apkList.get(i));
            }
        }

        return selectedApkList;
    }

    public void selectAll() {
        for(int i = 0; i < apkList.size(); i++) {
            if(apkList.get(i).isInstalled()) {
                apkList.get(i).setSelected(false);
            } else {
                apkList.get(i).setSelected(true);
            }
        }
        notifyDataSetChanged();
    }

    public void deselectAll() {
        for(int i = 0; i < apkList.size(); i++) {
            apkList.get(i).setSelected(false);
        }
        notifyDataSetChanged();
    }
}