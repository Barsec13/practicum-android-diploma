package ru.practicum.android.diploma.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.search.data.NetworkClient
import ru.practicum.android.diploma.search.data.dto.AreaSearchRequest
import ru.practicum.android.diploma.search.data.dto.CountriesSearchRequest
import ru.practicum.android.diploma.search.data.dto.Response
import ru.practicum.android.diploma.search.data.dto.SearchRequest

class RetrofitNetworkClient(private val api: Api, private val context: Context) : NetworkClient {

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun doRequest(dto: Any): Response {
        if (isConnected() == false) {
            return Response().apply { resultCode = -1 }
        }
        if (dto !is SearchRequest) {
            return Response().apply { resultCode = 400 }
        }
        return withContext(Dispatchers.IO) {
            try {
                val response = api.search(dto.expression,0,10)
                response.apply { resultCode = 200 }
            } catch (e: Throwable) {
                Response().apply { resultCode = 500 }

            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun getCountries(dto: Any): Response {
        if (isConnected() == false) {
            return Response().apply { resultCode = -1 }
        }
        if(dto !is CountriesSearchRequest ){
            return Response().apply { resultCode = 400 }
        }
        return withContext(Dispatchers.IO){
            try {
                val response = Response()
                val result = api.getCountries()
                response.apply { resultCode = 200
                resultCountries = result}
            } catch (e:Throwable){
                Response().apply { resultCode = 500 }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun getAres(dto: Any): Response {
        if(isConnected() == false){
            return Response().apply { resultCode = -1 }
        }
        if(dto !is AreaSearchRequest){
            return Response().apply { resultCode = 400 }
        }
        return withContext(Dispatchers.IO){
            try {
                val response = Response()
                val results = api.getAreas()
                response.apply { resultCode = 200
                    resultAreas= results  }
            } catch (e:Throwable){
                Response().apply { resultCode = 500 }
            }
        }
    }

    /* @RequiresApi(Build.VERSION_CODES.M)
     override suspend fun doRequest(dto: Any): Response {
         if (isConnected() == false) {
             return Response().apply { resultCode = -1 }
         }
         if (dto !is SearchRequestOptions) {
             return Response().apply { resultCode = 400 }
         }
         return withContext(Dispatchers.IO) {
             try {
                 val response = api.getVacancies(dto.options)
                 response.apply { resultCode = 200 }
             } catch (e: Throwable) {
                 Response().apply { resultCode = 500 }

             }
         }
     }*/

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}