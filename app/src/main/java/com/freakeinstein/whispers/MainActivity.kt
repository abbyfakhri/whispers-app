package com.freakeinstein.whispers

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.freakeinstein.whispers.api.ApiConfig
import com.freakeinstein.whispers.api.ApiResponse
import com.freakeinstein.whispers.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var encryptedMessage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        hideResult()

        binding.listenBtn.setOnClickListener {
            showAfterListen()
            getData()

            /*
            val decrypted = xorDecipher(binaryToString(message), key)
            println("Decrypted message: $decrypted")

             */
        }

        binding.enterCodeBtn.setOnClickListener {
            showAfterEnterCode()
            decryptMessage(encryptedMessage)
        }
    }

    private fun decryptMessage(message:String){
        var key = binding.keyField.text.toString()
        var decryptedMessage = message

        binding.actualMessage.text = xorCipher(binaryToString(decryptedMessage),key)
    }

    private fun getData(){

        var data:String

        val client = ApiConfig.getApiService().getDataFromServer()
        client.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(
                call: Call<ApiResponse>,
                response: Response<ApiResponse>
            ) {
                if (response.isSuccessful) {
                    data = response.body()?.message.toString()
                    encryptedMessage = data
                    binding.messageTv.text = binaryToString(data)


                } else {
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }

            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
                binding.messageTv.text = "can't connect to server"
            }
        })



    }

    private fun showAfterEnterCode() {
        binding.actualMessage.visibility = View.VISIBLE
        binding.resultTitle.visibility = View.VISIBLE
        binding.actualMessage.visibility = View.VISIBLE

    }

    private fun showAfterListen() {
        binding.keyField.visibility = View.VISIBLE
        binding.messageTv.visibility = View.VISIBLE
        binding.subtitle.visibility = View.VISIBLE
        binding.enterCodeBtn.visibility = View.VISIBLE

    }


    private fun hideResult(){
        binding.actualMessage.visibility = View.GONE
        binding.resultTitle.visibility = View.GONE
        binding.actualMessage.visibility = View.GONE
        binding.enterCodeBtn.visibility = View.GONE
        binding.keyField.visibility = View.GONE
        binding.messageTv.visibility = View.GONE
        binding.subtitle.visibility = View.GONE
    }

    fun xorCipher(message: String, key: String): String {
        val encryptedMessage = StringBuilder()

        for (i in message.indices) {
            val charCode = message[i].toInt()
            val keyChar = key[i % key.length].toInt()

            val encryptedChar = charCode.xor(keyChar)
            encryptedMessage.append(encryptedChar.toChar())
        }

        return encryptedMessage.toString()
    }

    fun xorDecipher(encryptedMessage: String, key: String): String {
        val decryptedMessage = StringBuilder()

        for (i in encryptedMessage.indices) {
            val charCode = encryptedMessage[i].toInt()
            val keyChar = key[i % key.length].toInt()

            val decryptedChar = charCode.xor(keyChar)
            decryptedMessage.append(decryptedChar.toChar())
        }

        return decryptedMessage.toString()
    }


    fun stringToBinary(string: String): String {
        val binaryBuilder = StringBuilder()

        val bytes = string.toByteArray()
        for (byte in bytes) {
            val binaryString = String.format("%8s", byte.toUByte().toString(2)).replace(' ', '0')
            binaryBuilder.append(binaryString).append(" ")
        }

        return binaryBuilder.toString().trim()
    }

    fun binaryToString(binary: String): String {
        val string = StringBuilder()
        val binaryChunks = binary.split(" ")

        for (chunk in binaryChunks) {
            val decimal = chunk.toInt(2) // Convert binary chunk to decimal
            string.append(decimal.toChar()) // Convert decimal to character
        }

        return string.toString()
    }
}