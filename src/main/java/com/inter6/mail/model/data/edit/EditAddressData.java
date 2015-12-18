package com.inter6.mail.model.data.edit;

import java.util.Collection;

import lombok.Data;

import com.inter6.mail.model.component.AddressData;

@Data
public class EditAddressData {
	private Collection<AddressData> addressDatas;
}
