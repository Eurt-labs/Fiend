package com.example.fiend

import android.util.Log

object AdblockEngine {
    init {
        // Load the shared library created by Rust
        System.loadLibrary("rust_adblock")
    }

    /**
     * Checks if a URL should be blocked based on the Brave adblock-rust engine.
     */
    fun shouldBlock(url: String, resourceType: String): Boolean {
        return shouldBlockNative(url, resourceType)
    }

    private external fun shouldBlockNative(url: String, resourceType: String): Boolean
}
