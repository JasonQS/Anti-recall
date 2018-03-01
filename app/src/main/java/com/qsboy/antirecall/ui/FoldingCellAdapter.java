package com.qsboy.antirecall.ui;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qsboy.antirecall.R;
import com.qsboy.antirecall.db.Messages;

import java.util.List;

/**
 * Created by JasonQS
 */


/**
 * Simple example of ListAdapter for using with Folding Cell
 * Adapter holds indexes of unfolded elements for correct work with default reusable views behavior
 */
public class FoldingCellAdapter extends BaseQuickAdapter<Messages, FoldingCellAdapter.ViewHolder> {

    Context context;

    public FoldingCellAdapter(@Nullable List<Messages> data, Context context) {
        super(R.layout.item_message, data);
        this.context = context;
    }

    @Override
    protected void convert(ViewHolder helper, Messages item) {
        MultiMessagesAdapter adapter = new MultiMessagesAdapter(null, context);
        adapter.addData(adapter.prepareData(item.getName(), item.isWX(), item.getId()));
        adapter.setPreLoadNumber(3);
        adapter.setUpFetchEnable(true);
        adapter.setUpFetchListener(() ->
                adapter.addData(0, adapter.fetchData(item.getName(), item.isWX(), item.getId())));
        adapter.setOnLoadMoreListener(() ->
                adapter.addData(adapter.prepareData(item.getName(), item.isWX(), item.getId())), getRecyclerView());

        helper.setText(R.id.cell_title, item.getName());
        helper.setText(R.id.cell_name, item.getSubName());
        helper.setText(R.id.cell_time, item.getTime());
        helper.setText(R.id.cell_message, item.getMessage());
        helper.setView(R.id.cell_recycler_view, adapter);
    }


    //
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return new ViewHolder
//                (LayoutInflater.from(parent.getContext()).inflate(, null));
//    }
//
//    @Override
//    protected void convert(BaseViewHolder helper, Messages item) {
//        helper.setText(R.id.cell_title, item.getName());
//        helper.setText(R.id.cell_name, item.getSubName());
//        helper.setText(R.id.cell_time, item.getTime());
//        helper.setText(R.id.cell_message, item.getMessage());
////        helper.setImageResource(R.id.icon, item.getImage());
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//
//        Messages item = messagesList.get(position);
//        ViewHolder vh = (ViewHolder) holder;
//        vh.getTitle().setText(item.getName());
//        vh.getName().setText(item.getSubName());
//        vh.getMessage().setText(item.getMessage());
//        vh.getTime().setText(item.getTime());
//
//        // TODO: unfold recycler view init
//    }
//
//    @Override
//    public int getItemCount() {
//        // TODO: get count
//        return 0;
//    }
//


    class ViewHolder extends BaseViewHolder {

        public ViewHolder(View view) {
            super(view);
        }

        public BaseViewHolder setView(@IdRes int viewId, RecyclerView.Adapter adapter) {
            RecyclerView view = getView(viewId);
            view.setAdapter(adapter);
            return this;
        }
    }

}
