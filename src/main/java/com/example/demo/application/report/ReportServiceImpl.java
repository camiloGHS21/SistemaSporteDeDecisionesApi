package com.example.demo.application.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.domain.core.DatoIndicador;
import com.example.demo.domain.core.DatoIndicadorRepository;
import com.example.demo.infrastructure.report.ReportRequest;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.opencsv.CSVWriter;

@Service
public class ReportServiceImpl implements ReportService {

    private final DatoIndicadorRepository datoIndicadorRepository;

    @Autowired
    public ReportServiceImpl(DatoIndicadorRepository datoIndicadorRepository) {
        this.datoIndicadorRepository = datoIndicadorRepository;
    }

    @Override
    public byte[] generateReport(ReportRequest request) {
        if (request.getPaisPrincipal() != null && !request.getPaisPrincipal().isEmpty()) {
            return generateGapAnalysisPdf(request);
        }

        List<String> paises = request.getPaises() != null ? request.getPaises() : new ArrayList<>();
        List<String> indicadores = request.getIndicadores() != null ? request.getIndicadores() : new ArrayList<>();
        
        List<String> upperCasePaises = paises.stream().map(String::toUpperCase).collect(Collectors.toList());
        List<String> upperCaseIndicadores = indicadores.stream().map(String::toUpperCase).collect(Collectors.toList());
        List<DatoIndicador> datos = datoIndicadorRepository.findByPaisesAndIndicadores(upperCasePaises, upperCaseIndicadores);

        if ("CSV".equalsIgnoreCase(request.getReportType())) {
            return generateCsv(datos);
        } else {
            return generateComparativePdf(request, datos);
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

    private byte[] generateGapAnalysisPdf(ReportRequest request) {
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
        List<DatoIndicador> datos = datoIndicadorRepository.findByPaisesAndIndicadores(upperCaseCountries, upperCaseIndicadores);
        
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