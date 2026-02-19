package com.trigger.detective

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.trigger.detective.data.Detective
import kotlin.coroutines.coroutineContext
import kotlin.math.abs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    // ==============================
    // Coroutine Game Loop
    // ==============================

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var gameJob: Job? = null

    // ==============================
    // Paint (reused - avoid reallocation)
    // ==============================

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // ==============================
    // Player
    // ==============================

    val player = Detective(
        x = 200f,
        y = 200f,
        width = 180f,
        height = 220f
    )

    // ==============================
    // Sprite Sheet
    // ==============================

    private val detectiveBitmap: Bitmap

    companion object {
        private const val SPRITE_ROWS = 4
        private const val SPRITE_COLS = 4

        private const val DIRECTION_DOWN = 0
        private const val DIRECTION_RIGHT = 1
        private const val DIRECTION_UP = 2
        private const val DIRECTION_LEFT = 3

        private const val TARGET_FPS = 60
        private const val FRAME_DELAY = 150L
    }

    private val frameWidth: Int
    private val frameHeight: Int

    private var currentFrame = 0
    private var currentRow = DIRECTION_DOWN

    private var animationTimer = 0L

    // Reuse rect objects (performance optimization)
    private val srcRect = Rect()
    private val destRect = Rect()

    init {
        holder.addCallback(this)

        detectiveBitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.detective_sheet
        )

        frameWidth = detectiveBitmap.width / SPRITE_COLS
        frameHeight = detectiveBitmap.height / SPRITE_ROWS
    }

    // ==============================
    // Game Loop
    // ==============================

    private suspend fun gameLoop() {

        val frameTime = 1000L / TARGET_FPS
        var lastTime = System.currentTimeMillis()

        while (coroutineContext.isActive) {

            val now = System.currentTimeMillis()
            val delta = now - lastTime
            lastTime = now

            update(delta)
            render()

            val workTime = System.currentTimeMillis() - now
            val sleep = frameTime - workTime

            if (sleep > 0) delay(sleep)
        }
    }

    // ==============================
    // Update
    // ==============================

    private fun update(deltaTime: Long) {

        // Move player
        player.x += player.velocityX
        player.y += player.velocityY

        // Clamp inside screen
        player.x = player.x.coerceIn(0f, width - player.width)
        player.y = player.y.coerceIn(0f, height - player.height)

        updateDirection()
        updateAnimation(deltaTime)
    }

    private fun updateDirection() {

        if (abs(player.velocityX) > abs(player.velocityY)) {
            when {
                player.velocityX > 0 -> currentRow = DIRECTION_RIGHT
                player.velocityX < 0 -> currentRow = DIRECTION_LEFT
            }
        } else {
            when {
                player.velocityY > 0 -> currentRow = DIRECTION_DOWN
                player.velocityY < 0 -> currentRow = DIRECTION_UP
            }
        }
    }

    private fun updateAnimation(deltaTime: Long) {

        val moving = player.velocityX != 0f || player.velocityY != 0f

        if (!moving) {
            currentFrame = 0
            return
        }

        animationTimer += deltaTime

        if (animationTimer >= FRAME_DELAY) {
            currentFrame = (currentFrame + 1) % SPRITE_COLS
            animationTimer = 0
        }
    }

    // ==============================
    // Render
    // ==============================

    private fun render() {

        if (!holder.surface.isValid) return

        val canvas = holder.lockCanvas() ?: return

        try {
            canvas.drawColor(Color.WHITE)

            // Calculate source rectangle
            srcRect.set(
                currentFrame * frameWidth,
                currentRow * frameHeight,
                (currentFrame + 1) * frameWidth,
                (currentRow + 1) * frameHeight
            )

            // Calculate destination rectangle
            destRect.set(
                player.x.toInt(),
                player.y.toInt(),
                (player.x + player.width).toInt(),
                (player.y + player.height).toInt()
            )

            canvas.drawBitmap(detectiveBitmap, srcRect, destRect, paint)

        } finally {
            holder.unlockCanvasAndPost(canvas)
        }
    }

    // ==============================
    // Lifecycle
    // ==============================

    fun startGame() {
        if (gameJob?.isActive == true) return
        gameJob = scope.launch { gameLoop() }
    }

    fun stopGame() {
        gameJob?.cancel()
        gameJob = null
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        startGame()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopGame()
    }

    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ) {}
}
