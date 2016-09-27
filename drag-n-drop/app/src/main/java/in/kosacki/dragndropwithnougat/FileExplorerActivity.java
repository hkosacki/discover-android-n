package in.kosacki.dragndropwithnougat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FileExplorerActivity extends AppCompatActivity {

    private final static String TAG = FileExplorerActivity.class.getSimpleName();

    private final static int READ_EXTERNAL_STORAGE_REQUEST_CODE = 500;

    @BindView(R.id.explorer_recycle_view)
    RecyclerView filesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);

        ButterKnife.bind(this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        filesList.setLayoutManager(llm);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this).setTitle("Allow to read external storage")
                        .setMessage("This is a file manager. I need this permission in order to work at all.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(FileExplorerActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        READ_EXTERNAL_STORAGE_REQUEST_CODE);
                            }
                        }).create().show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_STORAGE_REQUEST_CODE);

                // READ_EXTERNAL_STORAGE_REQUEST_CODE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            populateFilesListForDirectory(new File(Environment.getExternalStorageDirectory().getPath()));
        }

    }

    /*
     * Consume the permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[Arrays.asList(permissions).indexOf(Manifest.permission.READ_EXTERNAL_STORAGE)] == PackageManager.PERMISSION_DENIED) {
                finish();
            } else {
                populateFilesListForDirectory(new File(Environment.getExternalStorageDirectory().getPath()));
            }
        }
    }

    private void populateFilesListForDirectory(File f) {
        Log.d(TAG, "yey!");

        ArrayList<File> inFiles = new ArrayList<>();
        File[] files = f.listFiles();
        inFiles.addAll(new ArrayList<File>(Arrays.asList(files)));

        filesList.setAdapter(new ExplorerListAdapter(inFiles));

    }

}
