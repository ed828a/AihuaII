package com.dew.aihuaii.ui.helper

import java.io.InterruptedIOException

/**
 *  Created by Edward on 3/21/2019.
 */
object ErrorUtil {

    /**
     * Check if throwable have the exact cause From one of the causes to check.
     */
    private fun hasExactCauseThrowable(throwable: Throwable, vararg causesToCheck: Class<*>): Boolean {
        for (causesEl in causesToCheck) {
            if (throwable.javaClass == causesEl) {
                return true
            }
        }

        val cause: Throwable? = throwable.cause
        if (cause != null) {
            for (causesEl in causesToCheck) {
                if (cause.javaClass == causesEl) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Check if throwable have Interrupted* exception as one of its causes.
     */
    fun isInterruptedCaused(throwable: Throwable): Boolean {
        return hasExactCauseThrowable(throwable,
            InterruptedIOException::class.java,
            InterruptedException::class.java)
    }
}