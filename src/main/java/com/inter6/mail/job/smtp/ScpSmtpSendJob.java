package com.inter6.mail.job.smtp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.PostConstruct;

import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.model.data.ScpSourceData;
import com.inter6.mail.module.ModuleService;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ScpSmtpSendJob extends AbstractSmtpSendMasterJob {

	private LogPanel logPanel;

	@Setter
	private ScpSourceData scpSourceData;

	private float progressRate;
	private boolean isTerminated;

	@PostConstruct
	private void init() {
		logPanel = tabComponentManager.getTabComponent(tabName, LogPanel.class);
	}

	@Override
	protected void doMasterJob() throws Throwable {
		Session session = null;
		try {
			session = new JSch().getSession(scpSourceData.getUsername(), scpSourceData.getHost(), scpSourceData.getPort());
			session.setPassword(scpSourceData.getPassword());
			session.setUserInfo(scpUserInfo);
			session.connect(60 * 1000);

			int i = 0;
			for (String path : scpSourceData.getPaths()) {
				if (isTerminated) {
					return;
				}

				try {
					orderRemoteFile(session, path);
				} catch (Throwable e) {
					this.logPanel.error("eml send order fail ! - PATH:" + path, e);
				}
				i++;
				this.progressRate = (float) i / (float) path.length() * 100f;
			}
		} finally {
			if (session != null) {
				session.disconnect();
			}
		}
	}

	/*private List<String> findEmlPaths(Session session, String path) throws JSchException, IOException {
		ChannelExec channel = null;
		try {
			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand("find " + path + " -name \"*.eml\" -type f");
	
			InputStream in = channel.getInputStream();
			channel.connect(60 * 1000);
			return IOUtils.readLines(in);
		} finally {
			if (channel != null) {
				channel.disconnect();
			}
		}
	}*/

	private void orderRemoteFile(Session session, String path) throws JSchException, IOException {
		ChannelExec channel = null;
		try {
			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand("scp -f " + path);

			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();
			channel.connect();

			byte[] buf = new byte[1024];

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();

			while (true) {
				int c = checkAck(in);
				if (c != 'C') {
					break;
				}

				// read '0644 '
				in.read(buf, 0, 5);

				long filesize = 0L;
				while (true) {
					if (in.read(buf, 0, 1) < 0) {
						// error
						break;
					}
					if (buf[0] == ' ')
						break;
					filesize = filesize * 10L + (long) (buf[0] - '0');
				}

				String file;
				for (int i = 0;; i++) {
					in.read(buf, i, 1);
					if (buf[i] == (byte) 0x0a) {
						file = new String(buf, 0, i);
						break;
					}
				}

				logPanel.info("find remote file - FILE:" + file + " SIZE:" + filesize);

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();

				// read a content of lfile
				try (ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
					int foo;
					while (true) {
						if (buf.length < filesize)
							foo = buf.length;
						else
							foo = (int) filesize;
						foo = in.read(buf, 0, foo);
						if (foo < 0) {
							// error
							break;
						}
						fos.write(buf, 0, foo);
						filesize -= foo;
						if (filesize == 0L)
							break;
					}

					try {
						MimeSmtpSendJob mimeSmtpSendJob = ModuleService.getBean(MimeSmtpSendJob.class);
						mimeSmtpSendJob.setTabName(tabName);
						mimeSmtpSendJob.setMessageStream(new ByteArrayInputStream(fos.toByteArray()));
						mimeSmtpSendJob.setReplaceDateData(scpSourceData.getReplaceDateData());

						if (scpSourceData.getSendDelayData().isUse()) {
							mimeSmtpSendJob.execute();
							Thread.sleep(scpSourceData.getSendDelayData().getDelaySecond() * 1000);
						} else {
							this.orderWorker(mimeSmtpSendJob);
						}
					} catch (Throwable e) {
						this.logPanel.error("eml send order fail ! - PATH:" + path, e);
					}
				}

				if (checkAck(in) != 0) {
					throw new IOException();
				}

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
			}
		} finally {
			if (channel != null) {
				channel.disconnect();
			}
		}
	}

	private int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		//          1 for error,
		//          2 for fatal error,
		//          -1
		if (b == 0)
			return b;
		if (b == -1)
			return b;

		if (b == 1 || b == 2) {
			StringBuilder sb = new StringBuilder();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				logPanel.error("access remote file fail ! - " + sb.toString(), null);
			}
			if (b == 2) { // fatal error
				logPanel.error("access remote file fail ! - " + sb.toString(), null);
			}
		}
		return b;
	}

	@Override
	protected float getProgressRate() {
		return this.progressRate;
	}

	@Override
	public void terminate() throws InterruptedException {
		super.terminate();
		isTerminated = true;
	}

	@Override
	public String toString() {
		String info = "ScpSmtpSendJob PATH:" + this.scpSourceData.getPaths();
		if (info.length() > 50) {
			info = StringUtils.substring(info, 0, 100) + "...";
		}
		return info;
	}

	private UserInfo scpUserInfo = new UserInfo() {

		@Override
		public String getPassphrase() {
			return null;
		}

		@Override
		public String getPassword() {
			return null;
		}

		@Override
		public boolean promptPassword(String message) {
			return false;
		}

		@Override
		public boolean promptPassphrase(String message) {
			return false;
		}

		@Override
		public boolean promptYesNo(String message) {
			return true;
		}

		@Override
		public void showMessage(String message) {

		}
	};
}
