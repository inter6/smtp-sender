package com.inter6.mail.model.data;

import lombok.Data;

import java.util.Set;

@Data
public class EnvelopeData {
	private String mailFrom;
	private Set<String> rcptTos;
}
