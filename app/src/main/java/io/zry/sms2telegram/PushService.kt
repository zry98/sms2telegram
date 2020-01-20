package io.zry.sms2telegram

import android.app.IntentService
import android.content.Intent
import android.util.Log
import okhttp3.*
import okhttp3.dnsoverhttps.DnsOverHttps
import org.json.JSONException
import org.json.JSONObject
import squareup.okhttp3.extend.Tls12SocketFactory
import java.io.IOException
import java.net.InetAddress
import java.net.UnknownHostException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext

class PushService : IntentService {

    companion object {
        private const val TAG = "PushService"
        private lateinit var prefs: Preferences
        private val JSON_MIME_TYPE = MediaType.parse("application/json; charset=utf-8")
    }

    constructor() : super("PushService")

    constructor(name: String) : super(name)

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) {
            return
        }
        val senderNumber = intent.getStringExtra("senderNumber")!!
        val receiveDateTime = intent.getStringExtra("receiveDateTime")!!
        val messageBody = intent.getStringExtra("messageBody")!!

        pushMessage(senderNumber, receiveDateTime, messageBody)
    }

    private fun pushMessage(senderNumber: String, receiveDateTime: String, messageBody: String) {
        val jsonData = JSONObject()

        try {
            val smsData = JSONObject()

            smsData.put("from", senderNumber)
            smsData.put("datetime", receiveDateTime)
            smsData.put("text", messageBody)

            jsonData.put("type", "SMS")
            jsonData.put("data", smsData)
        } catch (e: JSONException) {
            Log.d(TAG, e.toString())
        }

        prefs = Preferences(this)
        val webhookUrl = prefs.webhookUrl

        if (webhookUrl.isNullOrBlank()) {
            MainActivity.showToast(resources.getString(R.string.set_webhook_url_hint_toast_text))
            return
        }

        val requestBody = RequestBody.create(JSON_MIME_TYPE, jsonData.toString())
        val httpClient = getOkHttpClient()
        val request = Request.Builder().url(webhookUrl).method("POST", requestBody).build()
        val call = httpClient.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.e(TAG, e.toString())
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                assert(response.body() != null)
                val bodyString = response.body()!!.string()
                assert(bodyString != "ERROR")
                Log.d(TAG, bodyString)
            }
        })
    }

    private fun getOkHttpClient(): OkHttpClient {
        val spec: ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA256,
                        CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA
                )
                .tlsVersions(TlsVersion.TLS_1_2)
                .build()
        val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
        var sc: SSLContext? = null
        try {
            sc = SSLContext.getInstance("TLSv1.2")
            sc!!.init(null, null, null)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }
        assert(sc != null)
        client.sslSocketFactory(Tls12SocketFactory(sc!!.socketFactory))
        val specs = ArrayList<ConnectionSpec>()
        specs.add(spec)
        specs.add(ConnectionSpec.COMPATIBLE_TLS)
        specs.add(ConnectionSpec.CLEARTEXT)
        client.connectionSpecs(specs)
        client.dns(
                DnsOverHttps.Builder().client(client.build())
                        .url(HttpUrl.get("https://cloudflare-dns.com/dns-query"))
                        .bootstrapDnsHosts(
                                getByIp("1.0.0.1"),
                                getByIp("9.9.9.9"),
                                getByIp("185.222.222.222"),
                                getByIp("2606:4700:4700::1001"),
                                getByIp("2620:fe::fe"),
                                getByIp("2a09::")
                        )
                        .includeIPv6(true)
                        .build()
        )

        return client.build()
    }

    private fun getByIp(host: String): InetAddress {
        try {
            return InetAddress.getByName(host)
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }
}
