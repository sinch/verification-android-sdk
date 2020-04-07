package com.sinch.verificationcore.config

import com.sinch.verificationcore.config.general.GlobalConfig

interface GlobalConfigSetter<LastSetter> {
    fun globalConfig(globalConfig: GlobalConfig): NumberSetter<LastSetter>
}

interface NumberSetter<LastSetter> {
    fun number(number: String): LastSetter
}