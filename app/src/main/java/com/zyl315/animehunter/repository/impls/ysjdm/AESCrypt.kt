package com.zyl315.animehunter.repository.impls.ysjdm

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESCrypt{
    //AES加密
    fun encrypt(input:String,password:String): String{
        //初始化cipher对象
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        // 生成密钥
        val keySpec = SecretKeySpec(password.toByteArray(),"AES")
        cipher.init(Cipher.ENCRYPT_MODE,keySpec, IvParameterSpec(ByteArray(cipher.blockSize)))
        //加密解密
        val encrypt = cipher.doFinal(input.toByteArray())
        val result = Base64.encode(encrypt, Base64.DEFAULT)
        return String(result)
    }

    //AES解密
    fun decrypt(input: String, password: String, ivParam:String?): String {
        //初始化cipher对象
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val iv = ivParam?.toByteArray() ?: ByteArray(cipher.blockSize)
        // 生成密钥
        val keySpec = SecretKeySpec(password.toByteArray(), "AES")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(iv))
        //加密解密
        val encrypt = cipher.doFinal(Base64.decode(input.toByteArray(), Base64.DEFAULT))
        return String(encrypt)
    }

    fun decrypt(input: String, password: String): String {
        return decrypt(input, password, null)
    }
}