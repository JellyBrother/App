//package com.example.myapp.base.widget.table.params;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.res.Resources;
//import android.text.TextUtils;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.PopupWindow;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.myapp.base.bridge.detail.entity.DetailParamsClassEntity;
//import com.example.myapp.base.bridge.detail.entity.DetailParamsEntity;
//import com.example.myapp.base.bridge.detail.entity.DetailParamsTwoClassEntity;
//import com.example.myapp.base.bridge.module.pk.PkParams;
//import com.example.myapp.base.bridge.utils.DataUtil;
//import com.example.myapp.base.utils.ListUtil;
//import com.example.myapp.base.utils.LogUtils;
//import com.example.myapp.base.utils.Utils;
//import com.example.myapp.base.widget.R;
//import com.example.myapp.base.widget.popupwindow.params.ParamsPopupWindow;
//import com.example.myapp.base.widget.table.TableView;
//
//import java.util.List;
//
//import kotlin.Unit;
//import kotlin.jvm.functions.Function1;
//
//public class ParamsTableView extends TableView {
//    protected Context context;
//    protected Activity activity;
//    protected Resources resources;
//    protected LayoutInflater layoutInflater;
//    private BaseParamsTableAdapter adapter;
//    private DetailParamsEntity detailParamsEntity;
//    private ParamsPopupWindow paramsPopup;
//    private String selectParamsId;
//
//    public ParamsTableView(@NonNull Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//        init(context);
//    }
//
//    public ParamsTableView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(context);
//    }
//
//    private void init(Context context) {
//        try {
//            this.context = context;
//            this.activity = Utils.getActivity(context);
//            this.resources = context.getResources();
//            this.layoutInflater = activity.getLayoutInflater();
//            initView();
//        } catch (Throwable t) {
//            LogUtils.d("init Throwable t:" + t);
//        }
//    }
//
//    protected void initView() {
//    }
//
//    public void setData(BaseParamsTableAdapter tableAdapter, DetailParamsEntity detailParamsEntity) {
//        boolean isNull = tableAdapter == null || detailParamsEntity == null;
//        LogUtils.d("setData isNull:" + isNull);
//        if (isNull) {
//            return;
//        }
//        this.adapter = tableAdapter;
//        this.detailParamsEntity = detailParamsEntity;
//        adapter.setData(detailParamsEntity);
//        setAdapter(adapter);
//        setCurrentTouchView(getFirstHListViewScrollView());
//        addTableTitleLayout(detailParamsEntity);
//        adapter.setIHideParamsListener(new IHideParamsListener() {
//            @Override
//            public void OnCheckedChange(View view, boolean isChecked) {
//                detailParamsEntity.setHideSame(isChecked);
//                setData(adapter, detailParamsEntity);
//            }
//        });
//    }
//
//    /**
//     * 添加表格固定标题，不滚动
//     */
//    private void addTableTitleLayout(DetailParamsEntity entity) {
//        List<DetailParamsClassEntity> classData = entity.getClassData();
//        if (ListUtil.isEmpty(classData)) {
//            getTitleLayout().setVisibility(GONE);
//            return;
//        }
//        getTitleLayout().setVisibility(VISIBLE);
//        View titleLayout = layoutInflater.inflate(R.layout.base_params_content_title2, null);
//        TextView tvTitle = titleLayout.findViewById(R.id.tv_title);
//        DetailParamsClassEntity classEntity = classData.get(0);
//        selectParamsId = classEntity.getParamsId();
//        tvTitle.setText(classEntity.getName());
//        tvTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (paramsPopup != null && paramsPopup.isShowing()) {
//                    return;
//                }
//                paramsPopup = new ParamsPopupWindow(getContext(), getWidth(), getPopupHeight(tvTitle));
//                paramsPopup.setOnItemClick(new Function1<PkParams, Unit>() {
//                    @Override
//                    public Unit invoke(PkParams pkParams) {
//                        List<DetailParamsTwoClassEntity> twoClassEntitys = detailParamsEntity.getTwoClassData();
//                        int position = 0;
//                        for (int i = 0; i < twoClassEntitys.size(); i++) {
//                            DetailParamsTwoClassEntity twoClassEntity = twoClassEntitys.get(i);
//                            selectParamsId = twoClassEntity.getParamsId();
//                            if (TextUtils.equals(selectParamsId, pkParams.getParamsId())) {
//                                position = i;
//                                break;
//                            }
//                        }
//                        setSelection(position);
//                        return null;
//                    }
//                });
//                paramsPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                    @Override
//                    public void onDismiss() {
//                        tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.base_arrow_down), null);
//                    }
//                });
//                tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.base_arrow_up), null);
//                List<PkParams> pkParams = DataUtil.INSTANCE.getPkParams(detailParamsEntity.getClassData(), selectParamsId);
//                paramsPopup.setData(pkParams);
//                paramsPopup.show(tvTitle);
//            }
//        });
//        addTitleLayout(titleLayout);
//        getContentRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int firstVisibleItemPosition = getRecyclerFirstVisibleItem(recyclerView);
//                List<DetailParamsTwoClassEntity> twoClassEntitys = detailParamsEntity.getTwoClassData();
//                if (ListUtil.isIndexOut(twoClassEntitys, firstVisibleItemPosition)) {
//                    return;
//                }
//                DetailParamsTwoClassEntity twoClassEntity = twoClassEntitys.get(firstVisibleItemPosition);
//                LogUtils.d("FirstVisibleItem：" + firstVisibleItemPosition + " | " + twoClassEntity.getName() + " | " + twoClassEntity.getParentParamsName());
//                selectParamsId = twoClassEntity.getParentParamsId();
//                if (TextUtils.isEmpty(selectParamsId)) {
//                    selectParamsId = twoClassEntity.getParamsId();
//                }
//                if (ListUtil.isEmpty(twoClassEntity.getTwoClassItemEntitys())) {
//                    tvTitle.setText(twoClassEntity.getName());
//                } else {
//                    tvTitle.setText(twoClassEntity.getParentParamsName());
//                }
//            }
//        });
//    }
//
//    private int getPopupHeight(TextView anchor) {
//        anchor.measure(0, 0);
//        return getHeight() - anchor.getMeasuredHeight() - getHeaderHeight();
//    }
//
//    private int getRecyclerFirstVisibleItem(RecyclerView recyclerView) {
//        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//        return layoutManager.findFirstVisibleItemPosition();
//    }
//}
