package com.inter6.mail.model.data;

import java.util.Collection;

import lombok.Data;

@Data
public class DirSourceData {
	private Collection<String> dirs; // NOPMD
	private boolean isRecursive; // NOPMD
}
