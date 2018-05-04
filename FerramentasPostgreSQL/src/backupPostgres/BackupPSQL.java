package backupPostgres;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Map;

import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;

public class BackupPSQL {

	private JFrame frmBackupPostgresql;
	private static JTextField txtBanco;
	private static JTextField txtDiretorio;
	private static JTextField endOutro;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BackupPSQL window = new BackupPSQL();
					window.frmBackupPostgresql.setVisible(true);
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public BackupPSQL() {
		initialize();
	}

	JRadioButton radioOutro = new JRadioButton("Outro");
	JRadioButton radioLocalHost = new JRadioButton("Local Host");

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		ImageIcon img = new ImageIcon("pgadmin.png");
		frmBackupPostgresql = new JFrame();
		frmBackupPostgresql.setIconImage(img.getImage());
		frmBackupPostgresql.setTitle("Backup PostgreSQL");
		frmBackupPostgresql.setBounds(100, 100, 311, 202);
		frmBackupPostgresql.setResizable(false);
		frmBackupPostgresql.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmBackupPostgresql.getContentPane().setLayout(null);

		JLabel lblBanco = new JLabel("Banco");
		lblBanco.setBounds(24, 16, 57, 14);
		frmBackupPostgresql.getContentPane().add(lblBanco);

		txtBanco = new JTextField();
		txtBanco.setBounds(91, 12, 184, 20);
		txtBanco.setColumns(10);
		frmBackupPostgresql.getContentPane().add(txtBanco);

		JLabel lblDiretorio = new JLabel("Local:");
		lblDiretorio.setBounds(24, 47, 51, 14);
		frmBackupPostgresql.getContentPane().add(lblDiretorio);

		txtDiretorio = new JTextField();
		txtDiretorio.setBounds(91, 43, 150, 20);
		txtDiretorio.setColumns(10);
		frmBackupPostgresql.getContentPane().add(txtDiretorio);

		JLabel lblRede = new JLabel("Rede");
		lblRede.setBounds(10, 74, 71, 14);
		frmBackupPostgresql.getContentPane().add(lblRede);

		radioLocalHost.setBounds(18, 89, 109, 23);
		frmBackupPostgresql.getContentPane().add(radioLocalHost);
		radioLocalHost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					verificaLocalhost();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});

		radioOutro.setBounds(18, 112, 109, 23);
		frmBackupPostgresql.getContentPane().add(radioOutro);
		radioOutro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					verificaOutro();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});

		endOutro = new JTextField();
		endOutro.setBounds(23, 136, 98, 20);
		frmBackupPostgresql.getContentPane().add(endOutro);
		endOutro.setColumns(10);
		endOutro.setEnabled(false);

		JButton btnBackup = new JButton("Backup");
		btnBackup.setBounds(186, 93, 89, 63);
		frmBackupPostgresql.getContentPane().add(btnBackup);

		btnBackup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (verificaSintaxe()) {
						Backup();
					}
				} catch (Exception e3) {
					e3.printStackTrace();
				}
			}
		});

		JButton btnV = new JButton("...");
		btnV.setBounds(247, 42, 28, 22);
		frmBackupPostgresql.getContentPane().add(btnV);
		btnV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					salvarArquivo();
				} catch (Exception e3) {
					e3.printStackTrace();
				}
			}
		});

		radioLocalHost.setSelected(true);

		/* Pode-se especificar um valor padrão para os campos */
		endOutro.setText("localhost");
		txtBanco.setText("sgfpod1");

	}

	private static void Backup() throws IOException, InterruptedException {

		String host, diretorio, banco;
		String log = "";

		host = getEndOutro().getText().toString();
		diretorio = getTxtDiretorio().getText().toString();
		banco = getTxtBanco().getText().toString();

		Runtime rt = Runtime.getRuntime();
		Process p;
		ProcessBuilder pb;
		rt = Runtime.getRuntime();

		/*
		 * Cria um processo, executando os parâmetros do pg_dump do PostgreSQL a partir
		 * das variáveis coletadas até então --host: define o host do servidor, pode ser
		 * tanto localhost quanto outro endereço. --port: define a porta com que o
		 * serviço do PostgreSQL trabalha no servidor. --username: usuário do servidor
		 * --format: define que o formato do backup é customizado --blobs: define que
		 * objetos grandes podem ser incluidos no dump --verbose: define que o log do
		 * backup será detalhado --file: especifica o diretório para onde o arquivo será
		 * salvo, incluindo o nome do arquivo.
		 */

		pb = new ProcessBuilder("C:\\Program Files\\PostgreSQL\\9.5\\bin\\pg_dump.exe", "--host", host, "--port",
				"5432", "--username", "postgres", "--no-password", "--format", "custom", "--blobs", "--verbose",
				"--file", diretorio, banco);

		try {
			final Map<String, String> env = pb.environment();
			env.put("PGPASSWORD", "postgres");
			p = pb.start();
			final BufferedReader r = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line = r.readLine();
			while (line != null) {
				System.err.println(line);
				line = r.readLine();
			}
			r.close();
			p.waitFor();

			System.out.println(p.exitValue());

			/*
			 * Caso ocorra um erro, mostra um diálogo genérico de erro, senão, mostra um
			 * resumo das informações do backup.
			 */

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Ocorreu um erro desconhecido");
		}

		JOptionPane.showMessageDialog(null, "Backup concluido!" + "\n" + "Local: "
				+ getTxtDiretorio().getText().toString() + "\n" + "Banco: " + getTxtBanco().getText().toString());

	}

	/*
	 * Função para salvar arquivo. filtro serve para definir o formato do arquivo
	 * para ser salvo O parâmetro "C:/" Determina que a Janela será aberta
	 * diretamente no C:/ Após escolher o diretório no fileToSave, ele vai jogar o
	 * valor para o txtDiretorio.
	 */
	private void salvarArquivo() {

		FileNameExtensionFilter filtro = new FileNameExtensionFilter("Arquivo Backup PostgreSQL *.backup", "*.backup");

		JFileChooser fileChooser = new JFileChooser("C:/");
		fileChooser.addChoosableFileFilter(filtro);
		fileChooser.setFileFilter(filtro);
		fileChooser.setDialogTitle("Escolha um local para salvar o arquivo");

		int userSelection = fileChooser.showSaveDialog(null);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			txtDiretorio.setText(fileToSave.getAbsolutePath() + ".backup");
		}
	}

	/* Verificar se campos estão validos */
	private boolean verificaSintaxe() {
		boolean ok = false;

		String host, diretorio, banco;
		host = getEndOutro().getText().toString();
		diretorio = getTxtDiretorio().getText().toString();
		banco = getTxtBanco().getText().toString();

		if (!banco.equals(""))
			if (!diretorio.equals(""))
				if (!host.equals(""))
					ok = true;
				else
					JOptionPane.showMessageDialog(null, "Insira host!");
			else
				JOptionPane.showMessageDialog(null, "Insira diretorio!");
		else
			JOptionPane.showMessageDialog(null, "Insira banco!");

		return ok;
	}

	/*
	 * Verificação entre localhost e outro Sempre retorna na variavel endOutro
	 */
	private String verificaOutro() {
		String result = "";

		if (radioOutro.isSelected()) {
			result = getEndOutro().getText().toString();
			radioLocalHost.setSelected(false);
			endOutro.setEnabled(true);
		} else {
			result = "localhost";
			radioLocalHost.setEnabled(true);
			endOutro.setEnabled(false);
		}
		return result;
	}

	/*
	 * Verificação entre localhost e outro, Sempre retorna na variavel endOutro
	 */
	private String verificaLocalhost() {
		String result = "";

		if (radioLocalHost.isSelected()) {
			radioOutro.setSelected(false);
			endOutro.setText("localhost");
			endOutro.setEnabled(false);
			result = endOutro.getText().toString();
		} else {
			endOutro.setText("localhost");
		}
		return result;
	}

	public JFrame getFrmBackupPostgresql() {
		return frmBackupPostgresql;
	}

	public void setFrmBackupPostgresql(JFrame frmBackupPostgresql) {
		this.frmBackupPostgresql = frmBackupPostgresql;
	}

	public static JTextField getTxtBanco() {
		return txtBanco;
	}

	public void setTxtBanco(JTextField txtBanco) {
		BackupPSQL.txtBanco = txtBanco;
	}

	public static JTextField getTxtDiretorio() {
		return txtDiretorio;
	}

	public void setTxtDiretorio(JTextField txtDiretorio) {
		BackupPSQL.txtDiretorio = txtDiretorio;
	}

	public static JTextField getEndOutro() {
		return endOutro;
	}

	public void setEndOutro(JTextField endOutro) {
		BackupPSQL.endOutro = endOutro;
	}
}
