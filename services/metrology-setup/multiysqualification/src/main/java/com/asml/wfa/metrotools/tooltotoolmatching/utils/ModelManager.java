package com.asml.wfa.metrotools.tooltotoolmatching.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asml.wfa.adel.fingerprintmodel.ADELfingerprintModel;
import com.asml.wfa.adel.fingerprintmodel.FingerprintModelMapper;
import com.asml.wfa.guicommons.model.ModelContainer;
import com.asml.wfa.metrotools.tooltotoolmatching.gui.state.ApplicationState;
import com.asml.wfa.modelfunctions.mathmodels.Model;
import com.asml.wfa.modelfunctions.mathmodels.ParameterModel;
import com.asml.wfa.ovlo.server.businesslogic.domain.flowcontext.SelectedModels;
import com.asml.wfa.xml.adel.jaxb.XMLException;
import com.google.common.base.Strings;

/**
 * TODO: refactor this to services maven module.
 * 
 * Manager that manages the models in the application state and in models directory on the file system. When the model directory structure is not
 * available on the specified location, the application will try to create the model directories and the initial asml models.
 * 
 * @author Roel Coset
 * @version 2015-03-10, RCPL, Initial version.
 */
@ApplicationScoped
public class ModelManager {
    private static final Logger LOG = LoggerFactory.getLogger(ModelManager.class);

    private static final String SCHEMA_LOCATION = "http://www.asml.com/XMLSchema/MT/Generic/ADELfingerprintModel/v1.1 ADELfingerprintModel.xsd";
    private static final String NAMESPACE_MAPPING_SPECIFICATION = "/xmlmapping/ADELfingerprintModel/v1.1/mapping.xml";
    private static final JAXBContext CTX = createJaxbContext();

    @SuppressWarnings("cdi-ambiguous-dependency")
    @Inject
    private FingerprintModelMapper fingerprintModelMapper;

    private Path customModelsPath;

    private Path asmlModelsPath;

    private ApplicationState applicationState;

    /**
     * Create the JAXBContext to be used to marshall the ADELfingerprintModel documents
     * 
     * @return The {@link JAXBContext}.
     */
    private static JAXBContext createJaxbContext() {
        try {
            final Class<?>[] classes = { ADELfingerprintModel.class };

            final InputStream stream = ModelManager.class.getResourceAsStream(NAMESPACE_MAPPING_SPECIFICATION);
            final Map<String, Source> metadataSourceMap = new HashMap<>();
            metadataSourceMap.put(ADELfingerprintModel.class.getPackage().getName(), new StreamSource(stream));
            final Map<String, Object> properties = new HashMap<>();
            properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);

            final JAXBContext jaxbContext = JAXBContext.newInstance(classes, properties);
            return jaxbContext;
        } catch (final JAXBException e) {
            throw new IllegalStateException("Could not create JAXBContext.", e);
        }
    }

    /**
     * Initialize the model manager. This method initializes the model paths and loads the initial models in the application state;.
     * 
     * @param modelDirectory
     *            The model directory.
     * @param applicationState
     *            The application state.
     */
    public void initialize(final String modelDirectory, final ApplicationState applicationState) {
        this.applicationState = applicationState;

        try {
            final Path modelDirectoryPath;
            if (Strings.isNullOrEmpty(modelDirectory)) {
                modelDirectoryPath = createTempDir();
            } else {
                modelDirectoryPath = Paths.get(modelDirectory);
                if (Files.notExists(modelDirectoryPath)) {
                    Files.createDirectory(modelDirectoryPath);
                }
            }
            LOG.info("Model Directory: " + modelDirectoryPath.toString());

            asmlModelsPath = createSubDirectory(modelDirectoryPath, "asml");

            customModelsPath = createSubDirectory(modelDirectoryPath, "custom");

        } catch (final IOException e) {
            throw new IllegalStateException("Could not load models.");
        }
    }

    /**
     * Create a sub directory in the path.
     * 
     * @param modelDirectoryPath
     *            The models directory path.
     * @param subdir
     *            The sub directory to create.
     * @return The {@link Path} of the sub directory.
     * @throws IOException
     *             An unexpected error occurred while creating the sub directory.
     */
    private Path createSubDirectory(final Path modelDirectoryPath, final String subdir) throws IOException {
        final Path subPath = modelDirectoryPath.resolve(subdir);
        if (Files.notExists(subPath)) {
            Files.createDirectory(subPath);
        }
        return subPath;
    }

    /**
     * Create a temporary directory to store the models in.
     * 
     * @return The path of the temporary directory.
     * @throws IOException
     *             An unexpected error occurred while creating a temporary directory.
     */
    private Path createTempDir() throws IOException {
        final Path tmpDir = Files.createTempDirectory("ModelAdvisor");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    Files.walkFileTree(tmpDir, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
                            Files.deleteIfExists(dir);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                            Files.deleteIfExists(file);
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (final IOException e) {
                    System.err.println("Could not remove temporary models directory.");
                }
            }
        });
        return tmpDir;
    }

    /**
     * List all ASML models from the file system. When there are no models available, the {@link ModelManager#populateModelsDirWithDefaultModels}
     * method will be called.
     * 
     * @return The collection of asml models.
     */

    /**
     * Populate the models directory with the default ASML models.
     */
    private void populateModelsDirWithDefaultModels() {
        final Set<String> defaultModelFiles = new Reflections("models.asml", new ResourcesScanner()).getResources(Pattern.compile(".*\\.xml"));
        try {
            for (final String modelFileName : defaultModelFiles) {
                final InputStream stream = this.getClass().getResourceAsStream("/" + modelFileName);
                final String fileName = modelFileName.replaceAll("models/asml/(.*)", "$1");
                final Path target = asmlModelsPath.resolve(fileName);
                Files.copy(stream, target);
            }
        } catch (final IOException e) {
            LOG.warn("Could not populate asml model directory with default models");
        }

        LOG.info("Populated the asml model directory with default models.");
    }

    /**
     * Retrieve the number of parameters present in an ADELfingerprintModel.
     * 
     * @param model
     *            The {@link ADELfingerprintModel}.
     * @return The number of parameters.
     */

}
