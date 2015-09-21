package com.inter6.mail.job.smtp;

import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.model.data.ScpSourceData;
import com.inter6.mail.module.ModuleService;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@Component
@Scope("prototype")
@Slf4j
public class ScpSmtpSendJob extends AbstractSmtpSendMasterJob {

	@Autowired
	private LogPanel logPanel;

	@Setter
	private ScpSourceData scpSourceData; // NOPMD

	private float progressRate;

	@Override
	protected void doMasterJob() throws Throwable {
		Session session = null;
		try {
			session = new JSch().getSession(scpSourceData.getUsername(), scpSourceData.getHost(), scpSourceData.getPort());
			session.setPassword(scpSourceData.getPassword());
			session.setUserInfo(scpUserInfo);
			session.connect(60 * 1000);

			for (String path : scpSourceData.getPaths()) {
				List<String> emlPaths = findEmlPaths(session, path);
				this.logPanel.info("eml file count - PATH:" + path + " COUNT:" + emlPaths.size());
				if (CollectionUtils.isEmpty(emlPaths)) {
					continue;
				}

				for (int i = 0; i < emlPaths.size(); i++) {
					String emlPath = emlPaths.get(i);
					try {
						MimeSmtpSendJob mimeSmtpSendJob = ModuleService.getBean(MimeSmtpSendJob.class);
						mimeSmtpSendJob.setMessageStream(new ByteArrayInputStream(getRemoteFile(session, emlPath)));
						mimeSmtpSendJob.setReplaceDateData(scpSourceData.getReplaceDateData());
						this.orderWorker(mimeSmtpSendJob);
					} catch (Throwable e) {
						this.logPanel.error("eml send order fail ! - EML:" + emlPath, e);
					}
					this.progressRate = (float) i / (float) emlPaths.size() * 100f;
				}
			}
		} finally {
			if (session != null) {
				session.disconnect();
			}
		}
	}

	private List<String> findEmlPaths(Session session, String path) throws JSchException, IOException {
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
	}

	private byte[] getRemoteFile(Session session, String path) throws JSchException, IOException {
		ChannelExec channel = (ChannelExec) session.openChannel("exec");
		channel.setCommand("scp -f " + path);
		channel.connect(60 * 1000);

		OutputStream out = channel.getOutputStream();
		InputStream in = channel.getInputStream();
		channel.connect();

		byte[] buf = new byte[1024];

		// send '\0'
		buf[0] = 0;
		out.write(buf, 0, 1);
		out.flush();

		byte[] bytes = null;
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
					throw new IOException();
				}
				if (buf[0] == ' ') break;
				filesize = filesize * 10L + (long) (buf[0] - '0');
			}

			String file = null;
			for (int i = 0; ; i++) {
				in.read(buf, i, 1);
				if (buf[i] == (byte) 0x0a) {
					file = new String(buf, 0, i);
					break;
				}
			}

			log.info("find remote file - PATH:" + file + " SIZE:" + filesize);

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();

			// read a content of lfile
			try (ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
				int foo;
				while (true) {
					if (buf.length < filesize) foo = buf.length;
					else foo = (int) filesize;
					foo = in.read(buf, 0, foo);
					if (foo < 0) {
						// error
						break;
					}
					fos.write(buf, 0, foo);
					filesize -= foo;
					if (filesize == 0L) break;
				}
				bytes = fos.toByteArray();
			}

			if (checkAck(in) != 0) {
				throw new IOException();
			}

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
		}
		return bytes;
	}

	private int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		//          1 for error,
		//          2 for fatal error,
		//          -1
		if (b == 0) return b;
		if (b == -1) return b;

		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			}
			while (c != '\n');
			if (b == 1) { // error
				throw new IOException(sb.toString());
			}
			if (b == 2) { // fatal error
				throw new IOException(sb.toString());
			}
		}
		return b;
	}

	@Override
	protected float getProgressRate() {
		return this.progressRate;
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
