package com.rouxinpai.stepper

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.use
import java.text.NumberFormat

/**
 * author : Saxxhw
 * email  : xingwangwang@cloudinnov.com
 * time   : 2023/1/9 22:15
 * desc   :
 */
class NumberInputEdittext @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    // 数据类型
    private var mDataElement: DataElement = DataElement.FLOAT

    // 保留小数位
    private var mDigits: Int = 3

    //
    private val mFormatter: NumberFormat

    // 数值
    private var mValue: Float? = null

    // 最小值
    private var mMinValue: Float = 0f

    // 最大值
    private var mMaxValue: Float = 0f

    // 输入监听
    private val mTextWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(editable: Editable?) {
            if (editable.isNullOrBlank() || editable.endsWith(".")) {
                mValue = null
                mListeners.forEach { it.onValueChanged(mValue) }
                return
            }
            try {
                val text = editable.toString()
                val value = text.toFloat()
                if ("0.0" == text || "0.00" == text) {
                    mValue = mMinValue
                    mListeners.forEach { it.onValueChanged(mValue) }
                } else {
                    when {
                        value <= mMinValue -> {
                            mValue = mMinValue
                            removeTextChangedListener(this)
                            setTextAndMoveSelection(mValue)
                            addTextChangedListener(this)
                            mListeners.forEach { it.onValueChanged(mValue) }
                        }
                        value >= mMaxValue -> {
                            mValue = mMaxValue
                            removeTextChangedListener(this)
                            setTextAndMoveSelection(mValue)
                            addTextChangedListener(this)
                            mListeners.forEach { it.onValueChanged(mValue) }
                        }
                        else -> {
                            mValue = value
                            mListeners.forEach { it.onValueChanged(mValue) }
                        }
                    }
                }
            } catch (e: NumberFormatException) {
                mValue = null
                mListeners.forEach { it.onValueChanged(mValue) }
            }
        }
    }

    //
    private val mListeners = arrayListOf<OnValueChangeListener>()

    init {
        //
        context.obtainStyledAttributes(attrs, R.styleable.NumberInputEdittext).use {
            mDataElement =
                when (it.getInteger(
                    R.styleable.NumberInputEdittext_input_data_element,
                    mDataElement.value
                )) {
                    DataElement.FLOAT.value -> DataElement.FLOAT
                    DataElement.INT.value -> DataElement.INT
                    else -> throw IllegalArgumentException("unknown argument: stepper_data_element")
                }
            mDigits = it.getInteger(R.styleable.NumberInputEdittext_input_digits, mDigits)
        }
        //
        if (mDataElement == DataElement.INT) {
            inputType = InputType.TYPE_CLASS_NUMBER
            keyListener = DigitsKeyListener.getInstance("0123456789")
        } else {
            inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
            keyListener = DigitsKeyListener.getInstance("0123456789.")
        }
        filters = arrayOf(NumberInputFilter(mDigits))
        //
        mFormatter = NumberFormat.getNumberInstance().apply {
            isGroupingUsed = false
            maximumFractionDigits = mDigits
        }
        // 绑定监听事件
        addTextChangedListener(mTextWatcher)
    }

    fun setMinValue(min: Float) {
        if (min < 0) {
            mMinValue = 0f
            return
        }
        mMinValue = min
    }

    fun setMaxValue(max: Float) {
        if (max < 0) {
            mMaxValue = 0f
            return
        }
        mMaxValue = max
    }

    fun setValue(value: Float?) {
        mValue = value
        removeTextChangedListener(mTextWatcher)
        setTextAndMoveSelection(mValue)
        addTextChangedListener(mTextWatcher)
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
        setText(value?.let { mFormatter.format(it) })
        setSelection(text?.length ?: 0)
    }
}