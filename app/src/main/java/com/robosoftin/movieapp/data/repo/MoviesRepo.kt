package com.robosoftin.movieapp.data.repo

import com.robosoftin.movieapp.data.remote.ApiInterface
import javax.inject.Inject

class MoviesRepo @Inject constructor(
    private val apiInterface: ApiInterface
) : ApiInterface by apiInterface