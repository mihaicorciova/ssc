package com.asml.lis.client.service.metrologysetup.util;

import java.io.InputStream;

import javax.inject.Inject;

import com.asml.wfa.commons.domainmodel.DomainModelFactory;
import com.asml.wfa.xml.adel.adelmetrology.AdelMetrology;
import com.asml.wfa.xml.adel.adelmetrology.reader.ADELmetrologyProducer;
import com.asml.wfa.xml.adel.jaxb.JaxbHelper;
import com.asml.wfa.xml.adel.jaxb.XMLException;
import com.asml.wfa.xml.adel.reader.ADELmetrologyReader;

public class AdelMetroDocumentReader {

    private static final DomainModelFactory FACTORY = DomainModelFactory.eINSTANCE;

    @Inject
    private ADELmetrologyReader metroReader;

    public static AdelMetrology loadAdelMetrology(final InputStream stream) throws XMLException {
        final AdelMetrology adelmetrology = JaxbHelper.parse(AdelMetrology.class, ADELmetrologyProducer.CTX, JaxbHelper.EMPTY_NAMESPACE, stream);
        return adelmetrology;
    }

}
