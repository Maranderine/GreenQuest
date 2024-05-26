package de.hsb.greenquest.domain.usecase

import android.util.Log
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.domain.repository.PlantPictureRepository
import javax.inject.Inject

class TakePictureUseCase @Inject constructor(
    private val repository: PlantPictureRepository
) {
    /*suspend fun takePicture(imagePath: String) {
        repository.savePlantPicture(Plant(
            //TODO the image name should be returned from the API instead of the actual filename
            imagePath.substringAfterLast("/"),"", imagePath, false
        ))
    }*/
}