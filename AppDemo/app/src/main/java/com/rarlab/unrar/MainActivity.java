package com.rarlab.unrar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static com.rarlab.unrar.R.id.linear1;

public class MainActivity extends Activity {

    private static long startTime = 0;
    private final Context context = MainActivity.this;
    private final ArrayList<File> filesToAdd = new ArrayList<>();
    private String key;
    private Handler mHandler;
    private ProgressDialog progressDialog;
    private boolean isExit = false;
    private boolean hasTask = false;
    private final Timer tExit = new Timer();
    private TextView logView;
    private final Runnable mBackgroundRunnable = new Runnable() {

        @Override
        public void run() {
            String path = filesToAdd.get(0).toString();
            Message msg = new Message();
            msg.what = Unrar.extractFiles(path, null, key);
            runOnUiThread(() -> escapeTime());
            mHandler.sendMessage(msg);
        }
    };
    private Button selectBtn;
    private Button runBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        logView = findViewById(R.id.logview);
        selectBtn = findViewById(R.id.select);
        Button clearBtn = findViewById(R.id.clear);
        runBtn = findViewById(R.id.run);
        runBtn.setEnabled(false);
        Button exitBtn = findViewById(R.id.exit);

        selectBtn.setOnClickListener(v -> checkPermissions(this));

        clearBtn.setOnClickListener(v -> {
            selectBtn.setEnabled(true);
            runBtn.setEnabled(false);
            filesToAdd.clear();
            logView.setText("");
        });

        runBtn.setOnClickListener(v -> onRunOption(true));

        exitBtn.setOnClickListener(v -> new AlertDialog.Builder(MainActivity.this).setTitle
                ("Warning").setMessage("Exit " + "this application?").setNegativeButton("OK",
                (dialog, which) -> {
            if (mHandler != null) mHandler.removeCallbacks(mBackgroundRunnable);
            android.os.Process.killProcess(android.os.Process.myPid());
        }).setPositiveButton("Cancel", null).show());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == linear1 || super.onOptionsItemSelected(item);
    }

    private void onRunOption(boolean needPassword) {
        if (!needPassword) {
            key = null;
            run();
        } else {
            final EditText inputKey = new EditText(MainActivity.this);
            new AlertDialog.Builder(MainActivity.this).setTitle("NEED A KEY").setView(inputKey)
                    .setNegativeButton("Cancel", null).setPositiveButton("OK", (dialog, which) -> {
                key = inputKey.getText().toString();
                run();
            }).show();
        }

    }

    private void run() {
        escapeTime();
        progressDialog = ProgressDialog.show(context, "", "", true);
        HandlerThread thread = new HandlerThread("unrar");
        thread.start();

        mHandler = new Handler(thread.getLooper()) {
            public void handleMessage(Message msg) {
                progressDialog.dismiss();
                String result = Unrar.rarExit(msg.what);
                runOnUiThread(() -> logView.append("result message: " + result + "\n\n"));

                switch (msg.what) {
                    case 0:
                        Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                        break;
                }
                super.handleMessage(msg);
            }
        };
        mHandler.post(mBackgroundRunnable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    filesToAdd.add(new File(Objects.requireNonNull(Objects.requireNonNull(data
                            .getExtras()).getString("path"))));
                    logView.append("SELECTED: " + filesToAdd.get(filesToAdd.size() - 1).getPath().
                            substring(Objects.requireNonNull(data.getExtras().getString("root"))
                                    .length() + 1) + "\n");
                    runBtn.setEnabled(true);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                exit();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            if (!hasTask) {
                tExit.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isExit = false;
                        hasTask = true;
                    }
                }, 2000);
            }
        } else {
            if (mHandler != null) mHandler.removeCallbacks(mBackgroundRunnable);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private void escapeTime() {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        } else {
            logView.append("escaped time: " + (System.currentTimeMillis() - startTime) + "ms\n");
            startTime = 0;
        }
    }

    private void onSelect() {
        filesToAdd.clear();
        logView.setText("");
        runBtn.setEnabled(false);
        Intent intent = new Intent(MainActivity.this, FileExplorer.class);
        startActivityForResult(intent, 0);
    }

    private void checkPermissions(Context context) {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,};
        List<String> permissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(context, p) != PackageManager
                    .PERMISSION_GRANTED) {
                permissionsNeeded.add(p);
            }
        }
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new
                    String[permissionsNeeded.size()]), 0);
        } else {
            onSelect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                }
                onSelect();
                break;
            default:
                break;
        }
    }
}