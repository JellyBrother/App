package com.jelly.othertool.search.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jelly.baselibrary.utils.FontScaleBigUtil;
import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.baselibrary.utils.PublicUtil;
import com.jelly.othertool.R;
import com.jelly.othertool.main.common.OtherToolCommon;
import com.jelly.othertool.search.entity.SearchListEntity;
import com.jelly.othertool.search.widget.SearchItem;

import java.util.ArrayList;
import java.util.List;

public class SearchListAdapter extends BaseAdapter {
    private static final String TAG = "SearchListAdapter";
    // 上下文
    private Context mContext;
    // 需要展示的所有数据
    private List<SearchListEntity> mData = new ArrayList<>();
    // 搜索的文本
    private String mSearchText;
    // FontScale
    private float mFontScale;
    // Resources
    private Resources mResources;
    // 本地数据库数据
    private List<SearchListEntity> mDbList = new ArrayList<>();

    public SearchListAdapter(Context context) {
        mContext = context;
        mResources = context.getResources();
        mFontScale = FontScaleBigUtil.getInstance().getFontScale();
    }

    @Override
    public int getCount() {
        if (mData == null || mData.isEmpty()) {
            return 0;
        }
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return OtherToolCommon.SearchList.ADAPTER_ITEM_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (mData == null || mData.isEmpty()) {
            return OtherToolCommon.SearchList.ADAPTER_ITEM_TYPE_OUTER;
        }
        SearchListEntity entity = mData.get(position);
        return entity.itemType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchListEntity entity = mData.get(position);
        if (entity == null) {
            return convertView;
        }
        int type = getItemViewType(position);
        switch (type) {
            case OtherToolCommon.SearchList.ADAPTER_ITEM_TYPE_CHATBOT:
                if (convertView == null) {
                    convertView = new SearchItem(mContext);
                }
                ((SearchItem) convertView).setData(entity, mSearchText);
                break;
            case OtherToolCommon.SearchList.ADAPTER_ITEM_TYPE_OUTER:
            default:
                ChatbotOuterHolder outerHolder = null;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.othertool_list_item_search_outer, null, false);
                    outerHolder = new ChatbotOuterHolder();
                    outerHolder.tvItemName = convertView.findViewById(R.id.tv_item_name);
                    convertView.setTag(outerHolder);
                } else {
                    outerHolder = (ChatbotOuterHolder) convertView.getTag();
                }
                if (entity.dataFrom == OtherToolCommon.SearchList.DATAFROM_DB) {// 条目是：本地小程序
                    outerHolder.tvItemName.setText(mContext.getString(R.string.othertool_search_local));
                } else {// 条目是：网络搜索
                    outerHolder.tvItemName.setText(mContext.getString(R.string.othertool_search_net));
                }
                if (mFontScale != 0) {
                    outerHolder.tvItemName.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontScale * mResources.getDimension(R.dimen.othertool_dimen_14sp));
                }
                break;
        }
        return convertView;
    }

    class ChatbotOuterHolder {
        TextView tvItemName;
    }

    /**
     * Author：liuguodong 72071152
     * Date：2019.11.20 21:19
     * Description：设置输入框搜索回来的数据
     *
     * @param list 输入框搜索回来的数据
     */
    public void setInputSearchData(List<SearchListEntity> list, String searchText) {
        LogUtil.getInstance().d(TAG, "setInputSearchData");
        mSearchText = searchText;
        mData.clear();
        if (mData != null && list != null) {
            mDbList.clear();
            List<SearchListEntity> netList = new ArrayList<>();
            // 对数据进行分类整理
            for (SearchListEntity entity : list) {
                // 本地数据库查找的小程序
                if (entity.dataFrom == OtherToolCommon.SearchList.DATAFROM_DB) {
                    entity.itemType = OtherToolCommon.SearchList.ADAPTER_ITEM_TYPE_CHATBOT;
                    mDbList.add(entity);
                } else {// 网络查找的小程序
                    entity.itemType = OtherToolCommon.SearchList.ADAPTER_ITEM_TYPE_CHATBOT;
                    netList.add(entity);
                }
            }
            // 对网络请求的数据加打钩标识
            markChatbot(mDbList, netList);
            // 对分类的数据进行排序（看是否需要）
            // 添加本地小程序
            if (!PublicUtil.isEmptyList(mDbList)) {
                // 对整理好的数据添加头部，然后再进行展示
                SearchListEntity entity = new SearchListEntity();
                entity.dataFrom = OtherToolCommon.SearchList.DATAFROM_DB;
                entity.itemType = OtherToolCommon.SearchList.ADAPTER_ITEM_TYPE_OUTER;
                mData.add(entity);
                mData.addAll(mDbList);
            }
            // 添加网络查找的数据
            if (!PublicUtil.isEmptyList(netList)) {
                // 对整理好的数据添加头部，然后再进行展示
                SearchListEntity entity = new SearchListEntity();
                entity.dataFrom = OtherToolCommon.SearchList.DATAFROM_NET;
                entity.itemType = OtherToolCommon.SearchList.ADAPTER_ITEM_TYPE_OUTER;
                mData.add(entity);
                mData.addAll(netList);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Author：liuguodong 72071152
     * Date：2019.11.20 21:19
     * Description：设置上拉加载搜索回来的数据
     *
     * @param list 上拉加载搜索回来的数据
     */
    public void setLoadMoreNetSearchData(List<SearchListEntity> list) {
        LogUtil.getInstance().d(TAG, "setLoadMoreNetSearchData");
        if (mData != null && list != null) {
            markChatbot(mDbList, list);
            mData.addAll(list);
        }
        notifyDataSetChanged();
    }

    private void markChatbot(List<SearchListEntity> dbList, List<SearchListEntity> netList) {
        if (PublicUtil.isEmptyList(dbList)) {
            return;
        }
        if (PublicUtil.isEmptyList(netList)) {
            return;
        }
        for (SearchListEntity dbEntity : dbList) {
            for (SearchListEntity netEntity : netList) {
                String dbId = PublicUtil.getString(dbEntity.serviceId);
                String netId = PublicUtil.getString(netEntity.serviceId);
                // 本地小程序不打钩，其实这里可以不用再设置了，因为resetData的时候已经初始化了
                dbEntity.isSelect = false;
                netEntity.isSelect = false;
                if (TextUtils.isEmpty(dbId) && TextUtils.isEmpty(netId)) {
                    continue;
                }
                if (TextUtils.equals(dbId, netId)) {
                    netEntity.isSelect = true;
                }
            }
        }
    }
}
