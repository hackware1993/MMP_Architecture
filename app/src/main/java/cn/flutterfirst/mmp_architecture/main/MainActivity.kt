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