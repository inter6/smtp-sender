package com.inter6.mail.model.data.edit;

import com.inter6.mail.model.component.AddressData;
import lombok.Data;

import java.util.Collection;

@Data
public class EditAddressData {
    private Collection<AddressData> addressDatas;
}
