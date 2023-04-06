//package com.example.myapp.base.widget.table.params;
//
//import android.app.Activity;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.myapp.base.bridge.detail.entity.DetailParamsEntity;
//import com.example.myapp.base.bridge.detail.entity.DetailParamsTwoClassEntity;
//import com.example.myapp.base.bridge.detail.entity.DetailParamsTwoClassItemEntity;
//import com.example.myapp.base.bridge.detail.entity.DetailType;
//import com.example.myapp.base.utils.ListUtil;
//import com.example.myapp.base.utils.LogUtils;
//import com.example.myapp.base.utils.SizeUtils;
//import com.example.myapp.base.widget.R;
//import com.example.myapp.base.widget.table.TableAdapter;
//import com.example.myapp.base.widget.table.TableCellView;
//import com.example.myapp.base.widget.table.TableRowTitleView;
//
//import java.util.List;
//
//public abstract class BaseParamsTableAdapter implements TableAdapter {
//    protected Activity activity;
//    protected LayoutInflater inflater;
//    protected DetailParamsEntity paramsEntity;
//    protected boolean isProduct = false;
//    protected IHideParamsListener hideParamsListener;
//    protected int dp92;
//    protected int dp130;
//
//    public BaseParamsTableAdapter(Activity activity) {
//        this.activity = activity;
//        inflater = activity.getLayoutInflater();
//        dp92 = SizeUtils.dp2px(92f);
//        dp130 = SizeUtils.dp2px(130f);
//    }
//
//    public void setData(DetailParamsEntity paramsEntity) {
//        if (paramsEntity == null) {
//            return;
//        }
//        this.paramsEntity = paramsEntity;
//        isProduct = paramsEntity.getItemType() == DetailType.TYPE_PRODUCT;
//    }
//
//
//    @Override
//    public boolean getFirstColumnIsMove() {
//        // 第一列是否可移动
//        return false;
//    }
//
//    @Override
//    public int getContentRows() {
//        // 表格内容的行数，不包括标题行
//        if (paramsEntity == null) {
//            return 0;
//        }
//        return paramsEntity.getTwoClassData().size();
//    }
//
//    @Override
//    public int getContentColumn() {
//        // 列数
//        if (paramsEntity == null) {
//            return 0;
//        }
//        if (isProduct) {
//            return 2;
//        }
//        return paramsEntity.getProductEntitys().size() + 1;
//    }
//
//    @Nullable
//    @Override
//    public View getTableCellView(int contentRow, int contentColumn, @Nullable View view, @Nullable ViewGroup parent) {
//        if (ListUtil.isIndexOut(paramsEntity.getTwoClassData(), contentRow)) {
//            return view;
//        }
//        // 表格正文的view，行和列都从0开始，宽度的话在载入的时候，默认会是以标题行各列的宽度，高度的话自适应
//        TableCellView tableCellView;
//        if (null == view) {
//            tableCellView = new TableCellView();
//            view = inflater.inflate(R.layout.base_params_content_item, null);
//            TextView tvTitle = view.findViewById(R.id.tvw_title);
//            tvTitle.setGravity(Gravity.CENTER);
//            tableCellView.setTvTitle(tvTitle);
//            view.setTag(tableCellView);
//        } else {
//            tableCellView = (TableCellView) view.getTag();
//        }
//        DetailParamsTwoClassEntity twoClassEntity = paramsEntity.getTwoClassData().get(contentRow);
//        List<DetailParamsTwoClassItemEntity> twoClassItemEntitys = twoClassEntity.getTwoClassItemEntitys();
//        if (ListUtil.isEmpty(twoClassItemEntitys)) {
//            view.setVisibility(View.GONE);
//            return view;
//        }
//        view.setVisibility(View.VISIBLE);
//        TextView tvTitle = tableCellView.getTvTitle();
//        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) tvTitle.getLayoutParams();
//        if (contentColumn == 0) {
//            layoutParams.width = dp92;
//            tvTitle.setLayoutParams(layoutParams);
//
//            tvTitle.setText(twoClassEntity.getName());
//            view.setBackgroundResource(R.drawable.base_params_content_item_white_bg);
//        } else {
//            layoutParams.width = dp130;
//            tvTitle.setLayoutParams(layoutParams);
//
//            if (ListUtil.isIndexOut(twoClassItemEntitys, contentColumn - 1)) {
//                view.setBackgroundResource(R.drawable.base_params_content_item_white_bg);
//            } else {
//                DetailParamsTwoClassItemEntity twoClassItemEntity = twoClassItemEntitys.get(contentColumn - 1);
//                String name = twoClassItemEntity.getName();
////            if (TextUtils.isEmpty(name) || TextUtils.equals(name, DetailModel.DEFAULT_TAB_VALUE)) {
//                tvTitle.setText(name);
//                if (twoClassItemEntity.isSame()) {
//                    view.setBackgroundResource(R.drawable.base_params_content_item_white_bg);
//                } else {
//                    view.setBackgroundResource(R.drawable.base_params_content_item_gray_bg);
//                }
//            }
//        }
//        return view;
//    }
//
//    @NonNull
//    @Override
//    public View getTableRowTitleView(int contentRow, @Nullable View view) {
//        if (ListUtil.isIndexOut(paramsEntity.getTwoClassData(), contentRow)) {
//            return view;
//        }
//        // 每一行的标题
//        TableRowTitleView rowTitleView;
//        if (null == view) {
//            rowTitleView = new TableRowTitleView();
//            view = inflater.inflate(R.layout.base_params_content_title, null);
//            TextView tvTitle = view.findViewById(R.id.tv_title);
//            rowTitleView.setTvTitle(tvTitle);
//            view.setTag(rowTitleView);
//        } else {
//            rowTitleView = (TableRowTitleView) view.getTag();
//        }
//        DetailParamsTwoClassEntity twoClassEntity = paramsEntity.getTwoClassData().get(contentRow);
//        if (ListUtil.isEmpty(twoClassEntity.getTwoClassItemEntitys())) {
//            rowTitleView.getTvTitle().setText(twoClassEntity.getName());
//            view.setVisibility(View.VISIBLE);
//        } else {
//            view.setVisibility(View.GONE);
//        }
//        return view;
//    }
//
//    @Nullable
//    @Override
//    public View getFooterView(@NonNull RecyclerView view) {
//        LogUtils.d("getFooterView");
//        return null;
//    }
//
//    @NonNull
//    @Override
//    public Object getItem(int contentRow) {
//        LogUtils.d("getItem contentRow:" + contentRow);
//        return null;
//    }
//
//    @Override
//    public void onClickContentRowItem(int row, @Nullable View convertView) {
//        // 每一行被点击的时候的回调
//        LogUtils.d("onClickContentRowItem row:" + row);
//    }
//
//    public void setIHideParamsListener(IHideParamsListener onCheckedChangeListener) {
//        this.hideParamsListener = onCheckedChangeListener;
//    }
//}