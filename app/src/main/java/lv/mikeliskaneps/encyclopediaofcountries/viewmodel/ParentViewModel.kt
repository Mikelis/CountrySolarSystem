package lv.mikeliskaneps.encyclopediaofcountries.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import lv.mikeliskaneps.encyclopediaofcountries.common.data.DataStoreRepository
import lv.mikeliskaneps.encyclopediaofcountries.navigation.NavigationEvent
import timber.log.Timber

abstract class ParentViewModel(private val dataStore: DataStoreRepository?) : ViewModel() {
    protected val _navigationEvent: MutableSharedFlow<NavigationEvent> = MutableSharedFlow()
    val navigationEvent =
        _navigationEvent.asSharedFlow().shareIn(viewModelScope, SharingStarted.Lazily, replay = 0)

    protected val _errorEvent: MutableSharedFlow<Throwable> = MutableSharedFlow()
    val errorEvent =
        _errorEvent.asSharedFlow().shareIn(viewModelScope, SharingStarted.Lazily, replay = 0)

    var isLoading = MutableLiveData<Boolean>(false)
    val errorLiveData: MutableLiveData<Throwable?> = MutableLiveData()


    val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            Timber.e(exception)
            _errorEvent.emit(exception)
            errorLiveData.value = exception
            isLoading.value = false
        }
    }
}