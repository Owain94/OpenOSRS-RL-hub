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
include(":annoyancemute")
include(":bankdiff")
include(":bankedexperience")
include(":bankheatmap")
include(":bankhistory")
include(":bankscreenshot")
include(":banktabnames")
include(":bankvalue")
include(":bitwarden")
include(":brushmarkers")
include(":calculator")
include(":cballrate")
include(":chatboxopacity")
include(":chattranscripts")
include(":chompyhunter")
include(":clanchatcountryflags")
include(":clanchatwarnings")
include(":clanrosterhelper")
include(":collectionlog")
include(":colourblindchat")
include(":contextualcursor")
include(":corpffa07")
include(":crabsolver")
include(":chatlogger")
include(":coxlightcolors")
include(":crabstuntimer")
include(":cratelimiter")
include(":crowdsourcing")
include(":customrsnhider")
include(":dailytaskextensions")
include(":danceparty")
include(":dataexport")
include(":deathindicator")
include(":decimalprices")
include(":denseessence")
include(":discordlootlogger")
include(":discordraredropnotificater")
include(":doorkicker")
include(":dorgeshkaanlights")
include(":eclecticjars")
include(":effectivelevel")
include(":eightball")
include(":emojipalette")
include(":emojimadness")
include(":emojiscape")
include(":equipmentscreenshot")
include(":essencerunning")
include(":esspouch")
include(":examinetooltip")
include(":fakeiron")
include(":fishingnotifier")
include(":fixedhidechat")
include(":flipper")
include(":flippingutilities")
include(":friendsexporter")
include(":fullscreen")
include(":gauntletrlhub")
include(":globalconsciousness")
include(":gnomerestaurant")
include(":gpuexperimental")
include(":greenscreen")
include(":groupironman")
include(":hamstoreroom")
include(":hamstorerooms")
include(":healthnotifier")
include(":herbsackpricecheck")
include(":hidewidgets")
include(":homeenforcer")
include(":httpserver")
include(":hubtickcounter")
include(":hubzalcano")
include(":influxdb")
include(":infernosplittimer")
include(":inventoryscrabble")
include(":inventorysetups")
include(":inventorysummary")
include(":loottable")
include(":lowercaseusernames")
include(":mapwaypoint")
include(":marksofgracecounter")
include(":magicsecateurs")
include(":mahoganyhomes")
include(":masterfarmer")
include(":monkeymetrics")
include(":naturerunechest")
include(":notempty")
include(":notificationmessages")
include(":npcidletimer")
include(":npcoverheaddialogue")
include(":optimalnmzpoints")
include(":optimalquestguide")
include(":organisedcrime")
include(":osrsstreamers")
include(":partypanel")
include(":pathfinder")
include(":petinfo")
include(":pickpocketinfo")
include(":playertile")
include(":playtime")
include(":pmcolors")
include(":polybarintegration")
include(":preemptive")
include(":probabilitycalculator")
include(":profittracker")
include(":pushnotification")
include(":pvpperformancetracker")
include(":raidplayernames")
include(":raidshamer")
include(":raidtracker")
include(":resourcepacks")
include(":rsnhider")
include(":runecafecashflow")
include(":runiterocks")
include(":smartmetronome")
include(":stepcounter")
include(":stonedloottracker")
include(":slayerwiki")
include(":skillstabprogressbars")
include(":teleportlogger")
include(":theatreofbloodstats")
include(":timetolevel")
include(":tobhealthbars")
include(":tobinfoboxes")
include(":toweroflife")
include(":trayindicators")
include(":traynotifications")
include(":trimmer")
include(":underwateragility")
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