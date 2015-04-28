package com.asml.wfa.metrotools.tooltotoolmatching.gui.adel;

import java.io.InputStream;

import javax.inject.Inject;

import com.asml.wfa.commons.adel.exception.ADELReadException;
import com.asml.wfa.commons.adel.exception.ADELReadValidationException;
import com.asml.wfa.commons.domainmodel.DomainModelFactory;
import com.asml.wfa.commons.domainmodel.Lot;
import com.asml.wfa.xml.adel.adelmetrology.AdelMetrology;
import com.asml.wfa.xml.adel.adelmetrology.reader.ADELmetrologyProducer;
import com.asml.wfa.xml.adel.jaxb.JaxbHelper;
import com.asml.wfa.xml.adel.jaxb.XMLException;
import com.asml.wfa.xml.adel.reader.ADELmetrologyReader;
import com.asml.wfa.xml.adel.reader.Report;

public class AdelMetroDocumentReader {

    private static final DomainModelFactory FACTORY = DomainModelFactory.eINSTANCE;

    @Inject
    private ADELmetrologyReader metroReader;

    public static AdelMetrology loadAdelMetrology(final InputStream stream) throws XMLException {
        final AdelMetrology adelmetrology = JaxbHelper.parse(AdelMetrology.class, ADELmetrologyProducer.CTX, JaxbHelper.EMPTY_NAMESPACE, stream);
        return adelmetrology;
    }

    public Lot creatDomainModel(final AdelMetrology adelMetro) {
        Lot wfaDomainModel = null;
        try {
            final Report<Lot> reportLer = metroReader.read(adelMetro, FACTORY.createLot());
            wfaDomainModel = reportLer.getResultObject();
        } catch (final ADELReadValidationException e) {
            e.printStackTrace();
        } catch (final ADELReadException e) {
            e.printStackTrace();
        }

        return wfaDomainModel;
    }

}
