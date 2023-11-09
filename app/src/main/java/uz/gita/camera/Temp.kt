package uz.gita.camera

class Temp {

    var fruitType = "UnKnown"

    infix fun type(mType: String): Unit {
        this.fruitType = mType
    }
}
    fun main() {
        inlinedFunc(
            {
                println("")
                return
            }, { print("") }
        )
    }


    inline fun inlinedFunc(call1: () -> Unit, call2: () -> Unit) {
        call1.invoke()
        call2.invoke()
    }

    fun inlinedFunc2(call1: () -> Unit, call2: () -> Unit) {
        call1.invoke()
        call2.invoke()
    }

