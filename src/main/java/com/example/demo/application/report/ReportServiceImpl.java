package com.example.demo.application.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.domain.core.DatoIndicador;
import com.example.demo.domain.core.DatoIndicadorRepository;
import com.example.demo.domain.core.Pais;
import com.example.demo.domain.core.PaisRepository;
import com.example.demo.domain.report.Informe;
import com.example.demo.domain.report.InformePaisComparacion;
import com.example.demo.domain.report.InformePaisComparacionRepository;
import com.example.demo.domain.report.InformeRepository;
import com.example.demo.domain.user.User;
import com.example.demo.infrastructure.report.ReportRequest;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.opencsv.CSVWriter;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class ReportServiceImpl implements ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final DatoIndicadorRepository datoIndicadorRepository;
    private final InformeRepository informeRepository;
    private final PaisRepository paisRepository;
    private final InformePaisComparacionRepository informePaisComparacionRepository;
    private final EntityManager entityManager;

    @Autowired
    public ReportServiceImpl(DatoIndicadorRepository datoIndicadorRepository, InformeRepository informeRepository, PaisRepository paisRepository, InformePaisComparacionRepository informePaisComparacionRepository, EntityManager entityManager) {
        this.datoIndicadorRepository = datoIndicadorRepository;
        this.informeRepository = informeRepository;
        this.paisRepository = paisRepository;
        this.informePaisComparacionRepository = informePaisComparacionRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public byte[] generateReport(ReportRequest request, User user) {
        Informe informe = new Informe();
        informe.setNombre_informe(request.getReportName());
        informe.setUsuario(user);
        informe.setFecha_generacion(LocalDateTime.now());
        informe.setIndicadores(request.getIndicadores());

        List<String> allPaises = new ArrayList<>();
        if (request.getPaises() != null) {
            allPaises.addAll(request.getPaises());
        }

        if (request.getPaisPrincipal() != null && !request.getPaisPrincipal().isEmpty()) {
            Pais paisPrincipal = paisRepository.findByNombrePais(request.getPaisPrincipal()).orElseThrow(() -> new RuntimeException("Pais principal no encontrado"));
            informe.setPais_principal(paisPrincipal);
            if (!allPaises.contains(request.getPaisPrincipal())) {
                allPaises.add(request.getPaisPrincipal());
            }
        }

        logger.info("Before saving informe: {}", informe);
        Informe savedInforme = informeRepository.save(informe);
        entityManager.flush();
        logger.info("After saving informe: {}", savedInforme);

        Set<InformePaisComparacion> paisesComparacion = new HashSet<>();
        if (request.getPaises() != null) {
            for (String nombrePais : request.getPaises()) {
                Pais pais = paisRepository.findByNombrePais(nombrePais).orElseThrow(() -> new RuntimeException("Pais de comparación no encontrado: " + nombrePais));
                InformePaisComparacion ipc = new InformePaisComparacion();
                ipc.setInforme(savedInforme);
                ipc.setPais_comparacion(pais);
                paisesComparacion.add(ipc);
            }
        }
        informePaisComparacionRepository.saveAll(paisesComparacion);

        if (request.getPaisPrincipal() != null && !request.getPaisPrincipal().isEmpty()) {
            return generateGapAnalysisPdf(request, user);
        }

        List<String> indicadores = request.getIndicadores() != null ? request.getIndicadores() : new ArrayList<>();
        
        List<String> upperCasePaises = allPaises.stream().map(String::toUpperCase).collect(Collectors.toList());
        List<String> upperCaseIndicadores = indicadores.stream().map(String::toUpperCase).collect(Collectors.toList());
        List<DatoIndicador> datos = datoIndicadorRepository.findByUserAndPaisesAndIndicadores(user, upperCasePaises, upperCaseIndicadores);

        if ("CSV".equalsIgnoreCase(request.getReportType())) {
            return generateCsv(datos);
        } else {
            return generateComparativePdf(request, datos);
        }
    }

    @Override
    @Transactional
    public byte[] generateReportPdf(Long reportId) throws IOException {
        Informe informe = informeRepository.findByIdWithAllData(reportId).orElseThrow(() -> new RuntimeException("Report not found"));

        if (informe.getPais_principal() != null) {
            // Logic for Gap Analysis PDF (similar to generateGapAnalysisPdf)
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
            Document doc = new Document(pdfDoc);

            doc.add(new Paragraph("Análisis de Brechas: " + informe.getNombre_informe()));
            doc.add(new Paragraph("País Principal: " + informe.getPais_principal().getNombre_pais()));
            
            List<String> refPaises = new ArrayList<>();
            informe.getPaises_comparacion().forEach(pc -> refPaises.add(pc.getPais_comparacion().getNombre_pais()));
            doc.add(new Paragraph("Países de Referencia: " + String.join(", ", refPaises)));
            doc.add(new Paragraph(""));

            List<String> allCountries = new ArrayList<>(refPaises);
            allCountries.add(informe.getPais_principal().getNombre_pais());
            
            List<String> indicadores = informe.getIndicadores() != null ? informe.getIndicadores() : new ArrayList<>();

            List<String> upperCaseCountries = allCountries.stream().map(String::toUpperCase).collect(Collectors.toList());
            List<String> upperCaseIndicadores = indicadores.stream().map(String::toUpperCase).collect(Collectors.toList());
            List<DatoIndicador> datos = datoIndicadorRepository.findByUserAndPaisesAndIndicadores(informe.getUsuario(), upperCaseCountries, upperCaseIndicadores);
            
            Map<String, List<DatoIndicador>> groupedByIndicator = datos.stream()
                    .collect(Collectors.groupingBy(d -> d.getTipoIndicador().toUpperCase()));

            float[] columnWidths = {5, 3, 3, 3};
            Table table = new Table(columnWidths);
            table.addHeaderCell(new Cell().add(new Paragraph("Indicador")));
            table.addHeaderCell(new Cell().add(new Paragraph("Valor de " + informe.getPais_principal().getNombre_pais())));
            table.addHeaderCell(new Cell().add(new Paragraph("Promedio de Referencia")));
            table.addHeaderCell(new Cell().add(new Paragraph("Brecha")));

            for (String indicador : indicadores) {
                List<DatoIndicador> indicatorData = groupedByIndicator.get(indicador.toUpperCase());
                if (indicatorData == null) continue;

                DatoIndicador mainCountryData = indicatorData.stream()
                        .filter(d -> d.getPais().getNombre_pais().equalsIgnoreCase(informe.getPais_principal().getNombre_pais()))
                        .findFirst().orElse(null);

                List<DatoIndicador> refCountriesData = indicatorData.stream()
                        .filter(d -> !d.getPais().getNombre_pais().equalsIgnoreCase(informe.getPais_principal().getNombre_pais()))
                        .collect(Collectors.toList());

                if (mainCountryData == null) continue;

                double refAverage = refCountriesData.stream().mapToDouble(DatoIndicador::getValor).average().orElse(0.0);
                float gap = mainCountryData.getValor() - (float) refAverage;

                table.addCell(new Cell().add(new Paragraph(indicador)));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", mainCountryData.getValor()))));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", refAverage))));
                
                Cell gapCell = new Cell().add(new Paragraph(String.format("%+.2f", gap)));
                if (gap > 0) {
                    gapCell.setBackgroundColor(ColorConstants.GREEN);
                } else if (gap < 0) {
                    gapCell.setBackgroundColor(ColorConstants.PINK);
                }
                table.addCell(gapCell);
            }
            doc.add(table);

            doc.close();
            return baos.toByteArray();
        } else {
            // Logic for Comparative PDF (similar to generateComparativePdf)
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
            Document doc = new Document(pdfDoc);

            doc.add(new Paragraph("Reporte Comparativo: " + informe.getNombre_informe()));
            doc.add(new Paragraph(""));

            float[] columnWidths = {3, 5, 2, 2, 4};
            Table table = new Table(columnWidths);
            table.addHeaderCell(new Cell().add(new Paragraph("País")));
            table.addHeaderCell(new Cell().add(new Paragraph("Indicador")));
            table.addHeaderCell(new Cell().add(new Paragraph("Año")));
            table.addHeaderCell(new Cell().add(new Paragraph("Valor")));
            table.addHeaderCell(new Cell().add(new Paragraph("Fuente")));

            List<String> allPaises = new ArrayList<>();
            informe.getPaises_comparacion().forEach(pc -> allPaises.add(pc.getPais_comparacion().getNombre_pais()));
            List<String> upperCasePaises = allPaises.stream().map(String::toUpperCase).collect(Collectors.toList());
            List<String> upperCaseIndicadores = informe.getIndicadores().stream().map(String::toUpperCase).collect(Collectors.toList());
            List<DatoIndicador> datos = datoIndicadorRepository.findByUserAndPaisesAndIndicadores(informe.getUsuario(), upperCasePaises, upperCaseIndicadores);

            for (DatoIndicador dato : datos) {
                table.addCell(new Cell().add(new Paragraph(dato.getPais().getNombre_pais())));
                table.addCell(new Cell().add(new Paragraph(dato.getTipoIndicador())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(dato.getAnio()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(dato.getValor()))));
                table.addCell(new Cell().add(new Paragraph(dato.getFuente())));
            }
            doc.add(table);
            
            doc.add(new Paragraph(""));
            doc.add(new Paragraph("Gráfico:"));
            doc.add(new Paragraph("La generación de gráficos aún no está implementada."));

            doc.close();
            return baos.toByteArray();
        }
    }

    private byte[] generateCsv(List<DatoIndicador> datos) {
        try (StringWriter writer = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(writer)) {

            String[] header = {"País", "Indicador", "Año", "Valor", "Fuente"};
            csvWriter.writeNext(header);

            for (DatoIndicador dato : datos) {
                String[] row = {
                        dato.getPais().getNombre_pais(),
                        dato.getTipoIndicador(),
                        String.valueOf(dato.getAnio()),
                        String.valueOf(dato.getValor()),
                        dato.getFuente()
                };
                csvWriter.writeNext(row);
            }
            return writer.toString().getBytes("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private byte[] generateComparativePdf(ReportRequest request, List<DatoIndicador> datos) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph("Reporte Comparativo: " + request.getReportName()));
        doc.add(new Paragraph(""));

        float[] columnWidths = {3, 5, 2, 2, 4};
        Table table = new Table(columnWidths);
        table.addHeaderCell(new Cell().add(new Paragraph("País")));
        table.addHeaderCell(new Cell().add(new Paragraph("Indicador")));
        table.addHeaderCell(new Cell().add(new Paragraph("Año")));
        table.addHeaderCell(new Cell().add(new Paragraph("Valor")));
        table.addHeaderCell(new Cell().add(new Paragraph("Fuente")));

        for (DatoIndicador dato : datos) {
            table.addCell(new Cell().add(new Paragraph(dato.getPais().getNombre_pais())));
            table.addCell(new Cell().add(new Paragraph(dato.getTipoIndicador())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(dato.getAnio()))));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(dato.getValor()))));
            table.addCell(new Cell().add(new Paragraph(dato.getFuente())));
        }
        doc.add(table);
        
        doc.add(new Paragraph(""));
        doc.add(new Paragraph("Gráfico:"));
        doc.add(new Paragraph("La generación de gráficos aún no está implementada."));

        doc.close();
        return baos.toByteArray();
    }

    private byte[] generateGapAnalysisPdf(ReportRequest request, User user) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph("Análisis de Brechas: " + request.getReportName()));
        doc.add(new Paragraph("País Principal: " + request.getPaisPrincipal()));
        
        List<String> refPaises = request.getPaises() != null ? request.getPaises() : new ArrayList<>();
        doc.add(new Paragraph("Países de Referencia: " + String.join(", ", refPaises)));
        doc.add(new Paragraph(""));

        List<String> allCountries = new ArrayList<>(refPaises);
        allCountries.add(request.getPaisPrincipal());
        
        List<String> indicadores = request.getIndicadores() != null ? request.getIndicadores() : new ArrayList<>();

        List<String> upperCaseCountries = allCountries.stream().map(String::toUpperCase).collect(Collectors.toList());
        List<String> upperCaseIndicadores = indicadores.stream().map(String::toUpperCase).collect(Collectors.toList());
        List<DatoIndicador> datos = datoIndicadorRepository.findByUserAndPaisesAndIndicadores(user, upperCaseCountries, upperCaseIndicadores);
        
        Map<String, List<DatoIndicador>> groupedByIndicator = datos.stream()
                .collect(Collectors.groupingBy(d -> d.getTipoIndicador().toUpperCase()));

        float[] columnWidths = {5, 3, 3, 3};
        Table table = new Table(columnWidths);
        table.addHeaderCell(new Cell().add(new Paragraph("Indicador")));
        table.addHeaderCell(new Cell().add(new Paragraph("Valor de " + request.getPaisPrincipal())));
        table.addHeaderCell(new Cell().add(new Paragraph("Promedio de Referencia")));
        table.addHeaderCell(new Cell().add(new Paragraph("Brecha")));

        for (String indicador : indicadores) {
            List<DatoIndicador> indicatorData = groupedByIndicator.get(indicador.toUpperCase());
            if (indicatorData == null) continue;

            DatoIndicador mainCountryData = indicatorData.stream()
                    .filter(d -> d.getPais().getNombre_pais().equalsIgnoreCase(request.getPaisPrincipal()))
                    .findFirst().orElse(null);

            List<DatoIndicador> refCountriesData = indicatorData.stream()
                    .filter(d -> !d.getPais().getNombre_pais().equalsIgnoreCase(request.getPaisPrincipal()))
                    .collect(Collectors.toList());

            if (mainCountryData == null) continue;

            double refAverage = refCountriesData.stream().mapToDouble(DatoIndicador::getValor).average().orElse(0.0);
            float gap = mainCountryData.getValor() - (float) refAverage;

            table.addCell(new Cell().add(new Paragraph(indicador)));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", mainCountryData.getValor()))));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", refAverage))));
            
            Cell gapCell = new Cell().add(new Paragraph(String.format("%+.2f", gap)));
            if (gap > 0) {
                gapCell.setBackgroundColor(ColorConstants.GREEN);
            } else if (gap < 0) {
                gapCell.setBackgroundColor(ColorConstants.PINK);
            }
            table.addCell(gapCell);
        }
        doc.add(table);

        doc.close();
        return baos.toByteArray();
    }
}