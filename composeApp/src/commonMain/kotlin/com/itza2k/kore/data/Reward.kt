package com.itza2k.kore.data

data class Reward(
    val id: String,
    val name: String,
    val description: String,
    val pointsCost: Int,
    val category: String = "",
    val isEcoFriendly: Boolean = false
)