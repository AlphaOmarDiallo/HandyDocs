package com.alphaomardiallo.handydocs.domain.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_image_doc")
data class ImageDoc(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    val name: String,
    val uri: Uri
)
