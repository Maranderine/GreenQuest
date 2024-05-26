package de.hsb.greenquest.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hsb.greenquest.R
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.domain.repository.PlantPictureRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.Flow
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val plantPictureRepository: PlantPictureRepository
): ViewModel() {

    private val _plantListFlow = MutableStateFlow<List<Plant>>(mutableListOf())
    val plantListFlow get() = _plantListFlow.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            plantPictureRepository.getAllPlantPictures().collect { plantList ->
                _plantListFlow.value = plantList
            }
        }
    }

}