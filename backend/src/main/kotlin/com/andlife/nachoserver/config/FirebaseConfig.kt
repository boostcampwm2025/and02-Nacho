package com.andlife.nachoserver.config

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.auth.oauth2.GoogleCredentials
import org.springframework.context.annotation.Configuration
import jakarta.annotation.PostConstruct
import java.io.FileInputStream

@Configuration
class FirebaseConfig {

    @PostConstruct
    fun initialize() {
        val serviceAccount = FileInputStream("serviceAccountKey.json")

        val options: FirebaseOptions = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        FirebaseApp.initializeApp(options)

    }
}