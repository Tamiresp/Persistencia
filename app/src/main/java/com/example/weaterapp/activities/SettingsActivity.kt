package com.example.weaterapp.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.weaterapp.R
import com.example.weaterapp.utils.Constants.ISC
import com.example.weaterapp.utils.Constants.ISEN
import com.example.weaterapp.utils.Constants.PREF
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {
    private val sp: SharedPreferences by lazy {
        getSharedPreferences(PREF, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        update()

        btn_save_settings.setOnClickListener {
            saveSp()
        }

        supportActionBar?.title = getString(R.string.settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    private fun update(){
        val temp = sp.getBoolean(ISC, true)
        val lan = sp.getBoolean(ISEN, true)

        radioGroupTemp.check(if (temp) R.id.radioC else R.id.radioButtonF)
        radioGroupLin.check(if (lan) R.id.radioButtonEn else R.id.radioButtonPt)
    }

    private fun saveSp(){
        sp.edit{
            putBoolean(ISC, radioC.isChecked)
            putBoolean(ISEN, radioButtonEn.isChecked)
        }
        Snackbar.make(findViewById(R.id.settings_layout), R.string.save_settings, Snackbar.LENGTH_LONG).show()
        finish()
    }
}
