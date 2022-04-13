# MMP_Architecture

More Modern Presenter.

A more lightweight and simpler to use MVVM architecture for Android, Data and views are two-way
bound.

Only **300** lines of code.

Currently implemented using reflection, it can be optimized to automatically generate code using
Gradle Plugin.

# Example

Presenter

```kotlin
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
                return ""
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
                "Can't add any more"
            } else {
                "Can't sub any more"
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
```

View

```kotlin
package cn.flutterfirst.mmp_architecture.main

import cn.flutterfirst.mmp_architecture.ContentView
import cn.flutterfirst.mmp_architecture.R
import cn.flutterfirst.mmp_architecture.StateChanged

@ContentView(R.layout.activity_main)
class MainActivity : MainPresenter() {

    override fun initViewsBeforeFirstSetState() {
    }

    @StateChanged("count")
    fun countChanged() {
        println("count changed, new value = $count")
    }

    @StateChanged("editValue")
    fun editValueChanged() {
        println("editValue changed, new value = $editValue")
    }
}
```

Layout

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".main.MainActivity">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="50dp"
        android:textSize="20sp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:vText="Your input: {{editValue}}" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="50dp"
        android:text="One-way bound EditText"
        android:textSize="20sp"
        app:layout_constraintBottom_toTopOf="@+id/one_way_bind_edit"
        app:layout_constraintLeft_toLeftOf="@+id/one_way_bind_edit" />

    <EditText
        android:id="@+id/one_way_bind_edit"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginHorizontal="50dp"
        android:layout_marginTop="150dp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:vEdit="{{editValue}}" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="50dp"
        android:text="Two-way bound EditText"
        android:textSize="20sp"
        app:layout_constraintBottom_toTopOf="@+id/two_way_bind_edit"
        app:layout_constraintLeft_toLeftOf="@+id/two_way_bind_edit" />

    <EditText
        android:id="@+id/two_way_bind_edit"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginHorizontal="50dp"
        android:layout_marginTop="250dp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:vEditText="{{editValue}}" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="50sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:vText="{{count}}" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="180dp"
        android:textSize="20sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:vText="{{hintText}}"
        app:vVisible="{{hintVisibility}}" />

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="100dp"
        android:text="Add"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:vClick="{{add}}"
        app:vEnable="{{addEnable}}" />

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="50dp"
        android:text="Sub"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:vClick="{{sub}}"
        app:vEnable="{{subEnable}}" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

![example.gif](https://github.com/hackware1993/MMP_Architecture/blob/main/example.gif?raw=true)

```
MIT License

Copyright (c) 2022 hackware1993

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
