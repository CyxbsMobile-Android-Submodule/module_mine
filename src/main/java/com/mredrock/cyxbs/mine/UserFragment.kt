package com.mredrock.cyxbs.mine


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.MINE_ENTRY
import com.mredrock.cyxbs.common.event.AskLoginEvent
import com.mredrock.cyxbs.common.event.LoginStateChangeEvent
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.mine.page.aboutme.AboutMeActivity
import com.mredrock.cyxbs.mine.page.ask.AskActivity
import com.mredrock.cyxbs.mine.page.draft.DraftActivity
import com.mredrock.cyxbs.mine.page.edit.EditInfoActivity
import com.mredrock.cyxbs.mine.page.help.HelpActivity
import com.mredrock.cyxbs.mine.page.setting.SettingActivity
import com.mredrock.cyxbs.mine.page.sign.DailySignActivity
import com.mredrock.cyxbs.mine.page.store.StoreActivity
import kotlinx.android.synthetic.main.mine_fragment_main.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.support.v4.startActivity

/**
 * Created by zzzia on 2018/8/14.
 * 我的 主界面Fragment
 */
@Route(path = MINE_ENTRY)
class UserFragment : BaseFragment(), View.OnClickListener {
    private val accountService = ServiceManager.getService(IAccountService::class.java)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //功能按钮
        mine_main_dailySign.setOnClickListener { checkLoginBeforeAction("签到") { startActivity<DailySignActivity>() } }
        mine_main_store.setOnClickListener { checkLoginBeforeAction("商店") { startActivity<StoreActivity>() } }
        mine_main_question.setOnClickListener { checkLoginBeforeAction("问一问") { startActivity<AskActivity>() } }
        mine_main_help.setOnClickListener { checkLoginBeforeAction("帮一帮") { startActivity<HelpActivity>() } }
        mine_main_draft.setOnClickListener { checkLoginBeforeAction("草稿箱") { startActivity<DraftActivity>() } }
        mine_main_relateMe.setOnClickListener { checkLoginBeforeAction("与我相关") { startActivity<AboutMeActivity>() } }
        mine_main_setting.setOnClickListener { startActivity<SettingActivity>() }

        mine_main_avatar.setOnClickListener(this)
        mine_main_username.setOnClickListener(this)
        mine_main_introduce.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (!accountService.getVerifyService().isLogin()) {
            EventBus.getDefault().post(AskLoginEvent("请先登陆哦~"))
        } else {
            startActivity<EditInfoActivity>()
        }
    }

    private fun checkLoginBeforeAction(msg: String, action: () -> Unit) {
        if (accountService.getVerifyService().isLogin()) {
            action.invoke()
        } else {
            EventBus.getDefault().post(AskLoginEvent("请先登陆才能查看${msg}哦~"))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshEditLayout()
    }

    private fun clearAllRemind() {
        mine_main_dailySign.isRemindIconShowing = false
        mine_main_question.isRemindIconShowing = false
        mine_main_help.isRemindIconShowing = false
        mine_main_relateMe.isRemindIconShowing = false
        mine_main_setting.isRemindIconShowing = false
    }


    private fun refreshEditLayout() {
        fun String.orDefault(default: String) = takeIf { isNotEmpty() } ?: default

        mine_main_username.text = accountService.getUserService().getNickname().orDefault(getString(R.string.mine_user_empty_username))
        mine_main_introduce.text = accountService.getUserService().getIntroduction().orDefault(getString(R.string.mine_user_empty_introduce))

        if (accountService.getVerifyService().isLogin()) {
            mine_main_avatar.setAvatarImageFromUrl(accountService.getUserService().getAvatarImgUrl())
        } else {
            mine_main_avatar.setImageResource(R.drawable.mine_default_avatar)
            clearAllRemind()
        }
    }

    override fun onLoginStateChangeEvent(event: LoginStateChangeEvent) {
        super.onLoginStateChangeEvent(event)
        refreshEditLayout()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.mine_fragment_main, container, false)
}
