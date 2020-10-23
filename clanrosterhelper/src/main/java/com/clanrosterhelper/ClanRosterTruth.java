/*
 * Copyright (c) 2020, Spencer Imbleau <spencer@imbleau.com>
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
package com.clanrosterhelper;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The clan roster which will be used as a truthful copy. All clan setup
 * members will be compared to this to determine how to match it.
 */
public class ClanRosterTruth {

    /**
     * The members of the clan roster
     */
    public final List<ClanMemberMap> MEMBERS;

    /**
     * Initiate a clan roster that
     *
     * @param members - the members of the clan
     */
    private ClanRosterTruth(final List<ClanMemberMap> members) {
        this.MEMBERS = members;
    }

    /**
     * Parse a clan roster from JSON
     *
     * @param source - the json raw data
     * @return a clan roster, if parsing is successful
     * @throws Exception on parsing failure
     */
    public static ClanRosterTruth fromJSON(final String source) throws Exception {
        Gson gson = new Gson();

        JsonParser parser = new JsonParser();
        JsonArray document = parser.parse(source).getAsJsonArray();

        List<ClanMemberMap> members = new ArrayList<>();
        for (JsonElement member : document) {
            JsonObject memberObject = member.getAsJsonObject();
            String rsn = memberObject.get("rsn").getAsString();
            String rank = memberObject.get("rank").getAsString();
            ClanMemberMap map = new ClanMemberMap(rsn, rank);
            members.add(map);
        }

        return new ClanRosterTruth(members);
    }
}
