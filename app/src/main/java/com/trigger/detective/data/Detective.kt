package com.trigger.detective.data

data class Detective(
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
    var speed: Float = 5f
){
    var velocityX = 0f
    var velocityY = 0f
}
