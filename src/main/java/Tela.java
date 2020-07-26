import controller.AutomatonController2;
import controller.GeradorXMLController;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;
import model.Automaton;
import model.State;
import model.Transition;
import sun.rmi.runtime.Log;

public class Tela extends javax.swing.JFrame {
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

                if(absolutePath.endsWith(".jff") || absolutePath.endsWith(".dff")){
                    textArea1.setText("");
                }else{
                    textArea1.setText("Arquivo de expressão não reconhecido");
                }
                textField1.setText(absolutePath);

                if(!textField1.getText().isEmpty()){
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

                if(absolutePath.endsWith(".txt")){
                    textArea1.setText("");
                }else{
                    textArea1.setText("Arquivo de lista de sentenças não reconhecido");
                }
                textField2.setText(absolutePath);

                if(!textField2.getText().isEmpty()){
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

                    expressao  = expressao.substring(expressao.indexOf("<expression>")+12,expressao.indexOf("</expression>"));

                    AutomatonController2 controller2 = new AutomatonController2();

                    Automaton automaton = controller2.converteExpressão(expressao);

                    //Gera o arquivo .xml do automato
                    GeradorXMLController geradorXml = new GeradorXMLController();
                    geradorXml.geraXML(automaton, expressao, arquivoSentencas.getParent());

                    StringBuilder result = new StringBuilder();

                    Files.readAllLines(arquivoSentencas.toPath()).forEach(linha ->{
                        String[] split = linha.split(" ");
                        String sentenca =  split[0];
                        String estado = "";

                        if(split.length > 1){
                            estado = split[1];
                        }else{
                            estado = "accept";
                        }

                        result.append("Input: ").append(sentenca).append(" Result: ");
                        boolean isSentencaAceita = controller2.executaMaquinaEstados(sentenca, sentenca.split(""), automaton);

                        if(isSentencaAceita){
                            result.append( "Accept ");
                            if("reject".equals(estado.toLowerCase())){
                                result.append( "(Reject)");
                            }

                        }else{
                            result.append( "Reject ");
                            if("accept".equals(estado.toLowerCase())){
                                result.append( "(Accept)");
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
                    JOptionPane.showMessageDialog(null,"Erro ao executar simulação", "Falha", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }finally {
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
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Tela.class
                .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Tela.class
                .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Tela.class
                .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Tela.class
                .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Tela().setVisible(true);
            }
        });
    }


    public void initComponents(){
        JFrame frame = new JFrame("Simulador AFD");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel1);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
