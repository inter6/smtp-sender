package com.inter6.mail.model.component.content;

import com.inter6.mail.model.component.EncodingTextData;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class AttachmentPartData extends PartData {
	private String contentTypeStr;
	private boolean isContentIdUse;
	private String contentId;
	private String contentDisposition;
	private String contentTransferEncoding;

	private String filePath;
	private EncodingTextData typeNameData;
	private EncodingTextData dispositionFilenameData;
}
