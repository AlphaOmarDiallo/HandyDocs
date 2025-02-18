package com.alphaomardiallo.handydocs.common.domain.repository

import com.alphaomardiallo.handydocs.common.domain.model.ImageDoc
import kotlinx.coroutines.flow.Flow

interface ImageDocRepository {

    suspend fun upsertImage(image: ImageDoc)

    suspend fun deleteImage(image: ImageDoc)

    fun getAllImages(): Flow<List<ImageDoc>>

    fun getAllImageNameAsc(): Flow<List<ImageDoc>>

    fun getAllImageNameDesc(): Flow<List<ImageDoc>>

    fun getAllImageTimeAsc(): Flow<List<ImageDoc>>

    fun getAllImageTimeDesc(): Flow<List<ImageDoc>>

    fun getSelectedImage(): Flow<ImageDoc>

    fun getAllFavoriteImage(): Flow<List<ImageDoc>>

    fun selectedImageToNull()

    fun searchImageDoc(name: String): Flow<List<ImageDoc>>
}
