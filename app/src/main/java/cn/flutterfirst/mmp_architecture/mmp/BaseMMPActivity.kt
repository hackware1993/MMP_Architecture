package cn.flutterfirst.mmp_architecture.mmp

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import cn.flutterfirst.mmp_architecture.*
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier

abstract class BaseMMPActivity : Activity() {
    private val vStatesMap = HashMap<String, Field>()
    private val vMethodsMap = HashMap<String, Method>()
    private val vClickMethodsMap = HashMap<String, Method>()
    private val initialValue = Object()
    private val statesBindMap =
        HashMap<Field, ArrayList<Pair<View, (field: Field, view: View) -> Unit>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        MMPInflater.install(this)
        super.onCreate(savedInstanceState)
        setContentView(javaClass.getAnnotation(ContentView::class.java)!!.value)

        javaClass.declaredMethods.forEach {
            if (it.isAnnotationPresent(StateChanged::class.java) && Modifier.isPublic(it.modifiers)) {
                it.isAccessible = true
                var bindFieldName = it.getAnnotation(StateChanged::class.java)!!.value
                if (bindFieldName.isEmpty()) {
                    bindFieldName = it.name
                }
                vMethodsMap[bindFieldName] = it
            }
        }

        javaClass.superclass.declaredMethods.forEach {
            if (it.isAnnotationPresent(ViewClick::class.java) && Modifier.isProtected(it.modifiers)) {
                it.isAccessible = true
                vClickMethodsMap[it.name] = it
            }
        }

        javaClass.superclass.declaredFields.forEach {
            if (it.isAnnotationPresent(State::class.java)) {
                it.isAccessible = true
                vStatesMap[it.name] = it
            }
        }

        val rootView = findViewById<ViewGroup>(android.R.id.content)
        iteratorView(rootView) {
            val binding = it.getTag(R.id.vBinding) as Binding?
            if (binding != null) {
                getBindFieldName(binding.vClick)?.apply {
                    if (vClickMethodsMap[this] == null) {
                        throw Exception("Bind method {{$this}} not defined, please define it use @ViewClick.")
                    }
                    bindClick(this, it)
                }
                getBindFieldName(binding.vText)?.apply {
                    if (vStatesMap[this] == null) {
                        throw Exception("Bind variable {{$this}} not defined, please define it use @State.")
                    }
                    bindText(this, it)
                }
                getBindFieldName(binding.vEdit)?.apply {
                    if (vStatesMap[this] == null) {
                        throw Exception("Bind variable {{$this}} not defined, please define it use @State.")
                    }
                    bindEdit(this, it)
                }
                getBindFieldName(binding.vEditText)?.apply {
                    if (vStatesMap[this] == null) {
                        throw Exception("Bind variable {{$this}} not defined, please define it use @State.")
                    }
                    bindEditText(this, it)
                }
                getBindFieldName(binding.vVisible)?.apply {
                    if (vStatesMap[this] == null) {
                        throw Exception("Bind variable {{$this}} not defined, please define it use @State.")
                    }
                    bindVisible(this, it)
                }
                getBindFieldName(binding.vEnable)?.apply {
                    if (vStatesMap[this] == null) {
                        throw Exception("Bind variable {{$this}} not defined, please define it use @State.")
                    }
                    bindEnable(this, it)
                }
            }
        }

        initViewsBeforeFirstSetState()
        setState(initial = true) { }
    }

    abstract fun initViewsBeforeFirstSetState()

    private fun getBindFieldName(express: String?): String? {
        if (express.isNullOrEmpty()) {
            return null
        }
        val startIndex = express.indexOf("{{")
        val endIndex = express.indexOf("}}")
        if (startIndex != -1 && endIndex != -1) {
            return express.substring(startIndex + 2, endIndex).trim()
        }
        return null
    }

    private fun bindClick(fieldName: String, view: View) {
        view.setOnClickListener {
            vClickMethodsMap[fieldName]?.invoke(this)
        }
    }

    private fun bindText(fieldName: String, view: View) {
        if (view is TextView) {
            bind(fieldName, view) { field, _ ->
                val binding = view.getTag(R.id.vBinding) as Binding
                val text = binding.getPreText() + (getField(field)?.toString()
                    ?: "") + binding.getPostText()
                view.text = text
            }
        }
    }

    private fun bindEdit(fieldName: String, view: View) {
        if (view is EditText) {
            view.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    setState {
                        setField(vStatesMap[fieldName], view.text.toString())
                    }
                }
            })
        }
    }

    private fun bindEditText(fieldName: String, view: View) {
        if (view is EditText) {
            var ignoreNextValueChange = false

            view.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    ignoreNextValueChange = true
                    setState {
                        setField(vStatesMap[fieldName], view.text.toString())
                    }
                    ignoreNextValueChange = false
                }
            })

            bind(fieldName, view) { field, _ ->
                if (!ignoreNextValueChange) {
                    view.setText(getField(field)?.toString() ?: "")
                }
            }
        }
    }

    private fun bindVisible(fieldName: String, view: View) {
        bind(fieldName, view) { field, _ ->
            view.visibility = getField(field) as Int
        }
    }

    private fun bindEnable(fieldName: String, view: View) {
        bind(fieldName, view) { field, _ ->
            view.isEnabled = getField(field) as Boolean
        }
    }

    private fun bind(fieldName: String, view: View, bind: (field: Field, view: View) -> Unit) {
        val field = vStatesMap[fieldName]
        if (field != null) {
            var bindList = statesBindMap[field]
            if (bindList == null) {
                bindList = ArrayList()
                statesBindMap[field] = bindList
            }
            bindList.add(Pair(view, bind))
        }
    }

    private fun iteratorView(view: View, filter: (view: View) -> Unit) {
        filter.invoke(view)
        if (view is ViewGroup) {
            for (index in 0 until view.childCount) {
                iteratorView(view.getChildAt(index), filter)
            }
        }
    }

    protected fun setState(initial: Boolean = false, block: () -> Unit) {
        val preStates = HashMap<String, Any?>()
        vStatesMap.forEach {
            if (initial) {
                preStates[it.key] = initialValue
            } else {
                preStates[it.key] = getField(it.value)
            }
        }
        block()
        val changedStates = ArrayList<Field>()
        vStatesMap.forEach {
            if (getField(it.value) != preStates[it.key]) {
                changedStates.add(it.value)
            }
        }
        changedStates.forEach { field ->
            vMethodsMap[field.name]?.invoke(this)
            statesBindMap[field]?.forEach {
                it.second.invoke(field, it.first)
            }
        }
    }

    private fun setField(field: Field?, value: Any?) {
        if (field == null) {
            return
        }
        field.set(this, value)
    }

    private fun getField(field: Field): Any? {
        val getterName: String = if (field.type == Boolean::class.java) {
            if (!field.name.startsWith("is")) {
                "get" + field.name[0].uppercase() + field.name.substring(1)
            } else {
                field.name
            }
        } else {
            "get" + field.name[0].uppercase() + field.name.substring(1)
        }
        return try {
            val getter = javaClass.superclass.getDeclaredMethod(getterName)
            getter.isAccessible = true
            getter.invoke(this)
        } catch (e: Exception) {
            field.get(this)
        }
    }
}