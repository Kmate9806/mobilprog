package hu.aut.android.kotlinshoppinglist

import android.widget.ImageView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import hu.aut.android.kotlinshoppinglist.adapter.WorkoutAdapter
import kotlinx.android.synthetic.main.activity_main.*
import android.preference.PreferenceManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import hu.aut.android.kotlinshoppinglist.data.AppDatabase
import hu.aut.android.kotlinshoppinglist.data.WorkoutItem
import hu.aut.android.kotlinshoppinglist.touch.WorkoutTouchHelperCallback
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class MainActivity : AppCompatActivity(), WorkoutItemDialog.WorkoutItemHandler {
    companion object {
        val KEY_FIRST = "KEY_FIRST"
        val KEY_ITEM_TO_EDIT = "KEY_ITEM_TO_EDIT"
    }

    private lateinit var adapter: WorkoutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        // Az ImageView (add_button) kattintásának kezelése
        val addButton = findViewById<ImageView>(R.id.add_button)
        addButton.setOnClickListener {
            WorkoutItemDialog().show(supportFragmentManager, "TAG_ITEM")
        }

        // RecyclerView inicializálása
        initRecyclerView()

        // Ha ez az első futtatás, megjelenítjük a MaterialTapTargetPrompt-ot
        if (isFirstRun()) {
            MaterialTapTargetPrompt.Builder(this@MainActivity)
                .setTarget(findViewById<View>(R.id.add_button))
                .setPrimaryText("New Workout Item")
                .setSecondaryText("Tap here to create new workout item")
                .show()
        }

        saveThatItWasStarted()
    }

    private fun isFirstRun(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(KEY_FIRST, true)
    }

    private fun saveThatItWasStarted() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        sp.edit().putBoolean(KEY_FIRST, false).apply()
    }

    private fun initRecyclerView() {
        val dbThread = Thread {
            // Lekéri az összes Workout Item-et az adatbázisból
            val items = AppDatabase.getInstance(this).workoutItemDao().findAllItems()

            runOnUiThread {
                // Adapter beállítása és a RecyclerView csatolása
                adapter = WorkoutAdapter(this, items)
                recyclerWorkout.adapter = adapter

                // Az elemeket érintő műveletekhez hozzáadunk egy callback-et
                val callback = WorkoutTouchHelperCallback(adapter)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(recyclerWorkout)
            }
        }
        dbThread.start()
    }

    // A módosítani kívánt elemhez egy szerkesztő dialógust nyitunk
    fun showEditItemDialog(itemToEdit: WorkoutItem) {
        val editDialog = WorkoutItemDialog()
        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM_TO_EDIT, itemToEdit)
        editDialog.arguments = bundle
        editDialog.show(supportFragmentManager, "TAG_ITEM_EDIT")
    }

    // Új Workout Item létrehozása és hozzáadása az adatbázishoz
    override fun workoutItemCreated(item: WorkoutItem) {
        val dbThread = Thread {
            val id = AppDatabase.getInstance(this@MainActivity).workoutItemDao().insertItem(item)
            item.itemId = id

            runOnUiThread {
                // Hozzáadás a RecyclerView adapterhez
                adapter.addItem(item)
            }
        }
        dbThread.start()
    }

    // Workout Item frissítése az adatbázisban
    override fun workoutItemUpdated(item: WorkoutItem) {
        // Frissítés az adapterben
        adapter.updateItem(item)

        val dbThread = Thread {
            AppDatabase.getInstance(this@MainActivity).workoutItemDao().updateItem(item)

            runOnUiThread {
                // Frissítés az adapterben
                adapter.updateItem(item)
            }
        }
        dbThread.start()
    }
}
