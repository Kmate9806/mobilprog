package hu.aut.android.kotlinshoppinglist.data

import java.io.Serializable

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.PrimaryKey

/*
Adatbázis táblát készti el.
Táblanév:workoutitem.
Oszlopok:itemId, name,  repeat, check, sets, resttime.
@PrimaryKey(autoGenerate = true): elsődleges kulcs, automatikusan generálva.
Ide szükséges a bővítés új adattal.
 */
@Entity(tableName = "workoutitem")
data class WorkoutItem(@PrimaryKey(autoGenerate = true) var itemId: Long?,
                        @ColumnInfo(name = "name") var name: String,
                        @ColumnInfo(name = "repeat") var repeat: Int,
                        @ColumnInfo(name = "check") var check: Boolean,
                        @ColumnInfo(name = "sets") var sets: String,
                        @ColumnInfo(name = "resttime") var restTime: String
) : Serializable
