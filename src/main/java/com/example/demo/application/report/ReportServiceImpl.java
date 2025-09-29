package com.example.demo.application.report;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.domain.file.FileData;
import com.example.demo.domain.file.FileDataRepository;
import com.example.demo.infrastructure.report.ReportRequest;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

@Service
public class ReportServiceImpl implements ReportService {

    private final FileDataRepository fileDataRepository;

    @Autowired
    public ReportServiceImpl(FileDataRepository fileDataRepository) {
        this.fileDataRepository = fileDataRepository;
    }

    @Override
    public byte[] generateReport(ReportRequest request) {
        List<FileData> files = fileDataRepository.findAllById(request.getFileIds());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph("Reporte: " + request.getReportName()));
        doc.add(new Paragraph("Tipo de Reporte: " + request.getReportType()));
        doc.add(new Paragraph(""));

        for (FileData file : files) {
            doc.add(new Paragraph("Archivo: " + file.getFileName()));
            doc.add(new Paragraph("Tipo: " + file.getFileType()));
            doc.add(new Paragraph("Contenido:"));
            doc.add(new Paragraph(file.getData()));
            doc.add(new Paragraph(""));
        }

        doc.close();
        return baos.toByteArray();
    }
}
