package com.bobby.nfccardscanner

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var techListsArray: Array<Array<String>>
    private lateinit var intentFiltersArray: Array<IntentFilter>
    private lateinit var pendingIntent: PendingIntent
    private var mNFCAdapter : NfcAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initNFC()
    }

    private fun initNFC() {
        mNFCAdapter = NfcAdapter.getDefaultAdapter(this)

        if (mNFCAdapter == null) {
            showToast("NFC Hardware not available on Device")
        } else if (!mNFCAdapter!!.isEnabled) {
            showToast("NFC is NOT Enabled, Please Enable NFC")
        }

        //Intent to be passed into pending intent
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        //Pending Intent that brings this activity up when card is scanned
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        //IntentFilters Array that basically listen for whether a card is tapped
        // regardless of whether it has a string extra
        intentFiltersArray = arrayOf(IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED))

        //All tags including MIFARE Classic will be detected as NfcA on all devices
        techListsArray = arrayOf(arrayOf(NfcA::class.java.name))
    }

    public override fun onPause() {
        super.onPause()
        mNFCAdapter?.disableForegroundDispatch(this)
    }

    public override fun onResume() {
        super.onResume()
        mNFCAdapter?.enableForegroundDispatch(this,pendingIntent,intentFiltersArray,techListsArray)
    }

    public override fun onNewIntent(intent: Intent) {

        val tagFromIntent: Tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)!!
        //Convert id (ByteArray) to a HexString and display the string
        val retrievedTag = tagFromIntent.id.toHexString()
        showToast(retrievedTag)
        tagTV.text = StringBuilder("Retrieved Tag: ").append(retrievedTag)
        super.onNewIntent(intent)
    }

    //Extension Method to convert the ByteArray to a HexString
    fun ByteArray.toHexString() : String {
        return this.joinToString("") {
            java.lang.String.format("%02x", it).toUpperCase(Locale.ROOT)
        }
    }

    private fun showToast(message: String){
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
    }
}