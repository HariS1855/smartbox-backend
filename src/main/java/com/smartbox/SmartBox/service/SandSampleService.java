package com.smartbox.SmartBox.service;

import com.smartbox.SmartBox.model.SandSample;
import com.smartbox.SmartBox.repository.SandSampleRepository;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class SandSampleService {

    private final SandSampleRepository repository;

    public SandSampleService(SandSampleRepository repository) {
        this.repository = repository;
    }

    // Save sample
    public SandSample saveSample(SandSample sample) {
        return repository.save(sample);
    }

    // Get all samples
    public List<SandSample> getAllSamples() {
        return repository.findAll();
    }

    // Get single sample by ID
    public SandSample getSampleById(Long id) {
        Optional<SandSample> sample = repository.findById(id);
        return sample.orElse(null); // or throw exception
    }

    // -----------------------------
    // CSV report for single sample
    // -----------------------------
    public void generateCSVReport(HttpServletResponse response, SandSample sample) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"sample_report.csv\"");

        String header = "ID,BeachName,Town,State,Country,SampleID,CoinType,LocationOnBeach,Latitude,Longitude,ImageUrl,Timestamp,D10,D50,D90,MeanSize,Category\n";
        response.getWriter().write(header);

        String row = String.format("%d,%s,%s,%s,%s,%s,%s,%s,%f,%f,%s,%s,%s,%s,%s,%s,%s\n",
                sample.getId(),
                sample.getBeachName(),
                sample.getTown(),
                sample.getState(),
                sample.getCountry(),
                sample.getSampleId(),
                sample.getCoinType(),
                sample.getLocationOnBeach(),
                sample.getLatitude(),
                sample.getLongitude(),
                sample.getImageUrl(),
                sample.getTimestamp(),
                sample.getD10(),
                sample.getD50(),
                sample.getD90(),
                sample.getMeanSize(),
                sample.getCategory()
        );

        response.getWriter().write(row);
        response.getWriter().flush();
    }

    // -----------------------------
    // PDF report for single sample
    // -----------------------------
    public void generatePDFReport(HttpServletResponse response, SandSample sample) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"sample_report.pdf\"");

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Sand Sample Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            Font contentFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Paragraph content = new Paragraph(
                    String.format(
                            "ID: %d\nBeach: %s\nTown: %s\nState: %s\nCountry: %s\nSample ID: %s\nCoin Type: %s\nLocation on Beach: %s\nLatitude: %.6f\nLongitude: %.6f\nImage URL: %s\nTimestamp: %s\nD10: %s\nD50: %s\nD90: %s\nMean Size: %s\nCategory: %s",
                            sample.getId(),
                            sample.getBeachName(),
                            sample.getTown(),
                            sample.getState(),
                            sample.getCountry(),
                            sample.getSampleId(),
                            sample.getCoinType(),
                            sample.getLocationOnBeach(),
                            sample.getLatitude(),
                            sample.getLongitude(),
                            sample.getImageUrl(),
                            sample.getTimestamp(),
                            sample.getD10(),
                            sample.getD50(),
                            sample.getD90(),
                            sample.getMeanSize(),
                            sample.getCategory()
                    ),
                    contentFont
            );
            document.add(content);

        } catch (DocumentException e) {
            throw new IOException("Error generating PDF", e);
        } finally {
            document.close();
        }
    }

    // -----------------------------
    // GeoJSON report for single sample
    // -----------------------------
    public String generateGeoJSON(SandSample sample) {
        return "{ \"type\": \"FeatureCollection\", \"features\": [" +
                "{ \"type\": \"Feature\", \"geometry\": { \"type\": \"Point\", \"coordinates\": [" +
                sample.getLongitude() + "," + sample.getLatitude() + "] }, \"properties\": {" +
                "\"beachName\": \"" + sample.getBeachName() + "\"," +
                "\"town\": \"" + sample.getTown() + "\"," +
                "\"state\": \"" + sample.getState() + "\"," +
                "\"country\": \"" + sample.getCountry() + "\"," +
                "\"sampleId\": \"" + sample.getSampleId() + "\"," +
                "\"coinType\": \"" + sample.getCoinType() + "\"," +
                "\"locationOnBeach\": \"" + sample.getLocationOnBeach() + "\"," +
                "\"imageUrl\": \"" + sample.getImageUrl() + "\"," +
                "\"d10\": " + sample.getD10() + "," +
                "\"d50\": " + sample.getD50() + "," +
                "\"d90\": " + sample.getD90() + "," +
                "\"meanSize\": " + sample.getMeanSize() + "," +
                "\"category\": \"" + sample.getCategory() + "\"" +
                "} } ] }";
    }
}
