package com.example.fiend

import android.util.Log

object AdblockEngine {
    private var isEngineLoaded = false

    init {
        try {
            // Load the shared library created by Rust
            System.loadLibrary("rust_adblock")
            isEngineLoaded = true
        } catch (e: UnsatisfiedLinkError) {
            Log.e("AdblockEngine", "Rust JNI library not found. Adblocking disabled. Please compile the Rust crate.", e)
            isEngineLoaded = false
        }
    }

    /**
     * Checks if a URL should be blocked based on the Brave adblock-rust engine.
     */
    fun shouldBlock(url: String, resourceType: String): Boolean {
        if (!isEngineLoaded) return false
        return shouldBlockNative(url, resourceType)
    }

    private external fun shouldBlockNative(url: String, resourceType: String): Boolean
}
