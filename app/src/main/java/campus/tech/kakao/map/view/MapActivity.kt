package campus.tech.kakao.map.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import campus.tech.kakao.map.BuildConfig
import campus.tech.kakao.map.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView


class MapActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var searchFloatingBtn: ExtendedFloatingActionButton
    private lateinit var clickedPlaceNameView: TextView
    private lateinit var clickedPlaceAddressView: TextView
    private lateinit var clickedPlaceView: LinearLayoutCompat
    private lateinit var KAKAO_APP_KEY: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        KAKAO_APP_KEY = BuildConfig.KAKAO_APP_KEY
        KakaoMapSdk.init(this, KAKAO_APP_KEY)

        initView()
        setListeners()
        initializeMap()
        handleIntent()
    }

    private fun initView() {
        mapView = findViewById(R.id.mapView)
        searchFloatingBtn = findViewById(R.id.searchFloatingBtn)
        clickedPlaceNameView = findViewById(R.id.clickedPlaceName)
        clickedPlaceAddressView = findViewById(R.id.clickedPlaceAddress)
        clickedPlaceView = findViewById(R.id.clickedPlaceView)
    }

    private fun setListeners() {
        searchFloatingBtn.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initializeMap() {
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.d("KakaoMap", "onMapDestroy")
            }

            override fun onMapError(error: Exception) {
                Log.e("KakaoMap", "onMapError: ", error)
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                Log.d("KakaoMap", "onMapReady")
            }
        })
    }

    private fun handleIntent() {
        val clickedPlaceName = intent.getStringExtra("clickPlaceName")
        val clickedPlaceAddress = intent.getStringExtra("clickPlaceAddress")

        if (clickedPlaceName != null && clickedPlaceAddress != null) {
            showClickedPlaceInfo(clickedPlaceName, clickedPlaceAddress)
        }
    }

    private fun showClickedPlaceInfo(clickedPlaceName: String?, clickedPlaceAddress: String?) {
        clickedPlaceNameView.text = clickedPlaceName
        clickedPlaceAddressView.text = clickedPlaceAddress
    }
}
