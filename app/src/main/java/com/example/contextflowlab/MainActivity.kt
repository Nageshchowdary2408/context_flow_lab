package com.example.contextflowlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.contextflowlab.ui.theme.ContextFlowLabTheme

data class Message(val text: String, val isUser: Boolean)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ContextFlowLabTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatScreen()
                }
            }
        }
    }
}

@Composable
fun ChatScreen() {
    var messages by remember { 
        mutableStateOf(listOf(Message("Hello! I'm your lab assistant. Try asking me about 'Android' or 'Kotlin'.", false))) 
    }
    var inputText by remember { mutableStateOf("") }
    var lastTopic by remember { mutableStateOf<String?>(null) }

    fun getBotResponse(userText: String): String {
        val text = userText.lowercase()
        
        // 1. Context Resolution & Follow-up (using memory)
        if ((text.contains("it") || text.contains("that") || text.contains("more")) && lastTopic != null) {
            return when (lastTopic) {
                "android" -> "Android is an open-source operating system for mobile devices. Would you like to know about its architecture?"
                "kotlin" -> "Kotlin is a modern programming language that makes developers happier. It's fully interoperable with Java!"
                else -> "I remember we were talking about $lastTopic. Could you be more specific?"
            }
        }

        // 2. Topic Switching & Memory
        return when {
            text.contains("android") -> {
                lastTopic = "android"
                "Android is great! It uses the Linux kernel. Do you want to know about its history?"
            }
            text.contains("kotlin") -> {
                lastTopic = "kotlin"
                "Kotlin is my favorite! It's concise and safe. Want to hear about Coroutines?"
            }
            text.contains("weather") -> {
                val transition = if (lastTopic != null) "A big change from talking about $lastTopic! " else ""
                lastTopic = "weather"
                "${transition}The weather is sunny in the digital world!"
            }
            else -> "That's interesting! Tell me more, or ask me about Android or Kotlin."
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).systemBarsPadding()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth().imePadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = {
                if (inputText.isNotBlank()) {
                    val userMsg = Message(inputText, true)
                    val response = getBotResponse(inputText)
                    messages = messages + userMsg + Message(response, false)
                    inputText = ""
                }
            }) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val color = if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
    val textColor = if (message.isUser) Color.White else MaterialTheme.colorScheme.onSecondaryContainer

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Surface(
            color = color,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = message.text,
                color = textColor,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
