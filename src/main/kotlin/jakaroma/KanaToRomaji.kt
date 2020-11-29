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
 Edited by paranoidcake in order to work as a Java library rather than a command line script
 */
package jakaroma

import java.util.*

class KanaToRomaji {
    var m: MutableMap<String, String> = HashMap()
    fun convert(s: String): String {
        val t = StringBuilder()
        var i = 0
        while (i < s.length) {
            if (i <= s.length - 2) {
                if (m.containsKey(s.substring(i, i + 2))) {
                    t.append(m[s.substring(i, i + 2)])
                    i++
                } else if (m.containsKey(s.substring(i, i + 1))) {
                    t.append(m[s.substring(i, i + 1)])
                } else if (s[i] == 'ッ') {
                    t.append(m[s.substring(i + 1, i + 2)]!![0])
                } else {
                    t.append(s[i])
                }
            } else {
                if (m.containsKey(s.substring(i, i + 1))) {
                    t.append(m[s.substring(i, i + 1)])
                } else {
                    t.append(s[i])
                }
            }
            i++
        }
        return t.toString()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val k2r = KanaToRomaji()
            val strs = arrayOf(
                "ピュートフクジャガー",
                "マージャン",
                "タンヤオトイトイドラドラ",
                "キップ",
                "プリキュア",
                "シャーペン",
                "カプッ",
                "@マーク",
                "ティーカップ",
                "ビルディング",
                "ロッポンギヒルズ",
                "トッツィ"
            )
            var num = 1
            for (s in strs) {
                println(
                    String.format("%1$2d", num++) +
                            " : " + s + "→" + k2r.convert(s)
                )
            }
        }
    }

    // Constructor
    init {
        m["ア"] = "a"
        m["イ"] = "i"
        m["ウ"] = "u"
        m["エ"] = "e"
        m["オ"] = "o"
        m["カ"] = "ka"
        m["キ"] = "ki"
        m["ク"] = "ku"
        m["ケ"] = "ke"
        m["コ"] = "ko"
        m["サ"] = "sa"
        m["シ"] = "shi"
        m["ス"] = "su"
        m["セ"] = "se"
        m["ソ"] = "so"
        m["タ"] = "ta"
        m["チ"] = "chi"
        m["ツ"] = "tsu"
        m["テ"] = "te"
        m["ト"] = "to"
        m["ナ"] = "na"
        m["ニ"] = "ni"
        m["ヌ"] = "nu"
        m["ネ"] = "ne"
        m["ノ"] = "no"
        m["ハ"] = "ha"
        m["ヒ"] = "hi"
        m["フ"] = "fu"
        m["ヘ"] = "he"
        m["ホ"] = "ho"
        m["マ"] = "ma"
        m["ミ"] = "mi"
        m["ム"] = "mu"
        m["メ"] = "me"
        m["モ"] = "mo"
        m["ヤ"] = "ya"
        m["ユ"] = "yu"
        m["ヨ"] = "yo"
        m["ラ"] = "ra"
        m["リ"] = "ri"
        m["ル"] = "ru"
        m["レ"] = "re"
        m["ロ"] = "ro"
        m["ワ"] = "wa"
        m["ヲ"] = "wo"
        m["ン"] = "n"
        m["ガ"] = "ga"
        m["ギ"] = "gi"
        m["グ"] = "gu"
        m["ゲ"] = "ge"
        m["ゴ"] = "go"
        m["ザ"] = "za"
        m["ジ"] = "ji"
        m["ズ"] = "zu"
        m["ゼ"] = "ze"
        m["ゾ"] = "zo"
        m["ダ"] = "da"
        m["ヂ"] = "ji"
        m["ヅ"] = "zu"
        m["デ"] = "de"
        m["ド"] = "do"
        m["バ"] = "ba"
        m["ビ"] = "bi"
        m["ブ"] = "bu"
        m["ベ"] = "be"
        m["ボ"] = "bo"
        m["パ"] = "pa"
        m["ピ"] = "pi"
        m["プ"] = "pu"
        m["ペ"] = "pe"
        m["ポ"] = "po"
        m["キャ"] = "kya"
        m["キュ"] = "kyu"
        m["キョ"] = "kyo"
        m["シャ"] = "sha"
        m["シュ"] = "shu"
        m["ショ"] = "sho"
        m["チャ"] = "cha"
        m["チュ"] = "chu"
        m["チョ"] = "cho"
        m["ニャ"] = "nya"
        m["ニュ"] = "nyu"
        m["ニョ"] = "nyo"
        m["ヒャ"] = "hya"
        m["ヒュ"] = "hyu"
        m["ヒョ"] = "hyo"
        m["リャ"] = "rya"
        m["リュ"] = "ryu"
        m["リョ"] = "ryo"
        m["ギャ"] = "gya"
        m["ギュ"] = "gyu"
        m["ギョ"] = "gyo"
        m["ジャ"] = "ja"
        m["ジュ"] = "ju"
        m["ジョ"] = "jo"
        m["ティ"] = "ti"
        m["ディ"] = "di"
        m["ツィ"] = "tsi"
        m["ヂャ"] = "dya"
        m["ヂュ"] = "dyu"
        m["ヂョ"] = "dyo"
        m["ビャ"] = "bya"
        m["ビュ"] = "byu"
        m["ビョ"] = "byo"
        m["ピャ"] = "pya"
        m["ピュ"] = "pyu"
        m["ピョ"] = "pyo"
        m["ー"] = "-"
    }
}