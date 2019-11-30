package com.jelly.chatlibrary.message.widget;//package com.jelly.chat.message.widget;
//
//import android.content.Context;
//import android.content.res.Resources;
//import android.text.Spannable;
//import android.text.SpannableString;
//import android.text.style.ForegroundColorSpan;
//import android.util.AttributeSet;
//import android.util.TypedValue;
//import android.view.View;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.android.mms.Gif.RoundedImageView;
//import com.android.mms.R;
//import com.android.mms.rcs.maap.chatbot.bean.ChatbotSearchListEntity;
//import com.android.mms.rcs.util.ImageManager;
//import com.android.mms.util.FontScaleBig;
//
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * Author：liuguodong 72071152
// * Date：2019.11.23 11:33
// * Description：小程序搜索列表的item
// */
//public class ChatbotSearchItem extends FrameLayout {
//    private static final String TAG = "ChatbotSearchItem";
//    // 上下文
//    private Context mContext;
//    // FontScale
//    private float mFontScale;
//    // Resources
//    private Resources mResources;
//    // 根布局
//    private View mRootView;
//    // 图标
//    private RoundedImageView riPic;
//    // 是否选中
//    private ImageView ivSelect;
//    // 名称
//    private TextView tvName;
//    // 内容描述
//    private TextView tvContent;
//    // 数据
//    private ChatbotSearchListEntity mEntity;
//
//    public ChatbotSearchItem(Context context) {
//        super(context);
//        init(context);
//    }
//
//    public ChatbotSearchItem(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init(context);
//    }
//
//    public ChatbotSearchItem(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(context);
//    }
//
//    private void init(Context context) {
//        mContext = context;
//        mResources = context.getResources();
//        mFontScale = FontScaleBig.getInstance().getFontScale();
//        mRootView = inflate(context, R.layout.chatbot_list_item_search, this);
//        riPic = mRootView.findViewById(R.id.ri_pic);
//        ivSelect = mRootView.findViewById(R.id.iv_select);
//        tvName = mRootView.findViewById(R.id.tv_name);
//        tvContent = mRootView.findViewById(R.id.tv_content);
//    }
//
//    /**
//     * Author：liuguodong 72071152
//     * Date：2019.11.23 11:40
//     * Description：设置数据
//     *
//     * @param entity 数据
//     */
//    public void setData(ChatbotSearchListEntity entity, String searchText) {
//        if (entity == null) {
//            entity = new ChatbotSearchListEntity();
//        }
//        mEntity = entity;
//        ImageManager.loadCircleImage(mContext, entity.chatbotPicUrl, riPic, R.drawable.ic_contact_picture);
//        ivSelect.setVisibility(entity.isSelect ? VISIBLE : GONE);
//        setText(tvName, entity.chatbotTitle + "", searchText + "");
//        setText(tvContent, entity.chatbotContent + "", searchText + "");
//        if (mFontScale != 0) {
//            tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontScale * mResources.getDimension(R.dimen.text_size_medium));
//            tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontScale * mResources.getDimension(R.dimen.text_size_small));
//        }
//    }
//
//    private void setText(TextView textView, String text, String searchText) {
//        text = text.replace("null", "");
//        searchText = searchText.replace("null", "");
//        SpannableString spannable = new SpannableString(text);
//        Pattern pattern = Pattern.compile(Pattern.quote(searchText), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
//        Matcher m = pattern.matcher(text);
//        while (m.find()) {
//            spannable.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.message_item_hightlight_text_color, null)),
//                    m.start(), m.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        }
//        textView.setText(spannable);
//    }
//
//    /**
//     * Author：liuguodong 72071152
//     * Date：2019.11.23 11:50
//     * Description：获取实体类数据
//     *
//     * @return 实体类
//     */
//    public ChatbotSearchListEntity getData() {
//        return mEntity;
//    }
//}
