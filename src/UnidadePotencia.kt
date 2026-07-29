enum class UnidadePotencia(
    private val descricao: String,
    val simbolo: String,
    val fatorParaMilliwatts: Double
) {
    PICOWATT("Picowatt (pW)", "pW", 0.000000001),
    NANOWATT("Nanowatt (nW)", "nW", 0.000001),
    MICROWATT("Microwatt (µW)", "µW", 0.001),
    MILLIWATT("Milliwatt (mW)", "mW", 1.0),
    WATT("Watt (W)", "W", 1_000.0),
    KILOWATT("Kilowatt (kW)", "kW", 1_000_000.0),
    MEGAWATT("Megawatt (MW)", "MW", 1_000_000_000.0);

    override fun toString(): String {
        return descricao
    }
}
