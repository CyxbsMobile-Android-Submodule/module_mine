package com.mredrock.cyxbs.mine.page.comment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.Comment
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.ui.RvFooter
import kotlinx.android.synthetic.main.mine_list_item_comment_comment.view.*

/**
 * Created by roger on 2019/12/5
 * 发出的评论
 */
class CommentFragment : BaseRVFragment<Comment>() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(CommentViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        viewModel.loadCommentList()
        viewModel.eventOnComment.observe(this, Observer {
            if (it == RvFooter.State.ERROR) {
                getFooter().showLoadError()
            } else if (it == RvFooter.State.NOMORE) {
                getFooter().showNoMore()
            }
        })
        viewModel.commentList.observe(this, Observer {
            setNewData(it)
        })
    }

    override fun getItemLayout(): Int {
        return R.layout.mine_list_item_comment_comment
    }

    //自动加载更多
    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (getFooter().state == RvFooter.State.LOADING) {
            viewModel.loadCommentList()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bindDataHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, data: Comment) {
        holder.itemView.mine_comment_tv_at_who.text = data.answerer
        holder.itemView.mine_comment_tv_content.text = data.commentContent
    }

    override fun onSwipeLayoutRefresh() {
        getFooter().showLoading()
        viewModel.cleanCommentPage()
        viewModel.loadCommentList()
        getSwipeLayout().isRefreshing = false
    }
}