import kotlin.math.log10
import kotlin.math.pow

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
        unidade: UnidadePotenciaLinear
    ): Double {
        require(potencia > 0) {
            "A potência deve ser maior que zero."
        }

        val potenciaEmMilliwatts =
            potencia * unidade.fatorParaMilliwatts

        return 10 * log10(potenciaEmMilliwatts)
    }

    fun converterParaLogaritmica(
        potencia: Double,
        unidadeLinear: UnidadePotenciaLinear,
        unidadeLogaritmica: UnidadePotenciaLogaritmica
    ): Double {
        val potenciaEmDbm = converterParaDbm(potencia, unidadeLinear)

        return when (unidadeLogaritmica) {
            UnidadePotenciaLogaritmica.DBM -> potenciaEmDbm
            UnidadePotenciaLogaritmica.DBW -> potenciaEmDbm - 30.0
        }
    }

    fun converterParaLinear(
        potencia: Double,
        unidadeLogaritmica: UnidadePotenciaLogaritmica,
        unidadeLinear: UnidadePotenciaLinear
    ): Double {
        val potenciaEmDbm = when (unidadeLogaritmica) {
            UnidadePotenciaLogaritmica.DBM -> potencia
            UnidadePotenciaLogaritmica.DBW -> potencia + 30.0
        }
        val potenciaEmMilliwatts = 10.0.pow(potenciaEmDbm / 10.0)

        return potenciaEmMilliwatts / unidadeLinear.fatorParaMilliwatts
    }
}
