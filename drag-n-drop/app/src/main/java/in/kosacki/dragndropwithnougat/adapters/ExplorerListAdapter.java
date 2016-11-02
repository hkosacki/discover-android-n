package in.kosacki.dragndropwithnougat.adapters;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.net.URLConnection;
import java.util.List;

import butterknife.BindView;
import in.kosacki.dragndropwithnougat.BuildConfig;
import in.kosacki.dragndropwithnougat.R;
import in.kosacki.dragndropwithnougat.events.NewPathEvent;
import in.kosacki.dragndropwithnougat.listeners.OnItemClickListener;
import in.kosacki.dragndropwithnougat.listeners.OnItemLongClickListener;
import in.kosacki.dragndropwithnougat.viewholders.BaseViewHolder;

/**
 * Created by hubert on 26/09/16.
 */

public class ExplorerListAdapter extends BaseRecyclerAdapter<File> {


    /*
     * Used to register to each adapter item, to handle click events
     */
    private OnItemClickListener listener = new OnItemClickListener<File>() {
        @Override
        public void onItemClick(View v, final File f) {
            if (f.isDirectory()) {
                EventBus.getDefault().post(new NewPathEvent(f.getAbsolutePath()));
            } else {
                Intent newIntent = new Intent(Intent.ACTION_VIEW);
                String mimeType = URLConnection.guessContentTypeFromName(f.getName());
                if (mimeType == null) {
                    Snackbar.make(v, "Can't open file. The file type is unknown.", Snackbar.LENGTH_LONG).show();
                    return;
                }
                // TODO: https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en
                Uri uri = FileProvider.getUriForFile(v.getContext(), BuildConfig.APPLICATION_ID + ".provider", f);
                newIntent.setDataAndType(uri, mimeType);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                List<ResolveInfo> resInfoList = v.getContext().getPackageManager().queryIntentActivities(newIntent, PackageManager.MATCH_DEFAULT_ONLY);
                // TODO: http://stackoverflow.com/a/33652695/1181162
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    v.getContext().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                try {
                    v.getContext().startActivity(newIntent);
                } catch (ActivityNotFoundException e) {
                    Snackbar.make(v, "Sorry, I found no handler for this type of file.", Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    };

    /*
     * Used to register to each adapter item, to handle long click events
     */
    private OnItemLongClickListener longClickListener = new OnItemLongClickListener<File>() {
        @Override
        public void onItemLongClick(View view, File f) {
            if (f.isDirectory()) {
                Snackbar.make(view, "Sorry, no drag'n'drop support for directories", Snackbar.LENGTH_SHORT).show();
                return;
            }
            // prepare drag parameters
            ClipDescription description = new ClipDescription(f.getName(), new String[]{ClipDescription.MIMETYPE_TEXT_URILIST});
            ClipData.Item clipDataItem = new ClipData.Item(Uri.fromFile(f));
            ClipData draggedData = new ClipData(description, clipDataItem);
            View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(view);
            // start drag and drop operation for proper platform
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.startDragAndDrop(draggedData, dragShadowBuilder, null, View.DRAG_FLAG_GLOBAL | View.DRAG_FLAG_GLOBAL_URI_READ);
            } else {
                //noinspection deprecation
                view.startDrag(draggedData, dragShadowBuilder, null, 0);
            }
        }
    };

//    public ExplorerListAdapter() {}

    public ExplorerListAdapter(List<File> dataList){
        super(dataList);
    }

    @Override
    public FileItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FileItemViewHolder viewHolder = new FileItemViewHolder(parent);
        viewHolder.itemView.setClickable(true);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<File> holder, int position) {
        ((FileItemViewHolder) holder).bind(dataList.get(position), listener, longClickListener);

    }

    /*
     * Custom ViewHolder class
     */
    static class FileItemViewHolder extends BaseViewHolder<File> {

        @BindView(R.id.fileItemIcon)
        ImageView icon;

        @BindView(R.id.fileItemNameTextView)
        TextView itemName;

        FileItemViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.file_item);
        }

        @Override
        public void bind(final File file) {
            icon.setImageDrawable(ContextCompat.getDrawable(icon.getContext(), file.isDirectory() ? R.drawable.ic_folder : R.drawable.ic_file));
            itemName.setText(file.getName());
        }

        void bind(final File file, final OnItemClickListener listener, final OnItemLongClickListener longClickListener) {
            bind(file);
            setOnItemClick(file, listener);
            setOnItemLongClick(file, longClickListener);
        }
    }
}
