package com.dew.aihuaii.report

import android.content.Context
import com.dew.aihuaii.report.AcraReportSender
import org.acra.config.ACRAConfiguration
import org.acra.sender.ReportSender
import org.acra.sender.ReportSenderFactory

class AcraReportSenderFactory : ReportSenderFactory {
    override fun create(context: Context, config: ACRAConfiguration): ReportSender {
        return AcraReportSender()
    }
}
