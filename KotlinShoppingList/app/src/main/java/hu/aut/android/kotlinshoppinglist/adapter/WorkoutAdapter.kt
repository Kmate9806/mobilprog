package hu.aut.android.kotlinshoppinglist.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import hu.aut.android.kotlinshoppinglist.MainActivity
import hu.aut.android.kotlinshoppinglist.R
import hu.aut.android.kotlinshoppinglist.adapter.WorkoutAdapter.ViewHolder
import hu.aut.android.kotlinshoppinglist.data.AppDatabase
import hu.aut.android.kotlinshoppinglist.data.WorkoutItem
import hu.aut.android.kotlinshoppinglist.touch.WorkoutTouchHelperAdapter
import kotlinx.android.synthetic.main.row_item.view.*
import java.util.*

class WorkoutAdapter : RecyclerView.Adapter<ViewHolder>, WorkoutTouchHelperAdapter {

    // WorkoutItem elemek listája
    private val items = mutableListOf<WorkoutItem>()
    private val context: Context

    constructor(context: Context, items: List<WorkoutItem>) : super() {
        this.context = context
        this.items.addAll(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.row_item, parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*Itt kérjük le az egyes WorkoutItem elemek adattagjait*/

        holder.tvName.text = items[position].name
        holder.tvRepeat.text = items[position].repeat.toString()
        holder.cbCheck.isChecked = items[position].check
        holder.tvSets.text = items[position].sets

        // Frissítsük a tvRestTime szöveget
        holder.tvRestTime.text = items[position].restTime // Feltételezve, hogy van ilyen adattag

        // Ha szükséges, kezeljük a láthatóságot
        holder.tvRestTime.visibility = if (items[position].restTime.isEmpty()) {
            View.GONE // Ha nincs restTime, elrejtjük
        } else {
            View.VISIBLE // Ha van restTime, megjelenítjük
        }

        /*Delete gomb eseménykezeője (a főoldalon)*/
        holder.btnDelete.setOnClickListener {
            deleteItem(holder.adapterPosition)
        }

        /*Edit gomb eseménykezelője (a főoldalon), megnyitja az edit dialógust, átadja az adott WorkoutItem-et neki*/
        holder.btnEdit.setOnClickListener {
            (holder.itemView.context as MainActivity).showEditItemDialog(
                items[holder.adapterPosition])
        }

        /*Checkbox eseménykezelője, állítja a checkbox értékét, azaz a WorkoutItem-nek, az isChecked adattagját.
        Az adatbázisban is frissíti*/
        holder.cbCheck.setOnClickListener {
            items[position].check = holder.cbCheck.isChecked
            val dbThread = Thread {
                //Itt frissíti a DB-ben
                AppDatabase.getInstance(context).workoutItemDao().updateItem(items[position])
            }
            dbThread.start()
        }
    }

    /*Új elem hozzáadásakor hívódik meg*/
    fun addItem(item: WorkoutItem) {
        items.add(item)
        notifyItemInserted(items.lastIndex)
    }

    /*Elem törlésekor hívódik meg. Az adatbázisból törli az elemet (DAO-n keresztül)*/
    fun deleteItem(position: Int) {
        val dbThread = Thread {
            AppDatabase.getInstance(context).workoutItemDao().deleteItem(
                items[position])
            (context as MainActivity).runOnUiThread{
                items.removeAt(position)
                notifyItemRemoved(position)
            }
        }
        dbThread.start()
    }

    /*Update-kor hívódik meg*/
    fun updateItem(item: WorkoutItem) {
        val idx = items.indexOf(item)
        items[idx] = item
        notifyItemChanged(idx)
    }

    override fun onItemDismissed(position: Int) {
        deleteItem(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(items, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /*a WorkoutItem elemek, ide kell a bővítés új taggal*/
        /*Itt a gombokat, checkboxot is lekérjük*/
        val tvName: TextView = itemView.tvName
        val tvRepeat: TextView = itemView.tvRepeat
        val cbCheck: CheckBox = itemView.cbCheck
        val btnDelete: Button = itemView.btnDelete
        val btnEdit: Button = itemView.btnEdit
        val tvSets: TextView = itemView.tvSets
        val tvRestTime: TextView = itemView.tvRestTime // tvRestTime hozzáadása
    }
}
