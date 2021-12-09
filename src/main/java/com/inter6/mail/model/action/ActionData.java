package com.inter6.mail.model.action;

import lombok.Data;

@Data
public class ActionData {
    private boolean isUseMultiThread;
    private int maxThreadCount;
}
