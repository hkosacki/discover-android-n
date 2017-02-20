package in.kosacki.dragndropwithnougat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.google.common.collect.Ordering;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.kosacki.dragndropwithnougat.adapters.ExplorerListAdapter;
import in.kosacki.dragndropwithnougat.events.NewPathEvent;
import in.kosacki.dragndropwithnougat.utils.comparators.FileNameComparator;
import in.kosacki.dragndropwithnougat.utils.comparators.FileTypeComparator;

public class FileExplorerActivity extends AppCompatActivity {

    private final static String TAG = FileExplorerActivity.class.getSimpleName();

    private final static int READ_EXTERNAL_STORAGE_REQUEST_CODE = 500;
    private static final String LAST_LOCATION_KEY = "last_location";

    @BindView(R.id.explorer_recycle_view)
    RecyclerView filesList;

    private ExplorerListAdapter listAdapter;

    private File currentPath;
    private MenuItem up;

    private boolean backPressedToExit = false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);

        ButterKnife.bind(this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        filesList.setLayoutManager(llm);
        filesList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(final View view,
                                       final int left, final int top, final int right, final int bottom,
                                       final int oldLeft, final int oldTop, final int oldRight, final int oldBottom) {
                for (int i = 0, count = filesList.getChildCount(); i < count; ++i) {
                    final View child = filesList.getChildAt(i);
                    int height = child.getHeight();
                    child.setTranslationY(height);
                    child.setAlpha(0);
                    child.animate().translationY(0).alpha(1).setDuration(250).setStartDelay(20 * i).setInterpolator(new DecelerateInterpolator()).withLayer();
                }
            }
        });


        EventBus.getDefault().register(this);

        listAdapter = new ExplorerListAdapter(null);
        filesList.setAdapter(listAdapter);

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
            setAdapterToList();
            populateFilesListForDirectory(new File(Environment.getExternalStorageDirectory().getPath()));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString(LAST_LOCATION_KEY, currentPath.getPath());
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        String path = savedInstanceState.getString(LAST_LOCATION_KEY);
        if (path != null) {
            setAdapterToList();
            populateFilesListForDirectory(new File(path));
        }
        super.onRestoreInstanceState(savedInstanceState);
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
                setAdapterToList();
                populateFilesListForDirectory(new File(Environment.getExternalStorageDirectory().getPath()));
            }
        }
    }

    private void setAdapterToList() {
        if (listAdapter == null) {
            listAdapter = new ExplorerListAdapter(null);
        }
        filesList.setAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        up = menu.findItem(R.id.action_up);
        up.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_up) {
            if (currentPath.getParent() == null) {
                Toast.makeText(this, "We are in the root directory now.", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }
            EventBus.getDefault().post(new NewPathEvent(currentPath.getParent()));
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateFilesListForDirectory(File f) {
        Log.d(TAG, "populateFilesListForDirectory() called with: f = [" + f + "]");
        ArrayList<File> inFiles = new ArrayList<>();
        File[] files = f.listFiles();
        inFiles.addAll(new ArrayList<>(Arrays.asList(files)));
        Collections.sort(inFiles, Ordering.from(new FileTypeComparator()).compound(new FileNameComparator()));

        listAdapter.setData(inFiles);
        listAdapter.notifyDataSetChanged();
        filesList.scrollToPosition(0);
        currentPath = f;
    }

    @Subscribe
    public void onNewPath(final NewPathEvent newPathEvent) {
        //animate recyclerView disappear, then change directory
        filesList.animate().alpha(0).setDuration(300).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                filesList.setAlpha(1);
                up.setEnabled(!newPathEvent.getPath().equals(Environment.getExternalStorageDirectory().getPath()));
                getSupportActionBar().setTitle(newPathEvent.getPath().substring(newPathEvent.getPath().lastIndexOf("/") + 1));
                populateFilesListForDirectory(new File(newPathEvent.getPath()));
            }
        }).withLayer();
    }

    @Override
    public void onBackPressed() {
        if (up.isEnabled()) {
            onOptionsItemSelected(up);
        } else {
            if (backPressedToExit) {
                super.onBackPressed();
                return;
            }
            this.backPressedToExit = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressedToExit = false;
                }
            }, 2000);
        }
    }
}

