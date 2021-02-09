/*
   Copyright 2020 Nicolas Raoul

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
/*
 Edited by paranoidcake in order to work as a Kotlin library rather than a Java based command line script
 */
package jakaroma

import com.atilika.kuromoji.ipadic.Token
import com.atilika.kuromoji.ipadic.Tokenizer

class Jakaroma {
    fun translate(input: String?): MutableMap<String, String> {
        val tokenizer = Tokenizer()
        val tokens = tokenizer.tokenize(input)


        // Display tokens
        if (DEBUG) {
            for (token in tokens) {
                println(token.surface + "\t" + token.allFeatures)
            }
        }

        // Display romaji
//        val buffer = StringBuffer()
        val kanaToRomaji = KanaToRomaji()

        val surfaceToRomaji: MutableMap<String, String> = mutableMapOf()

        // Main loop through tokens
        var i = 0
        while (i < tokens.size) {
            if (DEBUG) {
                println("Token: " + tokens[i].surface)
            }
            val type = tokens[i].allFeaturesArray[1]
            if (DEBUG) {
                println("Type: $type")
            }
            // keep newlines unaltered
            if (tokens[i].allFeaturesArray[0] == "記号") {
//                buffer.append(tokens[i].surface) // Append surface form
                i++
                continue
            }
//            val space = true
            when (tokens[i].allFeaturesArray[1]) {
                "数", "アルファベット", "サ変接続" -> {
//                    buffer.append(tokens[i].surface) // TODO - exception list for when this false positives
                    i++
                    continue  // avoid extra whitespace after symbols
                }
                else -> {
                    var romaji: String

                    // Kuromoji provided no katakana?
                    if (getKatakana(tokens[i]) == "*") {
                        // Look up all token characters against keys in Katakana map
                        // token may be composed of katakana but tripped up kuromoji
                        // Deal with small tsu
                        romaji = kuromojiFailedConvert(tokens[i])
                    } else {
                        // Convert katakana to romaji
                        var katakana = getKatakana(tokens[i])
                        if (DEBUG) println("Katakana: $katakana")
                        val currentRomaji = kanaToRomaji.convert(katakana)
//                        val nextTokenRomaji = ""

                        // sokuon at end of token list: exclamation mark
                        if (katakana.endsWith("ッ") && i == tokens.size - 1) {
                            //System.out.println("length of katakana:" + katakana.length());
                            val lastIndex = katakana.length - 1
                            katakana = katakana.substring(0, lastIndex)
                            // Remove space added in last loop
//                            if (i != 0) buffer.deleteCharAt(buffer.length - 1)
                            romaji = kanaToRomaji.convert(katakana) + "!"
                            if (DEBUG) {
                                println("Exclamation sokuon, romaji becomes: $romaji")
                            }
                        } else if (katakana.endsWith("ッ")) {
                            romaji = smallTsuRomaji(tokens[i], tokens[i + 1])
                            if (DEBUG) {
                                println("Sokuon detected, merged romaji :$romaji")
                            }
                            // Skip next token since it has been processed here and merged
                            i++
                        } else {
                            romaji = currentRomaji
                        }
                    }

                    // Capitalization
                    if (romaji !== "") {
                        if (CAPITALIZE_WORDS) {
                            surfaceToRomaji[tokens[i].surface] = romaji
//                            buffer.append(romaji.substring(0, 1)/* .toUpperCase() */)
//                            buffer.append(romaji.substring(1))
                        } else {
                            // Convert foreign katakana words to uppercase
                            if (tokens[i].surface == tokens[i].reading) // detect katakana
                                romaji = romaji.toUpperCase()
//                            buffer.append(romaji)
                            surfaceToRomaji[tokens[i].surface] = romaji
                        }
                    }
                }
            }
//            if (space) buffer.append(" ")
            i++
        }

        if (DEBUG) println("----------------------------")
        return surfaceToRomaji
    }

    companion object {
        private const val DEBUG = false
        private const val CAPITALIZE_WORDS = true
        private const val OUTPUT_MODE = "pronunciation"

        // OUTPUT_MODE field determines whether we output reading or pronounciation
        fun getKatakana(token: Token): String {
            var katakana = ""
            if (OUTPUT_MODE === "pronunciation") katakana =
                token.pronunciation else if (OUTPUT_MODE === "reading") katakana = token.reading
            return katakana
        }

        fun smallTsuRomaji(token: Token, nextToken: Token): String {
            val kanaToRomaji = KanaToRomaji()
            val romaji: String
            val nextRomaji = kanaToRomaji.convert(getKatakana(nextToken))
            val currentRomaji = kanaToRomaji.convert(getKatakana(token).substring(0, token.surface.length - 1))
            romaji = currentRomaji + nextRomaji.substring(0, 1) + nextRomaji
            return romaji
        }

        fun kuromojiFailedConvert(token: Token): String {
            val kanaToRomaji = KanaToRomaji()
            val buffer = StringBuffer()
            val surface = token.surface
            if (surface.contains("ッ") && !surface.endsWith("ッ")) {
                val splitTokenSurface = surface.split("ッ").toTypedArray()
                val romaji1 = kanaToRomaji.convert(splitTokenSurface[0])
                val romaji2 = kanaToRomaji.convert(splitTokenSurface[1])
                buffer.append(romaji1 + romaji2.substring(0, 1) + romaji2)
            } else {
                for (c in token.surface.toCharArray()) {
                    // Katakana character?  Convert to romaji
                    if (kanaToRomaji.m.containsKey(c.toString())) {
                        buffer.append(kanaToRomaji.convert(c.toString()))
                    } else {
                        // Append as is
                        buffer.append(c.toString())
                    }
                }
            }
            return buffer.toString()
        }
    }
}