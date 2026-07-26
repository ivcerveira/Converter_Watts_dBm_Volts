import kotlin.math.log10

/**
 * Converte uma potência para dBm.
 *
 * @param potencia Valor da potência.
 * @param unidade Unidade de entrada: "mW", "W" ou "kW".
 * @return Potência convertida para dBm.
 */
fun converterParaDbm(potencia: Double, unidade: String): Double {
    require(potencia > 0) {
        "A potência deve ser maior que zero."
    }

    val potenciaEmMilliwatts = when (unidade.lowercase()) {
        "mw" -> potencia
        "w" -> potencia * 1_000
        "kw" -> potencia * 1_000_000

        else -> throw IllegalArgumentException(
            "Unidade inválida. Utilize mW, W ou kW."
        )
    }

    return 10 * log10(potenciaEmMilliwatts)
}

fun main() {
    try {
        println("Conversor de Potência para dBm")
        println()

        print("Digite o valor da potência: ")
        val potencia = readln().replace(",", ".").toDouble()

        println("Escolha a unidade:")
        println("1 - mW")
        println("2 - W")
        println("3 - kW")
        print("Opção: ")

        val unidade = when (readln().toInt()) {
            1 -> "mW"
            2 -> "W"
            3 -> "kW"
            else -> throw IllegalArgumentException("Opção inválida.")
        }

        val resultado = converterParaDbm(potencia, unidade)

        println()
        println("%.6f %s = %.2f dBm".format(
            potencia,
            unidade,
            resultado
        ))
    } catch (_: NumberFormatException) {
        println("Erro: digite somente valores numéricos válidos.")
    } catch (erro: IllegalArgumentException) {
        println("Erro: ${erro.message}")
    }
}