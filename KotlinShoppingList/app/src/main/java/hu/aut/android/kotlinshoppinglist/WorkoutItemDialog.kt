package hu.aut.android.kotlinshoppinglist

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.widget.EditText
import hu.aut.android.kotlinshoppinglist.data.WorkoutItem
import kotlinx.android.synthetic.main.dialog_create_item.view.*
import java.util.*
/*
Ez a dialógus ablak szolgál az új Shipping Item felvitelére, és a meglevő Shopping Item módosítására
 */

class WorkoutItemDialog : DialogFragment() {

    private lateinit var workoutItemHandler: WorkoutItemHandler
    //Shopping Item elemek text-ben, ide szükséges a bővítés a Shopping Item új adattagja esetén
    private lateinit var etName: EditText
    private lateinit var etRepeat: EditText
    private lateinit var etSets: EditText
    private lateinit var etRestTime: EditText
    interface WorkoutItemHandler {
        fun workoutItemCreated(item: WorkoutItem)

        fun workoutItemUpdated(item: WorkoutItem)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is WorkoutItemHandler) {
            workoutItemHandler = context
        } else {
            throw RuntimeException("The Activity does not implement the ShoppingItemHandler interface")
        }
    }
/*Új Shopping Item felvitelekor ez hívódik meg. A felirat a New Item lesz*/
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Új Gyakorlat")

        initDialogContent(builder)

        builder.setPositiveButton("OK") { dialog, which ->
            // keep it empty
        }
        return builder.create()
    }

    private fun initDialogContent(builder: AlertDialog.Builder) {
        /*etName = EditText(activity)
        builder.setView(etName)*/

        //dialog_create_item.xml-el dolgozunk
        val rootView = requireActivity().layoutInflater.inflate(R.layout.dialog_create_item, null)
        //Shopping Item tagok az xml-ből (EditText elemek)
        //Itt is szükséges a bővítés új Shopping Item adattag esetén
        etName = rootView.etName
        etRepeat = rootView.etRepeat
        etSets=rootView.etSets
        etRestTime=rootView.etRestTime
        builder.setView(rootView)
        //Megnézzük, hogy kapott-e argumentumot (a fő ablakból), ha igen, akkor az adattagokat beállítjuk erre
        // tehát az Edittext-ek kapnak értéket, és az ablak címét beállítjuk
        val arguments = this.arguments
        if (arguments != null &&
                arguments.containsKey(MainActivity.KEY_ITEM_TO_EDIT)) {
            val item = arguments.getSerializable(
                    MainActivity.KEY_ITEM_TO_EDIT) as WorkoutItem
            //Itt is szükséges a bővítés új Shopping Item adattag esetén
            etName.setText(item.name)
            etRepeat.setText(item.repeat.toString())
            etSets.setText(item.sets)
            etRestTime.setText(item.restTime)
            builder.setTitle("Edit todo")
        }
    }


    override fun onResume() {
        super.onResume()

        val dialog = dialog as AlertDialog
        val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)
         //OK gomb a dialógus ablakon
        //vizsgálja az eseménykezelője, hogy a dialógus ablak kapott-e paramétereket
        //Ha kapott, akkor a handleItemEdit() hívódik meg (edit)
        //Ha nem kapott, akor a handleItemCreate() hívódik meg (create)
        positiveButton.setOnClickListener {
            if (etName.text.isNotEmpty()) {
                val arguments = this.arguments
                if (arguments != null &&
                        arguments.containsKey(MainActivity.KEY_ITEM_TO_EDIT)) {
                    handleItemEdit()
                } else {
                    handleItemCreate()
                }

                dialog.dismiss()
            } else {
                etName.error = "This field can not be empty"
            }
        }
    }
    //Új elem esetén hvódik meg, egy új ShoppingItem-et hoz létre
    //az itemId azért null, mert a DB adja a kulcsot
    //Itt is szükséges a bővítés új Shopping Item adattag esetén
    private fun handleItemCreate() {
        workoutItemHandler.workoutItemCreated(WorkoutItem(
            null,
            etName.text.toString(),
            etRepeat.text.toString().toInt(),
            false,
            etSets.text.toString(),
            etRestTime.text.toString() // Az utolsó paraméter után ne legyen vessző.
        ))
    }
    //Edit esetén hívódik meg, az edit-et csinálja, paraméterként átadja az adatokat
    //Itt is szükséges a bővítés új Shopping Item adattag esetén
    private fun handleItemEdit() {
        val itemToEdit = arguments?.getSerializable(
                MainActivity.KEY_ITEM_TO_EDIT) as WorkoutItem
        itemToEdit.name = etName.text.toString()
        itemToEdit.repeat = etRepeat.text.toString().toInt()
        itemToEdit.sets=etSets.text.toString()
        itemToEdit.restTime=etRestTime.text.toString()
        workoutItemHandler.workoutItemUpdated(itemToEdit)
    }
}
