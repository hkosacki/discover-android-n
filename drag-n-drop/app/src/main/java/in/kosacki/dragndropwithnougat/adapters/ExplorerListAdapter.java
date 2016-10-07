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
import android.support.v7.widget.RecyclerView;
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
import in.kosacki.dragndropwithnougat.NewPathEvent;
import in.kosacki.dragndropwithnougat.R;
import in.kosacki.dragndropwithnougat.listeners.OnItemClickListener;
import in.kosacki.dragndropwithnougat.listeners.OnItemLongClickListener;
import in.kosacki.dragndropwithnougat.viewholders.BaseViewHolder;

/**
 * Created by hubert on 26/09/16.
 */

public class ExplorerListAdapter extends RecyclerView.Adapter<ExplorerListAdapter.FileItemViewHolder> {


    /*
     * Used to register to each adapter item, to handle click events
     */
    private OnItemClickListener listener = new OnItemClickListener<File>() {
        @Override
        public void onItemClick(View v, final File f) {
            if (f.isDirectory()) {
                {
                    EventBus.getDefault().post(new NewPathEvent(f.getAbsolutePath()));
                }
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
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
                view.startDragAndDrop(new ClipData(new ClipDescription(f.getName(), new String[]{ClipDescription.MIMETYPE_TEXT_URILIST}), new ClipData.Item(Uri.fromFile(f))), new View.DragShadowBuilder(view), new Object(), View.DRAG_FLAG_OPAQUE | View.DRAG_FLAG_GLOBAL);
            } else {
                //noinspection deprecation
                view.startDrag(new ClipData(new ClipDescription(null,
                        new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}), new ClipData.Item(f.toString())), new View.DragShadowBuilder(view), new Object(), 0);
            }
        }
    };

    private List<File> mFilesList;

    public ExplorerListAdapter(){
    }

    public ExplorerListAdapter(List<File> fileList) {
        mFilesList = fileList;
    }

    @Override
    public FileItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FileItemViewHolder viewHolder = new FileItemViewHolder(parent);
        viewHolder.itemView.setClickable(true);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FileItemViewHolder holder, int position) {
        holder.bind(mFilesList.get(position), listener, longClickListener);
    }

    @Override
    public int getItemCount() {
        return mFilesList.size();
    }

    public void setData(List<File> dataList){
        mFilesList = dataList;
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
