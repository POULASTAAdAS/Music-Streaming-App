package com.poulastaa.data.model.pinned

import com.poulastaa.data.model.common.IdType
import kotlinx.serialization.Serializable

@Serializable
data class PinnedReq(
    val id: Long = -1,
    val type: IdType = IdType.ERR,
    val operation: PinnedOperation = PinnedOperation.ERR
)