package com.example.myapplication

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.Intent

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.provider.SyncStateContract.Helpers.update
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity

import kotlin.random.Random

var playerX=300f
var playerY=500f
var newplayerY=10f
var obstacleX=2000f
var obstacleY=500f
var chaserX=0f
var chaserY=500f
var vX=6f
var vY=5f
var p=2
private var count=0
var gameover=false
var isJumping=false
var isCollisionDetected=false
var score=0
var highscore=0
var currentBitmapIndex=0
var currentbitmap=0
var isChaserJumping=false
private var firsttime=true
private var jumpThreshold: Float = 5f
private var isDecrementDone=false
private lateinit var mediaPlayer: MediaPlayer
var collisioncount=0
var dialogshown=false



private fun highscore(){
    if (highscore>score){
        highscore=score
    }
}





class GameView (context: Context):View(context) {
    private val activityContext: Context = context



    // Change background to a drawable resource


    private lateinit var canvas: Canvas
    private lateinit var bitmap1: Bitmap
    private lateinit var bitmap2: Bitmap
    private lateinit var bitmap3: Bitmap
    private lateinit var bitmap4: Bitmap
    private lateinit var resizedBitmap1: Bitmap
    private lateinit var resizedBitmap2: Bitmap
    private lateinit var bitmaptodraw: Bitmap
    private lateinit var background: Bitmap


    private var nextObstacleDelay: Long = (500..1000).random().toLong()

    private fun generateNextObstacleDelay(): Long {
        return (500..1000).random().toLong()
    }


    private var originalY: Float = 0f

    private fun jump() {
        val jumpDuration = 1000L // Adjust the jump duration as needed
        val jumpHeight = 1000f // Adjust the jump height as needed

        originalY = playerY

        val jumpAnimator = ValueAnimator.ofFloat(originalY, originalY - jumpHeight)
        jumpAnimator.duration = jumpDuration
        jumpAnimator.interpolator = AccelerateDecelerateInterpolator()
        jumpAnimator.addUpdateListener { valueAnimator ->
            playerY = valueAnimator.animatedValue as Float
            invalidate()
        }
        jumpAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                jumpBack()
            }
        })
        jumpAnimator.start()
    }

    private fun jumpBack() {
        val jumpDuration = 1000L // Adjust the jump duration as needed

        val jumpBackAnimator = ValueAnimator.ofFloat(playerY, originalY)
        jumpBackAnimator.duration = jumpDuration
        jumpBackAnimator.interpolator = AccelerateDecelerateInterpolator()
        jumpBackAnimator.addUpdateListener { valueAnimator ->
            playerY = valueAnimator.animatedValue as Float
            invalidate()
        }
        jumpBackAnimator.start()

        if(playerX>obstacleX||obstacleX- playerX<200f){
            score+=5
        }
    }

    private fun jumpChaser() {

        originalY = chaserY
        chaserY -= 500f
        invalidate()
        postDelayed({
            // Return the object to its original position
            chaserY = originalY
            invalidate() // Redraw the view
        }, 1000)
        invalidate()
        /*val jumpDuration = 1000L // Adjust the jump duration as needed
        val jumpHeight = 1000f // Adjust the jump height as needed

        originalY = chaserY

        val jumpAnimator = ValueAnimator.ofFloat(originalY, originalY - jumpHeight)
        jumpAnimator.duration = jumpDuration
        jumpAnimator.interpolator = AccelerateDecelerateInterpolator()
        jumpAnimator.addUpdateListener { valueAnimator ->
            chaserY = valueAnimator.animatedValue as Float
            invalidate()
        }
        jumpAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                jumpBackChaser()
            }
        })
        jumpAnimator.start()*/
    }

    private fun jumpBackChaser() {
        val jumpDuration = 1000L // Adjust the jump duration as needed

        val jumpBackAnimator = ValueAnimator.ofFloat(chaserY, originalY)
        jumpBackAnimator.duration = jumpDuration
        jumpBackAnimator.interpolator = AccelerateDecelerateInterpolator()
        jumpBackAnimator.addUpdateListener { valueAnimator ->
            chaserY = valueAnimator.animatedValue as Float
            invalidate()
        }
        jumpBackAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // Start the chaser jump again when the jump back animation finishes
                jumpChaser()
            }
        })
        jumpBackAnimator.start()
    }

    private fun jumpChaser1() {
        if (!isChaserJumping && isObstacleNearby(bitmaptodraw)) {
            isChaserJumping = true

            val jumpDuration = 500 // Adjust the jump duration as needed
            val jumpHeight = 200 // Adjust the jump height as needed
            val jumpInterpolator = AccelerateDecelerateInterpolator()

            val startY = chaserY
            val endY = chaserY - jumpHeight

            val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
            valueAnimator.duration = jumpDuration.toLong()
            valueAnimator.interpolator = jumpInterpolator

            valueAnimator.addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                chaserY = (startY + (endY - startY) * animatedValue).toFloat()
            }

            valueAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    isChaserJumping = false
                }
            })

            valueAnimator.start()
        }
    }

    private fun isObstacleNearby(bitmap: Bitmap): Boolean {
        val proximityThreshold = 100f // Adjust the proximity threshold as needed

        return obstacleX + bitmaptodraw.width >= chaserX - proximityThreshold &&
                obstacleX <= chaserX + bitmap.width + proximityThreshold &&
                obstacleY == chaserY
    }

    private fun jumpchaser() {
        val jumpDuration = 1000L // Adjust the jump duration as needed
        val jumpHeight = 700f // Adjust the jump height as needed

        originalY = chaserY

        val jumpAnimator = ValueAnimator.ofFloat(originalY, originalY - jumpHeight)
        jumpAnimator.duration = jumpDuration
        jumpAnimator.interpolator = AccelerateDecelerateInterpolator()
        jumpAnimator.addUpdateListener { valueAnimator ->
            chaserY = valueAnimator.animatedValue as Float
            invalidate()
        }
        jumpAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                jumpBackc()
            }
        })
        jumpAnimator.start()
    }

    private fun jumpBackc() {

        val jumpDuration = 1000L // Adjust the jump duration as needed

        val jumpBackAnimator = ValueAnimator.ofFloat(chaserY, originalY)
        jumpBackAnimator.duration = jumpDuration
        jumpBackAnimator.interpolator = AccelerateDecelerateInterpolator()
        jumpBackAnimator.addUpdateListener { valueAnimator ->
            chaserX = valueAnimator.animatedValue as Float
            invalidate()
        }
        jumpBackAnimator.start()
    }

    private fun jumpChaseri() {
        val jumpDuration = 1000L // Adjust the jump duration as needed
        val jumpHeight = 1000f // Adjust the jump height as needed

        originalY = chaserY

        val jumpAnimator = ValueAnimator.ofFloat(originalY, originalY - jumpHeight)
        jumpAnimator.duration = jumpDuration
        jumpAnimator.interpolator = AccelerateDecelerateInterpolator()
        jumpAnimator.addUpdateListener { valueAnimator ->
            chaserY = valueAnimator.animatedValue as Float
            invalidate()
        }
        jumpAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                jumpBackchaser()
            }
        })
        jumpAnimator.start()
    }

    private fun jumpBackchaser() {
        val jumpDuration = 1000L // Adjust the jump duration as needed

        val jumpBackAnimator = ValueAnimator.ofFloat(chaserY, originalY)
        jumpBackAnimator.duration = jumpDuration
        jumpBackAnimator.interpolator = AccelerateDecelerateInterpolator()
        jumpBackAnimator.addUpdateListener { valueAnimator ->
            chaserY = valueAnimator.animatedValue as Float
            invalidate()
        }
        jumpBackAnimator.start()
    }


    private fun gameover() {
        if (gameover) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("GAMEOVER")
            builder.setMessage("SCORE: $score")
            builder.setPositiveButton("OK") { dialog, _ ->

                Toast.makeText(context, "GAMEOVER", Toast.LENGTH_SHORT).show()

                dialog.dismiss()
                // Handle positive button click

            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                // Handle negative button click
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()


        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        bitmap1 = BitmapFactory.decodeResource(resources, R.drawable.ball_player)
        bitmap2 = BitmapFactory.decodeResource(resources, R.drawable.obstacle)
        bitmap3 = BitmapFactory.decodeResource(resources, R.drawable.dogobstacle1)
        bitmap4 = BitmapFactory.decodeResource(resources, R.drawable.cutedragon)
        background = BitmapFactory.decodeResource(resources, R.drawable.bgsc)
        val rect = Rect(0, 0, measuredWidth, measuredHeight)



        canvas?.drawBitmap(background, null, rect, null)


        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = context.display
            display?.getRealMetrics(displayMetrics)
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        val screenWidth = displayMetrics.widthPixels
        Log.d("screenwisth","$screenWidth")
        val screenHeight = displayMetrics.heightPixels
        // Load the original bitmap from a resource or file


// Calculate the new width and height
        val newWidth = bitmap1.width / 2
        val newHeight = bitmap1.height / 2

// Create the resized bitmap
        val resizedBitmap1: Bitmap = Bitmap.createScaledBitmap(bitmap1, newWidth, newHeight, true)
        // Load the original bitmap from a resource or file


// Calculate the new width and height
        val newWidth2 = bitmap2.width / 4
        val newHeight2 = bitmap2.height / 4

// Create the resized bitmap
        val resizedBitmap2: Bitmap = Bitmap.createScaledBitmap(bitmap2, newWidth2, newHeight2, true)

        val newWidth3 = bitmap3.width / 4
        val newHeight3 = bitmap3.height / 4

// Create the resized bitmap
        val resizedBitmap3: Bitmap = Bitmap.createScaledBitmap(bitmap3, newWidth3, newHeight3, true)
        val newWidth4 = bitmap4.width / 3
        val newHeight4 = bitmap4.height / 3

// Create the resized bitmap
        val resizedBitmap4: Bitmap = Bitmap.createScaledBitmap(bitmap4, newWidth4, newHeight4, true)
        var bitmaparray = arrayOf(resizedBitmap2, resizedBitmap3)
        var size = bitmaparray.size


















        if (!isCollisionDetected) {
            playerX += vX
            obstacleX -= 40f
            chaserX += 3f
            bitmaptodraw = bitmaparray[currentbitmap]

            // 0 or 1 for two types of bitmaps
            // Generate random coordinates for drawing
            if (obstacleX + bitmaptodraw.width <= 0) {

                currentbitmap = (0 until size).random()
                obstacleX = width.toFloat()
                obstacleY = 500f

                if (p == 1) {
                    isDecrementDone = false
                }
                nextObstacleDelay = generateNextObstacleDelay()
                handler.postDelayed({ invalidate() }, nextObstacleDelay)
            } else {
                val proximityThreshold = 100 // Adjust the threshold as needed
                val isObstacleInProximity =
                    obstacleX + bitmaptodraw.width >= chaserX - proximityThreshold && obstacleX <= chaserX + resizedBitmap4.width + proximityThreshold && chaserY == obstacleY

                // Start the chaser jump if the obstacle is in proximity
                if (isObstacleInProximity) {

                    jumpChaser()

                }
                invalidate()


            }
        }
            /*if (playerX + bitmaptodraw.width >= obstacleX && playerX <= obstacleX + bitmaptodraw.width && playerY == obstacleY) {
            isCollisionDetected = true


            if (!isDecrementDone) {  // Check if decrement is not done yet
                p--
                isDecrementDone = true
                Log.d("tag", "$p")
                 // Set the flag to true to indicate decrement is done
            }

            if (p == 0) {
                gameover = true
                vX = 0f
                gameover()
                /*intent=Intent(activityContext,hey::class.java)
                activityContext.startActivity(intent)*/



            } else if (p == 1) {
                vX = 3f
                gameover()


                // Reset the flag for the next collision
                invalidate()
            }


        }*/
            bitmaptodraw = bitmaparray[currentbitmap]
            /*if (playerX + bitmaptodraw.width >= obstacleX && playerX <= obstacleX + bitmaptodraw.width && playerY == obstacleY) {
            isCollisionDetected = false
            if (!isDecrementDone) {  // Check if decrement is not done yet
                p--
                Log.d("tag", "$p")
                isDecrementDone = true  // Set the flag to true to indicate decrement is done
            }

            if (p == 0) {
                gameover = true
                vX = 0f
                gameover()

                /*intent=Intent(activityContext,hey::class.java)
                activityContext.startActivity(intent)*/



            } else if (p == 1) {
                vX = 3f




                // Reset the flag for the next collision
                invalidate()
            }
            isDecrementDone = true


        }*/


            // Check collision with obstacle
            /*if (obstacleX + bitmaptodraw.width <= 0) {
                score += 10
                currentBitmapIndex = (bitmaparray.indices).random()



                // Reset obstacle position and generate random delay for the next obstacle
                obstacleX = width.toFloat()
                obstacleY = 100f
                nextObstacleDelay = generateNextObstacleDelay()
                handler.postDelayed({ invalidate() }, nextObstacleDelay)
            }
            bitmaptodraw = bitmaparray[currentBitmapIndex]*/


            /*if( chaserX+resizedBitmap4.width>= obstacleX+resizedBitmap2.width){
            jumpchaser()
        }*/

            invalidate()




            bitmaptodraw = bitmaparray[currentbitmap]
        if (playerX + bitmaptodraw.width >= obstacleX && playerX <= obstacleX + bitmaptodraw.width && playerY == obstacleY) {

            if (!isDecrementDone) {
                p--// Check if decrement is not done yet

                collisioncount++



                isDecrementDone = true  // Set the flag to true to indicate decrement is done
            }





            if (collisioncount == 2) {
                gameover = true
                vX = 0f
                isCollisionDetected = true
                Log.d("gameover","$gameover")
                if(dialogshown==false){
                    gameover()
                    dialogshown=true
                }








                /*intent=Intent(activityContext,hey::class.java)
                activityContext.startActivity(intent)*/


            } else if (collisioncount == 1) {
                vX = 3f



            }


            // Reset the flag for the next collision
            invalidate()
        }
        Log.d("screen width","$screenWidth")
        if(playerX==screenWidth.toFloat()){
            gameover()
        }

















        invalidate()
            /*val proximityThreshold = 1 // Adjust the threshold as needed
            val isObstacleInProximity = obstacleX + bitmaptodraw.width >= chaserX - proximityThreshold && obstacleX <= chaserX + resizedBitmap4.width + proximityThreshold && chaserY== obstacleY

            // Start the chaser jump if the obstacle is in proximity
            if (isObstacleInProximity) {
                if (!isChaserJumping) {

                    jumpChaser()
                }
            }*/









            canvas?.drawBitmap(resizedBitmap1, playerX, playerY, null)
            canvas?.drawBitmap(bitmaptodraw, obstacleX, obstacleY, null)
            canvas?.drawBitmap(resizedBitmap4, chaserX, chaserY, null)































            invalidate()



        }



        override fun onTouchEvent(event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {

                    if (!isJumping) {
                        jump()
                        Log.d("playerX1","$playerX")
                        Log.d("+++++++score++++","$score")
                        Log.d("ObstacleX","$obstacleX")


                    }

                }
            }
            return super.onTouchEvent(event)
        }


    }














