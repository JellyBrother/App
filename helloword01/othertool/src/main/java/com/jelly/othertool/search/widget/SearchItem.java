package com.jelly.othertool.search.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jelly.baselibrary.utils.FontScaleBigUtil;
import com.jelly.baselibrary.utils.ImageUtil;
import com.jelly.baselibrary.utils.PublicUtil;
import com.jelly.baselibrary.widget.gif.RoundedImageView;
import com.jelly.othertool.R;
import com.jelly.othertool.search.entity.SearchListEntity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchItem extends FrameLayout {
    private static final String TAG = "SearchItem";
    // 上下文
    private Context mContext;
    // FontScale
    private float mFontScale;
    // Resources
    private Resources mResources;
    // 根布局
    private View mRootView;
    // 图标
    private RoundedImageView riPic;
    // 是否选中
    private ImageView ivSelect;
    // 名称
    private TextView tvName;
    // 内容描述
    private TextView tvContent;
    // 数据
    private SearchListEntity mEntity;

    public SearchItem(Context context) {
        super(context);
        init(context);
    }

    public SearchItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mResources = context.getResources();
        mFontScale = FontScaleBigUtil.getInstance().getFontScale();
        mRootView = inflate(context, R.layout.othertool_list_item_search, this);
        riPic = mRootView.findViewById(R.id.ri_pic);
        ivSelect = mRootView.findViewById(R.id.iv_select);
        tvName = mRootView.findViewById(R.id.tv_name);
        tvContent = mRootView.findViewById(R.id.tv_content);
    }

    /**
     * Description：设置数据
     *
     * @param entity 数据
     */
    public void setData(SearchListEntity entity, String searchText) {
        if (entity == null) {
            entity = new SearchListEntity();
        }
        mEntity = entity;
        ImageUtil.loadCircleImage(mContext, entity.chatbotPicUrl, riPic, -1);
        ivSelect.setVisibility(entity.isSelect ? VISIBLE : GONE);
        setText(tvName, entity.chatbotTitle, searchText);
        setText(tvContent, entity.chatbotContent, searchText);
        if (mFontScale != 0) {
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontScale * mResources.getDimension(R.dimen.othertool_dimen_14sp));
            tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontScale * mResources.getDimension(R.dimen.othertool_dimen_12sp));
        }
    }

    @SuppressLint("NewApi")
    private void setText(TextView textView, String text, String searchText) {
        text = PublicUtil.getString(text);
        searchText = PublicUtil.getString(searchText);
        SpannableString spannable = new SpannableString(text);
        Pattern pattern = Pattern.compile(Pattern.quote(searchText), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher m = pattern.matcher(text);
        while (m.find()) {
            spannable.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.othertool_color_456fff, null)),
                    m.start(), m.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        textView.setText(spannable);
    }

    /**
     * Description：获取实体类数据
     *
     * @return 实体类
     */
    public SearchListEntity getData() {
        return mEntity;
    }
}
