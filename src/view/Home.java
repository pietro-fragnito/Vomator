package view;

import it.unisannio.service.ghdl.utils.GHDLServiceClient;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.json.JSONArray;
import org.json.JSONObject;

public class Home {

	private JFrame frame;
	private JTextField username_tf;
	private JPasswordField password_tf;
	private JButton openFile, processFile;
	private JTextArea console;
	private JTextField project_tf;
	private JTextField entity_tf;
	private Checkbox mode_cb;
	private String sourceCode = "";
	private Object[] projects;
	private JComboBox comboBox;
	private JPanel buttonsPanel;
	private JButton btnSimulate,btnDownloadVcd;
	private JButton btnDelete;
	private GridBagConstraints gbc_comboBox;
	private JTextArea message_lbl;
	private JScrollPane messageScroll;
	private String vcdSourceFile = "";
	private String vcdSavedPath = "";
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Home window = new Home();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws URISyntaxException 
	 */
	public Home() throws URISyntaxException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws URISyntaxException 
	 */
	private void initialize() throws URISyntaxException {
		frame = new JFrame();
		frame.setBounds(100, 100, 648, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		final URI uri = new URI("https://90.147.102.139:8443/GHDLRegistrationModule/Registration.jsp");

		JPanel loginPanel = new JPanel();

		GridBagLayout gbl_loginPanel = new GridBagLayout();
		BorderLayout br_login = new BorderLayout();
		JPanel superLogin = new JPanel();

		superLogin.setLayout(br_login);

		loginPanel.setLayout(gbl_loginPanel);

		Image imgVhdl = new ImageIcon(this.getClass().getResource("/resources/VHDL-Uni-50.png")).getImage();
		imgVhdl.getScaledInstance(50, 50, Image.SCALE_DEFAULT);

		JLabel vhdlIco = new JLabel("");
		vhdlIco.setIcon(new ImageIcon(imgVhdl));

		JLabel label = new JLabel("Username");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.gridy = 0;
		gbc_label.fill = GridBagConstraints.HORIZONTAL;
		gbc_label.gridx = 0;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		loginPanel.add(label, gbc_label);

		username_tf = new JTextField();
		GridBagConstraints gbc_username_tf = new GridBagConstraints();
		gbc_username_tf.gridy = 0;
		gbc_username_tf.gridx = 1;
		gbc_username_tf.fill = GridBagConstraints.HORIZONTAL;
		gbc_username_tf.insets = new Insets(0, 0, 5, 5);
		loginPanel.add(username_tf, gbc_username_tf);
		username_tf.setColumns(10);

		JLabel lblPassword = new JLabel("Password");
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.gridy = 0;
		gbc_lblPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblPassword.gridx = 2;
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		loginPanel.add(lblPassword, gbc_lblPassword);

		password_tf = new JPasswordField();
		GridBagConstraints gbc_password_tf = new GridBagConstraints();
		gbc_password_tf.fill = GridBagConstraints.HORIZONTAL;
		gbc_password_tf.insets = new Insets(0, 0, 5, 5);
		gbc_password_tf.gridx = 3;
		gbc_password_tf.gridy = 0;
		loginPanel.add(password_tf, gbc_password_tf);
		password_tf.setColumns(10);

		JButton btnLogin = new JButton("Log In");
		JButton btnLogout = new JButton("Logout");
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				message_lbl.setText("Logged out");
				username_tf.setEnabled(true);
				password_tf.setEnabled(true);
				openFile.setEnabled(false);
				console.setEnabled(false);
				processFile.setEnabled(false);
				btnLogout.setVisible(false);
				btnLogin.setVisible(true);
				project_tf.setEnabled(false);
				entity_tf.setEnabled(false);
				mode_cb.setEnabled(false);
				comboBox.setEnabled(false);
				btnSimulate.setEnabled(false);
				btnDownloadVcd.setEnabled(false);
				btnDelete.setEnabled(false);
			}


		});
		btnLogout.setVisible(false);

		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String username = username_tf.getText();
				char password[] = password_tf.getPassword();
				boolean usernameExist = false, passwordCorrect = false, problems = false;
				GHDLServiceClient client = new GHDLServiceClient();

				try {
					
					
					projects = client.getProjects("90.147.102.139", username);
//					projects = client.getProjects("127.0.0.1", username);

					comboBox.setModel(new DefaultComboBoxModel < > (projects));
					comboBox.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							project_tf.setText(String.valueOf(comboBox.getSelectedItem()));
						}
					});

				} catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e2) {
					// TODO Auto-generated catch block
					message_lbl.setText("Comunication problem with Db connection. Sorry. :(");
					problems = true;
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}


				try {
					usernameExist = client.sendPostCheckUsername("90.147.102.139", username);
//					usernameExist = client.sendPostCheckUsername("127.0.0.1", username);

				} catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e3) {
					
					// TODO Auto-generated catch block
					message_lbl.setText("Comunication problem with Db connection. Sorry. :(");
					problems = true;
				} catch (Exception e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}

				try {
					passwordCorrect = client.sendPostCheckPassword("90.147.102.139", username,String.valueOf(password));
//					passwordCorrect = client.sendPostCheckPassword("127.0.0.1", username,String.valueOf(password));

				} catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e4) {
					// TODO Auto-generated catch block
					message_lbl.setText("Comunication problem with Db connection. Sorry. :(");
					problems = true;
				} catch (Exception e4) {
					// TODO Auto-generated catch block
					e4.printStackTrace();
				}


				if (problems == true) {
					//nothing
				} else if (usernameExist == false) {
					message_lbl.setText("User doesn't exist");
				} else if (usernameExist == true && passwordCorrect == false) {
					message_lbl.setText("Incorrect password");
				} else if (usernameExist == true && passwordCorrect == true) {
					message_lbl.setText("Logged in");
					username_tf.setEnabled(false);
					password_tf.setEnabled(false);
					openFile.setEnabled(true);
					console.setEnabled(true);
					processFile.setEnabled(true);
					btnLogout.setVisible(true);
					btnLogin.setVisible(false);
					project_tf.setEnabled(true);
					entity_tf.setEnabled(true);
					mode_cb.setEnabled(true);
					comboBox.setEnabled(true);

					if (projects.length == 0) btnDelete.setEnabled(false);
					else btnDelete.setEnabled(true);

				}
			}

		});

		btnLogin.setBounds(0, 0, 100, 20);
		btnLogout.setBounds(0, 0, 100, 20);

		GridBagConstraints gbc_btnLogin = new GridBagConstraints();
		gbc_btnLogin.gridy = 0;
		gbc_btnLogin.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnLogin.insets = new Insets(0, 0, 5, 5);
		gbc_btnLogin.gridx = 4;
		loginPanel.add(btnLogin, gbc_btnLogin);
		GridBagConstraints gbc_btnLogout = new GridBagConstraints();
		gbc_btnLogout.insets = new Insets(0, 0, 5, 0);
		gbc_btnLogout.gridy = 0;
		gbc_btnLogout.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnLogout.gridx = 5;
		loginPanel.add(btnLogout, gbc_btnLogout);

		JPanel messagePanel = new JPanel();
		BorderLayout br_messagePanel = new BorderLayout();
		messagePanel.setLayout(br_messagePanel);

		JLabel messageIco = new JLabel("");
		Image imgMessage = new ImageIcon(this.getClass().getResource("/resources/chat-bubble-icon.png")).getImage();
		imgMessage.getScaledInstance(50, 50, Image.SCALE_DEFAULT);
		messageIco.setIcon(new ImageIcon(imgMessage));

		messagePanel.add(messageIco, BorderLayout.WEST);

		message_lbl = new JTextArea("Here I show dialog messages! :)");
		message_lbl.setBackground(SystemColor.window);
		messageScroll = new JScrollPane(message_lbl);
		
		Border border = BorderFactory.createEmptyBorder( 0, 0, 0, 0 );
		messageScroll.setBorder( border );
		
		JPanel messageAreaPanel = new JPanel();
		Separator separator = new JToolBar.Separator();
		separator.setBackground(SystemColor.window);
		messageAreaPanel.setLayout(new BorderLayout());
		messageAreaPanel.add(separator, BorderLayout.NORTH);
		messageAreaPanel.add(messageScroll,BorderLayout.CENTER);
		
		messagePanel.add(messageAreaPanel,BorderLayout.CENTER);


		superLogin.add(loginPanel, BorderLayout.CENTER);


		superLogin.add(vhdlIco, BorderLayout.WEST);

		BorderLayout mainCenterBL = new BorderLayout();
		JPanel mainCentralPanel = new JPanel();
		mainCentralPanel.setLayout(mainCenterBL);

		final JFileChooser fileChooserOpen = new JFileChooser();
		final JFileChooser fileChooserSave = new JFileChooser();

		
		buttonsPanel = new JPanel();
		
		
		
		GridBagLayout gbl_buttonsPanel = new GridBagLayout();
		
		
		gbl_buttonsPanel.columnWidths = new int[] {
			0
		};
		gbl_buttonsPanel.rowWeights = new double[] {
			0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
		};
		gbl_buttonsPanel.rowHeights = new int[] {
			0, 0, 0, 0, 0, 0, 0
		};
		gbl_buttonsPanel.columnWeights = new double[] {
			1.0
		};
		buttonsPanel.setLayout(gbl_buttonsPanel);

		openFile = new JButton("Load File");
		openFile.setHorizontalAlignment(SwingConstants.LEFT);
		openFile.setEnabled(false);
		Image imgOpen = new ImageIcon(this.getClass().getResource("/resources/software-upload-icon.png")).getImage();
		openFile.setIcon(new ImageIcon(imgOpen));
		GridBagConstraints gbc_openFile = new GridBagConstraints();
		gbc_openFile.anchor = GridBagConstraints.WEST;
		gbc_openFile.insets = new Insets(0, 0, 5, 0);
		gbc_openFile.gridx = 0;
		gbc_openFile.gridy = 0;
		buttonsPanel.add(openFile, gbc_openFile);

		openFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileChooserOpen.showOpenDialog(frame);

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					File file = fileChooserOpen.getSelectedFile();
					String filePath = file.getAbsolutePath();
					String[] fileSplitted = filePath.split("\\.");
					if (fileSplitted[fileSplitted.length - 1].equalsIgnoreCase("vhdl")) {

						try {
							sourceCode = readFile(file);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// This is where a real application would open the file.
						console.setText(sourceCode);
						message_lbl.setText("Chosen file: " + file.getName());
						
						btnDownloadVcd.setEnabled(false);
						btnSimulate.setEnabled(false);
						
					} else {
						message_lbl.setText("Not a VHDL file.");
					}
				} else {
					message_lbl.setText("Open command cancelled by user.");
				}
			}

		});

		mainCentralPanel.add(buttonsPanel, BorderLayout.WEST);

		JPanel consolePanel = new JPanel();
		consolePanel.setLayout(new BorderLayout());

		console = new JTextArea();
		console.setTabSize(0);
		console.setRows(10);
		console.setToolTipText("");
		console.setEnabled(false);
		console.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(console);
		consolePanel.add(scrollPane, BorderLayout.CENTER);

		JPanel propertiesPanel = new JPanel();
		consolePanel.add(propertiesPanel, BorderLayout.NORTH);
		propertiesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		JLabel lblProjectName = new JLabel("Project");
		propertiesPanel.add(lblProjectName);

		project_tf = new JTextField();
		project_tf.setEnabled(false);
		propertiesPanel.add(project_tf);
		project_tf.setColumns(10);

		JLabel lblEntityName = new JLabel("Entity");
		propertiesPanel.add(lblEntityName);

		entity_tf = new JTextField();
		entity_tf.setEnabled(false);
		propertiesPanel.add(entity_tf);
		entity_tf.setColumns(10);

		mode_cb = new Checkbox("is a TB file");
		mode_cb.setEnabled(false);
		propertiesPanel.add(mode_cb);

		mainCentralPanel.add(consolePanel, BorderLayout.CENTER);

		processFile = new JButton("Compile File");
		processFile.setHorizontalAlignment(SwingConstants.LEFT);
		Image imgCompile = new ImageIcon(this.getClass().getResource("/resources/edit-tomboy-icon.png")).getImage();
		processFile.setIcon(new ImageIcon(imgCompile));
		processFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				GHDLServiceClient client = new GHDLServiceClient();
				String mode = "";
				JSONObject result = null;
				
				int n=-1;
				if (!Home.contain(projects, project_tf.getText())){
					//Custom button text
					Object[] options = {
						"Yes",
							"No"
					};
					
					n = JOptionPane.showOptionDialog(frame,
						"Be careful: project name absent or different from existing ones.\nDo you want proceed anyway (a new project will be created)?",
						"Project name warning",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[1]);
				}

				if(n==0 || n==-1){
					if (mode_cb.getState()) {
						mode = "vcd";
						btnDownloadVcd.setEnabled(true);
					}
	
					
					try {
						result = client.sendCompilePost("90.147.102.139",sourceCode, entity_tf.getText(), username_tf.getText(),project_tf.getText(), mode);
//						JSONObject result = client.sendCompilePost("127.0.0.1", sourceCode, entity_tf.getText(), username_tf.getText(), project_tf.getText(), mode);
						message_lbl.setText(result.getString("result")+"\n"+result.getString("output"));
						
						JSONArray JSONprojects = result.getJSONArray("projects");
						
						ArrayList<String> tempProjects = new ArrayList<String>();
						
						for(int i=0;i<JSONprojects.length();i++)
							tempProjects.add(String.valueOf(JSONprojects.get(i)));
						
						Object[] objectArray = tempProjects.toArray();
						projects = Arrays.copyOf(objectArray, objectArray.length, String[].class);
						comboBox.setModel(new DefaultComboBoxModel < > (projects));
						
						if(projects.length > 0)
							btnDelete.setEnabled(true);
						
						if(result.has("vcd_content"))
							vcdSourceFile = result.getString("vcd_content");
						
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						btnSimulate.setEnabled(false);
						btnDownloadVcd.setEnabled(false);
						
						message_lbl.setText("Opsss...something goes wrong.  :("+ "\n"
								+ result.getString("result"));
						
					} 
				}else{
						message_lbl.setText("Compile operation canceled!");
				}
			}
		});

		processFile.setEnabled(false);
		GridBagConstraints gbc_processFile = new GridBagConstraints();
		gbc_processFile.anchor = GridBagConstraints.WEST;
		gbc_processFile.insets = new Insets(0, 0, 5, 0);
		gbc_processFile.gridx = 0;
		gbc_processFile.gridy = 1;
		buttonsPanel.add(processFile, gbc_processFile);
		
		btnDownloadVcd = new JButton("Download VCD");
		btnDownloadVcd.setHorizontalAlignment(SwingConstants.LEFT);
		Image imgDownload = new ImageIcon(this.getClass().getResource("/resources/software-download-icon.png")).getImage();
		btnDownloadVcd.setIcon(new ImageIcon(imgDownload));
		btnDownloadVcd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int returnVal = fileChooserSave.showSaveDialog(frame);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						File newFile = fileChooserSave.getSelectedFile();
						vcdSavedPath = newFile.getAbsolutePath();
						PrintWriter out = new PrintWriter(newFile);
						out.print(vcdSourceFile);
						out.close();
						btnSimulate.setEnabled(true);
				        message_lbl.setText("VCD saved at: " + fileChooserSave.getCurrentDirectory().toString());

					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						message_lbl.setText("File VCD not saved :(");
					}
			    }
			    if (returnVal == JFileChooser.CANCEL_OPTION) {
				    message_lbl.setText("Saving canceled.");
			    }
			}
		});
		
		
		btnDownloadVcd.setEnabled(false);
		GridBagConstraints gbc_btnDownloadVcd = new GridBagConstraints();
		gbc_btnDownloadVcd.anchor = GridBagConstraints.WEST;
		gbc_btnDownloadVcd.insets = new Insets(0, 0, 5, 0);
		gbc_btnDownloadVcd.gridx = 0;
		gbc_btnDownloadVcd.gridy = 2;
		buttonsPanel.add(btnDownloadVcd, gbc_btnDownloadVcd);

		btnSimulate = new JButton("Simulate");
		btnSimulate.setHorizontalAlignment(SwingConstants.LEFT);
		Image imgSimulate = new ImageIcon(this.getClass().getResource("/resources/rocket-icon.png")).getImage();
		btnSimulate.setIcon(new ImageIcon(imgSimulate));
		btnSimulate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				JOptionPane.showMessageDialog(frame, "You need to have GTKwave installed.");
				
				/* ***** Setting Command Exec ****** */
				Executor exec = new DefaultExecutor();
				
				CommandLine cl_compile = new CommandLine("open");
				cl_compile.addArgument(vcdSavedPath);
				try {
					int exitvalue = exec.execute(cl_compile);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					message_lbl.setText("Opss...something goes wrong.  :(\n"+e1.getLocalizedMessage());
				}
				
				
			}
		});
		btnSimulate.setEnabled(false);
		GridBagConstraints gbc_btnSimulate = new GridBagConstraints();
		gbc_btnSimulate.anchor = GridBagConstraints.WEST;
		gbc_btnSimulate.insets = new Insets(0, 0, 5, 0);
		gbc_btnSimulate.gridx = 0;
		gbc_btnSimulate.gridy = 3;
		buttonsPanel.add(btnSimulate, gbc_btnSimulate);

		JLabel lblYourProjects = new JLabel("Your projects");
		GridBagConstraints gbc_lblYourProjects = new GridBagConstraints();
		gbc_lblYourProjects.insets = new Insets(0, 0, 5, 0);
		gbc_lblYourProjects.gridx = 0;
		gbc_lblYourProjects.gridy = 4;
		buttonsPanel.add(lblYourProjects, gbc_lblYourProjects);

		comboBox = new JComboBox();
		comboBox.setMaximumSize(new Dimension(140,25));
		comboBox.setMinimumSize(new Dimension(140,25));

		btnDownloadVcd.setMaximumSize(new Dimension(140,40));
		btnDownloadVcd.setMinimumSize(new Dimension(140,40));
		
		btnSimulate.setMaximumSize(new Dimension(140,40));
		btnSimulate.setMinimumSize(new Dimension(140,40));
		
		processFile.setMaximumSize(new Dimension(140,40));
		processFile.setMinimumSize(new Dimension(140,40));
		
		openFile.setMaximumSize(new Dimension(140,40));
		openFile.setMinimumSize(new Dimension(140,40));
		
		buttonsPanel.setMaximumSize(new Dimension(140,40));
		buttonsPanel.setMinimumSize(new Dimension(140,40));
		
		comboBox.setToolTipText("These are your projects on server");
		comboBox.setEnabled(false);

		btnDelete = new JButton("Delete Project");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String projectName = String.valueOf(comboBox.getSelectedItem());

				try {
					GHDLServiceClient client = new GHDLServiceClient();

					try {
						projects = client.sendDeletePost("90.147.102.139",project_tf.getText(),username_tf.getText());
//						projects = client.sendDeletePost("127.0.0.1", projectName, username_tf.getText());

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						message_lbl.setText("Something goes wrong on DELETE project (server side). Contact us.");
					}

				} catch (Exception e2) {
					// TODO Auto-generated catch block
					message_lbl.setText("Something goes wrong on DELETE project (server side). Contact us.");
				}

				comboBox.setModel(new DefaultComboBoxModel < > (projects));

				if (projects.length == 0) btnDelete.setEnabled(false);
				else btnDelete.setEnabled(true);

				message_lbl.setText(projectName + " deleted!");

			}
		});


		gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.gridx = 0;
		gbc_comboBox.gridy = 5;
		
		buttonsPanel.add(comboBox, gbc_comboBox);
		
		
		
		btnDelete.setEnabled(false);
		btnDelete.setHorizontalAlignment(SwingConstants.RIGHT);
		Image imgDelete = new ImageIcon(this.getClass().getResource("/resources/trash-icon.png")).getImage();
		btnDelete.setIcon(new ImageIcon(imgDelete));
		GridBagConstraints gbc_btnDelete = new GridBagConstraints();
		gbc_btnDelete.anchor = GridBagConstraints.EAST;
		gbc_btnDelete.gridx = 0;
		gbc_btnDelete.gridy = 6;
		buttonsPanel.add(btnDelete, gbc_btnDelete);


		frame.setResizable(false);
		frame.getContentPane().add(mainCentralPanel, BorderLayout.CENTER);
		frame.getContentPane().add(superLogin, BorderLayout.NORTH);
		
		
		JButton button = new JButton();
		superLogin.add(button, BorderLayout.SOUTH);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				open(uri);
			}
		});
		button.setText("<HTML>Click the <FONT color=\"#000099\"><U>link</U></FONT>" + " to sign to the website.</HTML>");

		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setBorderPainted(false);
		button.setOpaque(false);
		button.setBackground(Color.WHITE);
		button.setToolTipText(uri.toString());
		frame.getContentPane().add(messagePanel, BorderLayout.SOUTH);
	}

	private static String readFile(File fileToRead) throws IOException {

		FileReader reader = new FileReader(fileToRead);

		BufferedReader br = new BufferedReader(reader);
		String s, result = "";
		while ((s = br.readLine()) != null) {
			result += s + "\n";
		}
		reader.close();

		return result;
	}
	
	
	private static boolean contain(Object[] source, String element){
		
		String[] stringArray = Arrays.copyOf(source, source.length, String[].class);
		
		if(source.length == 0)
			return false;
		
		for (String string : stringArray) {
			if(string.equalsIgnoreCase(element))
				return true;
			
		}
		
		return false;
	}

	private static void open(URI uri) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(uri);
			} catch (IOException e) { /* TODO: error handling */
			}
		} else { /* TODO: error handling */
		}
	}
	
	
	
}