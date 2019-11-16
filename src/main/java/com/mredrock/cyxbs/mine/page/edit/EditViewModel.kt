package com.mredrock.cyxbs.mine.page.edit

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.normalStatus
import com.mredrock.cyxbs.mine.util.user
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Created by zia on 2018/8/26.
 */
class EditViewModel : BaseViewModel() {

    val updateInfoEvent = MutableLiveData<Boolean>()
    val upLoadImageEvent = MutableLiveData<Boolean>()

    fun updateUserInfo(nickname: String, introduction: String, qq: String, phone: String
                       , photoThumbnailSrc: String = user!!.photoThumbnailSrc ?: ""
                       , photoSrc: String = user!!.photoSrc ?: "") {
        apiService.updateUserInfo(user!!.stuNum!!, user!!.idNum!!,
                nickname, introduction, qq, phone, photoThumbnailSrc, photoSrc)
                .normalStatus(this)
                .safeSubscribeBy(
                        onNext = {
                            val userEditor = ServiceManager.getService(IAccountService::class.java).getUserEditorService()
                            userEditor.setNickname(nickname)
                            userEditor.setIntroduction(introduction)
                            userEditor.setQQ(qq)
                            userEditor.setPhone(phone)
                            updateInfoEvent.postValue(true)
                        },
                        onError = {
                            updateInfoEvent.postValue(false)
                        }
                )
                .lifeCycle()
    }

    fun uploadAvatar(stuNum: RequestBody,
                     file: MultipartBody.Part) {
        apiService.uploadSocialImg(stuNum, file)
                .mapOrThrowApiException()
                .flatMap {
                    LogUtils.d("ImageUpdateResult", it.toString())
                    ServiceManager.getService(IAccountService::class.java)
                            .getUserEditorService()
                            .setAvatarImgUrl(it.thumbnail_src)
                    apiService.updateUserImage(user!!.stuNum!!, user!!.idNum!!
                            , it.thumbnail_src, it.photosrc)
                }
                .normalStatus(this)
                .safeSubscribeBy(onError = { upLoadImageEvent.value = false }
                        , onNext = { upLoadImageEvent.value = true })
                .lifeCycle()
    }
}