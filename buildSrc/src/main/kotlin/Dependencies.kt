/*
 * Copyright (c) 2019 Owain van Brakel <https://github.com/Owain94>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

object ProjectVersions {
    const val openosrsVersion = "3.0.7"
    const val apiVersion = "0.0.1"
}

object Libraries {
    private object Versions {
        const val apacheCommonsText = "1.8"
        const val gson = "2.8.6"
        const val guava = "28.2-jre"
        const val guice = "4.2.2"
        const val jdatepicker = "1.3.2"
        const val jfreechart = "1.5.0"
        const val jopt = "5.0.4"
        const val lombok = "1.18.12"
        const val okhttp3 = "4.4.0"
        const val pf4j = "3.2.0"
    }

    const val apacheCommonsText = "org.apache.commons:commons-text:${Versions.apacheCommonsText}"
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
    const val guava = "com.google.guava:guava:${Versions.guava}"
    const val guice = "com.google.inject:guice:${Versions.guice}:no_aop"
    const val jdatepicker = "net.sourceforge.jdatepicker:jdatepicker:${Versions.jdatepicker}"
    const val jfreechart = "org.jfree:jfreechart:${Versions.jfreechart}"
    const val jopt = "net.sf.jopt-simple:jopt-simple:${Versions.jopt}"
    const val lombok = "org.projectlombok:lombok:${Versions.lombok}"
    const val okhttp3 = "com.squareup.okhttp3:okhttp:${Versions.okhttp3}"
    const val pf4j = "org.pf4j:pf4j:${Versions.pf4j}"

}
