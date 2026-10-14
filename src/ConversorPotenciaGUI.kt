import java.awt.*
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.math.BigDecimal
import java.math.RoundingMode
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.AbstractDocument
import javax.swing.text.AttributeSet
import javax.swing.text.DocumentFilter

class ConversorPotenciaGUI : JFrame() {

    private companion object {
        const val REDUCAO_LARGURA_CAIXA = 170
        const val REDUCAO_LARGURA_CAMPO_LINEAR = 120
        const val REDUCAO_LARGURA_CAMPO_LOGARITMICO = 120
        const val LIMITE_POTENCIA_LINEAR = 15
        const val LIMITE_POTENCIA_LOGARITMICA = 9
    }

    private val caixaUnidadePotenciaLinear = JComboBox(
        UnidadePotenciaLinear.entries.toTypedArray()
    )
    private val campoPotenciaLinear = JTextField(15).apply {
        (document as AbstractDocument).documentFilter =
            FiltroLimiteCaracteres(LIMITE_POTENCIA_LINEAR)
    }

    private val caixaUnidadePotenciaLogaritmica = JComboBox(
        UnidadePotenciaLogaritmica.entries.toTypedArray()
    )
    private val campoPotenciaLogaritmica = JTextField(15).apply {
        (document as AbstractDocument).documentFilter =
            FiltroLimiteCaracteres(LIMITE_POTENCIA_LOGARITMICA)
    }

    private val rotuloUnidadeLinear = JLabel(
        UnidadePotenciaLinear.MILLIWATT.simbolo
    )
    private val rotuloUnidadeLogaritmica = JLabel(
        UnidadePotenciaLogaritmica.DBM.simbolo
    )

    private val botaoLimpar = JButton("Limpar")

    private var campoDeOrigem = CampoDeOrigem.LINEAR
    private var ultimoCampoFocado = campoPotenciaLinear
    private var atualizandoCampos = false

    private enum class CampoDeOrigem {
        LINEAR,
        LOGARITMICO
    }

    init {
        title = "Conversor (Watts-dBm-Volts)"
        defaultCloseOperation = EXIT_ON_CLOSE

        criarInterface()
        configurarEventos()

        minimumSize = Dimension(580, 330)
        setSize(620, 380)
        isResizable = false
        setLocationRelativeTo(null)

        addWindowListener(
            object : WindowAdapter() {
                override fun windowOpened(evento: WindowEvent) {
                    campoPotenciaLinear.requestFocusInWindow()
                }
            }
        )
    }

    private fun criarInterface() {
        layout = BorderLayout(10, 10)

        val fonteCampos = Font("SansSerif", Font.PLAIN, 13)
        val fonteRotulos = Font("SansSerif", Font.PLAIN, 13)

        caixaUnidadePotenciaLinear.selectedItem =
            UnidadePotenciaLinear.MILLIWATT
        configurarCaixa(caixaUnidadePotenciaLinear, fonteCampos)
        configurarCaixa(
            caixaUnidadePotenciaLogaritmica,
            fonteCampos
        )
        configurarCampo(campoPotenciaLinear, fonteCampos)
        configurarCampo(campoPotenciaLogaritmica, fonteCampos)
        rotuloUnidadeLinear.font = fonteRotulos
        rotuloUnidadeLogaritmica.font = fonteRotulos

        val rotuloPotenciaLinear = JLabel("Potência Linear").apply {
            font = fonteRotulos
            horizontalAlignment = SwingConstants.RIGHT
        }
        val rotuloPotenciaLogaritmica =
            JLabel("Potência Logarítmica").apply {
                font = fonteRotulos
                horizontalAlignment = SwingConstants.RIGHT
            }
        val larguraRotulo = maxOf(
            rotuloPotenciaLinear.preferredSize.width,
            rotuloPotenciaLogaritmica.preferredSize.width
        )
        val larguraUnidade = maxOf(
            rotuloUnidadeLinear.preferredSize.width,
            rotuloUnidadeLogaritmica.preferredSize.width
        )
        rotuloUnidadeLinear.preferredSize = Dimension(
            larguraUnidade + REDUCAO_LARGURA_CAMPO_LINEAR,
            rotuloUnidadeLinear.preferredSize.height
        )
        rotuloUnidadeLogaritmica.preferredSize = Dimension(
            larguraUnidade + REDUCAO_LARGURA_CAMPO_LOGARITMICO,
            rotuloUnidadeLogaritmica.preferredSize.height
        )

        val painelFormulario = JPanel(GridBagLayout())
        val restricoes = GridBagConstraints().apply {
            insets = Insets(6, 8, 2, 8)
            fill = GridBagConstraints.HORIZONTAL
            gridx = 0
            weightx = 1.0
        }

        adicionarCaixa(
            painelFormulario,
            caixaUnidadePotenciaLinear,
            larguraRotulo,
            larguraUnidade,
            restricoes,
            0
        )
        adicionarCampo(
            painelFormulario,
            rotuloPotenciaLinear,
            campoPotenciaLinear,
            rotuloUnidadeLinear,
            larguraRotulo,
            restricoes,
            1
        )
        adicionarCaixa(
            painelFormulario,
            caixaUnidadePotenciaLogaritmica,
            larguraRotulo,
            larguraUnidade,
            restricoes,
            2
        )
        adicionarCampo(
            painelFormulario,
            rotuloPotenciaLogaritmica,
            campoPotenciaLogaritmica,
            rotuloUnidadeLogaritmica,
            larguraRotulo,
            restricoes,
            3
        )

        botaoLimpar.font = Font("SansSerif", Font.PLAIN, 13)
        botaoLimpar.preferredSize = Dimension(96, 34)

        val painelBotoes = JPanel(
            FlowLayout(FlowLayout.RIGHT, 10, 15)
        ).apply {
            border = BorderFactory.createEmptyBorder(25, 0, 0, 25)
            add(botaoLimpar)
        }

        val painelCentral = JPanel(BorderLayout(10, 10)).apply {
            border = BorderFactory.createEmptyBorder(15, 10, 25, 40)
            add(painelFormulario, BorderLayout.NORTH)
            add(painelBotoes, BorderLayout.CENTER)
        }

        add(painelCentral, BorderLayout.CENTER)
    } // fim de fun criarInterface()

    private fun adicionarCaixa(
        painel: JPanel,
        caixa: JComboBox<*>,
        larguraRotulo: Int,
        larguraUnidade: Int,
        restricoes: GridBagConstraints,
        linha: Int
    ) {
        restricoes.gridy = linha
        restricoes.insets = Insets(6, 8, 2, 8)
        painel.add(
            JPanel(BorderLayout(8, 0)).apply {
                add(
                    Box.createRigidArea(Dimension(larguraRotulo, 0)),
                    BorderLayout.WEST
                )
                add(caixa, BorderLayout.CENTER)
                add(
                    Box.createRigidArea(
                        Dimension(
                            larguraUnidade + REDUCAO_LARGURA_CAIXA,
                            0
                        )
                    ),
                    BorderLayout.EAST
                )
            },
            restricoes
        )
    }

    private fun adicionarCampo(
        painel: JPanel,
        rotulo: JLabel,
        campo: JTextField,
        rotuloUnidade: JLabel,
        larguraRotulo: Int,
        restricoes: GridBagConstraints,
        linha: Int
    ) {
        restricoes.gridy = linha
        restricoes.insets = Insets(2, 8, 6, 8)
        painel.add(
            JPanel(BorderLayout(8, 0)).apply {
                rotulo.preferredSize = Dimension(
                    larguraRotulo,
                    rotulo.preferredSize.height
                )
                add(rotulo, BorderLayout.WEST)
                add(campo, BorderLayout.CENTER)
                add(rotuloUnidade, BorderLayout.EAST)
            },
            restricoes
        )
    }

    private fun configurarEventos() {
        botaoLimpar.addActionListener { limparCampos() }

        val ouvinteDeFoco = object : FocusAdapter() {
            override fun focusGained(evento: FocusEvent) {
                ultimoCampoFocado = evento.component as JTextField
            }
        }
        campoPotenciaLinear.addFocusListener(ouvinteDeFoco)
        campoPotenciaLogaritmica.addFocusListener(ouvinteDeFoco)

        caixaUnidadePotenciaLinear.addActionListener {
            val unidade = caixaUnidadePotenciaLinear.selectedItem
                    as? UnidadePotenciaLinear
            rotuloUnidadeLinear.text = unidade?.simbolo.orEmpty()
            atualizarConversaoAutomatica()
        }
        caixaUnidadePotenciaLogaritmica.addActionListener {
            val unidade = caixaUnidadePotenciaLogaritmica.selectedItem
                    as? UnidadePotenciaLogaritmica
            rotuloUnidadeLogaritmica.text = unidade?.simbolo.orEmpty()
            atualizarConversaoAutomatica()
        }

        campoPotenciaLinear.document.addDocumentListener(
            criarOuvinteDeEdicao(CampoDeOrigem.LINEAR)
        )
        campoPotenciaLogaritmica.document.addDocumentListener(
            criarOuvinteDeEdicao(CampoDeOrigem.LOGARITMICO)
        )

        botaoLimpar.mnemonic = 'L'.code
    }

    private fun atualizarConversaoAutomatica() {
        if (atualizandoCampos) return

        campoDeOrigem = when {
            campoPotenciaLinear.hasFocus() -> CampoDeOrigem.LINEAR
            campoPotenciaLogaritmica.hasFocus() -> CampoDeOrigem.LOGARITMICO
            else -> campoDeOrigem
        }

        val campoOrigem = if (campoDeOrigem == CampoDeOrigem.LINEAR) {
            campoPotenciaLinear
        } else {
            campoPotenciaLogaritmica
        }
        val campoDestino = if (campoDeOrigem == CampoDeOrigem.LINEAR) {
            campoPotenciaLogaritmica
        } else {
            campoPotenciaLinear
        }

        if (campoOrigem.text.isBlank()) {
            definirTextoSemAlterarOrigem(campoDestino, "")
            return
        }

        try {
            if (campoDeOrigem == CampoDeOrigem.LINEAR) {
                converterDePotenciaLinearParaPotenciaLogaritmica()
            } else {
                converterDePotenciaLogaritmicaParaPotenciaLinear()
            }
        } catch (_: IllegalArgumentException) {
            definirTextoSemAlterarOrigem(campoDestino, "")
        }
    }

    private fun converterDePotenciaLinearParaPotenciaLogaritmica() {
        val potencia = lerPotencia(campoPotenciaLinear)
        val unidadeLinear =
            caixaUnidadePotenciaLinear.selectedItem as UnidadePotenciaLinear
        val unidadeLogaritmica =
            caixaUnidadePotenciaLogaritmica.selectedItem
                    as UnidadePotenciaLogaritmica
        val resultado = ConversorPotencia.converterParaLogaritmica(
            potencia,
            unidadeLinear,
            unidadeLogaritmica
        )

        definirTextoSemAlterarOrigem(
            campoPotenciaLogaritmica,
            formatarResultado(resultado, 2)
        )
    }

    private fun converterDePotenciaLogaritmicaParaPotenciaLinear() {
        val potencia = lerPotencia(campoPotenciaLogaritmica)
        val unidadeLogaritmica =
            caixaUnidadePotenciaLogaritmica.selectedItem
                    as UnidadePotenciaLogaritmica
        val unidadeLinear =
            caixaUnidadePotenciaLinear.selectedItem as UnidadePotenciaLinear
        val resultado = ConversorPotencia.converterParaLinear(
            potencia,
            unidadeLogaritmica,
            unidadeLinear
        )

        definirTextoSemAlterarOrigem(
            campoPotenciaLinear,
            formatarResultado(resultado, 3)
        )
    }

    private fun lerPotencia(campo: JTextField): Double {
        val texto = campo.text.trim()
        if (texto.isEmpty()) {
            throw IllegalArgumentException("Digite o valor da potência.")
        }

        return texto.replace(",", ".").toDoubleOrNull()
            ?: throw IllegalArgumentException(
                "Digite um valor numérico válido."
            )
    }

    private fun formatarResultado(
        resultado: Double,
        casasDecimais: Int
    ): String {
        return BigDecimal.valueOf(resultado)
            .setScale(casasDecimais, RoundingMode.HALF_UP)
            .toPlainString()
    }

    private fun limparCampos() {
        definirTextoSemAlterarOrigem(campoPotenciaLinear, "")
        definirTextoSemAlterarOrigem(campoPotenciaLogaritmica, "")
        campoDeOrigem = if (ultimoCampoFocado === campoPotenciaLinear) {
            CampoDeOrigem.LINEAR
        } else {
            CampoDeOrigem.LOGARITMICO
        }
        ultimoCampoFocado.requestFocusInWindow()
    }

    private fun configurarCaixa(caixa: JComboBox<*>, fonte: Font) {
        caixa.font = fonte
        caixa.isFocusable = false
        caixa.preferredSize = Dimension(caixa.preferredSize.width, 32)
        caixa.renderer = object : DefaultListCellRenderer() {
            override fun getListCellRendererComponent(
                list: JList<*>?,
                value: Any?,
                index: Int,
                isSelected: Boolean,
                cellHasFocus: Boolean
            ): Component {
                val componente = super.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus
                ) as JLabel
                componente.horizontalAlignment = LEFT
                componente.border =
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
                return componente
            }
        }
    }

    private fun configurarCampo(campo: JTextField, fonte: Font) {
        campo.font = fonte
        campo.horizontalAlignment = JTextField.LEFT
        campo.border = BorderFactory.createCompoundBorder(
            campo.border,
            BorderFactory.createEmptyBorder(0, 4, 0, 0)
        )
        campo.preferredSize = Dimension(
            campo.preferredSize.width,
            32
        )
    }

    private fun criarOuvinteDeEdicao(
        origem: CampoDeOrigem
    ): DocumentListener {
        return object : DocumentListener {
            private fun registrarOrigem() {
                if (!atualizandoCampos) {
                    campoDeOrigem = origem
                    atualizarConversaoAutomatica()
                }
            }

            override fun insertUpdate(evento: DocumentEvent) {
                registrarOrigem()
            }

            override fun removeUpdate(evento: DocumentEvent) {
                registrarOrigem()
            }

            override fun changedUpdate(evento: DocumentEvent) {
                registrarOrigem()
            }
        }
    }

    private fun definirTextoSemAlterarOrigem(
        campo: JTextField,
        texto: String
    ) {
        atualizandoCampos = true
        try {
            campo.text = texto
        } finally {
            atualizandoCampos = false
        }
    }
}

private class FiltroLimiteCaracteres(
    private val limite: Int
) : DocumentFilter() {
    override fun replace(
        filtro: FilterBypass,
        offset: Int,
        length: Int,
        texto: String?,
        atributos: AttributeSet?
    ) {
        val espacoDisponivel = limite - (filtro.document.length - length)
        val textoLimitado = texto.orEmpty().take(espacoDisponivel.coerceAtLeast(0))
        filtro.replace(offset, length, textoLimitado, atributos)
    }

    override fun insertString(
        filtro: FilterBypass,
        offset: Int,
        texto: String?,
        atributos: AttributeSet?
    ) {
        replace(filtro, offset, 0, texto, atributos)
    }
}

fun main() {
    SwingUtilities.invokeLater {
        configurarAparenciaDoSistema()
        ConversorPotenciaGUI().isVisible = true
    }
}

private fun configurarAparenciaDoSistema() {
    try {
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName()
        )
    } catch (erro: Exception) {
        System.err.println(
            "Não foi possível aplicar o tema do sistema: " +
                    erro.message
        )
    }
}
