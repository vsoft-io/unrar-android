package com.rarlab.unrar;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileExplorer extends ListActivity {

    private String _rootPath = null;
    private String _curtPath = null;
    private List<Map<String, Object>> _fileItems = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StorageList s = new StorageList(this);
        String[] storagePaths = s.getVolumePaths();
        _rootPath = storagePaths[0];

        if (_rootPath != null) {
            fillList(_rootPath);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        getListView().setOnItemLongClickListener((parent, view, position, id) -> {
            File file = new File(_curtPath + File.separator + _fileItems.get((int) id).get
                    ("fileName"));
            Intent intent = new Intent();
            intent.putExtra("path", file.getPath());
            intent.putExtra("root", _rootPath);
            setResult(Activity.RESULT_OK, intent);
            finish();
            return true;
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        File file = new File(_curtPath + File.separator + _fileItems.get((int) id).get("fileName"));
        if (file.isDirectory()) {
            fillList(file.getPath());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (_curtPath.equals(_rootPath)) {
                    finish();
                } else {
                    fillList(_curtPath.substring(0, _curtPath.lastIndexOf(File.separator)));
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void fillList(String path) {
        File[] files = new File(path).listFiles(pathname -> !pathname.isHidden());
        if (files == null) {
            return;
        }
        _fileItems = new ArrayList<>();

        for (File file : files) {
            Map<String, Object> listItem = new HashMap<>();
            listItem.put("fileName", file.getName());
            if (file.isDirectory()) {
                listItem.put("icon", R.drawable.case_normal);
                listItem.put("attribute", "total: " + (file.list() == null ? "0" : file.list()
                        .length));
            } else {
                listItem.put("icon", R.drawable.file_normal);
                listItem.put("attribute", "size: " + formatFileSize(file.length()));
            }
            _fileItems.add(listItem);
        }

        SimpleAdapter fileAdapter = new SimpleAdapter(this, _fileItems, R.layout.list_item, new
                String[]{"icon", "fileName", "attribute"}, new int[]{R.id.iv_file_icon, R.id
                .tv_file_name, R.id.tv_file_attribute});
        this.setListAdapter(fileAdapter);

        _curtPath = path;
        setTitle(_curtPath);
    }

    private String formatFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        if (fileSize == 0) {
            fileSizeString = "0B";
        } else if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format((double) fileSize / 1024) + "K";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double) fileSize / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileSize / 1073741824) + "G";
        }
        return fileSizeString;
    }

}
