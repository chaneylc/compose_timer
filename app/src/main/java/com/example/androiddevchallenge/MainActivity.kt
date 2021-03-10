/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.shapes
import java.util.Timer
import java.util.TimerTask

/**
 * A simple 60 second countdown timer.
 * Uses a hashset to save 'highscores' and notifies the user when a new high score is accomplished.
 */
class MainActivity : AppCompatActivity() {

    /**
     * track the highscores in memory,
     * TODO: use Room to persist score (this ties really well with compose)
     */
    private val mHighscoreStore = HashSet<Double>()

    /**
     * Timer used to countdown.
     */
    private var mTimer: Timer = Timer()

    /**
     * Called whenever the end button is pressed. Checks the high score set and notifies the user.
     */
    private fun checkScore(score: Double) {

        if (mHighscoreStore.isEmpty()) {

            mHighscoreStore.add(score)

            Toast.makeText(
                applicationContext,
                "New baseline established!", Toast.LENGTH_LONG
            ).show()
        }

        mHighscoreStore.maxOrNull()?.let { highscore ->

            if (highscore < score) {

                mHighscoreStore.add(score)

                Toast.makeText(
                    applicationContext,
                    "Wow, new high score, I love compose!", Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // nifty extension function grabbed from stack https://stackoverflow.com/questions/23086291/format-in-kotlin-string-templates
    private fun Double.format(digits: Int) = "%.${digits}f".format(this)

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }

    /**
     * A very basic centered column with two buttons that control the count down (start/end)
     */
    @ExperimentalAnimationApi
    @Composable
    fun MyTimer() {

        Column(
            Modifier.border(2.dp, Color.Black, shape = shapes.medium)
                .fillMaxSize()
                .padding(16.dp), // material design guideline padding
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var timerStatus by rememberSaveable { mutableStateOf(60.0) }
            var buttonEnabled by rememberSaveable { mutableStateOf(true) }
            Text(timerStatus.format(2), style = MaterialTheme.typography.h2)
            AnimatedVisibility(buttonEnabled) {
                Button(
                    onClick = {
                        buttonEnabled = false
                        // schedule a timer update every 1s (might be an alternative to this with animations or live data)
                        mTimer = Timer()
                        mTimer.scheduleAtFixedRate(
                            object : TimerTask() {
                                override fun run() {
                                    if (timerStatus <= 0.0) {
                                        buttonEnabled = true
                                        timerStatus = 60.0
                                        mTimer.cancel()
                                    } else timerStatus -= 0.1
                                }
                            },
                            0L, 100L
                        )
                    }
                ) {
                    Text("Begin", style = MaterialTheme.typography.h3)
                }
            }
            AnimatedVisibility(!buttonEnabled) {
                Button(
                    onClick = {
                        mTimer.cancel()
                        mTimer.purge()
                        checkScore(timerStatus)
                        timerStatus = 60.0
                        buttonEnabled = true
                    }
                ) {
                    Text("End", style = MaterialTheme.typography.h3)
                }
            }
        }
    }

    // Start building your app here!
    @ExperimentalAnimationApi
    @Composable
    fun MyApp() {
        // TODO: add 3 more rows that display 1st, 2nd, 3rd high scores
        Card(
            Modifier
                .border(2.dp, Color.Black, shape = shapes.large)
                .fillMaxSize()
                .padding(16.dp),
            shape = shapes.large
        ) {
            // Text(text = "Ready... Set... GO!")
            MyTimer()
        }
    }

    @ExperimentalAnimationApi
    @Preview("Light Theme", widthDp = 360, heightDp = 640)
    @Composable
    fun LightPreview() {
        MyTheme {
            MyApp()
        }
    }

    @ExperimentalAnimationApi
    @Preview("Dark Theme", widthDp = 360, heightDp = 640)
    @Composable
    fun DarkPreview() {
        MyTheme(darkTheme = true) {
            MyApp()
        }
    }
}
