package org.tensorflow.lite.examples.classification

import org.tensorflow.lite.examples.classification.Model.Country
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface service {
    @GET("info/{code}.json")
    fun getCountrys(@Path("code") codeCountry: String): Call<Country>
}