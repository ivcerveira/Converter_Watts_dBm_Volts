import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*

class ConversorPotenciaGUI : JFrame() {

    private val caixaUnidade = JComboBox(
        UnidadePotencia.entries.toTypedArray()
    )

    private val campoPotencia = JTextField(15)

    private val rotuloUnidadeSelecionada = JLabel(
        UnidadePotencia.MILLIWATT.simbolo
    )

    private val rotuloResultado = JLabel(
        "Digite uma potência e clique em Converter",
        SwingConstants.CENTER
    )

    private val botaoConverter = JButton("Converter")
    private val botaoLimpar = JButton("Limpar")

    init {
        //title = "Conversor de Potência para dBm"
        title = "Conversor (Watts-dBm-Volts)"
        defaultCloseOperation = EXIT_ON_CLOSE

        criarInterface()
        configurarEventos()

        minimumSize = Dimension(480, 380)
        setSize(520, 430)
        setLocationRelativeTo(null)

        addWindowListener(
            object : WindowAdapter() {
                override fun windowOpened(evento: WindowEvent) {
                    campoPotencia.requestFocusInWindow()
                }
            }
        )
    }

    private fun criarInterface() {
        layout = BorderLayout(10, 10)

        val fonteCampos = Font("SansSerif", Font.PLAIN, 13)
        val fonteRotulosEntrada = Font("SansSerif", Font.PLAIN, 13)

        caixaUnidade.selectedItem = UnidadePotencia.MILLIWATT
        caixaUnidade.font = fonteCampos
        caixaUnidade.isFocusable = false
        caixaUnidade.preferredSize = Dimension(
            caixaUnidade.preferredSize.width,
            32
        )
        caixaUnidade.renderer = object : DefaultListCellRenderer() {
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

                componente.border = BorderFactory.createEmptyBorder(
                    6,
                    8,
                    6,
                    8
                )

                return componente
            }
        }

        campoPotencia.font = fonteCampos
        campoPotencia.preferredSize = Dimension(
            campoPotencia.preferredSize.width,
            32
        )

        rotuloUnidadeSelecionada.font = fonteRotulosEntrada

        val rotuloPotenciaLinear = JLabel("Potência Linear").apply {
            font = fonteRotulosEntrada
        }

        val larguraRotuloPotencia = rotuloPotenciaLinear.preferredSize.width
        val larguraRotuloUnidade = rotuloUnidadeSelecionada.preferredSize.width

        val painelFormulario = JPanel(GridBagLayout())

        val restricoes = GridBagConstraints().apply {
            insets = Insets(6, 8, 2, 8)
            fill = GridBagConstraints.HORIZONTAL
            gridx = 0
            weightx = 1.0
        }

        // JComboBox acima do campo de texto
        restricoes.gridy = 0

        painelFormulario.add(
            JPanel(BorderLayout(8, 0)).apply {
                add(
                    Box.createRigidArea(
                        Dimension(larguraRotuloPotencia, 0)
                    ),
                    BorderLayout.WEST
                )
                add(caixaUnidade, BorderLayout.CENTER)
                add(
                    Box.createRigidArea(
                        Dimension(larguraRotuloUnidade, 0)
                    ),
                    BorderLayout.EAST
                )
            },
            restricoes
        )

        // JLabel e campo de texto
        restricoes.gridy = 1
        restricoes.insets = Insets(2, 8, 6, 8)

        val painelPotencia = JPanel(
            BorderLayout(8, 0)
        ).apply {
            add(rotuloPotenciaLinear, BorderLayout.WEST)
            add(campoPotencia, BorderLayout.CENTER)
            add(rotuloUnidadeSelecionada, BorderLayout.EAST)
        }

        painelFormulario.add(
            painelPotencia,
            restricoes
        )

        val painelBotoes = JPanel(
            FlowLayout(
                FlowLayout.CENTER,
                10,
                15
            )
        ).apply {
            botaoConverter.isFocusable = false
            add(botaoConverter)
            add(botaoLimpar)
        }

        rotuloResultado.apply {
            font = Font("SansSerif", Font.BOLD, 17)

            border = BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Resultado"),
                BorderFactory.createEmptyBorder(
                    25,
                    10,
                    25,
                    10
                )
            )
        }

        val painelCentral = JPanel(
            BorderLayout(10, 10)
        ).apply {
            border = BorderFactory.createEmptyBorder(
                15,
                40,
                25,
                40
            )

            add(painelFormulario, BorderLayout.NORTH)
            add(painelBotoes, BorderLayout.CENTER)
            add(rotuloResultado, BorderLayout.SOUTH)
        }

        add(painelCentral, BorderLayout.CENTER)
    }

    private fun configurarEventos() {
        botaoConverter.addActionListener {
            realizarConversao()
        }

        botaoLimpar.addActionListener {
            limparCampos()
        }

        caixaUnidade.addActionListener {
            atualizarRotuloUnidadeSelecionada()
            focarCampoPotencia()
        }

        campoPotencia.addActionListener {
            realizarConversao()
        }

        rootPane.defaultButton = botaoConverter

        botaoConverter.mnemonic = KeyEvent.VK_C
        botaoLimpar.mnemonic = KeyEvent.VK_L
    }

    private fun realizarConversao() {
        try {
            val textoDigitado = campoPotencia.text.trim()

            if (textoDigitado.isEmpty()) {
                throw IllegalArgumentException(
                    "Digite o valor da potência."
                )
            }

            val potencia = textoDigitado
                .replace(",", ".")
                .toDoubleOrNull()
                ?: throw IllegalArgumentException(
                    "Digite um valor numérico válido."
                )

            val unidadeSelecionada =
                caixaUnidade.selectedItem as? UnidadePotencia
                    ?: throw IllegalArgumentException(
                        "Selecione uma unidade."
                    )

            val resultado = ConversorPotencia.converterParaDbm(
                potencia,
                unidadeSelecionada
            )

            rotuloResultado.text = String.format(
                "%.6f %s = %.2f dBm",
                potencia,
                unidadeSelecionada.simbolo,
                resultado
            )
        } catch (erro: IllegalArgumentException) {
            mostrarMensagemDeErro(
                erro.message
                    ?: "Não foi possível realizar a conversão."
            )
        }

        focarCampoPotencia()
    }

    private fun limparCampos() {
        campoPotencia.text = ""

        rotuloResultado.text =
            "Digite uma potência e clique em Converter"

        campoPotencia.requestFocusInWindow()
    }

    private fun mostrarMensagemDeErro(mensagem: String) {
        JOptionPane.showMessageDialog(
            this,
            mensagem,
            "Dados inválidos",
            JOptionPane.WARNING_MESSAGE
        )

        campoPotencia.requestFocusInWindow()
        campoPotencia.selectAll()
    }

    private fun atualizarRotuloUnidadeSelecionada() {
        val unidadeSelecionada =
            caixaUnidade.selectedItem as? UnidadePotencia

        rotuloUnidadeSelecionada.text =
            unidadeSelecionada?.simbolo.orEmpty()
    }

    private fun focarCampoPotencia() {
        SwingUtilities.invokeLater {
            campoPotencia.requestFocusInWindow()
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
