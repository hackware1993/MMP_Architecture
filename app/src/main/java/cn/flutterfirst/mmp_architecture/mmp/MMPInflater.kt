package cn.flutterfirst.mmp_architecture.mmp

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.LayoutInflater.Factory2
import android.view.View
import cn.flutterfirst.mmp_architecture.R

object MMPInflater : Factory2 {

    fun install(activity: Activity?) {
        if (activity == null) {
            return
        }
        val layoutInflater = LayoutInflater.from(activity)
        if (layoutInflater.factory == null) {
            layoutInflater.factory2 = this
        }
    }

    private fun createView(context: Context, name: String, attrs: AttributeSet): View? {
        var view: View? = null
        try {
            val inflater = LayoutInflater.from(context)
            if (-1 == name.indexOf('.')) {
                if ("View" == name || "ViewStub" == name || "ViewGroup" == name) {
                    view = inflater.createView(
                        name, "android.view.", attrs
                    )
                }
                if (view == null) {
                    view = inflater.createView(
                        name, "android.widget.", attrs
                    )
                }
                if (view == null) {
                    view = inflater.createView(
                        name, "android.webkit.", attrs
                    )
                }
            } else {
                view = inflater.createView(name, null, attrs)
            }
        } catch (ex: Exception) {
            view = null
        }
        return view
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        val view = createView(context, name, attrs) ?: return null
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MMP)
        val vClick = typedArray.getString(R.styleable.MMP_vClick)
        val vText = typedArray.getString(R.styleable.MMP_vText)
        val vEdit = typedArray.getString(R.styleable.MMP_vEdit)
        val vEditText = typedArray.getString(R.styleable.MMP_vEditText)
        val vVisible = typedArray.getString(R.styleable.MMP_vVisible)
        val vEnable = typedArray.getString(R.styleable.MMP_vEnable)
        typedArray.recycle()
        if (!vClick.isNullOrEmpty() || !vText.isNullOrEmpty() || !vEdit.isNullOrEmpty() || !vEditText.isNullOrEmpty() || !vVisible.isNullOrEmpty() || !vEnable.isNullOrEmpty()) {
            val binding = Binding(vClick, vText, vEdit, vEditText, vVisible, vEnable)
            view.setTag(R.id.vBinding, binding)
        }
        return view
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return onCreateView(null, name, context, attrs)
    }
}