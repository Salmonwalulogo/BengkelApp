package com.example.bengkelapp.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.bengkelapp.data.local.dao.BengkelDao;
import com.example.bengkelapp.data.local.entities.InvoiceEntity;
import com.example.bengkelapp.data.local.entities.NotificationEntity;
import com.example.bengkelapp.data.local.entities.ServiceItemEntity;
import com.example.bengkelapp.data.local.entities.ServiceOrderEntity;
import com.example.bengkelapp.data.local.entities.SparePartEntity;
import com.example.bengkelapp.data.local.entities.UserEntity;
import com.example.bengkelapp.data.local.entities.VehicleEntity;
import com.example.bengkelapp.data.local.entities.WorkshopSettingsEntity;

@Database(
    entities = {
        UserEntity.class,
        VehicleEntity.class,
        ServiceItemEntity.class,
        SparePartEntity.class,
        ServiceOrderEntity.class,
        InvoiceEntity.class,
        NotificationEntity.class,
        WorkshopSettingsEntity.class
    },
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract BengkelDao bengkelDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "bengkel_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
