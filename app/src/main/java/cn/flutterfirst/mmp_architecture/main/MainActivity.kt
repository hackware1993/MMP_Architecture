package cn.flutterfirst.mmp_architecture.main

import cn.flutterfirst.mmp_architecture.ContentView
import cn.flutterfirst.mmp_architecture.R
import cn.flutterfirst.mmp_architecture.StateChanged

@ContentView(R.layout.activity_main)
class MainActivity : MainPresenter() {

    override fun initViewsBeforeFirstSetState() {
        // 可在此处初始化 RecyclerView 的 LayoutManager
    }

    @StateChanged("addCount")
    fun countChanged() {
        println("MMP: addCount changed, new value = $count")
    }

    @StateChanged("editValue")
    fun editValueChanged() {
        println("MMP: editValue changed, new value = $editValue")
    }
}