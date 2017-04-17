package net.podkopaev.grammar.BioGrammar

import net.podkopaev.cpsComb.*

/*
****H-type grammar.****

S -> A1A5 & A1A60   | A1A5 & A2A60   | A1A5 & A3A60   | A1A5 & A4A60   | A2A5 & A1A60
   | A2A5 & A2A60   | A2A5 & A3A60   | A2A5 & A4A60   | A3A5 & A1A60   | A3A5 & A2A60
   | A3A5 & A3A60   | A3A5 & A4A60   | A4A5 & A1A60   | A4A5 & A2A60   | A4A5 & A3A60
   | A4A5 & A4A60   | A8A46 & A1A60  | A8A46 & A2A60  | A8A46 & A3A60  | A8A46 & A4A60

A1 -> a
A2 -> u
A3 -> g
A4 -> c

A5 -> A1A6   | A2A6   | A3A6    | A4A6   | A8A54cd
A6 -> A1A7   | A2A7   | A3A7    | A4A7   | A8A54
A7 -> A1A7   | A2A7   | A3A7    | A4A7   | A8A54
A8 -> A1A9   | A2A10  | A3A11   | A4A12  | A2A12   | A3A9   | A1A8   | A2A8
             | A3A8   | A4A8    | A8A1   | A8A2    | A8A3   | A8A4   | A43A44

A9 -> A13A2
A10 -> A13A1
A11 -> A13A4
A12 -> A13A3

A13 -> A1A14   | A2A15   | A3A16   | A4A17   | A2A17   | A3A14   | A1A13   | A2A13
               | A3A13   | A4A13   | A13A1   | A13A2   | A13A3   | A13A4   | A43A44

A14 -> A18A2
A15 -> A18A1
A16 -> A18A4
A17 -> A18A3

A18 -> A1A19   | A2A20   | A3A21   | A4A22   | A2A22   | A3A19   | A1A18   | A2A18
               | A3A18   | A4A18   | A18A1   | A18A2   | A18A3   | A18A4   | A43A44

A19 -> A23A2
A20 -> A23A1
A21 -> A23A4
A22 -> A23A3

A23 -> A1A24   | A2A25   | A3A26   | A4A27   | A2A27   | A3A24   | A1A23   | A2A23
               | A3A23   | A4A23   | A23A1   | A23A2   | A23A3   | A23A4   | A43A44

A24 -> A28A2
A25 -> A28A1
A26 -> A28A4
A27 -> A28A3

A28 -> A1A29   | A2A30   | A3A31   | A4A32   | A2A32   | A3A29   | A1A28   | A2A28
               | A3A28   | A4A28   | A28A1   | A28A2   | A28A3   | A28A4   | A43A44

A29 -> A33A2
A30 -> A33A1
A31 -> A33A4
A32 -> A33A3

A33 -> A1A34   | A2A35   | A3A36   | A4A37   | A2A37   | A3A34   | A1A33   | A2A33
               | A3A33   | A4A33   | A33A1   | A33A2   | A33A3   | A33A4   | A43A44

A34 -> A38A2
A35 -> A38A1
A36 -> A38A4
A37 -> A38A3

A38 -> A1A39   | A2A40   | A3A41   | A4A42   | A2A42   | A3A39   | A1A38   | A2A38
               | A3A38   | A4A38   | A38A1   | A38A2   | A38A3   | A38A4   | A43A44

A39 -> A38A2
A40 -> A38A1
A41 -> A38A4
A42 -> A38A3
A43 -> a   | u   | g   | c

A44 -> A1A45   | A2A45   | A3A45   | A4A45
A45 -> A1A46   | A2A46   | A3A46   | A4A46
A46 -> A1A47   | A2A47   | A3A47   | A4A47
A47 -> A1A48   | A2A48   | A3A48   | A4A48   | a   | u   | g   | c
A48 -> A1A49   | A2A49   | A3A49   | A4A49   | a   | u   | g   | c
A49 -> A1A50   | A2A50   | A3A50   | A4A50   | a   | u   | g   | c
A50 -> A1A51   | A2A51   | A3A51   | A4A51   | a   | u   | g   | c
A51 -> A1A52   | A2A52   | A3A52   | A4A52   | a   | u   | g   | c
A52 -> A1A52   | A2A52   | A3A52   | A4A52   | a   | u   | g   | c

A53 -> a   | u   | g   | c
A54 -> A53A55
A55 -> A53A56
A56 -> A53A57
A57 -> A53A58
A58 -> A53A59
A59 -> A53A59  | A53A53
A60 -> A1A61   | A2A61   | A3A61   | A4A61
A61 -> A1A62   | A2A62   | A3A62   | A4A62
A62 -> A1A62   | A2A62   | A3A62   | A4A62   | A63A129   | A63A128
               | A1A64   | A2A65   | A3A66   | A4A67     | A2A67   | A3A64

A63 -> A1A64   | A2A65   | A3A66   | A4A67   | A2A67   | A3A64   | A1A63   | A2A63
               | A3A63   | A4A63   | A63A1   | A63A2   | A63A3   | A63A4   | A118A119

A64 -> A68A2
A65 -> A68A1
A66 -> A68A4
A67 -> A68A3
A68 -> A1A69   | A2A70   | A3A71   | A4A72   | A2A72   | A3A69   | A1A68   | A2A68
               | A3A68   | A4A68   | A68A1   | A68A2   | A68A3   | A68A4   | A118A119

A69 -> A73A2
A70 -> A73A1
A71 -> A73A4
A72 -> A73A3
A73 -> A1A74   | A2A75   | A3A76   | A4A77   | A2A77   | A3A74   | A1A73   | A2A73
               | A3A73   | A4A73   | A73A1   | A73A2   | A73A3   | A73A4   | A118A119

A74 -> A78A2
A75 -> A78A1
A76 -> A78A4
A77 -> A78A3
A78 -> A1A79   | A2A80   | A3A81   | A4A82   | A2A82   | A3A79   | A1A78   | A2A78
               | A3A78   | A4A78   | A78A1   | A78A2   | A78A3   | A78A4   | A118A119

A79 -> A83A2
A80 -> A83A1
A81 -> A83A4
A82 -> A83A3
A83 -> A1A84   | A2A85   | A3A86   | A4A87   | A2A87   | A3A84   | A1A83   | A2A83
               | A3A83   | A4A83   | A83A1   | A83A2   | A83A3   | A83A4   | A118A119

A84 -> A88A2
A85 -> A88A1
A86 -> A88A4
A87 -> A88A3
A88 -> A1A89   | A2A90   | A3A91   | A4A92   | A2A92   | A3A89   | A1A88   | A2A88
               | A3A88   | A4A88   | A88A1   | A88A2   | A88A3   | A88A4   | A118A119

A89 -> A93A2
A90 -> A93A1
A91 -> A93A4
A92 -> A93A3
A93 -> A1A94   | A2A95   | A3A96   | A4A97   | A2A97   | A3A94   | A1A93   | A2A93
               | A3A93   | A4A93   | A93A1   | A93A2   | A93A3   | A93A4   | A118A119

A94 -> A98A2
A95 -> A98A1
A96 -> A98A4
A97 -> A98A3
A98 -> A1A99   | A2A100   | A3A101   | A4A102   | A2A102   | A3A99   | A1A98   | A2A98
               | A3A98    | A4A98    | A98A1    | A98A2    | A98A3   | A98A4   | A118A119

A99 -> A103A2
A100 -> A103A1
A101 -> A103A4
A102 -> A103A3
A103 -> A1A104   | A2A105   | A3A106   | A4A107   | A2A107   | A3A104   | A1A103   | A2A103
                 | A3A103   | A4A103   | A103A1   | A103A2   | A103A3   | A103A4   | A118A119

A104 -> A108A2
A105 -> A108A1
A106 -> A108A4
A107 -> A108A3
A108 -> A1A109   | A2A110   | A3A111   | A4A112   | A2A112   | A3A109   | A1A108   | A2A108
                 | A3A108   | A4A108   | A108A1   | A108A2   | A108A3   | A108A4   | A118A119

A109 -> A113A2
A110 -> A113A1
A111 -> A113A4
A112 -> A113A3
A113 -> A1A114   | A2A115   | A3A116   | A4A117   | A2A117   | A3A114   | A1A113   | A2A113
                 | A3A113   | A4A113   | A113A1   | A113A2   | A113A3   | A113A4   | A118A119

A114 -> A113A2
A115 -> A113A1
A116 -> A113A4
A117 -> A113A3
A118 -> a   | u   | g   | c
A119 -> A1A120   | A2A120   | A3A120   | A4A120
A120 -> A1A121   | A2A121   | A3A121   | A4A121
A121 -> A1A122   | A2A122   | A3A122   | A4A122
A122 -> A1A123   | A2A123   | A3A123   | A4A123   | a   | u   | g   | c
A123 -> A1A124   | A2A124   | A3A124   | A4A124   | a   | u   | g   | c
A124 -> A1A125   | A2A125   | A3A125   | A4A125   | a   | u   | g   | c
A125 -> A1A126   | A2A126   | A3A126   | A4A126   | a   | u   | g   | c
A126 -> A1A127   | A2A127   | A3A127   | A4A127   | a   | u   | g   | c
A127 -> A1A127   | A2A127   | A3A127   | A4A127   | a   | u   | g   | c
A128 -> a   | u   | g   | c
A129 -> A128A130   | A128A128
A130 -> A128A131   | A128A128
A131 -> A128A132   | A128A128
A132 -> A128A132   | A128A128

 */

val A1 = terminal("a")
val A2 = terminal("u")
val A3 = terminal("g")
val A4 = terminal("c")

val A8 = fix { A8: Recognizer<Int> -> seq(A1, A9) / seq(A2, A10) / seq(A3, A11) /
        seq(A4, A12) / seq(A2, A12) / seq(A3, A9) / seq(A1, A8) / seq(A2, A8) /
        seq(A3, A8) / seq(A4, A8) / seq(A8, A1) / seq(A8, A2) / seq(A8, A3) /
        seq(A8, A4) / seq(A43, A44)
}

val A7 = fix { A7: Recognizer<Int> -> seq(A1, A7) / seq(A2, A7) / seq(A3, A7) /
        seq(A4, A7) / seq(A8, A1) }

val A13 = fix { A13: Recognizer<Int> -> seq(A1, A14) / seq(A2, A15) / seq(A3, A16)/
        seq(A4, A17) / seq(A2, A17) / seq(A3, A14) / seq(A1, A13) / seq(A2, A13) /
        seq(A3, A13) / seq(A4, A13) / seq(A13, A1) / seq(A13, A2) / seq(A13, A3) /
        seq(A13, A4) / seq(A43, A44)
}

val A9  = seq(A13, A2)
val A10 = seq(A13, A1)
val A11 = seq(A13, A4)
val A12 = seq(A13, A3)

val A18 = fix { A18: Recognizer<Int> -> seq(A1, A19) / seq(A2, A20) / seq(A3, A21) /
        seq(A4, A22) / seq(A2, A22) / seq(A3, A19) / seq(A1, A18) / seq(A2, A18) /
        seq(A3, A18) / seq(A4, A18) / seq(A18, A1) / seq(A18, A2) / seq(A18, A3) /
        seq(A18, A4) / seq(A43, A44)
}

val A14 = seq(A18, A2)
val A15 = seq(A18, A1)
val A16 = seq(A18, A4)
val A17 = seq(A18, A3)

val A23 = fix { A23: Recognizer<Int> -> seq(A1, A24) / seq(A2, A25) / seq(A3, A26) /
        seq(A4, A27) / seq(A2, A27) / seq(A3, A24) / seq(A1, A23) / seq(A2, A23) /
        seq(A3, A23) / seq(A4,A23) / seq(A23, A1) / seq(A23, A2) / seq(A23, A3) /
        seq(A23, A4) / seq(A43, A44)
}

val A19 = seq(A23, A2)
val A20 = seq(A23, A1)
val A21 = seq(A23, A4)
val A22 = seq(A23, A3)

val A28 = fix { A28: Recognizer<Int> -> seq(A1, A29) / seq(A2, A30) / seq(A3, A31) /
        seq(A4, A32) / seq(A2, A32) / seq(A3, A29) / seq(A1, A28) / seq(A2, A28) /
        seq(A3, A28) / seq(A4,A28) / seq(A28, A1) / seq(A28, A2) / seq(A28, A3) /
        seq(A28, A4) /seq(A43, A44)
}

val A24 = seq(A28, A2)
val A25 = seq(A28, A1)
val A26 = seq(A28, A4)
val A27 = seq(A28, A3)

val A33 = fix { A33: Recognizer<Int> -> seq(A1, A34) / seq(A2, A35) / seq(A3, A36) /
        seq(A4, A37) / seq(A2, A37) / seq(A3, A34) / seq(A1, A33) / seq(A2, A33) /
        seq(A3, A33) / seq(A4,A33) / seq(A33, A1) / seq(A33, A2) / seq(A33, A3) /
        seq(A33, A4) /seq(A43, A44)
}

val A29 = seq(A33, A2)
val A30 = seq(A33, A1)
val A31 = seq(A33, A4)
val A32 = seq(A33, A3)

val A38 = fix { A38: Recognizer<Int> -> seq(A1, A39) / seq(A2, A40) / seq(A3, A41) /
        seq(A4, A42) / seq(A2, A42) / seq(A3, A39) / seq(A1, A38) / seq(A2, A38) /
        seq(A3, A38) / seq(A4,A38) / seq(A38, A1) / seq(A38, A2) / seq(A38, A3) /
        seq(A38, A4) /seq(A43, A44)
}

val A34 = seq(A38, A2)
val A35 = seq(A38, A1)
val A36 = seq(A38, A3)
val A37 = seq(A38, A3)

val A39: Recognizer<Int> = seq(A38, A2)
val A40: Recognizer<Int> = seq(A38, A1)
val A41: Recognizer<Int> = seq(A38, A4)
val A42: Recognizer<Int> = seq(A38, A3)

val A43 = A1 / A2 / A3 / A4

val A52 = fix { A52: Recognizer<Int> -> seq(A1, A52) / seq(A2, A52) / seq(A3, A52) /
        seq(A4, A52) / A1 / A2 / A3 / A4 }

val A51 = seq(A1, A52) / seq(A2, A52) / seq(A3, A52) / seq(A4, A52) / A1 / A2 / A3 / A4
val A50 = seq(A1, A51) / seq(A2, A51) / seq(A3, A51) / seq(A4, A51) / A1 / A2 / A3 / A4
val A49 = seq(A1, A50) / seq(A2, A50) / seq(A3, A50) / seq(A4, A50) / A1 / A2 / A3 / A4
val A48 = seq(A1, A49) / seq(A2, A49) / seq(A3, A49) / seq(A4, A49) / A1 / A2 / A3 / A4
val A47 = seq(A1, A48) / seq(A2, A48) / seq(A3, A48) / seq(A4, A48) / A1 / A2 / A3 / A4
val A46 = seq(A1, A47) / seq(A2, A47) / seq(A3, A47) / seq(A4, A47)
val A45 = seq(A1, A46) / seq(A2, A46) / seq(A3, A46) / seq(A4, A46)
val A44 = seq(A1, A45) / seq(A2, A45) / seq(A3, A45) / seq(A4, A45)

val A53 = A1 / A2 / A3 / A4

val A59 = fix { A59: Recognizer<Int> -> seq(A53, A59) / seq(A53, A53) }
val A58 = seq(A53, A59)
val A57 = seq(A53, A58)
val A56 = seq(A53, A57)
val A55 = seq(A53, A56)
val A54 = seq(A53, A55)

val A6 = seq(A1, A7) / seq(A2, A7) / seq(A3, A7) / seq(A4, A7) / seq(A8, A54)
val A5 = seq(A1, A6) / seq(A2, A6) / seq(A3, A6) / seq(A4, A6) / seq(A8, A54)

val A113 = fix { A113: Recognizer<Int> -> seq(A1, A114) / seq(A2, A115) /
        seq(A3, A116) / seq(A4, A117) / seq(A2, A117) / seq(A3, A114) /
        seq(A1, A113) / seq(A2, A113) / seq(A3, A113) / seq(A4, A113) /
        seq(A113, A1) / seq(A113, A2) / seq(A113, A3)/ seq(A113, A4) / seq(A118, A119)
}

val A114: Recognizer<Int> = seq(A113, A2)
val A115: Recognizer<Int> = seq(A113, A1)
val A116: Recognizer<Int> = seq(A113, A4)
val A117: Recognizer<Int> = seq(A113, A3)
val A118 = A1 / A2 / A3 / A4
val A128 = A1 / A2 / A3 / A4

val A132 = fix { A132: Recognizer<Int> -> seq(A128, A132) / seq(A128, A128) }
val A131 = seq(A128, A132) / seq(A128, A128)
val A130 = seq(A128, A131) / seq(A128, A128)
val A129 = seq(A128, A130) / seq(A128, A128)

val A127 = fix { A127: Recognizer<Int> -> seq(A1, A127) / seq(A2, A127) / seq(A3, A127) /
        seq(A4, A127) / A1 / A2 / A3 / A4 }
val A126 = seq(A1, A127) / seq(A2, A127) / seq(A3, A127) / seq(A4, A127) / A1 / A2 / A3 / A4
val A125 = seq(A1, A126) / seq(A2, A126) / seq(A3, A126) / seq(A4, A126) / A1 / A2 / A3 / A4
val A124 = seq(A1, A125) / seq(A2, A125) / seq(A3, A125) / seq(A4, A125) / A1 / A2 / A3 / A4
val A123 = seq(A1, A124) / seq(A2, A124) / seq(A3, A124) / seq(A4, A124) / A1 / A2 / A3 / A4
val A122 = seq(A1, A123) / seq(A2, A123) / seq(A3, A123) / seq(A4, A123) / A1 / A2 / A3 / A4
val A121 = seq(A1, A122) / seq(A2, A122) / seq(A3, A122) / seq(A4, A122)
val A120 = seq(A1, A121) / seq(A2, A121) / seq(A3, A121) / seq(A4, A121)
val A119 = seq(A1, A120) / seq(A2, A120) / seq(A3, A120) / seq(A4, A120)



val A109 = seq(A113, A2)
val A110 = seq(A113, A1)
val A111 = seq(A113, A4)
val A112 = seq(A113, A3)

val A108 = fix { A108: Recognizer<Int> -> seq(A1, A109) / seq(A2, A110) / seq(A3, A111)/
        seq(A4, A112) / seq(A2, A112) / seq(A3, A109) / seq(A1, A108) / seq(A2, A108) /
        seq(A3, A108) / seq(A4, A108) / seq(A108, A1) / seq(A108, A2) / seq(A108, A3)/
        seq(A108, A4) / seq(A118, A119)
}

val A104 = seq(A108, A2)
val A105 = seq(A108, A1)
val A106 = seq(A108, A4)
val A107 = seq(A108, A3)

val A103 = fix { A103: Recognizer<Int> -> seq(A1, A104) / seq(A2, A105) /
        seq(A3, A106) / seq(A4, A107) / seq(A2, A107) / seq(A3, A104) / seq(A1, A103) /
        seq(A2, A103) / seq(A3, A103) / seq(A4, A103) / seq(A103, A1) / seq(A103, A2) /
        seq(A103, A3)/ seq(A103, A4) / seq(A118, A119)
}

val A99  = seq(A103, A2)
val A100 = seq(A103, A1)
val A101 = seq(A103, A4)
val A102 = seq(A103, A3)

val A98 = fix { A98: Recognizer<Int> -> seq(A1, A99) / seq(A2, A100) / seq(A3, A101) /
        seq(A4, A102) / seq(A2, A102) / seq(A3, A99) / seq(A1, A98) / seq(A2, A98) /
        seq(A3, A98) / seq(A4, A98) / seq(A98, A1) / seq(A98, A2) / seq(A98, A3) /
        seq(A98, A4) / seq(A118, A119)
}

val A94 = seq(A98, A2)
val A95 = seq(A98, A1)
val A96 = seq(A98, A4)
val A97 = seq(A98, A3)

val A93 = fix { A93: Recognizer<Int> -> seq(A1, A94) / seq(A2, A95) / seq(A3, A96) /
        seq(A4, A97) / seq(A2, A97) / seq(A3, A94) / seq(A1, A93) / seq(A2, A93) /
        seq(A3, A93) / seq(A4, A93) / seq(A93, A1) / seq(A93, A2) / seq(A93, A3) /
        seq(A93, A4) / seq(A118, A119)
}

val A89 = seq(A93, A2)
val A90 = seq(A93, A1)
val A91 = seq(A93, A4)
val A92 = seq(A93, A3)

val A88 = fix { A88: Recognizer<Int> -> seq(A1, A89) / seq(A2, A90) / seq(A3, A91) /
        seq(A4, A92) / seq(A2, A92) / seq(A3, A89) / seq(A1, A88) / seq(A2, A88) /
        seq(A3, A88) / seq(A4, A88) / seq(A88, A1) / seq(A88, A2) / seq(A88, A3) /
        seq(A88, A4) / seq(A118, A119)
}

val A84 = seq(A88, A2)
val A85 = seq(A88, A1)
val A86 = seq(A88, A4)
val A87 = seq(A88, A3)

val A83 = fix { A83: Recognizer<Int> -> seq(A1, A84) / seq(A2, A85) / seq(A3, A86) /
        seq(A4, A87) / seq(A2, A87) / seq(A3, A84) / seq(A1, A83) / seq(A2, A83) /
        seq(A3, A83) / seq(A4, A83) / seq(A83, A1) / seq(A83, A2) / seq(A83, A3) /
        seq(A83, A4) / seq(A118, A119)
}

val A79 = seq(A83, A2)
val A80 = seq(A83, A1)
val A81 = seq(A83, A4)
val A82 = seq(A83, A3)

val A78 = fix { A78: Recognizer<Int> -> seq(A1, A79) / seq(A2, A80) / seq(A3, A81) /
        seq(A4, A82) / seq(A2, A82) / seq(A3, A79) / seq(A1, A78) / seq(A2, A78) /
        seq(A3, A78) / seq(A4, A73) / seq(A78, A1) / seq(A78, A2) / seq(A78, A3) /
        seq(A78, A4) / seq(A118, A119)
}

val A74: Recognizer<Int> = seq(A78, A2)
val A75: Recognizer<Int> = seq(A78, A1)
val A76: Recognizer<Int> = seq(A78, A4)
val A77: Recognizer<Int> = seq(A78, A3)

val A73 = fix { A73: Recognizer<Int> -> seq(A1, A74) / seq(A2, A75) / seq(A3, A76) /
        seq(A4, A77) / seq(A2, A77) / seq(A3, A77) / seq(A1, A73) / seq(A2, A73) /
        seq(A3, A73) / seq(A4, A73) / seq(A73, A1) / seq(A73, A2) / seq(A73, A3) /
        seq(A73, A4) / seq(A118, A119)
}

val A69 = seq(A73, A2)
val A70 = seq(A73, A1)
val A71 = seq(A73, A4)
val A72 = seq(A73, A3)

val A68 = fix { A68: Recognizer<Int> -> seq(A1, A69) / seq(A2, A70) / seq(A3, A71) /
        seq(A4, A72) / seq(A2, A72) / seq(A3, A69) / seq(A1, A68) / seq(A2, A68) /
        seq(A3, A68) / seq(A4, A68) / seq(A68, A1) / seq(A68, A2) / seq(A68, A3) /
        seq(A68, A4) / seq(A118, A119)
}

val A67 = seq(A68, A3)
val A66 = seq(A68, A4)
val A65 = seq(A68, A1)
val A64 = seq(A68, A2)
val A63 = fix { A63: Recognizer<Int> -> seq(A1, A64) / seq(A2, A65) / seq(A3, A66) /
        seq(A4, A67) / seq(A2, A67) / seq(A3, A64) / seq(A1, A63) / seq(A2, A63) /
        seq(A3, A63) / seq(A4, A63) / seq(A63, A61) / seq(A63, A2) / seq(A63, A3) /
        seq(A63, A4) / seq(A118, A119)
}

val A62: Recognizer<Int> = fix { A62: Recognizer<Int> -> seq(A1, A62) / seq(A2, A62) /
        seq(A3, A62) / seq(A4, A62) / seq(A63, A129) / seq(A63, A128) / seq(A1, A64) /
        seq(A2, A65) / seq(A3, A66) / seq(A4, A67) /
        seq(A2, A67) / seq(A3, A64)
}

val A61 = seq(A1, A62) / seq(A2, A62) / seq(A3, A62) / seq(A4, A62)
val A60 = seq(A1, A61) / seq(A2, A61) / seq(A3, A61) / seq(A4, A61)

val S:Recognizer<Int> = and(seq(A1, A5), seq(A1, A60)) / and(seq(A1, A5), seq(A2, A60)) /
        and(seq(A1, A5), seq(A3, A60)) / and(seq(A1, A5), seq(A4, A60)) /
        and(seq(A2, A5), seq(A1, A60)) / and(seq(A2, A5), seq(A2, A60)) /
        and(seq(A2, A5), seq(A3, A60)) / and(seq(A2, A5), seq(A4, A60)) /
        and(seq(A3, A5), seq(A1, A60)) / and(seq(A3, A5), seq(A2, A60)) /
        and(seq(A3, A5), seq(A3, A60)) / and(seq(A3, A5), seq(A4, A60)) /
        and(seq(A4, A5), seq(A1, A60)) / and(seq(A4, A5), seq(A2, A60)) /
        and(seq(A4, A5), seq(A3, A60)) / and(seq(A4, A5), seq(A4, A60)) /
        and(seq(A8, A46), seq(A1, A60)) / and(seq(A8, A46), seq(A2, A60)) /
        and(seq(A8, A46), seq(A3, A60)) / and(seq(A8, A46), seq(A4, A60))