package com.example.morseralphabeta

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

import android.view.Menu
import android.view.MenuItem
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // buttons for playing message in morse code
        val ButtonPlay = findViewById<Button>(R.id.button_play)
        val ButtonPause = findViewById<Button>(R.id.button_pause)

        ButtonPlay.isEnabled = false
        ButtonPause.isEnabled = false

        // play button pressed -> repeat decoded message in morse code
        ButtonPlay.setOnClickListener {
            ButtonPlay.isEnabled = false
            ButtonPause.isEnabled = true

            // coroutine for non blocking action
            val job = GlobalScope.launch(context = Dispatchers.Main) {

                // loop goes trough decoded message
                for (x in text_result.text.toString()) {
                    if (x.equals('.')) {
                        audioReapeater(2646, 60)
                        delay(120)
                    } else if (x.equals('-')) {
                        audioReapeater(7938, 180)
                        delay(240)
                    } else {
                        delay(180)
                    }
                }
                ButtonPlay.isEnabled = true
                ButtonPause.isEnabled = false
            }
            // pause button cancels coroutine -> stops playing deooded message
            ButtonPause.setOnClickListener() {
                job.cancel()
                ButtonPlay.isEnabled = true
                ButtonPause.isEnabled = false
            }
        }
    }
    // gets user input from activity_main
    fun getInput(v: View) {

        val textInput = input_main.text.toString().toLowerCase()
        val textResult = findViewById<TextView>(R.id.text_result)
        var result = ""

        // dududu
        if (textInput.equals("dududu")) {
            // new activity opens a youtube video
            var url = Intent(android.content.Intent.ACTION_VIEW)
            url.data = Uri.parse("https://www.youtube.com/watch?v=y6120QOlsfU")
            startActivity(url)

            // empty user input -> Error message
        } else if (textInput.trim().equals("")) {
            input_main.setError("Enter message")

            // checks if user input contains only text
        } else if (checkInput(textInput).equals("text")) {
            result = decoder(textInput, type = true)
            findViewById<Button>(R.id.button_play).isEnabled = true

            // checks if user input contains only morse code
        } else if (checkInput(textInput).equals("morse")) {
            result = decoder(textInput, type = false)
            findViewById<Button>(R.id.button_play).isEnabled = false

            // check if user input is something else than text or morse code
        } else if (checkInput(textInput).equals("falseinput")) {
            input_main.setError("Only text or morse code")
        }
        textResult.text = result
    }

    // clears user input and program output
    fun clear(v: View) {
        findViewById<TextView>(R.id.text_result).text = ""
        input_main.text.clear()
        findViewById<Button>(R.id.button_play).isEnabled = false
    }

    // decodes user input
    private fun decoder(input: String, type: Boolean): String {
        var result = ""

        // if user input = text -> deoceds text to morse code
        if (type) {
            for (x in input.withIndex()) {
                for (y in Characters().letters.withIndex()) {
                    if (Characters().letters[y.index] == input.get(x.index)) {
                        result = result + (Characters().morse_characters[y.index] + " ")
                    }
                }
            }
        // if user input = morse -> deoodes morse code to text
        } else {
            // input to list
            val input_list = input.split(" ".toRegex())

            for (x in input_list.withIndex()) {
                for (y in Characters().morse_characters.withIndex()) {
                    if (Characters().morse_characters[y.index].equals(input_list.get(x.index))) {
                        result = result + (Characters().letters[y.index])
                    }
                }
            }
        }
        return result
    }

    // checks user input
    fun checkInput(input: String): String {

        // letter from A-Ö and numbers
        val regex = """^[a-zåäö0-9]*$""".toRegex()

        // characters . - /
        val mRegex = """^[-/.]*$""".toRegex()

        // removes whitespace
        val inputx = input.replace("\\s".toRegex(), "")

        if (regex.matches(inputx)) {
            return "text"
        } else if (mRegex.matches(inputx)) {
            return "morse"
        } else {
            Toast.makeText(this@MainActivity, "Only letters or morse code", Toast.LENGTH_SHORT).show()
            return "falseinput"
        }

    }
    // Help menu button
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_info, menu)
        return true
    }

    // Help button pressed -> start Help activity
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val intent = Intent(this, Help::class.java)
        startActivity(intent)
        return super.onOptionsItemSelected(item)
    }

}

