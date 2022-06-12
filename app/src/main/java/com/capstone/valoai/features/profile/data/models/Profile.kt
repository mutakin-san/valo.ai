package com.capstone.valoai.features.profile.data.models

data class Profile(
    val name: String = "",
    val birthDate: String = "",
    val riwayat1: String = "",
    val riwayat2: String = "",
    val riwayat3: String = "",
    val tanggalRiwayat1: String = "",
    val tanggalRiwayat2: String = "",
    val tanggalRiwayat3: String = "",
) {
    fun copyWith(
        name: String?,
        birthDate: String?,
        riwayat1: String?,
        riwayat2: String?,
        riwayat3: String?,
        tanggalRiwayat1: String?,
        tanggalRiwayat2: String?,
        tanggalRiwayat3: String?,

        ): Profile = Profile(
        name ?: this.name,
        birthDate ?: this.birthDate,
        riwayat1 ?: this.riwayat1,
        riwayat2 ?: this.riwayat2,
        riwayat3 ?: this.riwayat3,
        tanggalRiwayat1 ?: this.tanggalRiwayat1,
        tanggalRiwayat2 ?: this.tanggalRiwayat2,
        tanggalRiwayat3 ?: this.tanggalRiwayat3
    )
}