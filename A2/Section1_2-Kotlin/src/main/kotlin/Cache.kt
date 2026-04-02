package org.example

class Cache<K : Any, V: Any> {
    // k: tipo da chave, V: tipo do valor
    // mutable map é basicamente um dicionario
    private val storage: MutableMap<K, V> = mutableMapOf()

    fun put(key: K, value: V) {
        storage[key] = value
    }


    // se V existir devolve V, se nao existir devolve null
    fun get(key: K): V? {
        return storage[key]
    }

    fun evict(key: K) {
        storage.remove(key)
    }

    fun size(): Int {
        return storage.size
    }

    // k é a chave que vamos procurar. segundo parametro é uma função lambda
    // que não recebe argumentos e retorna o valor V
    // ou seja, quando for chamada, vai gerar um valor
    fun getOrPut(key: K, default: () -> V): V {
        // aqui, o if devolve um valor
        return if (key in storage) { // retorna apenas se existir
            storage[key]!! // !! diz que isto nunca será null.
            // se entrar no if, devolve a ultima linha do bloco
        } else {
            val value = default() // se nao existir, chama a funçao default
            storage[key] = value // guarda no mapa
            value // retorna value
        }
    }

    // (V) -> V função lambda que recebe um tipo V e devolve um tipo V
    fun transform(key: K, action: (V) -> V): Boolean {
        return if (key in storage) {
            val current = storage[key]!! // armazena o valor
            val newValue = action(current) // retorna um valor do tipo V (transformado)
            storage[key] = newValue // armazena na storage
            true
        } else {
            false
        }

    }

    // snapshot é uma função que retorna um map
    // o objetivo é retornar uma copia do map original para n mexer nele
    fun snapshot(): Map<K, V> {
        return storage.toMap()
    }
}