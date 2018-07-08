package com.rarlab.unrar;

import android.app.Activity;
import android.os.storage.StorageManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class StorageList {
    private StorageManager mStorageManager;
    private Method mMethodGetPaths;

    public StorageList(Activity activity) {
        if (activity != null) {
            mStorageManager = (StorageManager) activity.getSystemService(Activity.STORAGE_SERVICE);
            try {
                assert mStorageManager != null;
                mMethodGetPaths = Objects.requireNonNull(mStorageManager).getClass().getMethod
                        ("getVolumePaths");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public String[] getVolumePaths() {
        String[] paths = null;
        try {
            paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        List<String> list = new ArrayList<>();
        assert paths != null;
        for (String path : paths) {
            if (!path.contains("usb")) list.add(path);
        }
        String[] storagePaths = new String[list.size()];
        list.toArray(storagePaths);

        return storagePaths;
    }
}
