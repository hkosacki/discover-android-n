package in.kosacki.dragndropwithnougat.viewholders;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import butterknife.ButterKnife;
import in.kosacki.dragndropwithnougat.listeners.OnItemClickListener;
import in.kosacki.dragndropwithnougat.listeners.OnItemLongClickListener;


/**
 * Created by hubert on 07/10/16.
 */

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {



    public BaseViewHolder(ViewGroup viewGroup, int layoutId) {
        super(LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false));
        ButterKnife.bind(this, itemView);
    }

    public abstract void bind(T type);

    protected void setOnItemClick(final T object, final OnItemClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    int finalRadius = view.getWidth() / 2;
                    final Animator anim = ViewAnimationUtils.createCircularReveal(view, view.getWidth() / 2, view.getHeight() / 2, 0, finalRadius);
//                    anim.setInterpolator(new DecelerateInterpolator());
                    anim.setDuration(1000);
                    anim.setStartDelay(0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    view.setVisibility(View.VISIBLE);
                                    view.forceLayout();
                                }
                            });
                        }
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            listener.onItemClick(view, object);
                        }
                    });
                    anim.start();
                }

            }
        });
    }

    protected void setOnItemLongClick(final T object, final OnItemLongClickListener longClickListener){
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                longClickListener.onItemLongClick(itemView, object);
                return true;
            }
        });
    }

}
