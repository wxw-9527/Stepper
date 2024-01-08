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
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.rouxinpai.stepper.databinding.StepperBinding
import java.text.NumberFormat

/**
 * author : Saxxhw
 * email  : xingwangwang@cloudinnov.com
 * time   : 2022/12/30 11:34
 * desc   :
 */
class Stepper @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes),
    OnClickListener,
    TextWatcher {

    // 数据类型
    private var mDataElement: DataElement = DataElement.FLOAT

    // 保留小数位
    private var mDigits: Int = 3

    // 是否隐藏按钮
    private var mHideButton: Boolean = false

    // 左侧按钮背景
    private var mLeftButtonBackground: Drawable? = null

    // 右侧按钮背景
    private var mRightButtonBackground: Drawable? = null

    // 输入框背景
    private var mInputBackground: Drawable? = null

    // 输入框文字大小
    private var mInputTextSize: Float = 14f

    // 输入框文字颜色
    private var mInputTextColor: Int = Color.BLACK

    // 输入框是否允许点击
    private var mInputClickable: Boolean = false

    // 按钮尺寸
    private var mButtonSize: Int = 26

    // 输入框宽
    private var mInputWidth: Int = 72

    // 输入框高
    private var mInputHeight: Int = 34

    // 按钮与输入框之间的间距
    private var mPaddingInputButton: Int = 4

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
    private var mOnInputClickListener: OnClickListener? = null

    //
    private val mListeners = arrayListOf<OnValueChangeListener>()

    //
    private val mBinding = StepperBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        //
        initAttributes(attrs)
        //
        initView()
        //
        mFormatter = NumberFormat.getNumberInstance().apply {
            isGroupingUsed = false
            maximumFractionDigits = mDigits
        }
        // 绑定监听事件
        mBinding.btnLeft.setOnClickListener(this)
        mBinding.btnRight.setOnClickListener(this)
        mBinding.etInput.addTextChangedListener(this)
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

            mHideButton = it.getBoolean(R.styleable.Stepper_stepper_hide_button, mHideButton)

            if (!mHideButton) {
                mLeftButtonBackground = it.getDrawable(R.styleable.Stepper_stepper_left_button_background)
                mRightButtonBackground = it.getDrawable(R.styleable.Stepper_stepper_right_button_background)
                mButtonSize = it.getLayoutDimension(
                    R.styleable.Stepper_stepper_button_size,
                    mButtonSize
                )
                mPaddingInputButton = it.getLayoutDimension(
                    R.styleable.Stepper_stepper_padding_input_button,
                    mPaddingInputButton
                )
            }
            mInputBackground = it.getDrawable(R.styleable.Stepper_stepper_input_background)
            mInputTextSize = it.getDimension(
                R.styleable.Stepper_stepper_input_text_size,
                mInputTextSize
            )
            mInputTextColor = it.getColor(
                R.styleable.Stepper_stepper_input_text_color,
                mInputTextColor
            )
            mInputClickable = it.getBoolean(
                R.styleable.Stepper_stepper_input_clickable,
                mInputClickable
            )
            mInputWidth = it.getLayoutDimension(
                R.styleable.Stepper_stepper_input_width,
                mInputWidth
            )
            mInputHeight = it.getLayoutDimension(
                R.styleable.Stepper_stepper_input_height,
                mInputHeight
            )
        }
    }

    // 初始化控件
    private fun initView() {
        // 左侧按钮
        with(mBinding.btnLeft) {
            isGone = mHideButton
            if (isVisible) {
                // 按钮背景
                background = mLeftButtonBackground ?: ContextCompat.getDrawable(
                    context,
                    R.drawable.stepper_ic_left
                )
                val lp = layoutParams as LayoutParams
                // 按钮尺寸
                lp.width = mButtonSize
                lp.height = mButtonSize
                // 设置与按钮之间的间距
                lp.marginEnd = mPaddingInputButton
                layoutParams = lp
            }
        }
        // 右侧按钮
        with(mBinding.btnRight) {
            isGone = mHideButton
            if (isVisible) {
                // 按钮背景
                background = mRightButtonBackground ?: ContextCompat.getDrawable(
                    context,
                    R.drawable.stepper_ic_right
                )
                val lp = layoutParams as LayoutParams
                // 按钮尺寸
                lp.width = mButtonSize
                lp.height = mButtonSize
                // 设置与按钮之间的间距
                lp.marginStart = mPaddingInputButton
                layoutParams = lp
            }
        }
        // 填充输入框
        with(mBinding.etInput) {
            // 设置数据类型
            configureInputTypeAndKeyListener()
            // 设置输入框过滤器
            filters = arrayOf(NumberInputFilter(mDigits))
            // 设置输入框背景
            background = mInputBackground ?: ContextCompat.getDrawable(
                context,
                R.drawable.selector_input_background
            )
            // 设置输入框文字大小
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mInputTextSize)
            // 设置输入框文字颜色
            setTextColor(mInputTextColor)
            // 设置输入框间距
            val lp = layoutParams as LayoutParams
            // 输入框尺寸
            lp.width = mInputWidth
            lp.height = mInputHeight
            layoutParams = lp
            // 设置点击事件
            if (mInputClickable) {
                isFocusable = false
                isFocusableInTouchMode = false
                setOnClickListener { view -> mOnInputClickListener?.onClick(view) }
            } else {
                isFocusable = true
                isFocusableInTouchMode = true
            }
        }
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
            mBinding.btnLeft.setEnable(false)
            mBinding.btnRight.setEnable(false)
            mListeners.forEach { it.onValueChanged(this, mValue) }
            return
        }
        try {
            val text = editable.toString()
            val value = text.toFloat()
            if ("0.0" == text || "0.00" == text) {
                mValue = mMinValue
                mBinding.btnLeft.setEnable(false)
                mBinding.btnRight.setEnable(true)
                mListeners.forEach { it.onValueChanged(this, mValue) }
            } else {
                when {
                    value <= mMinValue -> {
                        mValue = mMinValue
                        mBinding.btnLeft.setEnable(false)
                        mBinding.btnRight.setEnable(true)
                        mBinding.etInput.removeTextChangedListener(this)
                        setTextAndMoveSelection(mValue)
                        mBinding.etInput.addTextChangedListener(this)
                        mListeners.forEach { it.onValueChanged(this, mValue) }
                    }

                    value >= mMaxValue -> {
                        mValue = mMaxValue
                        mBinding.btnLeft.setEnable(true)
                        mBinding.btnRight.setEnable(false)
                        mBinding.etInput.removeTextChangedListener(this)
                        setTextAndMoveSelection(mValue)
                        mBinding.etInput.addTextChangedListener(this)
                        mListeners.forEach { it.onValueChanged(this, mValue) }
                    }

                    else -> {
                        mValue = value
                        mBinding.btnLeft.setEnable(true)
                        mBinding.btnRight.setEnable(true)
                        mListeners.forEach { it.onValueChanged(this, mValue) }
                    }
                }
            }
        } catch (e: NumberFormatException) {
            mValue = null
            mBinding.btnLeft.setEnable(false)
            mBinding.btnRight.setEnable(false)
            mListeners.forEach { it.onValueChanged(this, mValue) }
        }
    }

    /**
     * 设置数据类型
     */
    fun setDataElement(dataElement: DataElement) {
        mDataElement = dataElement
        mBinding.etInput.configureInputTypeAndKeyListener()
    }

    // 配置输入框类型和监听器
    private fun EditText.configureInputTypeAndKeyListener() {
        if (mDataElement == DataElement.INT) {
            inputType = InputType.TYPE_CLASS_NUMBER
            keyListener = DigitsKeyListener.getInstance("0123456789")
        } else {
            inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
            keyListener = DigitsKeyListener.getInstance("0123456789.")
        }
    }

    /**
     * 设置最小值
     */
    fun setMinValue(min: Float) {
        if (min < 0) {
            mMinValue = 0f
            return
        }
        mMinValue = min
    }

    /**
     * 设置最大值
     */
    fun setMaxValue(max: Float) {
        if (max < 0) {
            mMaxValue = 0f
            return
        }
        mMaxValue = max
    }

    /**
     * 获取当前值
     */
    fun getValue(): Float? {
        return mValue
    }

    /**
     * 设置当前值
     */
    fun setValue(value: Float?) {
        if (value != null) {
            if (mMaxValue == 0f && mMinValue == 0f) {
                mValue = 0f
                mBinding.btnLeft.setEnable(false)
                mBinding.btnRight.setEnable(false)
            } else if (value >= mMaxValue) {
                mValue = mMaxValue
                mBinding.btnLeft.setEnable(true)
                mBinding.btnRight.setEnable(false)
            } else if (value <= mMinValue) {
                mValue = mMinValue
                mBinding.btnLeft.setEnable(false)
                mBinding.btnRight.setEnable(true)
            } else {
                mValue = value
                mBinding.btnLeft.setEnable(true)
                mBinding.btnRight.setEnable(true)
            }
        } else {
            mValue = null
            mBinding.btnLeft.setEnable(false)
            mBinding.btnRight.setEnable(false)
        }
        mBinding.etInput.removeTextChangedListener(this)
        setTextAndMoveSelection(mValue)
        mBinding.etInput.addTextChangedListener(this)
    }

    fun setEnable(enabled: Boolean) {
        //
        mEnabled = enabled
        //
        mBinding.btnLeft.isEnabled = enabled
        mBinding.btnRight.isEnabled = enabled
        with(mBinding.etInput) {
            isFocusableInTouchMode = enabled
            isFocusable = enabled
            isEnabled = enabled
        }
    }

    fun setOnInputClickListener(listener: OnClickListener) {
        mOnInputClickListener = listener
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

    private fun setTextAndMoveSelection(value: Float?) {
        with(mBinding.etInput) {
            setText(value?.let { mFormatter.format(it) })
            setSelection(text?.length ?: 0)
        }
    }

    //
    private fun ImageView.setEnable(enabled: Boolean) {
        if (mEnabled) {
            this.isEnabled = enabled
        }
    }
}