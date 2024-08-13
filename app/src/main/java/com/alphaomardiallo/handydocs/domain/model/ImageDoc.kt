package com.alphaomardiallo.handydocs.domain.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "table_image_doc")
data class ImageDoc(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    val name: String = "",
    val uriJpeg: List<Uri> = emptyList(),
    val displayName: String? = null,
    val uriPdf: Uri? = null,
    val time: Long = System.currentTimeMillis(),
    var isSelected: Boolean = false,
    var isFavorite: Boolean = false
) {
    fun getReadableTime(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val readableTime = sdf.format(Date(time))
        return readableTime
    }
}
