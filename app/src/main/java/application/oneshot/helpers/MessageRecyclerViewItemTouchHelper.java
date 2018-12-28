// https://www.androidhive.info/2017/09/android-recyclerview-swipe-delete-undo-using-itemtouchhelper/

package application.oneshot.helpers;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import application.oneshot.adapters.MessagesRecyclerViewAdapter;

public class MessageRecyclerViewItemTouchHelper
        extends ItemTouchHelper.SimpleCallback {

    private MessageRecyclerViewItemTouchHelperBase mItemTouchHelperBase;

    public MessageRecyclerViewItemTouchHelper(int dragDirs, int swipeDirs,
            MessageRecyclerViewItemTouchHelperBase itemTouchHelperBase) {

        super(dragDirs, swipeDirs);

        mItemTouchHelperBase = itemTouchHelperBase;
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView =
                ((MessagesRecyclerViewAdapter.MessageRecyclerViewHolder) viewHolder).relativeLayoutForeground;

        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY,
            int actionState, boolean isCurrentlyActive) {

        final View foregroundView =
                ((MessagesRecyclerViewAdapter.MessageRecyclerViewHolder) viewHolder).relativeLayoutForeground;

        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX,
            float dY, int actionState, boolean isCurrentlyActive) {

        final View foregroundView =
                ((MessagesRecyclerViewAdapter.MessageRecyclerViewHolder) viewHolder).relativeLayoutForeground;

        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
            RecyclerView.ViewHolder target) {

        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View foregroundView =
                    ((MessagesRecyclerViewAdapter.MessageRecyclerViewHolder) viewHolder).relativeLayoutForeground;

            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mItemTouchHelperBase.onItemSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    public interface MessageRecyclerViewItemTouchHelperBase {
        void onItemSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
