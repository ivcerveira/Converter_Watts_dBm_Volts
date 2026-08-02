import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.math.BigDecimal
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class ConversorPotenciaGUI : JFrame() {

    private val caixaUnidadePotenciaLinear = JComboBox(
        UnidadePotenciaLinear.entries.toTypedArray()
    )
    private val campoPotenciaLinear = JTextField(15)

    private val caixaUnidadePotenciaLogaritmica = JComboBox(
        UnidadePotenciaLogaritmica.entries.toTypedArray()
    )
    private val campoPotenciaLogaritmica = JTextField(15)

    private val rotuloUnidadeLinear = JLabel(
        UnidadePotenciaLinear.MILLIWATT.simbolo
    )
    private val rotuloUnidadeLogaritmica = JLabel(
        UnidadePotenciaLogaritmica.DBM.simbolo
    )

    private val botaoConverter = JButton("Converter")
    private val botaoLimpar = JButton("Limpar")

    private var campoDeOrigem = CampoDeOrigem.LINEAR
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

        minimumSize = Dimension(480, 330)
        setSize(520, 380)
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
        }
        val rotuloPotenciaLogaritmica =
            JLabel("Potência Logarítmica").apply {
                font = fonteRotulos
            }
        val larguraRotulo = maxOf(
            rotuloPotenciaLinear.preferredSize.width,
            rotuloPotenciaLogaritmica.preferredSize.width
        )
        val larguraUnidade = maxOf(
            rotuloUnidadeLinear.preferredSize.width,
            rotuloUnidadeLogaritmica.preferredSize.width
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

        val painelBotoes = JPanel(
            FlowLayout(FlowLayout.CENTER, 10, 15)
        ).apply {
            botaoConverter.isFocusable = false
            add(botaoConverter)
            add(botaoLimpar)
        }

        val painelCentral = JPanel(BorderLayout(10, 10)).apply {
            border = BorderFactory.createEmptyBorder(15, 40, 25, 40)
            add(painelFormulario, BorderLayout.NORTH)
            add(painelBotoes, BorderLayout.CENTER)
        }

        add(painelCentral, BorderLayout.CENTER)
    }

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
                    Box.createRigidArea(Dimension(larguraUnidade, 0)),
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
        botaoConverter.addActionListener { realizarConversao() }
        botaoLimpar.addActionListener { limparCampos() }

        caixaUnidadePotenciaLinear.addActionListener {
            val unidade = caixaUnidadePotenciaLinear.selectedItem
                    as? UnidadePotenciaLinear
            rotuloUnidadeLinear.text = unidade?.simbolo.orEmpty()
        }
        caixaUnidadePotenciaLogaritmica.addActionListener {
            val unidade = caixaUnidadePotenciaLogaritmica.selectedItem
                    as? UnidadePotenciaLogaritmica
            rotuloUnidadeLogaritmica.text = unidade?.simbolo.orEmpty()
        }

        campoPotenciaLinear.addActionListener { realizarConversao() }
        campoPotenciaLogaritmica.addActionListener {
            realizarConversao()
        }
        campoPotenciaLinear.document.addDocumentListener(
            criarOuvinteDeEdicao(CampoDeOrigem.LINEAR)
        )
        campoPotenciaLogaritmica.document.addDocumentListener(
            criarOuvinteDeEdicao(CampoDeOrigem.LOGARITMICO)
        )

        rootPane.defaultButton = botaoConverter
        botaoConverter.mnemonic = KeyEvent.VK_C
        botaoLimpar.mnemonic = KeyEvent.VK_L
    }

    private fun realizarConversao() {
        try {
            if (campoDeOrigem == CampoDeOrigem.LINEAR) {
                converterDePotenciaLinearParaPotenciaLogaritmica()
            } else {
                converterDePotenciaLogaritmicaParaPotenciaLinear()
            }
        } catch (erro: IllegalArgumentException) {
            mostrarMensagemDeErro(
                erro.message ?: "Não foi possível realizar a conversão."
            )
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
            formatarResultado(resultado)
        )
        posicionarCursorNoFim(campoPotenciaLinear)
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
            formatarResultado(resultado)
        )
        posicionarCursorNoFim(campoPotenciaLogaritmica)
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

    private fun formatarResultado(resultado: Double): String {
        return BigDecimal.valueOf(resultado)
            .stripTrailingZeros()
            .toPlainString()
    }

    private fun limparCampos() {
        definirTextoSemAlterarOrigem(campoPotenciaLinear, "")
        definirTextoSemAlterarOrigem(campoPotenciaLogaritmica, "")
        campoDeOrigem = CampoDeOrigem.LINEAR
        campoPotenciaLinear.requestFocusInWindow()
    }

    private fun mostrarMensagemDeErro(mensagem: String) {
        JOptionPane.showMessageDialog(
            this,
            mensagem,
            "Dados inválidos",
            JOptionPane.WARNING_MESSAGE
        )

        val campo = if (campoDeOrigem == CampoDeOrigem.LINEAR) {
            campoPotenciaLinear
        } else {
            campoPotenciaLogaritmica
        }
        campo.requestFocusInWindow()
        campo.selectAll()
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
        campo.preferredSize = Dimension(
            campo.preferredSize.width,
            32
        )
    }

    private fun posicionarCursorNoFim(campo: JTextField) {
        campo.requestFocusInWindow()
        campo.caretPosition = campo.document.length
    }

    private fun criarOuvinteDeEdicao(
        origem: CampoDeOrigem
    ): DocumentListener {
        return object : DocumentListener {
            private fun registrarOrigem() {
                if (!atualizandoCampos) {
                    campoDeOrigem = origem
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
