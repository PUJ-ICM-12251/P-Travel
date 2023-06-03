package com.example.proyecto

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds

import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.TravelMode

import kotlinx.coroutines.*
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale


class MapActivity: AppCompatActivity() , OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val RC_LOCATION_PERMISSION = 123

    private lateinit var currentLangTan: LatLng

    private lateinit var start:String
    private lateinit var end:String

    private lateinit var calculateBtn: Button
    private var location = LatLng(4.6097, -74.0817)
    /*
    private inner class MapAsyncTask : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {




            return null
        }

        override fun onPostExecute(result: Void?) {
            runOnUiThread {
                Toast.makeText(applicationContext, "Mensaje de actualización", Toast.LENGTH_SHORT).show()
            }
        }
    }
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)


       // val mapAsyncTask = MapAsyncTask()
        //mapAsyncTask.execute()



        calculateBtn = findViewById(R.id.CalculateButton)

        calculateBtn.setOnClickListener{
            start =""
            end=""

            if(::googleMap.isInitialized){
                googleMap.setOnMapClickListener{
                    if(start.isEmpty()){
                        start = "${it.longitude}, ${it.latitude}"
                    }else if(end.isEmpty()){
                        end = "${it.longitude}, ${it.latitude}"
                        createRoute()
                    }
                }
            }
        }

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RC_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso de ubicación concedido
                    // Aquí puedes realizar acciones relacionadas con la ubicación
                    obtenerUbicacionActual(googleMap)
                } else {
                    // Permiso de ubicación denegado
                    // Aquí puedes manejar la negación del permiso, mostrar un mensaje, etc.
                }
            }
        }
    }


    private fun obtenerUbicacionActual(mMap: GoogleMap) {

        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        // Aquí tienes la ubicación actual en el objeto 'location'
                        val latitude = location.latitude
                        val longitude = location.longitude

                        // Haz lo que necesites con la ubicación actual
                        // Por ejemplo, puedes mover la cámara del mapa a la ubicación actual
                        val currentLatLng = LatLng(latitude, longitude)
                        mMap.addMarker(MarkerOptions().position(currentLatLng).title("ORIGEN").snippet("Ubicacion Actual del Usuario")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
                        mMap?.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))

                        currentLangTan = currentLatLng
                    }
                }
                .addOnFailureListener { exception: Exception ->
                    // Maneja el error al obtener la ubicación actual
                }
        } else {
            // Solicita los permisos de ubicación al usuario
            EasyPermissions.requestPermissions(
                this,
                "Se requiere permiso de ubicación",
                RC_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    fun obtenerDestinosTuristicos( location: LatLng) {

        CoroutineScope(Dispatchers.IO).launch{
            val placesClient = Places.createClient(this@MapActivity)

            val radius = 5000 // Radio de búsqueda en metros
            val keyword = "lugares iconicos de bogota" // Palabra clave para la búsqueda de lugares turísticos

            val request = FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setLocationBias(RectangularBounds.newInstance(
                    LatLng(location.latitude - 0.75, location.longitude - 0.75),
                    LatLng(location.latitude + 0.05, location.longitude + 0.75)
                ))
                .setQuery(keyword)
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                    for (prediction: AutocompletePrediction in response.autocompletePredictions) {
                        val placeId = prediction.placeId
                        obtenerDetallesDestinoTuristico(this@MapActivity, placesClient, placeId)
                    }
                }
                .addOnFailureListener { exception: Exception ->
                    // Maneja cualquier error
                }
        }

    }

    fun obtenerDetallesDestinoTuristico(context: Context,placesClient: PlacesClient, placeId: String) {


            val destinoTuristicoDatabaseHelper = DestinoTuristicoDatabaseHelper(context)
            val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)
            val request = FetchPlaceRequest.builder(placeId, placeFields).build()

            placesClient.fetchPlace(request)
                .addOnSuccessListener { response: FetchPlaceResponse ->
                    val place = response.place
                    // Accede a los datos del lugar, como el nombre, dirección, etc.
                    val name = place.name
                    val address = place.address
                    // ...

                    // Aquí puedes almacenar los datos en la base de datos local

                    destinoTuristicoDatabaseHelper.insertDestinoTuristico(name, address)
                }
                .addOnFailureListener { exception: Exception ->
                    // Maneja cualquier error
                }

    }


    override fun onMapReady(map: GoogleMap) {

        googleMap = map
         // Coordenadas de Bogotá
        runOnUiThread{
            Places.initialize(applicationContext, getString(R.string.my_places_api_key))
            obtenerDestinosTuristicos(location)
            printPoints()
        }


        val databaseHelper = DestinoTuristicoDatabaseHelper(this)
        val tableName = "destinos_turisticos"
        val tableExists = databaseHelper.isTableExists(tableName)
        val destinosTuristicos = DestinoTuristicoDatabaseHelper(this@MapActivity)





        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))

        // Obtener ubicación actual y mostrarla en el mapa
        obtenerUbicacionActual(googleMap)

        setMarkerClickListener()
    }

    private fun getRetrofit():Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun createRoute(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getRute(getString(R.string.route_key),start,end)
            if(call.isSuccessful){
                drawRoute(call.body())
            }else{
                Log.i("aris", "KO")
            }
        }
    }

    private fun drawRoute(routeResponse: RouteResponse?) {
        val polyLineOptions = PolylineOptions()
        routeResponse?.features?.first()?.geometry?.coordinates?.forEach{
            polyLineOptions.add(LatLng(it[1], it[0]))
        }
        runOnUiThread{
            val poly = googleMap.addPolyline(polyLineOptions)
        }
    }

    fun convertirDireccionACoordenadas(direccion: String): LatLng? {
        val geocoder = Geocoder(this)
        val addressList: List<Address>? = geocoder.getFromLocationName(direccion, 1)

        if (addressList != null) {
            val address: Address = addressList[0]
            val latitud = address.latitude
            val longitud = address.longitude
            return LatLng(latitud, longitud)
        }

        return null
    }

    fun printPoints(){
        val destinosTuristicos = DestinoTuristicoDatabaseHelper(this@MapActivity)
        for (i in 0 until destinosTuristicos.getDestinosTuristicosCount()) {

            val destinos = destinosTuristicos.getDestinoTuristicoById(i)

            if (destinos != null) {
                val destinoDireccion = destinos.direccion

                val addressList = convertirDireccionACoordenadas(destinoDireccion)

                if (addressList != null) {
                    val address = addressList
                    val latLng = LatLng(address.latitude, address.longitude)

                    googleMap.addMarker(
                        MarkerOptions().position(latLng).title(destinos.nombre)
                            .snippet(destinos.direccion)
                    )
                }

            }
        }
    }

    private fun setMarkerClickListener() {
        googleMap.setOnMarkerClickListener { marker ->
            if (start.isEmpty()) {
                // Establece el marcador de inicio como el marcador seleccionado
                start = "${marker.position.longitude}, ${marker.position.latitude}"
                true
            } else if (end.isEmpty()) {
                // Establece el marcador de destino como el marcador seleccionado
                end = "${marker.position.longitude}, ${marker.position.latitude}"
                createRoute()
                true
            } else {
                false
            }
        }
    }

}