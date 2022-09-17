package Gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.log4j.BasicConfigurator;

public class ReceGui extends JFrame {

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
	public ReceGui() {
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
					// thiết lập môi trường cho JMS
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
					Connection con = factory.createConnection("admin", "admin");
					// nối đến MOM
					con.start();
					// tạo session
					Session session = con.createSession(/* transaction */false, /* ACK */Session.CLIENT_ACKNOWLEDGE);
					// tạo consumer
					Destination destination = (Destination) ctx.lookup("dynamicTopics/thanthidet");
					MessageConsumer receiver = session.createConsumer(destination);
					// receiver.receive();//blocked method
					// Cho receiver lắng nghe trên queue, chừng có message thì notify
					receiver.setMessageListener(new MessageListener() {

						public void onMessage(Message msg) {
							try {
								if (msg instanceof TextMessage) {
									TextMessage tm = (TextMessage) msg;
									String txt = tm.getText();
									System.out.println("XML= " + txt);
									msg.acknowledge();// gửi tín hiệu ack
								}
							} catch (Exception e2) {
								e2.printStackTrace();
							}

						}
					});
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		contentPane.add(btnNewButton, BorderLayout.EAST);
	}

}
