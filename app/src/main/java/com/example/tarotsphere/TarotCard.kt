package com.example.tarotsphere
import android.os.Parcel
import android.os.Parcelable

data class TarotCard(
    val name: String,
    val imageRes: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(imageRes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TarotCard> {
        override fun createFromParcel(parcel: Parcel): TarotCard {
            return TarotCard(parcel)
        }

        override fun newArray(size: Int): Array<TarotCard?> {
            return arrayOfNulls(size)
        }
    }

    override fun hashCode(): Int {
        return name.hashCode() * 31 + imageRes
    }

}
