/*
 * Copyright (c) 2019 Owain van Brakel <https:github.com/Owain94>
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

rootProject.name = "OpenOSRS RL hub"

include(":advancednotifications")
include(":analoguetimestamps")
include(":bankedexperience")
include(":bankheatmap")
include(":bankhistory")
include(":bankscreenshot")
include(":banktabnames")
include(":bankvalue")
include(":bitwarden")
include(":calculator")
include(":cballrate")
include(":chatboxopacity")
include(":chattranscripts")
include(":chompyhunter")
include(":clanchatcountryflags")
include(":clanchatwarnings")
include(":clanrosterhelper")
include(":crabsolver")
include(":chatlogger")
include(":coxlightcolors")
include(":crabstuntimer")
include(":crowdsourcing")
include(":dataexport")
include(":deathindicator")
include(":doorkicker")
include(":effectivelevel")
include(":eightball")
include(":emojipalette")
include(":emojimadness")
include(":emojiscape")
include(":esspouch")
include(":fakeiron")
include(":fixedhidechat")
include(":flipper")
include(":flippingutilities")
include(":essencerunning")
include(":friendsexporter")
include(":fullscreen")
include(":gauntletrlhub")
include(":gnomerestaurant")
include(":greenscreen")
include(":groupironman")
include(":hamstoreroom")
include(":hamstorerooms")
include(":healthnotifier")
include(":herbsackpricecheck")
include(":hidewidgets")
include(":httpserver")
include(":influxdb")
include(":inventoryscrabble")
include(":inventorysetups")
include(":inventorysummary")
include(":lowercaseusernames")
include(":mapwaypoint")
include(":marksofgracecounter")
include(":magicsecateurs")
include(":masterfarmer")
include(":monkeymetrics")
include(":notificationmessages")
include(":npcidletimer")
include(":npcoverheaddialogue")
include(":optimalquestguide")
include(":partypanel")
include(":petinfo")
include(":playertile")
include(":pmcolors")
include(":polybarintegration")
include(":probabilitycalculator")
include(":pushnotification")
include(":pvpperformancetracker")
include(":resourcepacks")
include(":rsnhider")
include(":runecafecashflow")
include(":runiterocks")
include(":smartmetronome")
include(":stonedloottracker")
include(":slayerwiki")
include(":skillstabprogressbars")
include(":teleportlogger")
include(":theatreofbloodstats")
include(":timetolevel")
include(":tobhealthbars")
include(":toweroflife")
include(":traynotifications")
include(":volcanicmine")
include(":volcanicminestabilitytracker")
include(":weightcalc")
include(":wellness")
include(":wikibanktagintegration")
include(":wikisearchshortcuts")
include(":worldhider")
include(":worldhighlighter")
include(":xpgrapher")
include(":zoom")

for (project in rootProject.children) {
    project.apply {
        projectDir = file(name)
        buildFileName = "$name.gradle.kts"

        require(projectDir.isDirectory) { "Project '${project.path} must have a $projectDir directory" }
        require(buildFile.isFile) { "Project '${project.path} must have a $buildFile build script" }
    }
}