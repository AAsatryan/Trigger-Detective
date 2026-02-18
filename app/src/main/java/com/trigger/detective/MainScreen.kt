package com.trigger.detective

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.trigger.detective.ui.theme.TriggerDetectiveTheme
import com.trigger.detective.ui.theme.dark
import com.trigger.detective.ui.theme.red
import com.trigger.detective.R.drawable.ic_detective_icon
import com.trigger.detective.ui.theme.Typography

@Composable
fun InitialScreen(
    onStartClick : () -> Unit,
    onChapterClick: () -> Unit
){
    TriggerDetectiveTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(dark)
        ) {
           Column(modifier = Modifier
               .padding(top = 40.dp),
               horizontalAlignment = Alignment.CenterHorizontally

           ) {

               Text(
                   modifier = Modifier.padding(top = 30.dp),
                   text = "CHAPTER I ",
                   color = red,
                   style = Typography.titleLarge
               )

               Image(
                   painter = painterResource(id = ic_detective_icon),
                   contentDescription = null,
                   modifier = Modifier.size(200.dp),
                   contentScale = ContentScale.Crop
               )
               Button(
                   onClick = onStartClick,
                   colors = ButtonDefaults.buttonColors(
                       containerColor = red
                   ),
                   modifier = Modifier.fillMaxWidth()
                       .padding(
                           top = 50.dp,
                           bottom = 10.dp
                       )
               ) {
                   Text("Start Investigation", color = Color.White)
               }
               Button(
                   onClick = onChapterClick,
                   colors = ButtonDefaults.buttonColors(
                       containerColor = red
                   ),
                   modifier = Modifier.fillMaxWidth()
               ) {
                   Text("Chapter Details", color = Color.White)
               }
           }
        }
    }
}