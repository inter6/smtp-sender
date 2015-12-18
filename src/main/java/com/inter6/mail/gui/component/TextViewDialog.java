package com.inter6.mail.gui.component;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.inter6.mail.gui.MainFrame;
import com.inter6.mail.module.ModuleService;

public class TextViewDialog {
	private final JDialog dialog;

	private TextViewDialog(Window owner, String text) {
		this.dialog = new JDialog(owner);
		this.dialog.setLayout(new BorderLayout());
		this.dialog.add(new JScrollPane(new JTextArea(text)), BorderLayout.CENTER);
	}

	public static TextViewDialog createDialog(String text) {
		return new TextViewDialog(ModuleService.getBean(MainFrame.class), text);
	}

	public static TextViewDialog createDialog(Window owner, String text) {
		return new TextViewDialog(owner, text);
	}

	public TextViewDialog setTitle(String title) {
		this.dialog.setTitle(title);
		return this;
	}

	public TextViewDialog setModal() {
		this.dialog.setModal(true);
		return this;
	}

	public TextViewDialog setSize(int width, int height) {
		this.dialog.setSize(width, height);
		return this;
	}

	public void show() {
		this.dialog.setVisible(true);
	}
}
