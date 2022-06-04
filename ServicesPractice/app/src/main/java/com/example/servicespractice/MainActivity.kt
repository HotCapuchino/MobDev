package com.example.servicespractice

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.servicespractice.broadcast.CryptoBroadcastReceiver
import com.example.servicespractice.dataclasses.CryptoCollection
import com.example.servicespractice.dataclasses.FiatCollection
import com.example.servicespractice.dataclasses.TrackingList
import com.example.servicespractice.dataclasses.TrackingListItem
import com.example.servicespractice.recyclerview.PriceTrackingAdapter
import com.example.servicespractice.service.PriceService
import com.example.servicespractice.viewmodel.MainViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

enum class SpinnerType {
    CRYPTO,
    FIAT
}

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    lateinit var trackingBtn: Button
    private lateinit var cryptoSpinner: Spinner
    private lateinit var fiatSpinner: Spinner
    private val gson = Gson()
    private lateinit var desiredPriceEditText: EditText
    private lateinit var cryptos: CryptoCollection
    lateinit var fiats: FiatCollection
    private val mainViewModel: MainViewModel by viewModels()
    lateinit var cryptoRecycler: RecyclerView
    private lateinit var recyclerAdapter: PriceTrackingAdapter
    private lateinit var cryptoBroadcastReceiver: CryptoBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
        initAdapter()
        initBroadcastReceiver()

        desiredPriceEditText.addTextChangedListener(textChangedListener)

        initSpinner(SpinnerType.CRYPTO)
        initSpinner(SpinnerType.FIAT)
        initObserving()

        PriceService.startService(this, mainViewModel.cryptoTicker.value!!, mainViewModel.fiatTicker.value!!)
    }

    private fun initUI() {
        trackingBtn = findViewById(R.id.track_btn)
        cryptoSpinner = findViewById(R.id.crypto_spinner)
        fiatSpinner = findViewById(R.id.fiat_spinner)
        desiredPriceEditText = findViewById(R.id.desired_price)
        cryptoRecycler = findViewById(R.id.tracking_cryptos)
    }

    private fun initAdapter() {
        mainViewModel.trackingList.value = TrackingList(mutableListOf())
        recyclerAdapter = PriceTrackingAdapter(mainViewModel.trackingList.value!!, ::removePositionFromTracking)
        cryptoRecycler.layoutManager = LinearLayoutManager(this)
        cryptoRecycler.adapter = recyclerAdapter
    }

    private fun initBroadcastReceiver() {
        cryptoBroadcastReceiver = CryptoBroadcastReceiver(::currentPriceCallback, ::priceTargetCallback)
        registerReceiver(cryptoBroadcastReceiver, IntentFilter().apply {
            addAction(PriceService.CURRENT_PRICE_INTENT)
            addAction(PriceService.TARGET_PRICE_INTENT)
        })
    }

    private val textChangedListener = object: TextWatcher {
        override fun beforeTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            // empty
        }

        override fun onTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {
            changeTrackingBtnState(text.toString())
        }

        override fun afterTextChanged(editText: Editable?) {
            // empty
        }
    }

    private fun removePositionFromTracking(position: Int) {
        val (cryptoTicker, fiatTicker, price) = mainViewModel.trackingList.value!!.trackingList[position]
        if (mainViewModel.removeTrackingPosition(position)) {
            PriceService.startService(this, cryptoTicker, fiatTicker, price)
            changeTrackingBtnState(desiredPriceEditText.text.toString())
        }
    }

    private fun initObserving() {
        mainViewModel.cryptoPrice.observe(this) {
            findViewById<TextView>(R.id.crypto_price).text = it
        }

        mainViewModel.trackingList.observe(this) {
            recyclerAdapter.notifyDataSetChanged()
        }
    }

    private fun checkBtnAvailability(): Boolean {
        return !(mainViewModel.cryptoTicker.value == null || mainViewModel.fiatTicker.value == null || desiredPriceEditText.text.toString().isEmpty() || desiredPriceEditText.text.toString().isBlank())
    }

    fun changeTrackingBtnState(text: String) {
        trackingBtn.isEnabled = checkBtnAvailability()
        if (text.isNotBlank() && text.isNotEmpty()) {
            val trackingPosition = mainViewModel.checkIfCryptoToFiatIsTracking(text)
            if (trackingPosition >= 0) {
                trackingBtn.text = "Перестать отслеживать"
                return
            }
        }
        trackingBtn.text = "Отслеживать"
    }

    private fun currentPriceCallback(price: String) {
        mainViewModel.cryptoPrice.value = price
    }

    private fun priceTargetCallback(trackingListItem: TrackingListItem) {
        val trackingPosition = mainViewModel.checkIfCryptoToFiatIsTracking(trackingListItem)
        if (trackingPosition >= 0) {
            if (mainViewModel.removeTrackingPosition(trackingPosition)) {
                changeTrackingBtnState(desiredPriceEditText.text.toString())
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun initSpinner(type: SpinnerType) {
        val resourceId = if (type == SpinnerType.CRYPTO) R.raw.crypto else R.raw.fiat
        val stream = resources.openRawResource(resourceId)

        val targetSpinner: Spinner

        if (type == SpinnerType.CRYPTO) {
            cryptos = gson.fromJson(InputStreamReader(stream), CryptoCollection::class.java)
            targetSpinner = cryptoSpinner
            targetSpinner.adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, cryptos.cryptos.map { it.name })
            mainViewModel.cryptoTicker.value = cryptos.cryptos[0].ticker
        } else {
            fiats = gson.fromJson(InputStreamReader(stream), FiatCollection::class.java)
            targetSpinner = fiatSpinner
            targetSpinner.adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, fiats.fiats.map { it.name })
            mainViewModel.fiatTicker.value = fiats.fiats[0].ticker
        }

        targetSpinner.onItemSelectedListener = this
    }

    fun onTrackingBtnClick(v: View) {
        val trackingPosition = mainViewModel.checkIfCryptoToFiatIsTracking(desiredPriceEditText.text.toString())
        if (trackingPosition >= 0) {
            removePositionFromTracking(trackingPosition)
        } else {
            mainViewModel.addTrackingPosition(desiredPriceEditText.text.toString())
            PriceService.startService(this, mainViewModel.cryptoTicker.value!!, mainViewModel.fiatTicker.value!!, desiredPriceEditText.text.toString())
        }
        changeTrackingBtnState(desiredPriceEditText.text.toString())
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val usedSpinner = parent as Spinner

        if (usedSpinner.id == cryptoSpinner.id) {
            mainViewModel.cryptoTicker.value = cryptos.cryptos[position].ticker
            changeTrackingBtnState(desiredPriceEditText.text.toString())
        } else {
            mainViewModel.fiatTicker.value = fiats.fiats[position].ticker
            changeTrackingBtnState(desiredPriceEditText.text.toString())
        }

        PriceService.startService(this, mainViewModel.cryptoTicker.value!!, mainViewModel.fiatTicker.value!!)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        // nothing here
    }

    override fun onRestart() {
        PriceService.startService(this, mainViewModel.cryptoTicker.value!!, mainViewModel.fiatTicker.value!!)
        super.onRestart()
    }

    override fun onDestroy() {
        PriceService.stopService(this)
        super.onDestroy()
    }
}