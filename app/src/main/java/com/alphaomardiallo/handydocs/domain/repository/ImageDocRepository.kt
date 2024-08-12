package com.alphaomardiallo.handydocs.domain.repository

import com.alphaomardiallo.handydocs.domain.model.ImageDoc
import kotlinx.coroutines.flow.Flow

interface ImageDocRepository {

    suspend fun upsertImage(image: ImageDoc)

    suspend fun deleteImage(image: ImageDoc)

    fun getAllImages(): Flow<List<ImageDoc>>

    fun getSelectedImage(): Flow<ImageDoc>
}
