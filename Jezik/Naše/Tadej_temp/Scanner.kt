import java.io.File

class Scanner {

    var table = Array(22) { IntArray(256) }
    var symbol = arrayListOf<String>("error", "whitespace", "variable", "variable", "reserved", "variable", "variable", "reserved",
        "number", "error", "number", "operator", "operator", "checker", "error", "seperator",
        "variable", "error", "error", "error", "error", "action")

    var tokens = arrayListOf<Token>()
    var everything = ""

    init {

        for (i in 0..21) {
            for (j in 0..255) {
                table[i][j] = 0
            }
        }

        //numbers
        for(i in 48..57) {
            table[0][i] = 8;
            table[8][i] = 8;
            table[9][i] = 10;
            table[10][i] = 10;
            table[2][i] = 16;
            table[16][i] = 16
        }
        table[8]['.'.toInt()] = 9;


        //seperator
        table[0]['('.toInt()] = 15;
        table[0][')'.toInt()] = 15;
        table[0]['{'.toInt()] = 15;
        table[0]['}'.toInt()] = 15;
        table[0][']'.toInt()] = 15;
        table[0]['['.toInt()] = 15;
        table[0][','.toInt()] = 15
        table[0][34] = 15


        //whitespace
        table[0][9] = 1;
        table[0][10] = 1;
        table[0][32] = 1;
        table[1][9] = 1;
        table[1][10] = 1;
        table[1][32] = 1;

        //big chars
        for (i in 65..90) {
            table[0][i] = 2
            table[2][i] = 2

        }
        //small chars
        for (i in 97..122) {
            table[0][i] = 2
            table[2][i] = 2
            //for
            table[5][i] = 2
            table[6][i] = 2
            table[7][i] = 2
            //if
            table[3][i] = 2
            table[4][i] = 2
            //action
            table[16][i] = 2
            table[17][i] = 2
            table[18][i] = 2
            table[19][i] = 2
            table[20][i] = 2
            table[21][i] = 2

        }

        //reserved for
        table[0]['f'.toInt()] = 5;
        table[5]['o'.toInt()] = 6;
        table[6]['r'.toInt()] = 7;

        //reserved if
        table[0]['i'.toInt()] = 3
        table[3]['f'.toInt()] = 4


        //operator
        table[0]['*'.toInt()] = 11;
        table[0]['+'.toInt()] = 11;
        table[0]['-'.toInt()] = 11;
        table[0]['/'.toInt()] = 11;
        table[0]['%'.toInt()] = 11;
        table[0]['^'.toInt()] = 11;
        table[0]['='.toInt()] = 12;
        table[12]['='.toInt()] = 13
        table[0]['!'.toInt()] = 14
        table[14]['='.toInt()] = 13
        table[0]['<'.toInt()] = 13
        table[0]['>'.toInt()] = 13

        //action
        table[0]['a'.toInt()] = 16
        table[16]['c'.toInt()] = 17
        table[17]['t'.toInt()] = 18
        table[18]['i'.toInt()] = 19
        table[19]['o'.toInt()] = 20
        table[20]['n'.toInt()] = 21

    }

    fun readFile(fileName: String){
        File(fileName).forEachLine { everything += it }
        everything = everything.replace(" ", "", false)
        everything = everything.replace("\n", "", false)
        everything = everything.replace("\t", "", false)


    }

    fun getTokens(){

        var state = 0
        var lexem = ""
        var i = 0
        var lexemPrev = ""
        var statePrev = 0

        while(i < everything.length){

            lexemPrev = lexem
            statePrev = state
            state = table[state][everything[i].toInt()];

            if(everything[i] != '\t' && everything[i] != '\n' && everything[i] != ' ')
                lexem += everything[i]

            if(state == 0){
                tokens.add(Token(lexemPrev, symbol[statePrev]))
                i--
                lexem = ""
            }
            i++
        }
        tokens.add(Token(lexem, symbol[state]))

    }

}