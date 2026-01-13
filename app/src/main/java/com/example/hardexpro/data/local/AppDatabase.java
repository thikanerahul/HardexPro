package com.example.hardexpro.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.hardexpro.data.model.Customer;
import com.example.hardexpro.data.model.Item;
import com.example.hardexpro.data.model.Supplier;
import com.example.hardexpro.data.model.Transaction;

@Database(entities = { Item.class, Customer.class, Supplier.class,
        Transaction.class }, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract AppDao appDao();

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "hardex_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
