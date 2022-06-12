package com.capstone.valoai.features.profile.data.models

data class Profile(
    val name: String = "",
    val birthDate: String = "",
    val vaksin1: String = "",
    val vaksin2: String = "",
    val vaksin3: String = "",
    val tanggal_vaksin1: String = "",
    val tanggal_vaksin2: String = "",
    val tanggal_vaksin3: String = "",
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
        riwayat1 ?: this.vaksin1,
        riwayat2 ?: this.vaksin2,
        riwayat3 ?: this.vaksin3,
        tanggalRiwayat1 ?: this.tanggal_vaksin1,
        tanggalRiwayat2 ?: this.tanggal_vaksin2,
        tanggalRiwayat3 ?: this.tanggal_vaksin3
    )
}