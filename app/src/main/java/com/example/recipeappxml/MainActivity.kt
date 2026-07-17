package com.example.recipeappxml

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.recipeappxml.databinding.ActivityMainBinding
import com.example.recipeappxml.ui.CategoriesListFragment
import com.example.recipeappxml.ui.FavoritesFragment

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.mainContainer, CategoriesListFragment())
                .commit()
        }

        binding.categoriesButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, CategoriesListFragment())
                .commit()
        }

        binding.favoritesButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, FavoritesFragment())
                .commit()
        }

    }
}
