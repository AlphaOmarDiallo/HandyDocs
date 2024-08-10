package com.alphaomardiallo.handydocs.data

import android.app.Application
import android.net.Uri
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Upsert
import com.alphaomardiallo.handydocs.domain.model.ImageDoc
import kotlinx.coroutines.flow.Flow

@Database(entities = [(ImageDoc::class)], version = 1)
@TypeConverters(UriTypeConverter::class)
abstract class AppDataBase: RoomDatabase() {
    abstract fun imageDao(): ImageDao
}

@Dao
interface ImageDao {

    @Upsert
    suspend fun upsertImage(image: ImageDoc)

    @Delete
    suspend fun deleteImage(image: ImageDoc)

    @Query("SELECT * FROM table_image_doc")
    fun getAllImage(): Flow<List<ImageDoc>>
}

class UriTypeConverter {

    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(it) }
    }
}

fun provideDataBase(application: Application): AppDataBase =
    Room.databaseBuilder(
        application,
        AppDataBase::class.java,
        "app_database"
    ).fallbackToDestructiveMigration().build()

fun provideImageDao(appDataBase: AppDataBase): ImageDao = appDataBase.imageDao()
