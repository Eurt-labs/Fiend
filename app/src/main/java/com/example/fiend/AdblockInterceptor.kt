package com.example.fiend

import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class AdblockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        
        // Simple mapping, can be more sophisticated
        val resourceType = "xmlhttprequest" 
        
        if (AdblockEngine.shouldBlock(url, resourceType)) {
            // Block the request by returning an empty 403 Forbidden response
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(403)
                .message("Blocked by Rust Adblock")
                .body("".toResponseBody(null))
                .build()
        }
        
        return chain.proceed(request)
    }
}
