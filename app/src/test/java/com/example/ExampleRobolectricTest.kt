package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppLanguage
import com.example.data.ChatMessage
import com.example.data.MessageSender
import com.example.data.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ShivAI", appName)
    }

    @Test
    fun `verify user profile defaults and language support`() {
        val profile = UserProfile()
        assertEquals("Shivraj", profile.name)
        assertEquals("shiv_user", profile.username)
        assertEquals(6000, profile.dailyGoal)
        assertEquals(AppLanguage.MARATHI, profile.language)
        assertTrue(profile.isAiEnabled)
    }

    @Test
    fun `verify chat message creation`() {
        val message = ChatMessage(
            sender = MessageSender.SHIVAI,
            text = "नमस्कार! मी तुमचा ShivAI असिस्टंट आहे."
        )
        assertNotNull(message.id)
        assertEquals(MessageSender.SHIVAI, message.sender)
        assertTrue(message.text.contains("ShivAI"))
    }
}
