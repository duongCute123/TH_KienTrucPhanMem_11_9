package Gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.BasicConfigurator;

public class ServerGui extends JFrame {

	private JPanel contentPane;
	private JTextField txtNhpTinMun;
	private JButton btnNewButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGui frame = new ServerGui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Create the frame.
	 */
	public ServerGui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 562, 257);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JTextArea textArea = new JTextArea();
		textArea.setRows(9);
		contentPane.add(textArea, BorderLayout.NORTH);

		txtNhpTinMun = new JTextField();
		txtNhpTinMun.setColumns(20);
		txtNhpTinMun.setText("Nh\u1EADp tin mu\u1ED1n send");
		contentPane.add(txtNhpTinMun, BorderLayout.WEST);

		btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// thiết lập môi trường cho JMS logging
					BasicConfigurator.configure();
					// thiết lập môi trường cho JJNDI
					Properties settings = new Properties();
					settings.setProperty(Context.INITIAL_CONTEXT_FACTORY,
							"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
					settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:81616");
					// tạo context
					Context ctx = new InitialContext(settings);
					// lookup JMS connection factory
					Object obj = ctx.lookup("TopicConnectionFactory");
					ConnectionFactory factory = (ConnectionFactory) obj;
					// tạo connection
					Connection con = (Connection) factory.createConnection("admin", "admin");
					// nối đến MOM
					con.start();
					// tạo session
					Session session = con.createSession(/* transaction */false, /* ACK */Session.AUTO_ACKNOWLEDGE);
					Destination destination = (Destination) ctx.lookup("dynamicTopics/thanthidet");
					// tạo producer
					MessageProducer producer = session.createProducer(destination);
					Message msg = session.createTextMessage(txtNhpTinMun.getText());
					// gửi
					producer.send(msg);
					// shutdown connection
					session.close();
					con.close();
					System.out.println("Finished...");
				} catch (Exception e2) {
					e2.printStackTrace();
				}

			}
		});
		contentPane.add(btnNewButton, BorderLayout.EAST);
	}

}
