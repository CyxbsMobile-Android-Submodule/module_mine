package com.mredrock.cyxbs.mine.page.sign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.bean.User
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.mine.network.model.Product
import com.mredrock.cyxbs.mine.network.model.ScoreStatus
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.normalWrapper
import io.reactivex.Observable
import io.reactivex.functions.Function

/**
 * Created by zia on 2018/8/22.
 * 需要确保用户已登录
 */
class DailyViewModel : BaseViewModel() {

    //签到状态
    private val _status = MutableLiveData<ScoreStatus>()
    val status: LiveData<ScoreStatus>
        get() = _status

    //积分商城物品
    //作为唯一置信源，驱动视图的更新
    private val _products = MutableLiveData<MutableList<Product>>()
    val products: LiveData<List<Product>> = Transformations.map(_products) {
        it.toList()
    }

    //作为唯一置信源，用来弹出寒暑假不可签到的toast，同时为了解决LiveData粘性事件的限制，采用SingleLiveEvent
    private val _isInVacation = SingleLiveEvent<Boolean>()
    val isInVacation: LiveData<Boolean>
        get() = _isInVacation

    private var page = 1
    private val pagesize = 6


    fun loadAllData(user: User) {
        apiService.getScoreStatus(user.stuNum ?: return, user.idNum ?: return)
                .normalWrapper(this)
                .safeSubscribeBy {
                    _status.postValue(it)
                }
                .lifeCycle()


    }


    //用flatmap解决嵌套请求的问题
    fun checkIn(user: User) {
        apiService.checkIn(user.stuNum ?: return, user.idNum ?: return)
                .flatMap(Function<RedrockApiStatus, Observable<RedrockApiWrapper<ScoreStatus>>> {
                    //如果status为405，说明是在寒暑假，此时不可签到
                    if (it.status == 405) {
                        _isInVacation.postValue(true)
                    }
                    return@Function user.stuNum?.let { stuNum ->
                        user.idNum?.let { idNum ->
                            apiService.getScoreStatus(stuNum, idNum)
                        }
                    }
                })
                .setSchedulers()
                .normalWrapper(this)
                .safeSubscribeBy {
                    //如果status没有内容没有更新，就不用postValue
                    if (it != _status.value) {
                        _status.postValue(it)
                    }
                }
                .lifeCycle()
    }

    fun loadProduct(user: User) {
        if (page == 5) {
            return
        }
        val fakeItem: Product = Product("roger" + page + Math.random(), 222, 10, "")
        val newList = mutableListOf<Product>(fakeItem)

        if (_products.value == null) {
            _products.value = newList
        } else {
            _products.value?.let { list ->
                list.addAll(newList)
                _products.value = list
            }
        }

        page++
        loadProduct(user)
    }
}
