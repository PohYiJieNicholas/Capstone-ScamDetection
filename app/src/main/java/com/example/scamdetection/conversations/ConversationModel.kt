package com.example.scamdetection.conversations

data class ConversationModel(
    val conversation:String ?= null,
    val date:String?= null,
    val prediction:String?= null
)
