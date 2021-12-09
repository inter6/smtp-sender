package com.inter6.mail.model.data;

import com.inter6.mail.model.component.DateData;
import com.inter6.mail.model.component.SendDelayData;
import lombok.Data;

import java.util.Collection;

@Data
public class EmlSourceData {
    private Collection<String> files;
    private boolean isRecursive;
    private DateData replaceDateData;
    private SendDelayData sendDelayData;
}
