package com.aboust.jellybox

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop

/**
 *
 *
 */
class JellyBoxLayout : LinearLayout, TextWatcher, View.OnKeyListener, OnFocusChangeListener {

    private var textChangeListener: OnTextChangeListener? = null

    /**
     * 输入框数量
     */
    private var count: Int = 0

    /**
     * 输入框类型
     */
    private var boxType: Types = Types.NUMBER

    /**
     * 输入框的宽度
     */
    private var boxSize: Int = 120

    private var typeface: Typeface? = null


//    /**
//     * 输入框宽度
//     */
//    private var boxWidth = 0

    /**
     * 文字颜色
     */
    private var textColor: Int = Color.GRAY

    /**
     * 文字大小
     */
    private var textSize: Float = 16F

    /**
     * 输入框背景
     */
    private var boxBackground: Int = R.drawable.selector_edit_code

    /**
     * 输入框间距
     */
    private var boxMargin = 0F


    /**
     * 是否显示光标
     */
    private var cursorVisible: Boolean = true

    /**
     * 光标样式
     */
    private var cursorDrawable: Int = R.drawable.selector_edit_cursor


    enum class Types {

        /**
         * 数字类型
         */
        NUMBER,

        /**
         * 数字密码
         */
        NUMBER_PASSWORD,

        /**
         * 文字
         */
        TEXT,

        /**
         * 文字密码
         */
        TEXT_PASSWORD
    }

    private fun initView(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.JellyBoxLayout)
        val type = typedArray.getInt(R.styleable.JellyBoxLayout_box_input_type, Types.NUMBER.ordinal)
        boxType = Types.values()[type]
        count = typedArray.getInteger(R.styleable.JellyBoxLayout_box_figures, 4)
        boxSize = typedArray.getDimensionPixelSize(R.styleable.JellyBoxLayout_box_size, 120)
        textColor = typedArray.getColor(R.styleable.JellyBoxLayout_box_text_color, Color.BLACK)
        textSize =
            typedArray.getDimensionPixelSize(R.styleable.JellyBoxLayout_box_text_size, 16).toFloat()
        boxBackground = typedArray.getResourceId(
            R.styleable.JellyBoxLayout_box_background,
            R.drawable.selector_edit_code
        )
        cursorDrawable = typedArray.getResourceId(
            R.styleable.JellyBoxLayout_box_cursor_drawable,
            R.drawable.selector_edit_cursor
        )
        cursorVisible = typedArray.getBoolean(R.styleable.JellyBoxLayout_box_cursor_visible, true)
        boxMargin = typedArray.getDimension(R.styleable.JellyBoxLayout_box_spacing, 0F)
        val typefaceId = typedArray.getString(R.styleable.JellyBoxLayout_typeface_assets)
        if (null != typefaceId) typeface =
            Typeface.createFromAsset(this.resources.assets, typefaceId)
//        orientation = HORIZONTAL
        addBox()

        //释放资源
        typedArray.recycle()
    }

    private fun addBox() {
        for (i in 0 until count) {
            val editText = editText(i)
            addView(editText)
            //设置第一个editText获取焦点
            if (0 == i) editText.isFocusable = true
        }
    }

    private fun editText(index: Int): EditText {
        val editText = EditText(context)
        editText.layoutParams = getLayoutParams(index)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            editText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        }
        editText.gravity = Gravity.CENTER
        editText.id = index
        editText.isCursorVisible = false
        editText.maxEms = 1
        editText.setTextColor(textColor)
        editText.textSize = textSize
        editText.isCursorVisible = cursorVisible
        editText.maxLines = 1
        editText.filters = arrayOf<InputFilter>(LengthFilter(1))
        when (boxType) {
            Types.NUMBER -> editText.inputType = InputType.TYPE_CLASS_NUMBER
            Types.NUMBER_PASSWORD -> {
                editText.inputType =
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                editText.transformationMethod = AsteriskPasswordTransformationMethod()
            }
            Types.TEXT -> editText.inputType = InputType.TYPE_CLASS_TEXT
            Types.TEXT_PASSWORD -> editText.inputType = InputType.TYPE_CLASS_NUMBER
            else -> editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
        editText.setPadding(0, 0, 0, 0)
        editText.setOnKeyListener(this)
        editText.setBackgroundResource(boxBackground)
        editTextCursorDrawable(editText)
        editText.addTextChangedListener(this)
        editText.setOnKeyListener(this)
        editText.onFocusChangeListener = this
        return editText
    }

    /**
     * 获取EditText 的 LayoutParams
     * 当boxMargin 为空的时候默认取boxSize的 1/4大小
     */
    private fun getLayoutParams(index: Int): LayoutParams {
        val layoutParams = LayoutParams(boxSize, boxSize)
        val margin = if (0F == this.boxMargin) boxSize / 4 else this.boxMargin.toInt()
        if (0 != index) when (orientation) {
            HORIZONTAL -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutParams.marginStart = margin
            } else {
                layoutParams.leftMargin = margin
            }
            VERTICAL -> layoutParams.topMargin = margin
        }
        layoutParams.gravity = Gravity.CENTER
        return layoutParams
    }

    private fun editTextCursorDrawable(editText: EditText?) {
        //修改光标的颜色（反射）
        if (cursorVisible) {
            try {
                val f = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                f.isAccessible = true
                f[editText] = cursorDrawable
            } catch (ignored: Exception) {
                Log.e(
                    this.javaClass.simpleName,
                    String.format(
                        "mCursorDrawableRes put value failure,cause: %s",
                        ignored.javaClass.name
                    )
                )
            }
        }
    }

    private fun updateBoxMargin() {
        for (index in 0 until count) {
            val editText = getChildAt(index) as EditText
            editText.layoutParams = getLayoutParams(index)
        }
    }

    /**
     * http://images2015.cnblogs.com/blog/606814/201606/606814-20160605164121055-1503021621.png
     *
     * 1. EXACTLY  表示父视图希望子视图的大小应该是由specSize的值来决定的，系统默认会按照这个规则来设置子视图的大小，简单的说（当设置width或height为match_parent时，模式为EXACTLY，因为子view会占据剩余容器的空间，所以它大小是确定的）
     * 2. AT_MOST 表示子视图最多只能是specSize中指定的大小。（当设置为wrap_content时，模式为AT_MOST, 表示子view的大小最多是多少，这样子view会根据这个上限来设置自己的尺寸）
     * 3. UNSPECIFIED 表示开发人员可以将视图按照自己的意愿设置成任意的大小，没有任何限制。这种情况比较少见，不太会用到。
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        updateBoxMargin()
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    private fun measureWidth(measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        var lastWidth = 0
        when (specMode) {
            MeasureSpec.AT_MOST -> {
                when (orientation) {
                    VERTICAL -> {
                        if (0 == lastWidth) lastWidth = specSize
                        for (index in 0 until count) {
                            val editText = getChildAt(index) as EditText
                            val lp = editText.layoutParams
                            val w = editText.marginStart + editText.marginEnd + lp.width
                            lastWidth = w.coerceAtMost(lastWidth)
                        }
                    }
                    HORIZONTAL -> {
                        for (index in 0 until count) {
                            val editText = getChildAt(index) as EditText
                            val lp = editText.layoutParams
                            lastWidth += lp.width + editText.marginStart + editText.marginEnd
                        }
                        lastWidth += paddingLeft + paddingRight
                    }
                }
            }
            MeasureSpec.EXACTLY -> lastWidth = specSize
            MeasureSpec.UNSPECIFIED -> lastWidth = suggestedMinimumWidth.coerceAtLeast(specSize)

        }
        return lastWidth
    }

    private fun measureHeight(measureSpec: Int): Int {
        var lastHeight = 0
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        when (specMode) {
            MeasureSpec.AT_MOST -> {
                when (orientation) {
                    VERTICAL -> {
                        for (index in 0 until count) {
                            val editText = getChildAt(index) as EditText
                            val lp = editText.layoutParams
                            lastHeight += editText.marginTop + editText.marginBottom + lp.height
                        }
                        lastHeight += paddingTop + paddingBottom
                    }
                    HORIZONTAL -> {
                        if (0 == lastHeight) lastHeight = specSize
                        for (index in 0 until count) {
                            val editText = getChildAt(index) as EditText
                            val lp = editText.layoutParams
                            val h = editText.marginBottom + editText.marginTop + lp.height
                            lastHeight = h.coerceAtMost(lastHeight)
                        }
                    }
                }


            }
            MeasureSpec.EXACTLY -> lastHeight = specSize
            MeasureSpec.UNSPECIFIED -> lastHeight = suggestedMinimumWidth.coerceAtLeast(specSize)
        }
        return lastHeight
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d(this.javaClass.simpleName, "onSizeChanged -> w:$w h:$h old_w:$oldw old_h:$oldh")
    }

    override fun onFocusChange(view: View, b: Boolean) {
        if (b) focus()
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {
        if (s.isNotEmpty()) focus()
        textChangeListener?.onTextChange(this, result)
        //如果最后一个输入框有字符，则返回结果
        val lastEditText = getChildAt(count - 1) as EditText
        if (lastEditText.text.isNotEmpty()) textChangeListener?.onComplete(this, result)

    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
            backFocus()
        }
        return false
    }

    override fun setEnabled(enabled: Boolean) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.isEnabled = enabled
        }
    }

    /**
     * 获取焦点
     */
    private fun focus() {
        val count = childCount
        var editText: EditText
        //利用for循环找出还最前面那个还没被输入字符的EditText，并把焦点移交给它。
        for (i in 0 until count) {
            editText = getChildAt(i) as EditText
            if (editText.text.isEmpty()) {
                editText.isCursorVisible = cursorVisible
                editText.requestFocus()
                return
            } else {
                editText.isCursorVisible = false
                if (i == count - 1) {
                    editText.requestFocus()
                }
            }
        }
    }

    private fun backFocus() {
        var editText: EditText
        //循环检测有字符的`editText`，把其置空，并获取焦点。
        for (i in count - 1 downTo 0) {
            editText = getChildAt(i) as EditText
            if (editText.text.isNotEmpty()) {
                editText.setText("")
                editText.isCursorVisible = cursorVisible
                editText.requestFocus()
                return
            }
        }
    }

    private val result: String
        get() {
            val stringBuffer = StringBuilder()
            for (i in 0 until count) {
                val v = getChildAt(i)
                if (v is EditText) stringBuffer.append(v.text)
            }
            return stringBuffer.toString()
        }

    interface OnTextChangeListener {
        /**
         * 文本改变
         */
        fun onTextChange(view: View?, content: String?)

        /**
         * 输入完成
         */
        fun onComplete(view: View?, content: String?)
    }

    /**
     * 清空验证码输入框
     */
    fun clear() {
        for (i in count - 1 downTo 0) {
            val view = getChildAt(i)
            if (view is EditText) {
                view.setText("")
                if (i == 0) {
                    view.isCursorVisible = cursorVisible
                    view.requestFocus()
                }
            }
        }
    }


    constructor(context: Context?) : super(context) {
        initView(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(attrs)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(attrs)
    }

}

class AsteriskPasswordTransformationMethod : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence, view: View): CharSequence =
        PasswordCharSequence(source)

    class PasswordCharSequence(private val mSource: CharSequence) : CharSequence {
        override val length: Int get() = mSource.length
        override fun get(index: Int): Char = '•'
        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
            mSource.subSequence(startIndex, endIndex)
    }
}

