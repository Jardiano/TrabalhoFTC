package screen;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import controller.AutomatonController2;
import controller.GeradorXMLController;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileSystemView;
import model.Automaton;

public class Tela extends JFrame {
    private JPanel panel1;
    private JButton listaDeSentencasButton;
    private JTextField textField1;
    private JTextArea textArea1;
    private JButton expressaoRegularButton;
    private JTextField textField2;
    private JButton simularButton;

    private File arquivoExpressao;
    private File arquivoSentencas;


    public Tela() {
        initComponents();
        expressaoRegularButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                int returnValue = jfc.showOpenDialog(null);
                String absolutePath = "";
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    arquivoExpressao = jfc.getSelectedFile();
                    absolutePath = arquivoExpressao.getAbsolutePath();
                }

                if (absolutePath.endsWith(".jff") || absolutePath.endsWith(".dff")) {
                    textArea1.setText("");
                }
                else {
                    textArea1.setText("Arquivo de expressão não reconhecido");
                }
                textField1.setText(absolutePath);

                if (!textField1.getText().isEmpty()) {
                    listaDeSentencasButton.setEnabled(true);
                }

            }
        });

        listaDeSentencasButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                int returnValue = jfc.showOpenDialog(null);
                String absolutePath = "";
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    arquivoSentencas = jfc.getSelectedFile();
                    absolutePath = arquivoSentencas.getAbsolutePath();
                }

                if (absolutePath.endsWith(".txt")) {
                    textArea1.setText("");
                }
                else {
                    textArea1.setText("Arquivo de lista de sentenças não reconhecido");
                }
                textField2.setText(absolutePath);

                if (!textField2.getText().isEmpty()) {
                    simularButton.setEnabled(true);
                }
            }
        });
        simularButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String expressao = Files.readAllLines(arquivoExpressao.toPath()).stream().reduce("", String::concat);

                    expressao = expressao.substring(expressao.indexOf("<expression>") + 12, expressao.indexOf("</expression>"));

                    AutomatonController2 controller2 = new AutomatonController2();

                    Automaton automaton = controller2.converteExpressão(expressao);

                    //Gera o arquivo .xml do automato
                    GeradorXMLController geradorXml = new GeradorXMLController();
                    geradorXml.geraXML(automaton, expressao, arquivoSentencas.getParent());

                    StringBuilder result = new StringBuilder();

                    Files.readAllLines(arquivoSentencas.toPath()).forEach(linha -> {
                        String[] split = linha.split(" ");
                        String sentenca = split[0];
                        String estado = "";

                        if (split.length > 1) {
                            estado = split[1];
                        }
                        else {
                            estado = "accept";
                        }

                        result.append("Input: ").append(sentenca).append(" Result: ");
                        boolean isSentencaAceita = controller2.executaMaquinaEstados(sentenca, sentenca.split(""), automaton);

                        if (isSentencaAceita) {
                            result.append("Accept ");
                            if ("reject".equals(estado.toLowerCase())) {
                                result.append("(Reject)");
                            }

                        }
                        else {
                            result.append("Reject ");
                            if ("accept".equals(estado.toLowerCase())) {
                                result.append("(Accept)");
                            }
                        }
                        result.append("\n");
                    });

                    textArea1.setText(result.toString());

                    //Armazena os resultados da simulação
                    File file = new File(arquivoSentencas.getParent().concat(System.getProperty("file.separator")).concat("Result.txt"));
                    FileWriter writer = new FileWriter(file);
                    writer.write(result.toString());
                    writer.close();


                }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao executar simulação", "Falha", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
                finally {
                    AutomatonController2.states = new ArrayList<>();
                    AutomatonController2.transitions = new ArrayList<>();
                    AutomatonController2.countState = 0;
                }

            }
        });
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(Tela.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex) {
            Logger.getLogger(Tela.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex) {
            Logger.getLogger(Tela.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Tela.class.getName()).log(Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Tela().setVisible(true);
            }
        });
    }


    public void initComponents() {
        JFrame frame = new JFrame("Simulador AFD");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel1);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        /*panel1 = new JPanel();
        listaDeSentencasButton = new JButton();
        textField1 = new JTextField();
        textArea1 = new JTextArea();
        expressaoRegularButton = new JButton();
        textField2 = new JTextField();
        simularButton = new JButton();*/

    }


    {
        // GUI initializer generated by IntelliJ IDEA GUI Designer
        // >>> IMPORTANT!! <<<
        // DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(6, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setMinimumSize(new Dimension(800, 600));
        panel1.setPreferredSize(new Dimension(800, 600));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1,
            new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2,
            new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textField1 = new JTextField();
        textField1.setText("");
        panel1.add(textField1, new GridConstraints(1,
            0,
            1,
            2,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            new Dimension(150, -1),
            null,
            0,
            false));
        listaDeSentencasButton = new JButton();
        listaDeSentencasButton.setEnabled(true);
        listaDeSentencasButton.setText("Lista de Sentencas");
        panel1.add(listaDeSentencasButton, new GridConstraints(2,
            0,
            1,
            2,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
        expressaoRegularButton = new JButton();
        expressaoRegularButton.setText("Expressao Regular");
        panel1.add(expressaoRegularButton, new GridConstraints(0,
            0,
            1,
            2,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
        textField2 = new JTextField();
        panel1.add(textField2, new GridConstraints(3,
            0,
            1,
            2,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            new Dimension(150, -1),
            null,
            0,
            false));
        simularButton = new JButton();
        simularButton.setText("Simular");
        panel1.add(simularButton, new GridConstraints(5,
            0,
            1,
            2,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(1,
            2,
            4,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
        textArea1 = new JTextArea();
        textArea1.setEditable(false);
        textArea1.setText(
            "Para testar as expressões(carregue os arquivos no formato do Jflap versão 7.0):\n\n1. Carregue o arquivo contendo a expressão regular.\n\n2. Carregue o arquivo de extensão .txt contendo as sentenças que serão testadas\n(lembrando que nessa versão do Jflap as sentencas são separadas do \nestado de aceitação/rejeição esperado por um espaço simples)\n\n\nOs arquivos XML do autômato(dfa.jff) e o resultado \nda simulação(Result.txt) serão armazenados no mesmo diretório \ndo arquivo de sentenças.");
        panel2.add(textArea1, new GridConstraints(0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            new Dimension(150, 50),
            null,
            0,
            false));
    }


    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
