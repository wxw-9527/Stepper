package com.rouxinpai.stepper

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import androidx.core.view.setPadding
import java.text.NumberFormat

/**
 * author : Saxxhw
 * email  : xingwangwang@cloudinnov.com
 * time   : 2022/12/30 11:34
 * desc   :
 */
class Stepper @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes),
    OnClickListener,
    TextWatcher {

    // 数据类型
    private var mDataElement: DataElement = DataElement.FLOAT

    // 保留小数位
    private var mDigits: Int = 3

    // 控件高度
    private var mHeight: Int = WRAP_CONTENT

    // 按钮相关
    private var mButtonWidth: Int = WRAP_CONTENT // 按钮宽
    private var mButtonTextSize: Float = 14f // 按钮文字大小
    private var mButtonTextColor: Int = Color.WHITE // 按钮文字颜色
    private var mLeftButtonBackground: Drawable? = null // 左侧按钮背景
    private var mRightButtonBackground: Drawable? = null // 右侧按钮背景
    private var mLeftButtonText: String? = null // 左侧按钮文字
    private var mRightButtonText: String? = null // 右侧按钮文字

    // 输入框相关
    private var mInputWidth: Int = WRAP_CONTENT // 输入框宽
    private var mInputBackground: Drawable? = null // 输入框背景
    private var mInputTextSize: Float = 14f // 输入框文字大小
    private var mInputTextColor: Int = Color.BLACK // 输入框文字颜色

    //
    private lateinit var mLeftButton: AppCompatTextView
    private lateinit var mInput: AppCompatEditText
    private lateinit var mRightButton: AppCompatTextView

    //
    private val mFormatter: NumberFormat

    // 数值
    private var mValue: Float? = null

    // 最小值
    private var mMinValue: Float = 0f

    // 最大值
    private var mMaxValue: Float = 0f

    // 是否启用
    private var mEnabled: Boolean = true

    //
    private val mListeners = arrayListOf<OnValueChangeListener>()

    init {
        //
        initAttributes(attrs)
        //
        initView()
        //
        mFormatter = NumberFormat.getNumberInstance().apply { maximumFractionDigits = mDigits }
        // 绑定监听事件
        mLeftButton.setOnClickListener(this)
        mRightButton.setOnClickListener(this)
        mInput.addTextChangedListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_left -> setTextAndMoveSelection(((mValue ?: 0f).minus(1)))
            R.id.btn_right -> setTextAndMoveSelection(((mValue ?: 0f).plus(1)))
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    override fun afterTextChanged(editable: Editable?) {
        if (editable.isNullOrBlank() || editable.endsWith(".")) {
            mValue = null
            mLeftButton.isEnabled = false
            mRightButton.isEnabled = false
            mListeners.forEach { it.onValueChanged(mValue) }
            return
        }
        try {
            val text = editable.toString()
            val value = text.toFloat()
            if ("0.0" == text || "0.00" == text) {
                mValue = mMinValue
                mLeftButton.isEnabled = false
                mRightButton.isEnabled = true
                mListeners.forEach { it.onValueChanged(mValue) }
            } else {
                when {
                    value <= mMinValue -> {
                        mValue = mMinValue
                        mLeftButton.isEnabled = false
                        mRightButton.isEnabled = true
                        mInput.removeTextChangedListener(this)
                        setTextAndMoveSelection(mValue)
                        mInput.addTextChangedListener(this)
                        mListeners.forEach { it.onValueChanged(mValue) }
                    }
                    value >= mMaxValue -> {
                        mValue = mMaxValue
                        mLeftButton.isEnabled = true
                        mRightButton.isEnabled = false
                        mInput.removeTextChangedListener(this)
                        setTextAndMoveSelection(mValue)
                        mInput.addTextChangedListener(this)
                        mListeners.forEach { it.onValueChanged(mValue) }
                    }
                    else -> {
                        mValue = value
                        mLeftButton.isEnabled = true
                        mRightButton.isEnabled = true
                        mListeners.forEach { it.onValueChanged(mValue) }
                    }
                }
            }
        } catch (e: NumberFormatException) {
            mValue = null
            mLeftButton.isEnabled = false
            mRightButton.isEnabled = false
            mListeners.forEach { it.onValueChanged(mValue) }
        }
    }

    fun setMinValue(min: Float) {
        mMinValue = min
    }

    fun setMaxValue(max: Float) {
        mMaxValue = max
    }

    fun setValue(value: Float?) {
        mValue = value
        mInput.removeTextChangedListener(this)
        setTextAndMoveSelection(mValue)
        mInput.addTextChangedListener(this)
        // 按钮状态初始化
        if (value != null) {
            if (value >= mMaxValue) {
                mRightButton.isEnabled = false
            }
            if (value <= mMinValue) {
                mLeftButton.isEnabled = false
            }
        }
    }

    fun setEnable(enabled: Boolean) {
        //
        mEnabled = enabled
        //
        mLeftButton.isEnabled = enabled
        mRightButton.isEnabled = enabled
        with(mInput) {
            isFocusableInTouchMode = enabled
            isFocusable = enabled
            isEnabled = enabled
        }
    }

    fun addListener(listener: OnValueChangeListener?) {
        if (listener != null) {
            mListeners.add(listener)
        }
    }

    fun removeListener(listener: OnValueChangeListener?) {
        if (listener != null) {
            mListeners.remove(listener)
        }
    }

    // 初始化自定义参数
    private fun initAttributes(attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.Stepper).use {
            mDataElement =
                when (it.getInteger(R.styleable.Stepper_stepper_data_element, mDataElement.value)) {
                    DataElement.FLOAT.value -> DataElement.FLOAT
                    DataElement.INT.value -> DataElement.INT
                    else -> throw IllegalArgumentException("unknown argument: stepper_data_element")
                }
            mDigits = it.getInteger(R.styleable.Stepper_stepper_digits, mDigits)
            mHeight = it.getLayoutDimension(R.styleable.Stepper_android_layout_height, mHeight)
            mButtonWidth =
                it.getLayoutDimension(R.styleable.Stepper_stepper_button_width, mButtonWidth)
            mButtonTextSize =
                it.getDimension(R.styleable.Stepper_stepper_button_text_size, mButtonTextSize)
            mButtonTextColor =
                it.getColor(R.styleable.Stepper_stepper_button_text_color, mButtonTextColor)
            mLeftButtonBackground =
                it.getDrawable(R.styleable.Stepper_stepper_left_button_background)
            mRightButtonBackground =
                it.getDrawable(R.styleable.Stepper_stepper_right_button_background)
            mLeftButtonText = it.getString(R.styleable.Stepper_stepper_left_button_text)
            mRightButtonText = it.getString(R.styleable.Stepper_stepper_right_button_text)
            mInputWidth =
                it.getLayoutDimension(R.styleable.Stepper_stepper_input_width, mButtonWidth)
            mInputBackground = it.getDrawable(R.styleable.Stepper_stepper_input_background)
            mInputTextSize =
                it.getDimension(R.styleable.Stepper_stepper_input_text_size, mInputTextSize)
            mInputTextColor =
                it.getColor(R.styleable.Stepper_stepper_input_text_color, mInputTextColor)
        }
    }

    // 初始化控件
    private fun initView() {
        // 设置布局方向
        orientation = HORIZONTAL
        // 填充左侧按钮
        mLeftButton = AppCompatTextView(context).apply {
            id = R.id.btn_left
            width = mButtonWidth
            height = mHeight
            background = mLeftButtonBackground ?: ContextCompat.getDrawable(
                context,
                R.drawable.selector_left_button_background
            )
            gravity = Gravity.CENTER
            setTextColor(mButtonTextColor)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mButtonTextSize)
            text = mLeftButtonText ?: "—"
        }
        addView(mLeftButton)
        // 填充输入框
        mInput = AppCompatEditText(context).apply {
            id = R.id.et_input
            setPadding(0)
            setSingleLine()
            background = mInputBackground ?: ContextCompat.getDrawable(
                context,
                R.drawable.selector_input_background
            )
            gravity = Gravity.CENTER
            setTextColor(mInputTextColor)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mInputTextSize)
            if (mDataElement == DataElement.INT) {
                inputType = InputType.TYPE_CLASS_NUMBER
                keyListener = DigitsKeyListener.getInstance("0123456789")
            } else {
                inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
                keyListener = DigitsKeyListener.getInstance("0123456789.")
            }
            filters = arrayOf(NumberInputFilter(mDigits))
        }
        addView(mInput)
        mInput.layoutParams = mInput.layoutParams.apply {
            width = mInputWidth
            height = mHeight
        }
        // 填充右侧按钮
        mRightButton = AppCompatTextView(getContext()).apply {
            id = R.id.btn_right
            width = mButtonWidth
            height = mHeight
            background = mRightButtonBackground ?: ContextCompat.getDrawable(
                context,
                R.drawable.selector_right_button_background
            )
            gravity = Gravity.CENTER
            setTextColor(mButtonTextColor)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mButtonTextSize)
            text = mRightButtonText ?: "+"
        }
        addView(mRightButton)
    }

    private fun setTextAndMoveSelection(value: Float?) {
        with(mInput) {
            setText(value?.let { mFormatter.format(it) })
            setSelection(text?.length ?: 0)
        }
    }
}