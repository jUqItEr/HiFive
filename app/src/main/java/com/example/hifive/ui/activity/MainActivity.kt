package com.example.hifive.ui.activity

import android.content.Intent
import android.os.Bundle
import com.example.hifive.databinding.ActivityMainBinding
import com.example.hifive.ui.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initComponents()
    }

    private fun initComponents() = with(binding) {
        logoutButton.setOnClickListener{
            //logout()
            finish()
        }
        button1.setOnClickListener{
            val intent: Intent = Intent(this@MainActivity, MonthlyListActivity::class.java)
            startActivity(intent)
        }
        button2.setOnClickListener{

        }
    }

    companion object {
        private const val tag = "MainActivity"
    }
}