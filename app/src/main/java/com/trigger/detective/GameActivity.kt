package com.trigger.detective

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.yourpackage.game.GameView

class GameActivity : ComponentActivity() {

    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setContent {
            Box(modifier = Modifier.fillMaxSize()) {

                // GameView Surface
                AndroidView(factory = { context ->
                    gameView = GameView(context)
                    gameView
                }, modifier = Modifier.fillMaxSize())


                Joystick(
                    modifier = Modifier
                        .align(Alignment.BottomEnd),
                    onMove = { dx, dy ->
                        // dx/dy are -1..1, multiply by speed
                        gameView.player.velocityX = dx * gameView.player.speed
                        gameView.player.velocityY = dy * gameView.player.speed
                    }
                )
            }
        }
    }

     override fun onPause() {
         super.onPause()
         gameView.stopGame()
     }
}