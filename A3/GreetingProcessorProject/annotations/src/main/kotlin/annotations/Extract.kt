package org.example.annotations

@Target(AnnotationTarget.FUNCTION) // alvo da annotation, ao usar isto estamos a dizer que 
                                   // a anotation extract so pode ser aplicada a funções 

/* esta linha diz que a annotation extract o existe no codigo fonte e é usada
durante a compilação. depois da comp n fica disponivel em runtime
é adequado pois o objetivo é que o processor leia a annotation durante a comp  */
@Retention(AnnotationRetention.SOURCE)
annotation class Extract(val regex: String) 
// regex aqui n faz nada, é so uma variavel
// quem vai interpretar regex é o processor

/*
extract é uma annotation class
annotation class é uma etiqueta/metadado que colocamos no codigo
mas que por si só não altera o comportamento do programa
 */