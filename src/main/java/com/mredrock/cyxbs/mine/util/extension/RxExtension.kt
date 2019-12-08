package com.mredrock.cyxbs.mine.util.extension

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.utils.extensions.checkError
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import io.reactivex.Observable

/**
 * Created by zia on 2018/8/26.
 */
fun <T> Observable<RedrockApiWrapper<T>>.normalWrapper(viewModel: BaseViewModel): Observable<T> =
        mapOrThrowApiException()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { false }
                .doFinally { viewModel.progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { viewModel.progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }

fun Observable<RedrockApiStatus>.normalStatus(viewModel: BaseViewModel): Observable<RedrockApiStatus> =
        checkError()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { false }
                .doFinally { viewModel.progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { viewModel.progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }