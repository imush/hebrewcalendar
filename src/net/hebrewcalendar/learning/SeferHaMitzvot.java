package net.hebrewcalendar.learning;

import java.time.LocalDate;

/**
 * Sefer Hamitzvot Yomi (Rambam's Book of Commandments — Daily Study).
 *
 * <p>Instituted by the Lubavitcher Rebbe on 27 Nisan 5744 (Sunday, 29 April
 * 1984) alongside the daily Rambam Mishneh Torah. Runs on a fixed
 * <b>339-day cycle</b>; the same cycle-day always studies the same
 * commandment(s).
 *
 * <p>Readings are given in the compact chabad.org notation:
 * {@code P<n>} = Positive Commandment n, {@code N<n>} = Negative Commandment
 * n. Some entries are prose descriptions (e.g. {@code "Nusach HaTefila"}).
 *
 * <p>The 339-day table is baked in from hebcal's MIT-licensed
 * {@code seferHaMitzvot.json}.
 */
public final class SeferHaMitzvot {

    private SeferHaMitzvot() {}

    private static final long EPOCH  = LocalDate.of(1984, 4, 29).toEpochDay();
    private static final int  CYCLE  = 339;

    /** Immutable result: day-in-cycle + raw and expanded readings. */
    public static final class Result {
        private final int dayInCycle;
        private final String raw;
        Result(int dayInCycle, String raw) {
            this.dayInCycle = dayInCycle; this.raw = raw;
        }
        public int    dayInCycle() { return dayInCycle; }
        /** Compact form as printed on chabad.org, e.g. {@code "N193, N153, N194, P146"}. */
        public String raw()        { return raw; }
        /**
         * Expanded English form, e.g.
         * {@code "Negative Commandments 193, 153, 194; Positive Commandment 146"}.
         * Prose entries pass through unchanged.
         */
        public String label() {
            return format(raw, false);
        }

        /**
         * Hebrew expansion: {@code "מצות עשה ע״ג"} for {@code "P73"},
         * {@code "מצות לא תעשה קצ״ג"} for {@code "N193"}, groups repeated
         * types like {@link #label()}. Prose entries pass through in
         * English — they have no standard Hebrew form in this table.
         */
        public String labelHe() {
            return format(raw, true);
        }

        private static String format(String raw, boolean he) {
            String[] parts = raw.split(", ");
            // Fast-path: prose (no leading P/N + digit).
            for (String p : parts) {
                if (p.isEmpty()) continue;
                char c = p.charAt(0);
                if ((c != 'P' && c != 'N') || p.length() < 2
                        || p.charAt(1) < '0' || p.charAt(1) > '9') {
                    return raw;   // prose or mixed — return as-is (English only)
                }
            }
            StringBuilder sb = new StringBuilder();
            char group = 0;   // 'P' or 'N'
            java.util.List<String> bucket = new java.util.ArrayList<>();
            for (int i = 0; i <= parts.length; i++) {
                String p = i < parts.length ? parts[i] : null;
                char c  = p == null ? 0 : p.charAt(0);
                if (c != group) {
                    if (group != 0 && !bucket.isEmpty()) {
                        if (sb.length() > 0) sb.append(he ? "; " : "; ");
                        String noun;
                        if (he) {
                            noun = group == 'P' ? "מצות עשה" : "מצות לא תעשה";
                        } else {
                            noun = group == 'P' ? "Positive Commandment" : "Negative Commandment";
                            if (bucket.size() > 1) noun += "s";
                        }
                        String nums = he
                                ? String.join(", ", bucket.stream().map(s -> Gematria.of(Integer.parseInt(s))).toArray(String[]::new))
                                : String.join(", ", bucket);
                        sb.append(noun).append(' ').append(nums);
                        bucket.clear();
                    }
                    group = c;
                }
                if (p != null) bucket.add(p.substring(1));
            }
            return sb.toString();
        }
    }

    /**
     * Reading for the given Gregorian date.
     *
     * @return the reading, or {@code null} for dates before 29 April 1984.
     */
    public static Result forDate(LocalDate date) {
        long abs = date.toEpochDay();
        if (abs < EPOCH) return null;
        int day = (int)((abs - EPOCH) % CYCLE) + 1;  // 1..339
        return new Result(day, TABLE[day - 1]);
    }

    // Positions in TABLE are day-of-cycle − 1.  Data is verbatim from
    // hebcal/hebcal-learning (MIT), which sources it from the printed
    // Chabad Kehot calendar.
    private static final String[] TABLE = {
        "Maimonides’ Introduction to Sefer Hamitzvot",  // day 1
        "Principle 1-3",  // day 2
        "Principle 4-9",  // day 3
        "Principle 10-14",  // day 4
        "P1, N1, P2",  // day 5
        "P3, P4, P9",  // day 6
        "N63, N65, P172",  // day 7
        "N64, P8, P6, P206",  // day 8
        "P207, N302, P205, N303",  // day 9
        "N256, N301, N304, N305",  // day 10
        "P11",  // day 11
        "P209",  // day 12
        "N10, N47, N60, N6, N5, N2, N3, N4, N15, P186, N23, N24",  // day 13
        "N16, N17, N18, N19, N20, N21, N26, N28, N27, N29, N14, N8, N9, N7",  // day 14
        "N11, N12, N13, P185, N25, N22, N48, N50, N51, N30, N33, N31, N32",  // day 15
        "N35, N38, N36, N37, N34, N43, N44, N40, N39, N41, N45, N171",  // day 16
        "P73",  // day 17
        "P73",  // day 18
        "P73",  // day 19
        "P73, P10",  // day 20
        "P10, P5",  // day 21
        "P5",  // day 22
        "P5",  // day 23
        "P5",  // day 24
        "P5",  // day 25
        "P26, P12",  // day 26
        "P13",  // day 27
        "P15, P18",  // day 28
        "P17",  // day 29
        "P14",  // day 30
        "P19",  // day 31
        "P19",  // day 32
        "P19",  // day 33
        "P19, P215",  // day 34
        "P215, Nusach HaTefila",  // day 35
        "Nusach HaTefila, Order of Prayer",  // day 36
        "Nusach HaTefila, Order of Prayer, P154",  // day 37
        "P154",  // day 38
        "N320",  // day 39
        "N320",  // day 40
        "N322",  // day 41
        "N322",  // day 42
        "N321",  // day 43
        "N321",  // day 44
        "P155",  // day 45
        "P155",  // day 46
        "P155",  // day 47
        "N320",  // day 48
        "N321",  // day 49
        "P165, N329, P164, N196",  // day 50
        "P159, N323, P160, N324",  // day 51
        "P162, N325, P163, N326",  // day 52
        "P166, N327, P167, N328, N199",  // day 53
        "P156, N197, N198",  // day 54
        "N200, N201, P158",  // day 55
        "P157, Text of the Haggadah, P170",  // day 56
        "P168",  // day 57
        "P169",  // day 58
        "P171",  // day 59
        "P171, P153",  // day 60
        "P153",  // day 61
        "P153",  // day 62
        "P153",  // day 63
        "P153",  // day 64
        "P153",  // day 65
        "P153, P59",  // day 66
        "P59",  // day 67
        "P59, Laws of Megillah and Chanukah Chapters 1-2",  // day 68
        "Laws of Megillah and Chanukah Chapters 3-4, P213",  // day 69
        "P213",  // day 70
        "N355",  // day 71
        "N355",  // day 72
        "N355",  // day 73
        "N262",  // day 74
        "N262",  // day 75
        "P212",  // day 76
        "P212",  // day 77
        "P222",  // day 78
        "P222",  // day 79
        "N356",  // day 80
        "N356",  // day 81
        "N356, P216",  // day 82
        "P217",  // day 83
        "N357",  // day 84
        "P220, P218, N358, P219, N359",  // day 85
        "P223, N104",  // day 86
        "N105, N330, N331, N332, N333, N334",  // day 87
        "N336, N335, N337, N338, N339",  // day 88
        "N340, N341, N342, N343, N344, N345",  // day 89
        "N348, N349, N350, N351",  // day 90
        "N352, N347, N346",  // day 91
        "N52, N53, N55, N54, N354, N360, N361",  // day 92
        "N161, N162, P38, N160, N158, N159",  // day 93
        "N353, P149",  // day 94
        "P150, P151, P152, N172, N174",  // day 95
        "N173, N175, N176, N177, N178",  // day 96
        "N179, N180, N188",  // day 97
        "N181, N182, N184, N185, N183",  // day 98
        "N187, N186, N189, N190, N191, N192",  // day 99
        "N193, N153, N194, P146",  // day 100
        "N101",  // day 101
        "P147",  // day 102
        "N306",  // day 103
        "P148",  // day 104
        "N61",  // day 105
        "N62",  // day 106
        "N248, N249",  // day 107
        "P7",  // day 108
        "P94",  // day 109
        "P94",  // day 110
        "N157",  // day 111
        "P95",  // day 112
        "P95, P92, N209",  // day 113
        "N202, N203, N204",  // day 114
        "N205, N206, N208, N207",  // day 115
        "P93, P114",  // day 116
        "P115, P116, P117",  // day 117
        "P145, N110",  // day 118
        "N111, N215",  // day 119
        "N216",  // day 120
        "N217, N218",  // day 121
        "N42, P120, N210",  // day 122
        "P121, N211, P123, N212",  // day 123
        "P124, N213, P122, N214",  // day 124
        "P130, P195, N232",  // day 125
        "P126, P129",  // day 126
        "N154",  // day 127
        "N133, N134",  // day 128
        "N135, N136",  // day 129
        "N137",  // day 130
        "P127",  // day 131
        "P127",  // day 132
        "P127",  // day 133
        "P127",  // day 134
        "P127, P128, N152",  // day 135
        "N150, N151",  // day 136
        "N141, N142, N143",  // day 137
        "P119",  // day 138
        "P131, P125",  // day 139
        "N149, P132",  // day 140
        "P133, P143, P144",  // day 141
        "P80, P81",  // day 142
        "P82, P135, N220, N221, N222",  // day 143
        "N223, P134, P141, N230, N231",  // day 144
        "P140, P136, P137, N224, N225, N226",  // day 145
        "P138, N227, P139",  // day 146
        "N169, N170, P183, N228, P20",  // day 147
        "N79, N80",  // day 148
        "P21",  // day 149
        "P22, N67, P35, N83, N84",  // day 150
        "N85, N82, P34, N86",  // day 151
        "P23, N72, P32, P36",  // day 152
        "P33, N88, N87, N73, N163, N164",  // day 153
        "N68, N165, P31, N77, N78",  // day 154
        "N75, N76, P24, N69, N70, N71",  // day 155
        "N74, P61, N91, N92, N93",  // day 156
        "N94, N95, N96, N97, P86",  // day 157
        "P60, N100, N98, P62, N99",  // day 158
        "P63, N146, P64",  // day 159
        "N139, N112, P65",  // day 160
        "P89, N145, N148",  // day 161
        "P66, N147, P67, N102, N103, N138",  // day 162
        "N124, P88, P83, N155",  // day 163
        "P84, P85, N90",  // day 164
        "N89, P39, P29, N81, P30",  // day 165
        "P28, P25, P40, P41, P27, P42",  // day 166
        "P43, P44, P45, P46, P47, P48, P50, P51",  // day 167
        "P161, N140",  // day 168
        "N132",  // day 169
        "N120",  // day 170
        "N131",  // day 171
        "N130, N129",  // day 172
        "P91",  // day 173
        "P90",  // day 174
        "P49",  // day 175
        "P49, P118",  // day 176
        "N113",  // day 177
        "N114",  // day 178
        "N114, P55, N115, N116",  // day 179
        "P57, P56, P58",  // day 180
        "N125, N123, N128, N126, N127, N121, N122",  // day 181
        "N117, N119, N118, P53, P52",  // day 182
        "P54, N156, N229, P16, P79",  // day 183
        "N144, N108",  // day 184
        "P78",  // day 185
        "N109, P69",  // day 186
        "P70",  // day 187
        "P70",  // day 188
        "P71",  // day 189
        "P72",  // day 190
        "P68, P75, P76",  // day 191
        "P74, P77",  // day 192
        "N106, P87",  // day 193
        "N107, P107",  // day 194
        "P107",  // day 195
        "P107",  // day 196
        "P107",  // day 197
        "P107",  // day 198
        "P107",  // day 199
        "P107",  // day 200
        "P107",  // day 201
        "P107, P113",  // day 202
        "P113",  // day 203
        "P113",  // day 204
        "P108",  // day 205
        "P108",  // day 206
        "P108, P101",  // day 207
        "N308, N307",  // day 208
        "P112",  // day 209
        "P110",  // day 210
        "P111",  // day 211
        "P102, P103",  // day 212
        "P99",  // day 213
        "P100",  // day 214
        "P106",  // day 215
        "P104",  // day 216
        "P104, P96",  // day 217
        "P96",  // day 218
        "P97",  // day 219
        "P97",  // day 220
        "P105",  // day 221
        "P105",  // day 222
        "P105",  // day 223
        "P98",  // day 224
        "P98",  // day 225
        "P98",  // day 226
        "P98",  // day 227
        "P98",  // day 228
        "P98",  // day 229
        "P107",  // day 230
        "P108",  // day 231
        "P101",  // day 232
        "P99",  // day 233
        "P100",  // day 234
        "P106",  // day 235
        "P104",  // day 236
        "P96",  // day 237
        "P109",  // day 238
        "P109",  // day 239
        "P109",  // day 240
        "P109",  // day 241
        "P109, P237",  // day 242
        "P240",  // day 243
        "P238",  // day 244
        "P241",  // day 245
        "P241",  // day 246
        "N244, P239",  // day 247
        "P208, N271, N272",  // day 248
        "N246, N243",  // day 249
        "N245",  // day 250
        "N247",  // day 251
        "N265",  // day 252
        "N266",  // day 253
        "P194",  // day 254
        "N269, P204",  // day 255
        "P236",  // day 256
        "P236",  // day 257
        "P236, N289, N296",  // day 258
        "P225, N295, N292",  // day 259
        "P247, N293, N297, P182",  // day 260
        "P181, N309, N298, P184",  // day 261
        "N299, P202, P203, N270",  // day 262
        "P245",  // day 263
        "P245",  // day 264
        "N250",  // day 265
        "N250",  // day 266
        "N251",  // day 267
        "N251",  // day 268
        "N253",  // day 269
        "N253",  // day 270
        "N252",  // day 271
        "N252",  // day 272
        "P245",  // day 273
        "P245",  // day 274
        "P245",  // day 275
        "P245",  // day 276
        "P236",  // day 277
        "P236",  // day 278
        "P236",  // day 279
        "P236",  // day 280
        "P245",  // day 281
        "P245",  // day 282
        "P245",  // day 283
        "P245",  // day 284
        "P232, N258, N259, N257, N260",  // day 285
        "P196, N233, P234, P233, N261",  // day 286
        "P235, N254, N255",  // day 287
        "P243",  // day 288
        "P200, N238",  // day 289
        "P201",  // day 290
        "N267, N268",  // day 291
        "N219, P244",  // day 292
        "P244",  // day 293
        "P242",  // day 294
        "P197, N234",  // day 295
        "P142",  // day 296
        "N239",  // day 297
        "P199, N240",  // day 298
        "N241, N242",  // day 299
        "N235",  // day 300
        "N236",  // day 301
        "N237",  // day 302
        "P198",  // day 303
        "P246",  // day 304
        "P246",  // day 305
        "P246",  // day 306
        "P246",  // day 307
        "P246",  // day 308
        "P246, P248",  // day 309
        "P248",  // day 310
        "P248",  // day 311
        "P248",  // day 312
        "P176, N284, P175",  // day 313
        "N282, N283, P229, P228",  // day 314
        "P226, P227, P230, P231, N66",  // day 315
        "N310, P224, N300",  // day 316
        "N294, N290",  // day 317
        "N279, N277, N275, N278, N273",  // day 318
        "N280, P177, N276, N274",  // day 319
        "N315, N281, N316",  // day 320
        "N317, P178",  // day 321
        "P179",  // day 322
        "N291",  // day 323
        "N288",  // day 324
        "N286",  // day 325
        "N287",  // day 326
        "N285",  // day 327
        "P180",  // day 328
        "P174, N312, N313, N314",  // day 329
        "N318, N319, P210, P211",  // day 330
        "N195, P37",  // day 331
        "N168",  // day 332
        "N167",  // day 333
        "N166",  // day 334
        "N166",  // day 335
        "P173, N362, N364, N363, N365",  // day 336
        "P187, N49, P188, P189, N59",  // day 337
        "N46, P190, N56, N57, P192, P193",  // day 338
        "P191, P214, N311, N58, P221, N263, N264",  // day 339
    };
}
