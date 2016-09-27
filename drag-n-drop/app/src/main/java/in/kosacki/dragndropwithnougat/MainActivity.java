package in.kosacki.dragndropwithnougat;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.imageDrag)
    ImageView imageDrag;

    @BindView(R.id.imageTrash)
    ImageView imageTrash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        ButterKnife.bind(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        imageDrag.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                return false;
            }
        });
        imageTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("dupa", "dupa");
            }
        });
        imageTrash.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch(dragEvent.getAction()){
                    case DragEvent.ACTION_DRAG_STARTED:
                    case DragEvent.ACTION_DRAG_ENTERED:
                    case DragEvent.ACTION_DRAG_LOCATION:
                    case DragEvent.ACTION_DRAG_EXITED:
                    case DragEvent.ACTION_DRAG_ENDED:
//                        Log.w("event", dragEvent.getAction() + "");
                        return true;
                    case DragEvent.ACTION_DROP:
                        Log.d("drop!", dragEvent.getClipData().getItemAt(0).getText().toString());
                        return true;
                }
                return false;
            }
        });
        imageDrag.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
                    view.startDragAndDrop(new ClipData(new ClipDescription(null,
                            new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN }), new ClipData.Item("myItem")), new View.DragShadowBuilder(view), new Object(), View.DRAG_FLAG_OPAQUE | View.DRAG_FLAG_GLOBAL);
                }
                else {
                    view.startDrag(new ClipData(new ClipDescription(null,
                            new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN }), new ClipData.Item("myItem")), new View.DragShadowBuilder(view), new Object(), 0);
                }
                return false;
            }
        });
    }

}
