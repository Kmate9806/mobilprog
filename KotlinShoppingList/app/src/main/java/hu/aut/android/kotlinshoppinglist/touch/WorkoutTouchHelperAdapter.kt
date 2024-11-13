package hu.aut.android.kotlinshoppinglist.touch

interface WorkoutTouchHelperAdapter {

    fun onItemDismissed(position: Int)

    fun onItemMoved(fromPosition: Int, toPosition: Int)
}