package lab2.bicycles

class GenericBox<T>(var value : T) {    // because for some reason i cant change int in hashmap directly
    fun get() = value
    fun set(new : T) {
        value = new
    }
}