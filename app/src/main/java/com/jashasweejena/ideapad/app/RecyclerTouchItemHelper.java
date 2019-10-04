package com.jashasweejena.ideapad.app;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;

import com.jashasweejena.ideapad.adapters.IdeaAdapter;

public class RecyclerTouchItemHelper extends ItemTouchHelper.SimpleCallback{

    private RecyclerTouchListener listener;


    public RecyclerTouchItemHelper(int dragDirs, int swipeDirs, RecyclerTouchListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        final View foregroundView = ((IdeaAdapter.IdeaViewHolder)viewHolder).viewForeground;
        getDefaultUIUtil().clearView(foregroundView);

    }


    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());

    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        if(viewHolder != null) {

            final View foregroundView = ((IdeaAdapter.IdeaViewHolder)viewHolder).viewForeground;

            getDefaultUIUtil().onSelected(foregroundView);

        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        final View foregroundView = ((IdeaAdapter.IdeaViewHolder)viewHolder).viewForeground;
        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);

    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        final View foregroundView = ((IdeaAdapter.IdeaViewHolder)viewHolder).viewForeground;
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
    }


    public interface RecyclerTouchListener {

        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);

    }
}


