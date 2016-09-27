package in.kosacki.dragndropwithnougat;

import android.content.ClipData;
import android.content.ClipDescription;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hubert on 26/09/16.
 */

public class ExplorerListAdapter extends RecyclerView.Adapter<ExplorerListAdapter.FileItemViewHolder> {

    interface OnItemClickListener{
        void onItemClick(File f);
    }

    interface OnItemLongClickListener{
        void onItemLongClick(View v, File f);
    }

    private OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClick(File f) {
            if(f.isDirectory()) {
                EventBus.getDefault().post(new NewPathEvent(f.getAbsolutePath()));
            }
        }
    };

    private OnItemLongClickListener longClickListener = new OnItemLongClickListener() {
        @Override
        public void onItemLongClick(View view, File f) {
            if(Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
                view.startDragAndDrop(new ClipData(new ClipDescription(f.getName(), new String[]{ClipDescription.MIMETYPE_TEXT_URILIST}), new ClipData.Item(Uri.fromFile(f))), new View.DragShadowBuilder(view), new Object(), View.DRAG_FLAG_OPAQUE | View.DRAG_FLAG_GLOBAL);
            }
            else {
                view.startDrag(new ClipData(new ClipDescription(null,
                        new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN }), new ClipData.Item(f.toString())), new View.DragShadowBuilder(view), new Object(), 0);
            }
//            return false;
        }
    };

    private List<File> mFilesList;

    public ExplorerListAdapter(List<File> fileList){
        mFilesList = fileList;
    }

    @Override
    public FileItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
        return new FileItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FileItemViewHolder holder, int position) {
        holder.bind(mFilesList.get(position), listener, longClickListener);
    }

    @Override
    public int getItemCount() {
        return mFilesList.size();
    }

    static class FileItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.fileItemIcon)
        protected ImageView icon;
        @BindView(R.id.fileItemNameTextView)
        protected TextView itemName;

        public FileItemViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void bind(final File file, final OnItemClickListener listener, final OnItemLongClickListener longClickListener){
            icon.setImageDrawable(icon.getContext().getResources().getDrawable(file.isDirectory() ? android.R.drawable.ic_menu_gallery : R.mipmap.ic_launcher));
            itemName.setText(file.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(file);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    longClickListener.onItemLongClick(itemView, file);
                    return false;
                }
            });
        }
    }
}
