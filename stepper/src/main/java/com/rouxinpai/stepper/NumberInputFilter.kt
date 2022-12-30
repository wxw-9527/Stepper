package com.rouxinpai.stepper

import android.text.InputFilter
import android.text.Spanned

/**
 * author : Saxxhw
 * email  : xingwangwang@cloudinnov.com
 * time   : 2022/12/30 16:39
 * desc   :
 */
class NumberInputFilter(private val digits: Int) : InputFilter {

    override fun filter(
        source: CharSequence, // 将要输入的字符串,如果是删除操作则为空
        start: Int, // 将要输入的字符串起始下标，一般为0
        end: Int, // start + source字符的长度
        dest: Spanned, // 输入之前文本框中的内容
        dstart: Int, // 将会被替换的起始位置
        dend: Int // dstart+将会被替换的字符串长度
    ): CharSequence { // 方法返回的值将会替换掉dest字符串中dstartd位置到dend位置之间字符，返回source表示不做任何处理，返回空字符串""表示不输入任何字符

        val start = dest.subSequence(0, dstart)
        val end = dest.subSequence(dend, dest.length)
        val target = start.toString() + source + end // 字符串变化后的结果
        val backup = dest.subSequence(dstart, dend) // 将要被替换的字符串

        // 不允许第一个字符为.
        if (target.indexOf(".") == 0) {
            return backup
        }

        // 不允许出现多个.
        if (target.filter { '.' == it }.length >= 2) {
            return backup
        }

        // 不允许出现0123、0456这类字符串
        if (target.startsWith("0") && !target.startsWith("0.") && "0" != target) {
            return backup
        }

        // 限制小数点后面只能有三位小数
        val index = target.indexOf(".")
        if (index >= 0 && index + digits + 2 <= target.length) {
            return backup
        }

        return source
    }
}