package com.inter6.mail.gui.component.content;

import com.inter6.mail.model.ContentType;
import com.inter6.mail.model.component.content.MultiPartData;
import com.inter6.mail.model.component.content.PartData;
import org.apache.commons.collections4.CollectionUtils;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class MultiPartPanel extends ContentPartPanel {
    private static final long serialVersionUID = -4555796509776825034L;

    protected MultiPartPanel(String tabName, ContentType contentType, Integer nested) {
        super(tabName, contentType, nested);
    }

    @Override
    protected void initLayout() {
        this.wrapPanel.setLayout(new BorderLayout());
        this.wrapPanel.add(new JLabel("Content-Type: " + this.contentType), BorderLayout.NORTH);
    }

    @Override
    public Object buildContentPart() throws Throwable {
        MimeMultipart mp = new MimeMultipart(this.contentType.getSubType());

        List<ContentPartPanel> childPanels = this.getUnwrapChildPanels();
        if (CollectionUtils.isEmpty(childPanels)) {
            return mp;
        }
        for (ContentPartPanel childPanel : childPanels) {
            Object childPart = childPanel.buildContentPart();
            if (childPart instanceof Multipart) {
                MimeBodyPart wrapPart = new MimeBodyPart();
                wrapPart.setContent((Multipart) childPart);
                mp.addBodyPart(wrapPart);
            } else if (childPart instanceof BodyPart) {
                mp.addBodyPart((BodyPart) childPart);
            } else {
                throw new MessagingException("unknown part type ! - PART:" + childPart);
            }
        }
        return mp;
    }

    @Override
    protected PartData getPartDataFromComponents() {
        return new MultiPartData();
    }

    @Override
    protected void setComponentsFromPartData(PartData partData) {
        // multipart has not data
    }

    @Override
    protected Vector<ContentType> getAvailableChildTypes(List<ContentPartPanel> addedChildPanels) {
        Vector<ContentType> childTypes = new Vector<>();
        childTypes.add(ContentType.MULTIPART_MIXED);
        childTypes.add(ContentType.MULTIPART_ALTERNATIVE);
        childTypes.add(ContentType.MULTIPART_RELATED);
        childTypes.add(ContentType.TEXT_PLAIN);
        childTypes.add(ContentType.TEXT_HTML);
        childTypes.add(ContentType.ATTACHMENT);
        return childTypes;
    }
}
