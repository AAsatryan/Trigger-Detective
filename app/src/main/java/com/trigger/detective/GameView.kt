package com.trigger.detective

import android.content.Context
import android.graphics.*
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.trigger.detective.R
import com.trigger.detective.data.Detective
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.*
import kotlin.math.abs

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    // ==============================
    // Coroutine Game Loop
    // ==============================

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var gameJob: Job? = null

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

    private var detectiveBitmap: Bitmap

    companion object {
        private const val SPRITE_ROWS = 4
        private const val SPRITE_COLS = 4

        private const val DIRECTION_DOWN = 0
        private const val DIRECTION_RIGHT = 1
        private const val DIRECTION_UP = 2
        private const val DIRECTION_LEFT = 3
    }


    private var frameWidth = 0
    private var frameHeight = 0

    private var currentFrame = 0
    private var currentRow = DIRECTION_DOWN

    private var frameTimer = 0L
    private val frameDelay = 150L

    // ==============================
    // Init
    // ==============================

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
        val targetFrameTime = 16L // ~60 FPS

        var lastFrameTime = System.currentTimeMillis()

        while (coroutineContext.isActive) {
            val currentTime = System.currentTimeMillis()
            val deltaTime = currentTime - lastFrameTime
            lastFrameTime = currentTime

            update(deltaTime)
            drawGame()

            val frameTime = System.currentTimeMillis() - currentTime
            val sleepTime = targetFrameTime - frameTime

            if (sleepTime > 0) {
                delay(sleepTime)
            }
        }
    }

    // ==============================
    // Update Logic
    // ==============================

    private fun update(deltaTime: Long) {

        // Move player
        player.x += player.velocityX
        player.y += player.velocityY

        // Keep inside screen bounds
        player.x = player.x.coerceIn(0f, width - player.width)
        player.y = player.y.coerceIn(0f, height - player.height)

        // Determine direction (priority to strongest axis)
        if (abs(player.velocityX) > abs(player.velocityY)) {
            if (player.velocityX > 0) currentRow = DIRECTION_RIGHT
            else if (player.velocityX < 0) currentRow = DIRECTION_LEFT
        } else {
            if (player.velocityY > 0) currentRow = DIRECTION_DOWN
            else if (player.velocityY < 0) currentRow = DIRECTION_UP
        }

        // Animate only when moving
        if (player.velocityX != 0f || player.velocityY != 0f) {

            frameTimer += deltaTime

            if (frameTimer >= frameDelay) {
                currentFrame = (currentFrame + 1) % SPRITE_COLS
                frameTimer = 0
            }

        } else {
            currentFrame = 0 // idle frame
        }
    }

    // ==============================
    // Draw
    // ==============================

    private fun drawGame() {

        if (!holder.surface.isValid) return

        val canvas = holder.lockCanvas()

        try {

            // Background
            canvas.drawColor(android.graphics.Color.WHITE)

            // Source frame from sprite sheet
            val src = Rect(
                currentFrame * frameWidth,
                currentRow * frameHeight,
                (currentFrame + 1) * frameWidth,
                (currentRow + 1) * frameHeight
            )

            // Destination on screen
            val dest = Rect(
                player.x.toInt(),
                player.y.toInt(),
                (player.x + player.width).toInt(),
                (player.y + player.height).toInt()
            )

            canvas.drawBitmap(detectiveBitmap, src, dest, null)

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
