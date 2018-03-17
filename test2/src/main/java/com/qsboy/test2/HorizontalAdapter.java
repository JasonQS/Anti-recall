package lee.com.test;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;


public class HorizontalAdapter extends BaseQuickAdapter<Entity, BaseViewHolder> {

    public static final String TAG = HorizontalAdapter.class.getSimpleName();

    public HorizontalAdapter(List<Entity> data) {
        super(R.layout.item_horizontal, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Entity item) {
        helper.setText(R.id.tv_title, item.title);
        final RecyclerView recyclerView = helper.getView(R.id.rv_item);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        InnerAdapter innerAdapter = new InnerAdapter(item.innerEntities);
        recyclerView.setAdapter(innerAdapter);
        if (item.scrollOffset > 0) {
            layoutManager.scrollToPositionWithOffset(item.scrollPosition, item.scrollOffset);
        }
        recyclerView.addOnScrollListener(new MyOnScrollListener(item, layoutManager));
    }


    private class MyOnScrollListener extends RecyclerView.OnScrollListener {

        private LinearLayoutManager mLayoutManager;
        private Entity mEntity;
        private int mItemWidth;
        private int mItemMargin;

        public MyOnScrollListener(Entity shopItem, LinearLayoutManager layoutManager) {
            mLayoutManager = layoutManager;
            mEntity = shopItem;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:

                    int offset = recyclerView.computeHorizontalScrollOffset();
                    mEntity.scrollPosition = mLayoutManager.findFirstVisibleItemPosition() < 0 ? mEntity.scrollPosition : mLayoutManager.findFirstVisibleItemPosition() + 1;
                    if (mItemWidth <= 0) {
                        View item = mLayoutManager.findViewByPosition(mEntity.scrollPosition);
                        if (item != null) {
                            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) item.getLayoutParams();
                            mItemWidth = item.getWidth();
                            mItemMargin = layoutParams.rightMargin;
                        }
                    }
                    if (offset > 0 && mItemWidth > 0) {
                        //offset % mItemWidth：得到当前position的滑动距离
                        //mEntity.scrollPosition * mItemMargin：得到（0至position）的所有item的margin
                        //用当前item的宽度-所有margin-当前position的滑动距离，就得到offset。
                        mEntity.scrollOffset = mItemWidth - offset % mItemWidth + mEntity.scrollPosition * mItemMargin;
                    }
                    break;
            }
        }
    }


    private class InnerAdapter extends BaseQuickAdapter<Entity.InnerEntity, BaseViewHolder> {

        public InnerAdapter(List<Entity.InnerEntity> datas) {
            super(R.layout.item_horizontal_inner, datas);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final Entity.InnerEntity item) {

            helper.setText(R.id.title, item.innerTitle);
            ((ImageView) helper.getView(R.id.iv)).setImageResource(item.innerImageId);
        }

    }
}
