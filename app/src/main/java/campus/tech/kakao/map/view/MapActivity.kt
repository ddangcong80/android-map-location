package campus.tech.kakao.map.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import campus.tech.kakao.map.BuildConfig
import campus.tech.kakao.map.R
import campus.tech.kakao.map.model.PlaceInfo
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles

class MapActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var searchFloatingBtn: ExtendedFloatingActionButton
    private lateinit var clickedPlaceNameView: TextView
    private lateinit var clickedPlaceAddressView: TextView
    private lateinit var clickedPlaceView: LinearLayoutCompat
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var KAKAO_APP_KEY: String
    private lateinit var kakaoMap: KakaoMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        KAKAO_APP_KEY = BuildConfig.KAKAO_APP_KEY
        KakaoMapSdk.init(this, KAKAO_APP_KEY)

        initView()
        setListeners()
        initializeMap()
    }

    private fun initView() {
        mapView = findViewById(R.id.mapView)
        searchFloatingBtn = findViewById(R.id.searchFloatingBtn)
        clickedPlaceNameView = findViewById(R.id.clickedPlaceName)
        clickedPlaceAddressView = findViewById(R.id.clickedPlaceAddress)
        clickedPlaceView = findViewById(R.id.clickedPlaceView)
        bottomSheetBehavior = BottomSheetBehavior.from(clickedPlaceView)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
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
                kakaoMap = map
                handleIntent()
            }
        })
    }

    private fun handleIntent() {
        val clickedPlaceInfo = intent.getParcelableExtra<PlaceInfo>("placeInfo")
        clickedPlaceInfo.let {
            if (it != null) {
                val pos: LatLng = LatLng.from(it.y.toDouble(), it.x.toDouble())
                showClickedPlaceInfo(it.place_name, it.road_address_name)
                showLabel(pos.latitude, pos.longitude)
                moveClickedPlace(pos.latitude, pos.longitude)
            }
        }
    }

    private fun showClickedPlaceInfo(clickedPlaceName: String?, clickedPlaceAddress: String?) {
        clickedPlaceNameView.text = clickedPlaceName
        clickedPlaceAddressView.text = clickedPlaceAddress
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun showLabel(latitude: Double, longitude: Double) {
        val styles: LabelStyles? = kakaoMap.labelManager
            ?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.pink_marker)))
        val options: LabelOptions =
            LabelOptions.from(LatLng.from(latitude, longitude)).setStyles(styles)
        val layer: LabelLayer? = kakaoMap.labelManager?.layer
        layer?.addLabel(options)
    }

    private fun moveClickedPlace(latitude: Double, longitude: Double) {
        kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(LatLng.from(latitude, longitude)))
    }
}