enum class UnidadePotenciaLogaritmica(
    private val descricao: String,
    val simbolo: String
) {
    DBM("Decibel-milliwatt (dBm)", "dBm"),
    DBW("Decibel-watt (dBW)", "dBW");

    override fun toString(): String = descricao
}
