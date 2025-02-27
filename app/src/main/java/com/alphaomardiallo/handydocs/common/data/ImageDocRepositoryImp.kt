package com.alphaomardiallo.handydocs.common.data

import com.alphaomardiallo.handydocs.common.domain.model.ImageDoc
import com.alphaomardiallo.handydocs.common.domain.repository.ImageDocRepository
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

    override fun getSelectedImage(): Flow<ImageDoc> {
        return appDataBase.imageDao().getSelectedImageDoc()
    }

    override fun selectedImageToNull() {
        return appDataBase.imageDao().selectedImageToNull()
    }

    override fun getAllImageNameAsc(): Flow<List<ImageDoc>> {
        return appDataBase.imageDao().getAllImageNameAsc()
    }

    override fun getAllImageNameDesc(): Flow<List<ImageDoc>> {
        return appDataBase.imageDao().getAllImageNameDesc()
    }

    override fun getAllImageTimeAsc(): Flow<List<ImageDoc>> {
        return appDataBase.imageDao().getAllImageTimeAsc()
    }

    override fun getAllImageTimeDesc(): Flow<List<ImageDoc>> {
        return appDataBase.imageDao().getAllImageTimeDesc()
    }

    override fun getAllFavoriteImage(): Flow<List<ImageDoc>> {
        return appDataBase.imageDao().getFavoriteImageDoc()
    }

    override fun searchImageDoc(name: String): Flow<List<ImageDoc>> {
        return appDataBase.imageDao().searchImageDoc(name)
    }
}
