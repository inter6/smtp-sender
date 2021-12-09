package com.inter6.mail.model.data;

import com.inter6.mail.model.component.DateData;
import com.inter6.mail.model.component.EncodingTextData;
import com.inter6.mail.model.data.edit.EditAddressData;
import com.inter6.mail.model.data.edit.EditHeaderData;
import com.inter6.mail.model.data.edit.EditMessageData;
import lombok.Data;

@Data
public class EditSourceData {
    private EncodingTextData subjectData;
    private DateData dateData;
    private EditAddressData editAddressData;
    private EditHeaderData editHeaderData;
    private EditMessageData editMessageData;
}
