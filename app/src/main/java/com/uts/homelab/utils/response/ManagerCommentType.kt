package com.uts.homelab.utils.response

import com.uts.homelab.network.dataclass.CommentType

sealed class ManagerCommentType{
    data class Success(val modelSuccess:List<CommentType>) : ManagerCommentType()
    data class Error(val error:String) : ManagerCommentType()
}


