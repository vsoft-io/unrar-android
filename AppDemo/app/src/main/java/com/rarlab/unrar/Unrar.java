package com.rarlab.unrar;

import java.io.File;

@SuppressWarnings("JniMissingFunction")
final class Unrar {

    static {
        System.loadLibrary("unrar-jni");
    }

    public static int extractFiles(String src) {
        return extractFiles(src, null, null);
    }

    public static int extractFiles(String src, String dest) {
        return extractFiles(src, dest, null);
    }

    public static int extractFiles(String src, String dest, String key) {
        if (src == null || src.trim().isEmpty()) {
            return -1;
        }
        File file = new File(src.trim());
        if (!file.isFile()) {
            return -1;
        }
        if (dest == null || dest.trim().equals("")) {
            dest = file.getParent();
        } else if (!new File(dest.trim()).isDirectory()) {
            return -1;
        }
        if (key == null || key.trim().equals("")) {
            key = "-p[]";
        } else {
            key = "-p[" + key + "]";
        }

        String argv[] = {"unrar", "-y", "x", src.trim(), dest, key};

        return unrar(argv);
    }

    public static String rarExit(int code) {

        switch (code) {
            case 0:
                return "RARX_SUCCESS";
            case 1:
                return "RARX_WARNING";
            case 2:
                return "RARX_FATAL";
            case 3:
                return "RARX_CRC";
            case 4:
                return "RARX_LOCK";
            case 5:
                return "RARX_WRITE";
            case 6:
                return "RARX_OPEN";
            case 7:
                return "RARX_USERERROR";
            case 8:
                return "RARX_MEMORY ";
            case 9:
                return "RARX_CREATE";
            case 10:
                return "RARX_NOFILES";
            case 11:
                return "RARX_BADPWD";
            case 255:
                return "RARX_USERBREAK";
            default:
                return "RARX_UNKNOWN";
        }
    }

    private native static int unrar(String[] s);
}
