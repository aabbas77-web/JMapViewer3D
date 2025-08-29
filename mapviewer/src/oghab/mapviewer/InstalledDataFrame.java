package oghab.mapviewer;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.cache.FileStore;
import gov.nasa.worldwind.data.*;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import org.w3c.dom.*;

import javax.swing.*;
import javax.xml.xpath.XPath;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import static oghab.mapviewer.InstallImageryAndElevations.addInstalledData;
import static oghab.mapviewer.InstallImageryAndElevations.createDataStore;
import static oghab.mapviewer.InstallImageryAndElevations.createDataStoreProducerFromFiles;
import static oghab.mapviewer.InstallImageryAndElevations.setFallbackParams;

public class InstalledDataFrame extends JFrame
{
    public static final String TOOLTIP_FULL_PYRAMID =
            "Installing a full pyramid takes longer and consumes more space on the user's hard drive, "
                    + "but has the best runtime performance, which is important for WorldWind Server";

    public static final String TOOLTIP_PARTIAL_PYRAMID =
            "Installing a partial pyramid takes less time and consumes less space on the user's hard drive"
                    + "but requires that the original data not be moved or deleted";

    protected FileStore fileStore;
    protected InstalledDataPanel dataConfigPanel;
    protected JFileChooser fileChooser;
    protected File lastUsedFolder = null;

    public InstalledDataFrame(FileStore fileStore, WorldWindow worldWindow) throws HeadlessException
    {
        ImageIcon icon = new ImageIcon(getClass().getResource("/res/mapviewer_icon.png"));  
        this.setIconImage(icon.getImage());  

        if (fileStore == null)
        {
                String msg = Logging.getMessage("nullValue.FileStoreIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
        }

        this.fileStore = fileStore;
        this.dataConfigPanel = new InstalledDataPanel("Installed Surface Data", worldWindow);
        this.fileChooser = new JFileChooser(this.getLastUsedFolder());
        this.fileChooser.setAcceptAllFileFilterUsed(true);
        this.fileChooser.addChoosableFileFilter(new InstallableDataFilter());
        this.fileChooser.setMultiSelectionEnabled(true);
        this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.layoutComponents();
        this.loadPreviouslyInstalledData();
    }

    protected File getLastUsedFolder()
    {
            if (WWUtil.isEmpty(this.lastUsedFolder))
                    this.setLastUsedFolder(new File(Configuration.getUserHomeDirectory()));

            return this.lastUsedFolder;
    }

    protected void setLastUsedFolder(File folder)
    {
            if (null != folder && folder.isDirectory())
                    this.lastUsedFolder = folder;
    }

    protected void loadPreviouslyInstalledData()
    {
            Thread t = new Thread(new Runnable()
            {
                    public void run()
                    {
                        loadInstalledDataFromFileStore(fileStore, dataConfigPanel);
                    }
            });
            t.start();
    }

    protected static void loadInstalledDataFromFileStore(FileStore fileStore, InstalledDataPanel panel)
    {
        for (File file : fileStore.getLocations())
        {
            if (!file.exists())
                continue;

            if (!fileStore.isInstallLocation(file.getPath()))
                continue;

            loadInstalledDataFromDirectory(file, panel);
        }
    }

    protected static void loadInstalledDataFromDirectory(File dir, InstalledDataPanel panel)
    {
        String[] names = WWIO.listDescendantFilenames(dir, new DataConfigurationFilter(), false);
        if (names == null || names.length == 0)
            return;

        for (String filename : names)
        {
            Document doc = null;

            try
            {
                File dataConfigFile = new File(dir, filename);
                doc = WWXML.openDocument(dataConfigFile);
//                System.out.println("loadInstalledDataFromDirectory: "+doc.toString()+" , "+dataConfigFile);
                doc = DataConfigurationUtils.convertToStandardDataConfigDocument(doc);
            }
            catch (WWRuntimeException e)
            {
                e.printStackTrace();
            }

            if (doc == null)
                continue;

            // This data configuration came from an existing file from disk, therefore we cannot guarantee that the
            // current version of WorldWind's data installer produced it. This data configuration file may have been
            // created by a previous version of WorldWind, or by another program. Set fallback values for any missing
            // parameters that WorldWind needs to construct a Layer or ElevationModel from this data configuration.
            AVList params = new AVListImpl();
            setFallbackParams(doc, filename, params);

            // Add the data configuraiton to the InstalledDataPanel.
            addInstalledData(doc, params, panel);
        }
    }

    protected void installFromFiles()
    {
            int retVal = this.fileChooser.showDialog(this, "Install");
            if (retVal != JFileChooser.APPROVE_OPTION)
                    return;

            this.setLastUsedFolder(this.fileChooser.getCurrentDirectory());

            final File[] files = this.fileChooser.getSelectedFiles();
            if (files == null || files.length == 0)
                    return;

            Thread thread = new Thread(new Runnable()
            {
                    public void run()
                    {
                            Document dataConfig = null;

                            try
                            {
                                    // Install the file into a form usable by WorldWind components.
                                    dataConfig = installDataFromFiles(InstalledDataFrame.this, files, fileStore);
                            }
                            catch (Exception e)
                            {
                                    final String message = e.getMessage();
                                    Logging.logger().log(java.util.logging.Level.FINEST, message, e);

                                    // Show a message dialog indicating that the installation failed, and why.
                                    SwingUtilities.invokeLater(new Runnable()
                                    {
                                            public void run()
                                            {
                                                    JOptionPane.showMessageDialog(InstalledDataFrame.this, message, "Installation Error",
                                                            JOptionPane.ERROR_MESSAGE);
                                            }
                                    });
                            }

                            if (dataConfig != null)
                            {
                                    AVList params = new AVListImpl();
                                    addInstalledData(dataConfig, params, dataConfigPanel);
                            }
                    }
            });
            thread.start();
    }

    protected static Document installDataFromFiles(Component parentComponent, File[] files, FileStore fileStore)
        throws Exception
    {
        // Create a DataStoreProducer which is capable of processing the file.
        final DataStoreProducer producer = createDataStoreProducerFromFiles(files);

        // Create a ProgressMonitor that will provide feedback on how
        final ProgressMonitor progressMonitor = new ProgressMonitor(parentComponent, "Importing ....", null, 0, 100);

        final AtomicInteger progress = new AtomicInteger(0);

        // Configure the ProgressMonitor to receive progress events from the DataStoreProducer. This stops sending
        // progress events when the user clicks the "Cancel" button, ensuring that the ProgressMonitor does not
        PropertyChangeListener progressListener = new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent evt)
            {
                if (progressMonitor.isCanceled())
                    return;

                if (evt.getPropertyName().equals(AVKey.PROGRESS))
                    progress.set((int) (100 * (Double) evt.getNewValue()));
            }
        };
        producer.addPropertyChangeListener(progressListener);
        progressMonitor.setProgress(0);

        // Configure a timer to check if the user has clicked the ProgressMonitor's "Cancel" button. If so, stop
        // production as soon as possible. This just stops the production from completing; it doesn't clean up any state
        // changes made during production,
        java.util.Timer progressTimer = new java.util.Timer();
        progressTimer.schedule(new TimerTask()
        {
            public void run()
            {
                progressMonitor.setProgress(progress.get());

                if (progressMonitor.isCanceled())
                {
                    producer.stopProduction();
                    this.cancel();
                }
            }
        }, progressMonitor.getMillisToDecideToPopup(), 100L);

        Document doc = null;
        try
        {
            // Install the file into the specified FileStore.
            doc = createDataStore(files, fileStore, producer);

            // The user clicked the ProgressMonitor's "Cancel" button. Revert any change made during production, and
            // discard the returned DataConfiguration reference.
            if (progressMonitor.isCanceled())
            {
                doc = null;
                producer.removeProductionState();
            }
        }
        finally
        {
            // Remove the progress event listener from the DataStoreProducer. stop the progress timer, and signify to the
            // ProgressMonitor that we're done.
            producer.removePropertyChangeListener(progressListener);
            producer.removeAllDataSources();
            progressMonitor.close();
            progressTimer.cancel();
        }

        return doc;
    }

    protected void layoutComponents()
    {
            this.setTitle("Installed Data");
            this.getContentPane().setLayout(new BorderLayout(0, 0)); // hgap, vgap
            this.getContentPane().add(this.dataConfigPanel, BorderLayout.CENTER);

            JButton installButton = new JButton("Install...");
            installButton.addActionListener(new ActionListener()
            {
                    public void actionPerformed(ActionEvent e)
                    {
                            installFromFiles();
                    }
            });

            JCheckBox fullPyramidCheckBox = new JCheckBox("Create a full pyramid", true);
            // set default option "Full pyramid"
            Configuration.setValue(AVKey.PRODUCER_ENABLE_FULL_PYRAMID, true);
            Configuration.removeKey(AVKey.TILED_RASTER_PRODUCER_LIMIT_MAX_LEVEL);
            fullPyramidCheckBox.setToolTipText(TOOLTIP_FULL_PYRAMID);

            fullPyramidCheckBox.addActionListener(new ActionListener()
            {
                    public void actionPerformed(ActionEvent e)
                    {
                            Object source = e.getSource();
                            if (source instanceof JCheckBox)
                            {
                                    JCheckBox checkBox = (JCheckBox) source;
                                    String tooltipText;

                                    if (checkBox.isSelected())
                                    {
                                            Configuration.setValue(AVKey.PRODUCER_ENABLE_FULL_PYRAMID, true);
                                            Configuration.removeKey(AVKey.TILED_RASTER_PRODUCER_LIMIT_MAX_LEVEL);
                                            tooltipText = TOOLTIP_FULL_PYRAMID;
                                    }
                                    else
                                    {
                                            Configuration.removeKey(AVKey.PRODUCER_ENABLE_FULL_PYRAMID);
                                            // Set partial pyramid level:
                                            // "0" - level zero only; "1" levels 0 and 1; "2" levels 0,1,2; etc
                                            // "100%" - full pyramid, "50%" half pyramid, "25%" quarter of pyramid, etc
                                            // "Auto" - whatever default is set in a TileProducer (50%)
                                            Configuration.setValue(AVKey.TILED_RASTER_PRODUCER_LIMIT_MAX_LEVEL, "50%");
                                            tooltipText = TOOLTIP_PARTIAL_PYRAMID;
                                    }
                                    checkBox.setToolTipText(tooltipText);
                            }
                    }
            });

            Box box = Box.createHorizontalBox();
            box.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // top, left, bottom, right
            box.add(installButton);
            box.add(fullPyramidCheckBox);
            this.getContentPane().add(box, BorderLayout.SOUTH);

            this.setPreferredSize(new Dimension(400, 500));
            this.validate();
            this.pack();
    }

    protected static class InstallableDataFilter extends javax.swing.filechooser.FileFilter
    {
        public InstallableDataFilter()
        {
        }

        public boolean accept(File file)
        {
            if (file == null || file.isDirectory())
                return true;

            if (DataInstallUtil.isDataRaster(file, null))
                return true;
            else if (DataInstallUtil.isWWDotNetLayerSet(file))
                return true;

            return false;
        }

        public String getDescription()
        {
            return "Supported Images/Elevations";
        }
    }
}
