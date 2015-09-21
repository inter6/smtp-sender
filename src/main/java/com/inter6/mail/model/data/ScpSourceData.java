package com.inter6.mail.model.data;

import com.inter6.mail.model.component.DateData;
import lombok.Data;

import java.util.Collection;

@Data
public class ScpSourceData {
	private String host; // NOPMD
	private int port; // NOPMD
	private String username; // NOPMD
	private String password; // NOPMD

	private Collection<String> paths; // NOPMD
	private DateData replaceDateData; // NOPMD
}
