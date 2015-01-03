package com.inter6.mail.model.data;

import java.io.File;
import java.util.Collection;

import lombok.Data;

@Data
public class EmlSourceData {
	private Collection<File> files; // NOPMD

	public EmlSourceData() {
	}

	public EmlSourceData(Collection<File> files) {
		super();
		this.files = files;
	}
}
