package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import com.example.proyecto.databinding.ActivityPerfilBinding

class perfil : androidx.appcompat.app.AppCompatActivity() {
    private lateinit var binding: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Add your own logic for retrieving user data and displaying it in the UI
        val username = "John Doe"
        val email = "johndoe@example.com"

        binding.username.text = username
        binding.email.text = email

        // Set up click listener for edit profile button
        binding.editProfileButton.setOnClickListener {
            // TODO: Start activity for editing user profile
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            // For example: val intent = Intent(this, EditProfileActivity::class.java)
        }

        // Set up click listener for logout button
        binding.logoutButton.setOnClickListener {
            // TODO: Add your own logic for logging out the user
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}