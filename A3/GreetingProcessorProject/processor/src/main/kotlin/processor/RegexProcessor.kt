package org.example.processor

import org.example.annotations.Extract
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_23)
@SupportedAnnotationTypes("org.example.annotations.Extract")
class RegexProcessor : AbstractProcessor() {

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val classMethodMap = mutableMapOf<TypeElement, MutableList<ExecutableElement>>()

        for (element in roundEnv.getElementsAnnotatedWith(Extract::class.java)) {
            if (element is ExecutableElement) {
                val enclosingClass = element.enclosingElement as TypeElement
                classMethodMap.computeIfAbsent(enclosingClass) { mutableListOf() }.add(element)
            }
        }

        for ((classElement, methods) in classMethodMap) {
            generateExtractorClass(classElement, methods)
        }
        return true
    }

    private fun generateExtractorClass(classElement: TypeElement, methods: List<ExecutableElement>) {
        val packageName = processingEnv.elementUtils.getPackageOf(classElement).toString()
        val originalClassName = classElement.simpleName.toString()
        val extractorClassName = "${originalClassName}Extractor"

        // 1. Criar a classe que HERDA da original
        val classBuilder = TypeSpec.classBuilder(extractorClassName)
            .superclass(ClassName(packageName, originalClassName)) // : DataProcessor
            .addModifiers(KModifier.PUBLIC)
            // 2. Construtor primário que recebe 'input' e passa para o pai
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("input", String::class)
                    .build()
            )
            .addSuperclassConstructorParameter("input")

        // 3. Gerar os overrides para cada método abstrato
        for (method in methods) {
            val methodName = method.simpleName.toString()
            val regexValue = method.getAnnotation(Extract::class.java).regex

            val methodBuilder = FunSpec.builder(methodName)
                .addModifiers(KModifier.OVERRIDE) // É um override do método abstrato
                .returns(String::class.asTypeName().copy(nullable = true)) // Retorna String?

            // Lógica Regex pedida no enunciado
            methodBuilder.addStatement("val match = Regex(%S).find(input)", regexValue)
            methodBuilder.addStatement("return match?.groupValues?.get(1)")

            classBuilder.addFunction(methodBuilder.build())
        }

        val file = FileSpec.builder(packageName, extractorClassName).build()

        try {
            val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
            if (kaptKotlinGeneratedDir != null) {
                FileSpec.builder(packageName, extractorClassName)
                    .addType(classBuilder.build())
                    .build()
                    .writeTo(File(kaptKotlinGeneratedDir))
            }
        } catch (e: Exception) {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Erro: ${e.message}")
        }
    }
}