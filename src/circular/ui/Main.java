package circular.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

import circular.framework.Circular;
import circular.framework.CircularProperties;
import circular.framework.Configure;
import circular.framework.ControlServer;
import circular.framework.Cycle;
import circular.framework.Observer;
import circular.framework.observer.IdleObserver;
import circular.framework.observer.Observers;
import circular.framework.observer.SynchronizedObserver;

/**
 * Circular Framework�ɕt������f�t�H���g�̑���GUI��main�N���X�ł��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
public class Main {

	private static final Map<Configure, SignalPanel> signalPanelMap = new HashMap<Configure, SignalPanel>();

	private static final Map<Configure, String> configNameMap = new HashMap<Configure, String>();

	private static final List<ControlServer> controlServers = new LinkedList<ControlServer>();

	private static final List<Circular> circulars = new LinkedList<Circular>();

	private static File configDir;

	/**
	 * Circular���N�����܂��B
	 * <p>
	 * Circular�ɂ́A�O��ނ̐ݒ�t�@�C��������܂��B��́ACircular Framework��{@link Configure}�p�̐ݒ�t�@�C��(�K�{)�A�x�񃁁[����{@link Mail}�p�̐ݒ�t�@�C��(�C��)�A{@link ObserverFactory}�p�̐ݒ�t�@�C��(�C��)�ł��B
	 * <br>
	 * �����̓p�����[�^�Ŏw�肳���config�f�B���N�g������
	 * <br>
	 * "CircularFrameworkConfigure.properties"
	 * <br>
	 * "AlertMail.properties"
	 * <br>
	 * "ObserverFactory.properties"
	 * <br>
	 * ���g�p����悤�ɂȂ��Ă��܂��B
	 * <p>
	 * "AlertMail.properties"�����݂��Ȃ��ꍇ�A�J�����̎��s�ł���Ƃ݂Ȃ��A�x�񃁁[�����o�͂���Ȃ��Ȃ�܂��B
	 * <p>
	 * config�f�B���N�g������"autostart"�Ƃ������̂̃t�@�C�������݂���ꍇ�A�N����A���ׂĂ�Circular�����s���J�n���܂��B
	 * 
	 * @param args ���p�����[�^��config�f�B���N�g���ւ̃p�X
	 */
	public static void main(String[] args) {
		if (args.length != 1) throw new IllegalArgumentException(
			"�p�����[�^�ɂ�config�f�B���N�g���̐�΃p�X���K�v�ł��B");

		File configDir = ConfigureUtils.createConfigDirectory(args[0]);
		setConfigDirectory(configDir);

		File alertMailFile = new File(configDir, "AlertMail.properties");
		URL alertMailPropertiesUrl;
		if (alertMailFile.exists()) {
			alertMailPropertiesUrl = ConfigureUtils.parse(
				alertMailFile,
				Mail.class);
		} else {
			alertMailPropertiesUrl = null;
		}

		File observerFactoryFile = new File(
			configDir,
			"ObserverFactory.properties");
		URL observerFactoryPropertiesUrl;
		if (observerFactoryFile.exists()) {
			observerFactoryPropertiesUrl = ConfigureUtils.parse(
				observerFactoryFile,
				ObserverFactory.class);
		} else {
			observerFactoryPropertiesUrl = null;
		}

		final JFrame frame = new JFrame();

		frame.setEnabled(false);

		frame.setTitle("Circular");
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				frame.setEnabled(false);
				frame.setVisible(false);
				synchronized (controlServers) {
					for (ControlServer server : controlServers) {
						server.shutdown();
					}
				}

				synchronized (circulars) {
					for (Circular circular : circulars) {
						circular.stop();
					}
				}

				System.exit(0);
			}

			@Override
			public void windowActivated(WindowEvent e) {
				frame.repaint();
			}
		});

		final JTabbedPane tabbedPane = new JTabbedPane();

		frame.add(tabbedPane);

		LinkedHashMap<String, URL> map = ConfigureUtils.getConfigureFiles(configDir);
		int index = 0;
		for (Entry<String, URL> entry : map.entrySet()) {
			createTab(
				frame,
				tabbedPane,
				index++,
				entry.getKey(),
				entry.getValue(),
				alertMailPropertiesUrl,
				observerFactoryPropertiesUrl);
		}

		//�S�Ă̏������������̂ŁA�C�x���g�f�B�X�p�b�`�X���b�h���瑀��\�ɂ���
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				frame.setLocation(40, 40);
				frame.pack();
				frame.setResizable(false);
				frame.setVisible(true);
				frame.setEnabled(true);

				if (new File(getConfigDirectory(), "autostart").exists()) {
					for (Circular circular : circulars)
						circular.start();
				}
			}
		});
	}

	/**
	 * �Ǝ��̌x����GUI�ɕ\�����܂��B
	 * <p>
	 * �u�x��v�p�l���̐F�����F�ɂȂ�A�x�������\������܂��B
	 * <p>
	 * ���̃��\�b�h�́A{@link Cycle#execute()}���ŌĂяo���K�v������܂��B
	 * 
	 * @param message �Ǝ��̌x����
	 * @throws IllegalStateException {@link Cycle#execute()}�̊O�ŌĂяo�����ꍇ
	 */
	public static void warn(String message) {
		warn(message, Cycle.getConfigure());
	}

	/**
	 * �Ǝ��̌x����GUI�ɕ\�����܂��B
	 * <p>
	 * �u�x��v�p�l���̐F�����F�ɂȂ�A�x�������\������܂��B
	 * 
	 * @param message �Ǝ��̌x����
	 * @param config �u�x��v�p�l������肷�邽�߂̃L�[
	 */
	public static void warn(String message, Configure config) {
		LogObserver.warn(message);

		SignalPanel signalPanel;
		synchronized (signalPanelMap) {
			signalPanel = signalPanelMap.get(config);
		}
		signalPanel.warn(message);
	}

	/**
	 * �ݒ�̖��̂�ԋp���܂��B
	 * <p>
	 * ���̃��\�b�h�́A{@link Cycle#execute()}���ŌĂяo���K�v������܂��B
	 * 
	 * @return �ݒ�̖���
	 */
	public static String getConfigName() {
		return getConfigName(Cycle.getConfigure());
	}

	/**
	 * �ݒ�̖��̂�ԋp���܂��B
	 * 
	 * @param config ���̂��K�v�Ȑݒ�
	 * @return �ݒ�̖���
	 */
	public static String getConfigName(Configure config) {
		synchronized (configNameMap) {
			return configNameMap.get(config);
		}
	}

	/**
	 * Circular�p�̐ݒ�t�@�C����u���Ă���f�B���N�g���̃p�X��ԋp���܂��B
	 * 
	 * @return Circular�ݒ�t�@�C���f�B���N�g��
	 */
	public static synchronized File getConfigDirectory() {
		return configDir;
	}

	private Main() {}

	private static synchronized void setConfigDirectory(File dir) {
		configDir = dir;
	}

	private static void createTab(
		JFrame frame,
		final JTabbedPane tabbedPane,
		int tabIndex,
		String configName,
		URL configurePropertiesUrl,
		URL alertMailPropertiesUrl,
		URL observerFactoryPropertiesUrl) {
		NextTimePanel nextTime = new NextTimePanel();

		TitledPanel phaseInformation = new TitledPanel(
			"���ݎ��s���̃t�F�C�Y",
			25,
			SwingConstants.LEFT);

		final SignalPanel signalPanel = new SignalPanel(tabbedPane, tabIndex);

		Observer signalObserver = signalPanel.getObserver();

		final CircularPanel circularPanel = new CircularPanel(
			nextTime,
			tabbedPane,
			tabIndex,
			signalPanel);

		LogObserver logObserver = new LogObserver(configName);

		Observer observersObserver = buildObservers(new Observer[] {
			logObserver,
			signalObserver }, new IdleObserver());

		List<Observer> observerList = new LinkedList<Observer>();
		observerList.add(logObserver);

		if (alertMailPropertiesUrl != null) {
			CircularProperties properties;
			try {
				properties = new CircularProperties(alertMailPropertiesUrl);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			observerList.add(new AlertObserver(properties));
		}

		observerList.add(circularPanel.getObserver());
		observerList.add(signalObserver);
		observerList.add(nextTime.getObserver());
		observerList.add(new PhaseInformationObserver(phaseInformation));

		if (observerFactoryPropertiesUrl != null) {
			CircularProperties properties;
			try {
				properties = new CircularProperties(
					observerFactoryPropertiesUrl);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			String className = properties.getProperty(ObserverFactory.CLASS_KEY);
			try {
				ObserverFactory factory = (ObserverFactory) Class.forName(
					className).newInstance();
				Collections.addAll(
					observerList,
					factory.createObservers(configName));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		Observer observers = buildObservers(
			observerList.toArray(new Observer[observerList.size()]),
			observersObserver);

		CircularProperties properties;
		try {
			properties = new CircularProperties(configurePropertiesUrl);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Configure config = new Configure(properties, new SynchronizedObserver(
			observers));

		final Circular circular = new Circular(config);
		synchronized (circulars) {
			circulars.add(circular);
		}

		circularPanel.setCircular(circular);

		ControlServer server = null;
		if (config.useControlServer()) {
			server = new ControlServer(config, circular);
			synchronized (controlServers) {
				controlServers.add(server);
			}
		}

		synchronized (signalPanelMap) {
			signalPanelMap.put(config, signalPanel);
		}

		synchronized (configNameMap) {
			configNameMap.put(config, configName);
		}

		final JPanel base = new JPanel();

		GridBagLayout layout = new GridBagLayout();
		base.setLayout(layout);

		GridBagConstraints constraints = new GridBagConstraints();

		JPanel top = new JPanel();
		top.add(circularPanel);
		top.add(new IntervalMinutesControlPanel(config));

		constraints.gridx = 0;
		constraints.gridy = 0;
		layout.setConstraints(top, constraints);
		base.add(top);

		constraints.gridx = 0;
		constraints.gridy = 1;
		layout.setConstraints(signalPanel, constraints);
		base.add(signalPanel);

		JPanel middle = new JPanel();
		middle.add(nextTime);
		middle.add(createConfigureCheckPanel(frame, config));

		constraints.gridx = 0;
		constraints.gridy = 2;
		layout.setConstraints(middle, constraints);
		base.add(middle);

		constraints.gridx = 0;
		constraints.gridy = 3;
		layout.setConstraints(phaseInformation, constraints);
		base.add(phaseInformation);

		JPanel bottom = new JPanel();
		TitledPanel monitorPanel = new TitledPanel(
			"�������g�p��(KB)",
			10,
			SwingConstants.RIGHT);
		bottom.add(monitorPanel);
		bottom.add(createThreadDumpPanel());

		MemoryMonitor memoryMonitor = new MemoryMonitor(monitorPanel);
		monitorPanel.add(createGCButton(memoryMonitor));
		memoryMonitor.execute();

		constraints.gridx = 0;
		constraints.gridy = 4;
		layout.setConstraints(bottom, constraints);
		base.add(bottom);

		String title = config.getTitle();
		if (title == null || title.length() == 0) title = configName;

		tabbedPane.addTab(title, base);

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				circularPanel.setStopped();
				signalPanel.reset();
			}
		});

		tabbedPane.setVisible(true);

		if (config.useControlServer()) {
			server.startup();
		}

		circularPanel.start();
		new Timer(1000, memoryMonitor).start();
	}

	private static JButton createGCButton(final MemoryMonitor monitor) {
		JButton button = new JButton("GC");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.gc();
				monitor.execute();
			}
		});
		button.setToolTipText("�K�[�x�b�W�R���N�V���������s���܂�");
		return button;
	}

	private static Observer buildObservers(
		Observer[] observers,
		Observer observersObserver) {
		return new SynchronizedObserver(new Observers(
			observers,
			observersObserver));
	}

	private static JPanel createThreadDumpPanel() {
		JPanel threadDumpPanel = new JPanel();

		threadDumpPanel.setBorder(new TitledBorder("Stack trace"));

		JButton button = new JButton("Copy");
		button.addActionListener(new ThreadDumper(threadDumpPanel.getToolkit()
			.getSystemClipboard()));
		button.setToolTipText("�S�X���b�h�̃X�^�b�N�g���[�X���N���b�v�{�[�h�ɃR�s�[���܂�");

		threadDumpPanel.add(button);

		return threadDumpPanel;
	}

	private static JPanel createConfigureCheckPanel(
		JFrame frame,
		Configure config) {
		JPanel configureCheckPanel = new JPanel();

		configureCheckPanel.setBorder(new TitledBorder("DB�ڑ�"));

		JButton button = new JButton("Check");
		button.addActionListener(new ConfigureChecker(frame, config));
		button.setToolTipText("�ݒ�t�@�C�����̑S�ڑ������e�X�g���܂�");

		configureCheckPanel.add(button);

		return configureCheckPanel;
	}

	private static class ThreadDumper implements ActionListener, ClipboardOwner {

		private final Clipboard clipboard;

		private ThreadDumper(Clipboard clipboard) {
			this.clipboard = clipboard;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			StringWriter content = new StringWriter();

			PrintWriter writer = new PrintWriter(content);

			writer.println(DateFormat.getDateTimeInstance().format(new Date()));
			writer.println();

			Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
			for (Entry<Thread, StackTraceElement[]> entry : map.entrySet()) {
				StackTraceElement[] elements = entry.getValue();
				writer.println(entry.getKey().getName());
				for (StackTraceElement element : elements) {
					writer.println("\t" + element);
				}
				writer.println();
			}
			writer.flush();
			writer.close();

			clipboard.setContents(new StringSelection(content.toString()), this);
		}

		@Override
		public void lostOwnership(Clipboard clipboard, Transferable contents) {}
	}

	private static class MemoryMonitor implements ActionListener {

		private static final int denominator = 1024;

		private final Runtime runtime = Runtime.getRuntime();

		private final TitledPanel monitorPanel;

		private long free = runtime.freeMemory() / denominator;

		private long total = runtime.totalMemory() / denominator;

		private MemoryMonitor(TitledPanel monitorPanel) {
			this.monitorPanel = monitorPanel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			execute();
		}

		private void execute() {
			long newFree = runtime.freeMemory() / denominator;
			long newTotal = runtime.totalMemory() / denominator;
			if (free == newFree && total == newTotal) return;
			monitorPanel.setText((newTotal - newFree) + "/" + newTotal);
			free = runtime.freeMemory() / denominator;
			total = runtime.totalMemory() / denominator;
		}
	}

	private static class ConfigureChecker implements ActionListener {

		private final JFrame frame;

		private final Configure config;

		private ConfigureChecker(JFrame frame, Configure config) {
			this.frame = frame;
			this.config = config;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			StringWriter content = new StringWriter();
			PrintWriter writer = new PrintWriter(content);

			boolean inputResult = config.testInputDriver();
			writer.println(getResultMessage(inputResult)
				+ " "
				+ config.getInputDriverInformation());

			boolean outputResult = config.testOutputDriver();
			writer.println(getResultMessage(outputResult)
				+ " "
				+ config.getOutputDriverInformation());

			boolean ourResult = config.testOurDriver();
			writer.println(getResultMessage(ourResult)
				+ " "
				+ config.getOurDriverInformation());

			boolean backupResult = config.testBackupDriver();
			writer.println(getResultMessage(backupResult)
				+ " "
				+ config.getBackupDriverInformation());

			boolean result = inputResult
				&& outputResult
				&& ourResult
				&& backupResult;

			JOptionPane.showMessageDialog(frame, content.toString(), "�e�X�g����"
				+ " "
				+ getResultMessage(result), result
				? JOptionPane.INFORMATION_MESSAGE
				: JOptionPane.ERROR_MESSAGE);
		}
	}

	private static String getResultMessage(boolean result) {
		return result ? "OK " : "NG ";
	}
}
