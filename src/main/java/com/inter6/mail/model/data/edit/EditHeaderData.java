package com.inter6.mail.model.data.edit;

import com.inter6.mail.model.component.HeaderData;
import lombok.Data;

import java.util.Collection;

@Data
public class EditHeaderData {
    private Collection<HeaderData> headerDatas;
}
