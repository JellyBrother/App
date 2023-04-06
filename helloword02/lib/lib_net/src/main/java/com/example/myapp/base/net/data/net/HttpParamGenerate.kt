package com.example.myapp.base.net.data.net

import com.example.myapp.base.utils.LogUtils
import java.net.URLEncoder
import java.nio.charset.Charset
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

object HttpParamGenerate {
    private const val publicKey =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmG4iLOvTOu5gZ2NGjC2DWHvo67iWBLkJcoAUwQFhCcDvRK8VdPztHvSBZ6pk4yUIApEtSm4YtdgyPbyXyOMSaBl1RkccJnBLDL+NZIs1ER/ki3cO8rwChmMslfxxLCVdkE7pgudyCkyKAuCRZrPitUiojVJ2mMQVg+bN0MTiaplDLiUIZGtBj8QluK7/y77J7cPJlOGYNIzFP22g6kloC2ecaptTbUITf+mROImL702KOxZokCHZnxELV4NwLfCl+sgrE6EW3lXU8H+UngFkYmmW7yowj7MLcPif9R+TbBdcjY6RRjErCd4BZdV58kbuc6ndK+XjuTFyPV6qXYM2RwIDAQAB"

    fun generateAuthorization(clientId: String, clientSecret: String): String {
        val clientIdAndSecret = "$clientId:$clientSecret"
        val clientIdAndSecretU8 = String(clientIdAndSecret.toByteArray(), Charset.forName("UTF-8"))
//        val clientIdAndSecret64 =
//            Base64.encodeToString(clientIdAndSecretU8.toByteArray(), Base64.DEFAULT)
        val clientIdAndSecret64 =
            Base64.getUrlEncoder().encodeToString(clientIdAndSecretU8.encodeToByteArray())
        return "Basic ${getValueEncoded(clientIdAndSecret64)}"
    }

    private fun getValueEncoded(value: String?): String {
        if (value == null) return "null"
        val newValue = value.replace("\n", "")
        val newValueChar = newValue.toCharArray()
        for (i in newValueChar.indices) {
            val c = newValueChar[i]
            if (c <= '\u001f' || c >= '\u007f') {
                return URLEncoder.encode(newValue, "UTF-8")
            }
        }
        return newValue
    }

    /**
     * 注册接口密码采用Bcrypt加密
     */
    fun generateBcryptPassword(password: String): String {
        return "{bcrypt}${BCrypt.hashpw(password, BCrypt.gensalt())}"
    }

    /**
     * 用户登录接口密码采用RSA加密
     */
    fun generateEncryptPassword(password: String): String {
        return try {
            val decoded = Base64.getDecoder().decode(publicKey)
            val pubKey =
                KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(decoded))
            val cipher1 = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher1.init(Cipher.ENCRYPT_MODE, pubKey)
            val encryptedPwd =
                Base64.getEncoder().encodeToString(cipher1.doFinal(password.toByteArray()))
            encryptedPwd
        } catch (e: Exception) {
            LogUtils.e("generateEncryptPassword", e)
            ""
        }
    }

    /**
     * 获取排序参数
     */
    fun getOrderBy(): String {
        return "seq asc,updateTime desc"
    }
}