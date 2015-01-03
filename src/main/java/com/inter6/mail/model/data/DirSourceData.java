package com.inter6.mail.model.data;

import java.io.File;
import java.util.Collection;

import lombok.Data;

@Data
public class DirSourceData {
	private Collection<File> dirs; // NOPMD
	private boolean isRecursive; // NOPMD
}
