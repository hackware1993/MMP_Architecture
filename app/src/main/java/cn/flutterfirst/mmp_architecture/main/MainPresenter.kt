package cn.flutterfirst.mmp_architecture.main

import android.view.View
import cn.flutterfirst.mmp_architecture.State
import cn.flutterfirst.mmp_architecture.ViewClick
import cn.flutterfirst.mmp_architecture.mmp.BaseMMPActivity

abstract class MainPresenter : BaseMMPActivity() {

    @State
    protected var editValue: String? = null
        get() {
            if (field == null || field!!.isEmpty()) {
                return "空"
            }
            return field
        }

    @State
    protected var count = 0
    private val countMax = 5
    private val countMin = 0

    @State
    protected var hintVisibility: Int? = null
        get() {
            if (count == countMax || count == countMin) {
                return View.VISIBLE
            }
            return View.GONE
        }

    @State
    protected var hintText: String? = null
        get() {
            return if (count == countMax) {
                "不能再加了"
            } else {
                "不能再减了"
            }
        }

    @State
    protected var addEnable: Boolean? = null
        get() {
            return count != countMax
        }

    @State
    protected var subEnable: Boolean? = null
        get() {
            return count != countMin
        }

    @ViewClick
    protected fun add() {
        if (count == countMax) {
            return
        }
        setState {
            count++
        }
    }

    @ViewClick
    protected fun sub() {
        if (count == countMin) {
            return
        }
        setState {
            count--
        }
    }
}