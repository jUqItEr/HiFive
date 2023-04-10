package com.example.hifive.security

data class EncryptedData(val cipherText: ByteArray, val initVector: ByteArray)