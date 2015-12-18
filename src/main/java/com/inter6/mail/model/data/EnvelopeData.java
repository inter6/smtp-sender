package com.inter6.mail.model.data;

import java.util.Set;

import lombok.Data;

@Data
public class EnvelopeData {
	private String mailFrom;
	private Set<String> rcptTos;
}
