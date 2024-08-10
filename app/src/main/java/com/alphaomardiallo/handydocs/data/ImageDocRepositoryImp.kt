package com.alphaomardiallo.handydocs.data

import com.alphaomardiallo.handydocs.domain.model.ImageDoc
import com.alphaomardiallo.handydocs.domain.repository.ImageDocRepository
import kotlinx.coroutines.flow.Flow

class ImageDocRepositoryImp(private val appDataBase: AppDataBase) : ImageDocRepository {
    override suspend fun upsertImage(image: ImageDoc) {
        return appDataBase.imageDao().upsertImage(image)
    }

    override suspend fun deleteImage(image: ImageDoc) {
        return appDataBase.imageDao().deleteImage(image)
    }

    override fun getAllImages(): Flow<List<ImageDoc>> {
        return appDataBase.imageDao().getAllImage()
    }
}
