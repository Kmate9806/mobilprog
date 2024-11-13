package hu.aut.android.kotlinshoppinglist.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/*
Elkészíti az adatbázist, azaz a workout.db-t, a WorkoutItem alapján lesz létrehozva a tábla.
A verziószámot (version) megnöveltük 3-re a változások miatt.
*/
@Database(entities = arrayOf(WorkoutItem::class), version = 3)
abstract class AppDatabase : RoomDatabase() {

    // Az adatbázis DAO elérésére szolgáló absztrakt függvény.
    abstract fun workoutItemDao(): WorkoutItemDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        // Singleton minta alkalmazás az adatbázis példányának megszerzéséhez.
        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "workout.db"
                ).fallbackToDestructiveMigration() // Ezt a sort kell hozzáadni
                    .build()
            }
            return INSTANCE!!
        }

        // Példány törlése, ha szükséges az alkalmazás kontextusából való eltávolításhoz.
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
