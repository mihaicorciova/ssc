/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.controller.content.sumary;

import com.google.common.io.Files;
import com.ro.ssc.app.client.exporter.Column;
import com.ro.ssc.app.client.exporter.PDFTableGenerator;
import com.ro.ssc.app.client.exporter.TableBuilder;
import com.ro.ssc.app.client.exporter.XlsxTableExporter;
import com.ro.ssc.app.client.model.commons.GenericModel;
import com.ro.ssc.app.client.service.impl.DataProviderImpl;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import javax.mail.PasswordAuthentication;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.ro.ssc.app.client.ui.commons.UiCommonTools;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DauBufu
 */
public class SumaryController implements Initializable {

    private DateTime iniDate;
    private DateTime endDate;
    private final java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
    private final org.joda.time.format.DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
    private static final String ALL = "all";
    private static final Logger log = LoggerFactory.getLogger(SumaryController.class);
    private static final List<String> reportName = Arrays.asList("Raport Cumulativ", "Raport Periodic", "Raport Lunar");
    private static final List<String> overallReport = Arrays.asList("Nume", "Departament", "Timp Lucru", "Timp Pauza", "Timp Total", "Ore noapte", "Ore zi", "Ore suplimentare", "Ore lipsa", "Total ore suplimentare/lipsa", "Absente", "Intarzieri", "Plecari timpurii");
    private static final List<String> dailyReport = Arrays.asList("Nume", "Intrare", "Iesire", "Timp Lucru", "Timp Pauza", "Timp Total", "Departament", "Data", "Ore suplimentare", "Absente","Intarzieri", "Plecari timpurii","Ore noapte", "Ore zi"  );
    private static final UiCommonTools fxCommonTools = UiCommonTools.getInstance();

    @FXML
    private DatePicker iniDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button selectButton;
    @FXML
    private Button exportcButton;
    @FXML
    private ListView filesListView;
    @FXML
    private TableView sumaryTableView;
    @FXML
    private TableColumn<GenericModel, Object> dateTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> hourTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> nameTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> cardNoTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> departmentTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> eventTableColumn;
    private final DateTimeFormatter dtf2 = DateTimeFormat.forPattern("dd/MM");
    private static final List<String> fixedCols = Arrays.asList("cnp_salariat", "ContractNr", "den_salariat","den_formatie","tip_ore");

    /**
     * Initializes the controller class.
     *
     * @param url URL
     * @param rb resource bundle
     */
    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        log.info("Initializing Sumary controller");

        final FileChooser fileChooser = new FileChooser();

        selectButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        configureFileChooser(fileChooser);
                        List<File> files = fileChooser.showOpenMultipleDialog(selectButton.getContextMenu());
                        if (files != null) {
                            populateListView(files);

                            for (File file : files) {
                                if (file.getName().contains("mdb")) {

                                    DataProviderImpl.getInstance().saveMdbFile(file);
                                    DataProviderImpl.getInstance().getUserData().clear();
                                } else {
                                    DataProviderImpl.getInstance().importUserData(file);
                                }
                            }

                            if (!DataProviderImpl.getInstance().getUserData().isEmpty()) {
                                iniDate = DataProviderImpl.getInstance().getPossibleDateStart(ALL);
                                endDate = DataProviderImpl.getInstance().getPossibleDateEnd(ALL);

                                iniDatePicker.setOnAction(new EventHandler<ActionEvent>() {

                                    public void handle(final ActionEvent e) {
                                        iniDate = DateTime.parse(iniDatePicker.getValue().format(formatter), dtf);
                                    }
                                });

                                endDatePicker.setOnAction(new EventHandler<ActionEvent>() {

                                    public void handle(final ActionEvent e) {
                                        endDate = DateTime.parse(endDatePicker.getValue().format(formatter), dtf);
                                    }
                                });

                                if (iniDate != null) {
                                    iniDatePicker.setValue(LocalDate.parse(iniDate.toString(dtf), formatter));
                                }

                                if (endDate != null) {
                                    endDatePicker.setValue(LocalDate.parse(endDate.toString(dtf), formatter));
                                }
                                populateMyTable();
                            }
                        }
                    }
                });

    }

    private void populateListView(List<File> files) {
        List<String> ls = new ArrayList<>();
        files.stream().forEach((file) -> {
            ls.add(file.getName());
        });

        filesListView.setItems(FXCollections.observableArrayList(ls));

    }

    @FXML
    private void emailReports() {

        List<File> files = saveReportsLocally();
        sendEmail(files);
    }

    @FXML
    private void printReports() {
        List<File> files = saveReportsLocally();
        printPDF(files);
    }

    @FXML
    private void exportTableToXLS() {
        String[] ext = {".xlsx"};
        if(endDate.getMonthOfYear()!=iniDate.getMonthOfYear()){
            UiCommonTools.getInstance().showInfoDialogStatus("Atentionare", "Va rugam selectati date din aceeasi luna ", "");

        }else {

            File file = fxCommonTools.getFileByChooser(exportcButton.getContextMenu(), "XLSX files (*.xlsx)", Arrays.asList(ext));

            if (file == null) {
                return;
            }

            XlsxTableExporter pptExporter = new XlsxTableExporter();

            pptExporter.exportTableToXls(getGridBase2(ALL, 1), file, "Raport lunar ",
                    "", iniDate.toString(dtf), endDate.toString(dtf));
            fxCommonTools.showInfoDialogStatus("Raport exportat", "Status-ul exportului", "Raportul s- a exportat cu succes in XLSX.");
        }
    }
    private GridBase getGridBase2(String department, int p) {

        List<String> users = new ArrayList();

        users.addAll(DataProviderImpl.getInstance().getUsersDep(department, 2));

        List<DateTime> dates = getDatesForMonth();

        final GridBase grid = new GridBase(users.size(), 36);

        grid.getColumnHeaders().clear();
        grid.getRowHeaders().clear();



        for (int column = 0; column < grid.getColumnCount(); ++column) {

            if (column <5) {
                grid.getColumnHeaders().add(fixedCols.get(column));

            } else if (column <14) {
                grid.getColumnHeaders().add("ore_0"+(column-4));
            } else {
                grid.getColumnHeaders().add("ore_"+(column-4));
            }
        }

        final ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        for (int row = 0; row < grid.getRowCount(); ++row) {

            final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();

            for (int column = 0; column < grid.getColumnCount(); ++column) {

                if (p == 1) {
                    String user = users.get(row);
                    if (user.contains("$")) {
                        user = user.substring(0, user.length() - 2);
                    }
                    if (column == 2) {
                        list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1, users.get(row).split("#")[0]));
                    } else if (column == 3) {

                        department = DataProviderImpl.getInstance().getDepartmentFromUser(user);
                        list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1,
                                department));
                    } else if (column ==0) {
                        String s=user.split("#")[1];

                        list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1,
                                s));
                    } else if (column ==1) {
                        list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1,
                                DataProviderImpl.getInstance().getCtrFromUser(user)));
                    } else if (column == 4) {
                        String tip="RG";
                        if(users.get(row).contains("$1")){
                            tip="SUP";
                        }
                        if(users.get(row).contains("$2")){
                            tip="NPT";
                        }
                        list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1,
                                tip));

                    } else {
                        final int dd=column-4;
                        Optional<DateTime> date= dates.stream().filter(d->d.getDayOfMonth()==dd).findFirst();
                        int n=1;
                        if(users.get(row).contains("$1")){
                            n=0;
                        }
                        if(users.get(row).contains("$2")){
                            n=2;
                        }
                        final SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1,
                                date.isPresent()?  DataProviderImpl.getInstance().getCellData(user,date.get(),date.get(), 0,n, true):"");
                        list.add(cell);
                    }
                }
            }
            rows.add(list);
        }
        grid.setRows(rows);

        return grid;
    }
    public void printPDF(List<File> files) {
        PrinterJob printerJob = PrinterJob.getPrinterJob();

        PrintService printService = null;
        if (printerJob.printDialog()) {
            printService = printerJob.getPrintService();
        }
        DocFlavor docType = DocFlavor.INPUT_STREAM.AUTOSENSE;
        if (printService != null) {
            for (File file : files) {
                DocPrintJob printJob = printService.createPrintJob();
                final byte[] byteStream;
                try {
                    byteStream = Files.toByteArray(file);
                    Doc documentToBePrinted = new SimpleDoc(new ByteArrayInputStream(byteStream), docType, null);

                    printJob.print(documentToBePrinted, null);
                } catch (IOException | PrintException ex) {
                    java.util.logging.Logger.getLogger(SumaryController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }

    public void populateMyTable() {

        dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("one"));
        hourTableColumn.setCellValueFactory(new PropertyValueFactory<>("two"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("three"));
        cardNoTableColumn.setCellValueFactory(new PropertyValueFactory<>("four"));
        departmentTableColumn.setCellValueFactory(new PropertyValueFactory<>("five"));
        eventTableColumn.setCellValueFactory(new PropertyValueFactory<>("six"));

        dateTableColumn.setComparator(new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {

                DateTimeFormatter format = DateTimeFormat.forPattern("EEE dd-MMM-yyyy");
                DateTime d1 = DateTime.parse((String) o1, format);
                DateTime d2 = DateTime.parse((String) o2, format);
                return Long.compare(d1.getMillis(), d2.getMillis());

            }

        });
        dateTableColumn.setStyle("-fx-alignment:CENTER;");
        hourTableColumn.setStyle("-fx-alignment:CENTER;");
        nameTableColumn.setStyle("-fx-alignment:CENTER;");
        cardNoTableColumn.setStyle("-fx-alignment:CENTER;");
        departmentTableColumn.setStyle("-fx-alignment:CENTER;");
        eventTableColumn.setStyle("-fx-alignment:CENTER;");

        ObservableList data = FXCollections.observableArrayList(DataProviderImpl.getInstance().getUserData());

        sumaryTableView.getItems().setAll(data);
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        FileChooser.ExtensionFilter extFilter
                = new FileChooser.ExtensionFilter("Files (*.xls,*.xlsx,*.mdb)", "*.xlsx;*.xls;*.mdb");
        fileChooser.getExtensionFilters().add(extFilter);
    }

    private List<File> saveReportsLocally() {
        List<File> files = new ArrayList();
        for (String report : reportName) {

            if (report.equals("Raport Cumulativ")) {

                List<GenericModel> listAll = DataProviderImpl.getInstance().getOverallTableData(iniDate, endDate, null);
                System.out.println("Overall list" + listAll.size());

                listAll.stream().collect(Collectors.groupingBy(p -> p.getTwo())).entrySet().forEach(entry -> {
                    final List<GenericModel> list = entry.getValue();
                    System.out.println("Overdep list" + list.size());
                    final File file = new File(report + "_" + ((String) entry.getKey()).replace("\\", "_") + ".pdf");
                    PDFTableGenerator pdfTableGenerator = new PDFTableGenerator();
                    TableBuilder tableBuilder = new TableBuilder();
                    List<Column> columns = new ArrayList<>();
                    int colNo = overallReport.size();
                    for (int i = 0; i < colNo; i++) {
                        if (i == 0 || i == 1 || i == 9) {
                            columns.add(new Column(overallReport.get(i), 100));
                        } else {
                            columns.add(new Column(overallReport.get(i), 60));
                        }
                    }
                    tableBuilder.setColumns(columns);

                    String[][] content = new String[list.size()][overallReport.size()];

                    int rowNo = 0;
                    for (GenericModel tableData : list) {
                        content[rowNo][0] = (String) tableData.getOne();
                        content[rowNo][1] = (String) tableData.getTwo();
                        content[rowNo][2] = (String) tableData.getThree();
                        content[rowNo][3] = (String) tableData.getFour();
                        content[rowNo][4] = (String) tableData.getFive();
                        content[rowNo][5] = (String) tableData.getTwelve();
                        content[rowNo][6] = (String) tableData.getThirteen();
                        content[rowNo][7] = (String) tableData.getSix();
                        content[rowNo][8] = (String) tableData.getTen();
                        content[rowNo][9] = (String) tableData.getEleven();
                        content[rowNo][10] = (String) tableData.getSeven();
                        content[rowNo][11] = (String) tableData.getEight();
                        content[rowNo][12] = (String) tableData.getNine();
                        rowNo++;
                    }
                    tableBuilder.setTitle(report+" de la: "+iniDate+" pana la: "+endDate +" pentru departamenutul "+((String) entry.getKey()).replace("\\", "_") );
                    tableBuilder.setContent(content);
                    tableBuilder.setNumberOfRows(list.size());
                    tableBuilder.setHeight(600);
                    tableBuilder.setRowHeight(30);
                    tableBuilder.setFontSize(8);
                    tableBuilder.setPageSize(new PDRectangle(1080, 720));
                    tableBuilder.setCellMargin(2);
                    tableBuilder.setLandscape(false);
                    tableBuilder.setMargin(20);
                    tableBuilder.setTextFont(PDType1Font.TIMES_ROMAN);
                    try {

                        pdfTableGenerator.generatePDF(tableBuilder.build(), file);
                        files.add(file);
                    } catch (IOException | COSVisitorException e) {
                        System.out.println(e.getMessage());

                    }

                });
            } else if (report.equals("Raport Periodic")) {
                String aaa = null;
                List<GenericModel> listAll = new ArrayList<>();

                for (DateTime date = iniDate.withTimeAtStartOfDay(); date.isBefore(endDate.plusDays(1).withTimeAtStartOfDay()); date = date.plusDays(1)) {

                    listAll.addAll(DataProviderImpl.getInstance().getDaySpecificTableData(aaa, date));
                }

                listAll.stream().collect(Collectors.groupingBy(p -> p.getEight())).entrySet().forEach(entry -> {
                    final List<GenericModel> list = entry.getValue();
                    System.out.println("Overdep list" + list.size());
                    final File file = new File(report + "_" + ((String) entry.getKey()).replace("\\", "_") + ".pdf");
                    PDFTableGenerator pdfTableGenerator = new PDFTableGenerator();
                    TableBuilder tableBuilder = new TableBuilder();
                    List<Column> columns = new ArrayList<>();
                    int colNo = dailyReport.size();
                    for (int i = 0; i < colNo; i++) {
                        if (i == 0 || i == 6) {
                            columns.add(new Column(dailyReport.get(i), 100));
                        } else {
                            columns.add(new Column(dailyReport.get(i), 60));
                        }
                    }
                    tableBuilder.setColumns(columns);

                    String[][] content = new String[list.size()][dailyReport.size()];

                    int rowNo = 0;
                    for (GenericModel tableData : list) {
                        content[rowNo][0] = (String) tableData.getOne();
                        content[rowNo][1] = (String) tableData.getTwo();

                        content[rowNo][2] = (String) tableData.getFour();
                        content[rowNo][3] = (String) tableData.getFive();
                        content[rowNo][4] = (String) tableData.getSix();
                        content[rowNo][5] = (String) tableData.getSeven();
                        content[rowNo][6] = (String) tableData.getEight();
                        content[rowNo][7] = (String) tableData.getNine();
                        content[rowNo][8] = (String) tableData.getTen();
                        content[rowNo][9] = (String) tableData.getEleven();
                        content[rowNo][10] = (String) tableData.getTwelve();
                        content[rowNo][11] = (String) tableData.getThirteen();
                        content[rowNo][12] = (String) tableData.getFourteen();
                        content[rowNo][13] = (String) tableData.getFifteen();
                        rowNo++;
                    }
                     tableBuilder.setTitle(report+" de la: "+iniDate+" pana la: "+endDate +" pentru departamenutul "+((String) entry.getKey()).replace("\\", "_") );
                   
                    tableBuilder.setContent(content);
                    tableBuilder.setNumberOfRows(list.size());
                    tableBuilder.setHeight(600);
                    tableBuilder.setRowHeight(30);
                    tableBuilder.setFontSize(8);
                    tableBuilder.setPageSize(new PDRectangle(1080, 720));
                    tableBuilder.setCellMargin(2);
                    tableBuilder.setLandscape(false);
                    tableBuilder.setMargin(20);
                    tableBuilder.setTextFont(PDType1Font.TIMES_ROMAN);
                    try {

                        pdfTableGenerator.generatePDF(tableBuilder.build(), file);
                        files.add(file);
                    } catch (IOException | COSVisitorException e) {
                        System.out.println(e.getMessage());

                    }

                });
            } else {
                for (String dep : DataProviderImpl.getInstance().getDepartments()) {
                    final File file = new File(report + "_" + dep.replace("\\", "_") + ".pdf");
                    final List<String> users = DataProviderImpl.getInstance().getUsersDep(dep, 1);
                    PDFTableGenerator pdfTableGenerator = new PDFTableGenerator();
                    TableBuilder tableBuilder = new TableBuilder();
                    List<Column> columns = new ArrayList<>();
                    List<DateTime> dates = getDatesForMonth();

                    int colNo = dates.size() + 7;
                    for (int i = 0; i < colNo; i++) {

                        if (i == 0) {
                            columns.add(new Column("Nume", 70));
                        } else if (i == colNo - 6) {
                            columns.add(new Column("Timp lucrat", 40));
                        } else if (i == colNo - 5) {
                            columns.add(new Column("Timp pauza", 40));
                        } else if (i == colNo - 4) {
                            columns.add(new Column("Timp total", 40));
                        } else if (i == colNo - 3) {
                            columns.add(new Column("Timp suplimentar", 45));
                        } else if (i == colNo - 2) {
                            columns.add(new Column("Timp noapte", 40));
                        } else if (i == colNo - 1) {
                            columns.add(new Column("Timp zi", 40));
                        } else {
                            columns.add(new Column(dates.get(i - 1).toString(dtf2), 22));

                        }
                        
                    }
                    tableBuilder.setColumns(columns);

                    String[][] content = new String[users.size()][colNo];
                    
                    for (int row = 0; row < users.size(); ++row) {

                        for (int column = 0; column < colNo; ++column) {

                            if (column == 0) {
                                content[row][column] =users.get(row).split("#")[0];
                             
                    } else if (column == colNo - 1) {
                                content[row][column] =
                                        DataProviderImpl.getInstance().getCellData(users.get(row), iniDate, endDate, 1, 1, false);
                            } else if (column == colNo - 2) {
                               content[row][column] =
                                        DataProviderImpl.getInstance().getCellData(users.get(row), iniDate, endDate, 2, 1, false);
                            } else if (column == colNo - 3) {
                               content[row][column] =
                                        DataProviderImpl.getInstance().getCellData(users.get(row), iniDate, endDate, 3, 1, false);
                            } else if (column == colNo - 4) {
                                content[row][column] =
                                        DataProviderImpl.getInstance().getCellData(users.get(row), iniDate, endDate, 4, 1, false);

                            } else if (column == colNo - 5) {
                               content[row][column] =
                                        DataProviderImpl.getInstance().getCellData(users.get(row), iniDate, endDate, 5, 1, false);
                            } else if (column == colNo - 6) {
                               content[row][column] =
                                        DataProviderImpl.getInstance().getCellData(users.get(row), iniDate, endDate, 6, 1, false);
                            } else {
                                String user = users.get(row);
                                if (user.contains("$1")) {
                                    user = user.substring(0, user.length() - 2);
                                }
                              content[row][column] =
                                        DataProviderImpl.getInstance().getCellData(user, dates.get(column - 1), dates.get(column - 1), 0, users.get(row).contains("$1") ? 0 : 1, false);
                              
                            }
                        }
                    }
                     tableBuilder.setTitle(report+" de la: "+iniDate+" pana la: "+endDate +" pentru departamenutul "+dep );
                   
                     tableBuilder.setContent(content);
                    tableBuilder.setNumberOfRows(users.size());
                    tableBuilder.setHeight(600);
                    tableBuilder.setRowHeight(20);
                    tableBuilder.setFontSize(6);
                    tableBuilder.setPageSize(new PDRectangle(1080, 720));
                    tableBuilder.setCellMargin(2);
                    tableBuilder.setLandscape(false);
                    tableBuilder.setMargin(10);
                    tableBuilder.setTextFont(PDType1Font.TIMES_ROMAN);
                    try {

                        pdfTableGenerator.generatePDF(tableBuilder.build(), file);
                        files.add(file);
                    } catch (IOException | COSVisitorException e) {
                        System.out.println(e.getMessage());

                    }
                }
            }
        }
        return files;
    }

    private List<DateTime> getDatesForMonth() {
        final List<DateTime> result = new ArrayList<>();
        for (DateTime dd = iniDate.withTimeAtStartOfDay(); dd.isBefore(endDate.withTimeAtStartOfDay().plusDays(1)); dd = dd.plusDays(1)) {
            result.add(dd);
        }
        return result;

    }

    private void sendEmail(List<File> files) {
        for (String dep : DataProviderImpl.getInstance().getDepartments()) {
            // Recipient's email ID needs to be mentioned.
            String to = DataProviderImpl.getInstance().getEmailFromDep(dep);

            // Sender's email ID needs to be mentioned
            String from = "pontaj@mnir.ro";

            final String username = "pontaj@mnir.ro";//change accordingly
            final String password = "MNIR2017pontaj";//change accordingly

            // Assuming you are sending email through relay.jangosmtp.net
            String host = "mail.mnir.ro";

            Properties props = new Properties();
             props.put("mail.smtp.socketFactory.port", "465");
    props.put("mail.smtp.socketFactory.class",
            "javax.net.ssl.SSLSocketFactory");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.port", "465"); 
            props.put("mail.smtp.host", host);
           

            // Get the Session object.
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            try {
                // Create a default MimeMessage object.
                Message message = new MimeMessage(session);

                // Set From: header field of the header.
                message.setFrom(new InternetAddress(from));

                // Set To: header field of the header.
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(to));

                // Set Subject: header field
                message.setSubject("Rapoarte soft pontaj");

               

                // Now set the actual message
                message.setText("Rapoarte selectate in perioada: " + iniDate.toString(dtf) + " / " + endDate.toString(dtf));

                // Create a multipar message
                Multipart multipart = new MimeMultipart();

              
                files.stream().filter(f -> f.getName().contains(dep)).forEach(file -> {

                    try {
                        // Part two is attachment
                        BodyPart messageBodyPartFile = new MimeBodyPart();
                        String filename = file.getPath();
                        DataSource source = new FileDataSource(filename);
                        messageBodyPartFile.setDataHandler(new DataHandler(source));
                        messageBodyPartFile.setFileName(file.getName());
                        multipart.addBodyPart(messageBodyPartFile);
                    } catch (MessagingException ex) {
                        java.util.logging.Logger.getLogger(SumaryController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });

                // Send the complete message parts
                message.setContent(multipart);

                // Send message
                Transport.send(message);

                System.out.println("Sent message successfully....");

            } catch (MessagingException e) {
                java.util.logging.Logger.getLogger(SumaryController.class.getName()).log(Level.SEVERE, null, e);

            }
        }
    }
}
