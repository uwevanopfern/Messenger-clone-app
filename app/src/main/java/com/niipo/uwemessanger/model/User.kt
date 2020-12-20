package com.niipo.uwemessanger.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


//Parcelable helps to pass whole object of class between activity
@Parcelize
class User(val uid: String, val username: String, val profileImageUrl: String?) : Parcelable {
    constructor() : this("", "", null)
}