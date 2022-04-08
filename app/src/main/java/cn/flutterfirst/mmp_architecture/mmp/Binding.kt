package cn.flutterfirst.mmp_architecture.mmp

data class Binding(
    val vClick: String?,
    val vText: String?,
    val vEdit: String?,
    val vEditText: String?,
    val vVisible: String?,
    val vEnable: String?,
) {
    fun getPreText(): String {
        val index = vText?.indexOf("{{")
        if (index != null) {
            if (index != -1) {
                return vText!!.substring(0, index)
            }
        }
        return ""
    }

    fun getPostText(): String {
        val index = vText?.indexOf("}}")
        if (index != null) {
            if (index != -1) {
                return vText!!.substring(index + 2, vText.length)
            }
        }
        return ""
    }
}

