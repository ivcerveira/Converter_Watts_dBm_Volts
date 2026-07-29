import kotlin.math.log10

object ConversorPotencia {

    /**
     * Converte uma potência em mW, W ou kW para dBm.
     *
     * @param potencia Valor da potência.
     * @param unidade Unidade da potência informada.
     * @return Potência convertida para dBm.
     */
    fun converterParaDbm(
        potencia: Double,
        unidade: UnidadePotencia
    ): Double {
        require(potencia > 0) {
            "A potência deve ser maior que zero."
        }

        val potenciaEmMilliwatts =
            potencia * unidade.fatorParaMilliwatts

        return 10 * log10(potenciaEmMilliwatts)
    }
}