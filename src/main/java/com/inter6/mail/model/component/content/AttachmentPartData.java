package com.inter6.mail.model.component.content;

import lombok.Data;

@Data
public class AttachmentPartData extends PartData {
	private String contentTypeStr; // NOPMD
	private String contentId; // NOPMD
	private String contentDisposition; // NOPMD
	private String contentTransferEncoding; // NOPMD

	private String filePath; // NOPMD
	private String filename; // NOPMD
	private String filenameCharset; // NOPMD
	private String filenameEncoding; // NOPMD
}
