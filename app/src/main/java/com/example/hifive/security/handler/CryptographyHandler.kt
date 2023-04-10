package com.example.hifive.security.handler

import com.example.hifive.security.EncryptedData
import javax.crypto.Cipher

interface CryptographyHandler {
    fun getInitializedCipherForEncryption(keyName: String): Cipher

    fun getInitializedCipherForDecryption(keyName: String, initVector: ByteArray): Cipher

    fun encryptData(plaintext: String, cipher: Cipher): EncryptedData

    fun decryptData(ciphertext: ByteArray, cipher: Cipher): String
}