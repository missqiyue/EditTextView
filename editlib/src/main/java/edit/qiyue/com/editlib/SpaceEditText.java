package edit.qiyue.com.editlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * Description:每4位自动添加在后面添加空格的EditText 并带有一键清空icon,同时过滤表情
 * Data：2018\12\22 0022-9:54
 * Author: zgl
 */
public class SpaceEditText extends ContainsEmojiEditText {
    private static final int DRAWABLE_LEFT = 0;
    private static final int DRAWABLE_TOP = 1;
    private static final int DRAWABLE_RIGHT = 2;
    private static final int DRAWABLE_BOTTOM = 3;
    protected Drawable mClearDrawable;

    private boolean isShowDelete = false;//是否显示删除icon
    private int spaceSplitCount = 4;//每隔几位分割


    //上次输入框中的内容
    private String lastString;
    //光标的位置
    private int selectPosition;
    //光标是否在最后
    private boolean isSelectPositionEnd = false;

    //输入框内容改变监听
    private TextChangeListener listener;

    public SpaceEditText(Context context) {
        super(context);
        initView();
    }

    public SpaceEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context, attrs);
        initView();
    }

    public SpaceEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initData(context, attrs);
        initView();
    }

    private void initData(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CommonEditTextView);

        try {
            //通过属性获取配置信息
            spaceSplitCount = array.getInteger(R.styleable.CommonEditTextView_spaceSplitCount, spaceSplitCount);
            isShowDelete = array.getBoolean(R.styleable.CommonEditTextView_isShowDelete, isShowDelete);
        } finally {
            array.recycle();
        }
    }


    private void initView() {
        mClearDrawable = getResources().getDrawable(R.mipmap.search_delete);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /**
             * 当输入框内容改变时的回调
             * @param s  改变后的字符串
             * @param start 改变之后的光标下标
             * @param before 删除了多少个字符
             * @param count 添加了多少个字符
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isShowDelete) {
                    setClearIconVisible(hasFocus() && s.length() > 0);
                }

                //因为重新排序之后setText的存在
                //会导致输入框的内容从0开始输入，这里是为了避免这种情况产生一系列问题
                if (start == 0 && count > 1 && SpaceEditText.this.getSelectionStart() == 0) {
                    return;
                }

                String textTrim = EditTextUtils.getTextTrim(SpaceEditText.this);
                if (TextUtils.isEmpty(textTrim)) {
                    return;
                }

                //如果 before >0 && count == 0,代表此次操作是删除操作
                if (before > 0 && count == 0) {
                    selectPosition = start;
                    if (TextUtils.isEmpty(lastString)) {
                        return;
                    }
                    //将上次的字符串去空格 和 改变之后的字符串去空格 进行比较
                    //如果一致，代表本次操作删除的是空格
                    if (textTrim.equals(lastString.replaceAll(" ", ""))) {
                        //帮助用户删除该删除的字符，而不是空格
                        StringBuilder stringBuilder = new StringBuilder(lastString);
                        stringBuilder.deleteCharAt(start - 1);
                        selectPosition = start - 1;
                        SpaceEditText.this.setText(stringBuilder.toString());
                    }
                } else {
                    //此处代表是添加操作
                    //当光标位于空格之前，添加字符时，需要让光标跳过空格，再按照之前的逻辑计算光标位置
                    if ((start + count) % (spaceSplitCount + 1) == 0) {
                        selectPosition = start + count + 1;
                    } else {
                        selectPosition = start + count;
                    }
                }

                if (selectPosition == s.length()) {
                    isSelectPositionEnd = true;
                } else {
                    isSelectPositionEnd = false;
                }

            }


            @Override
            public void afterTextChanged(Editable s) {
                //获取输入框中的内容,不可以去空格
                String etContent = EditTextUtils.getText(SpaceEditText.this);
                if (TextUtils.isEmpty(etContent)) {
                    if (listener != null) {
                        listener.textChange("");
                    }
                    return;
                }
                //重新拼接字符串
                String newContent = addSpaceByCredit(etContent);
                //保存本次字符串数据
                lastString = newContent;

                //如果有改变，则重新填充
                //防止EditText无限setText()产生死循环
                if (!newContent.equals(etContent)) {
                    SpaceEditText.this.setText(newContent);
                    //保证光标的位置
                    if (isSelectPositionEnd) {
                        SpaceEditText.this.setSelection(SpaceEditText.this.getText().length());

                    } else {
                        SpaceEditText.this.setSelection(selectPosition > newContent.length() ? newContent.length() : selectPosition);
                    }
                }
                //触发回调内容
                if (listener != null) {
                    listener.textChange(newContent);
                }

            }
        });
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (isShowDelete) {
            setClearIconVisible(focused && length() > 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                Drawable drawable = getCompoundDrawables()[DRAWABLE_RIGHT];
                if (drawable != null && event.getX() <= (getWidth() - getPaddingRight())
                        && event.getX() >= (getWidth() - getPaddingRight() - drawable.getBounds().width())) {
                    setText("");
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void setClearIconVisible(boolean visible) {
        setCompoundDrawablesWithIntrinsicBounds(getCompoundDrawables()[DRAWABLE_LEFT], getCompoundDrawables()[DRAWABLE_TOP],
                visible ? mClearDrawable : null, getCompoundDrawables()[DRAWABLE_BOTTOM]);
    }


    /**
     * 输入框内容回调，当输入框内容改变时会触发
     */
    public interface TextChangeListener {
        void textChange(String text);
    }

    public void setTextChangeListener(TextChangeListener listener) {
        this.listener = listener;
    }

    public void setDeleteIconShow(boolean iconShow) {
        this.isShowDelete = iconShow;
    }

    /**
     * 每X位添加一个空格
     *
     * @param content
     * @return
     */
    public String addSpaceByCredit(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        content = content.replaceAll(" ", "");
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        StringBuilder newString = new StringBuilder();
        for (int i = 1; i <= content.length(); i++) {
            if (i % spaceSplitCount == 0 && i != content.length()) {
                newString.append(content.charAt(i - 1) + " ");
            } else {
                newString.append(content.charAt(i - 1));
            }
        }
        return newString.toString();
    }
}
