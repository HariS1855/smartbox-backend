package com.smartbox.SmartBox.controller;
import com.smartbox.SmartBox.repository.SandSampleRepository;


import com.smartbox.SmartBox.model.SandSample;
import com.smartbox.SmartBox.service.SandSampleService;
import com.smartbox.SmartBox.service.FlaskClientService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/samples")
public class SandSampleController {

    private final SandSampleService service;
    private final SandSampleRepository repository;
    private final FlaskClientService flaskClientService;

    public SandSampleController(
            SandSampleService service,
            SandSampleRepository repository,
            FlaskClientService flaskClientService
    ) {
        this.service = service;
        this.repository = repository;
        this.flaskClientService = flaskClientService;
    }

    // -----------------------------
    // Analyze Sample
    // -----------------------------
    @PostMapping("/analyze")
    public SandSample analyzeSample(@RequestBody SandSample sample) {
        SandSample saved = repository.save(sample);

        // Call Flask API
        Map<String, Object> analysis = flaskClientService.analyzeImage(saved.getImageUrl());

        if (analysis != null) {
            saved.setD10((Double) analysis.get("d10"));
            saved.setD50((Double) analysis.get("d50"));
            saved.setD90((Double) analysis.get("d90"));
            saved.setMeanSize((Double) analysis.get("meanSize")); // 🔥 fix: use same key as Flask
            saved.setCategory((String) analysis.get("category"));
            repository.save(saved);
        }

        return saved;
    }

    // -----------------------------
    // Upload Sample
    // -----------------------------
    @PostMapping("/upload")
    public SandSample uploadSample(
            @RequestParam("image") MultipartFile image,
            @RequestParam String beachName,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam String coinType,
            @RequestParam String locationOnBeach,
            @RequestParam(required = false) String town,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String sampleId
    ) throws IOException {

        // -----------------------------
        // 1. Save image locally
        // -----------------------------
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path uploadPath = Paths.get("uploads");
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, image.getBytes());

        // For frontend display
        String imageUrl = "uploads/" + fileName;

        // -----------------------------
        // 2. Call Flask API (absolute path)
        // -----------------------------
        Map<String, Object> analysis = flaskClientService.analyzeImage(filePath.toAbsolutePath().toString());

        // -----------------------------
        // 3. Build SandSample entity
        // -----------------------------
        SandSample sample = new SandSample();
        sample.setImageUrl(imageUrl);
        sample.setLatitude(latitude);
        sample.setLongitude(longitude);
        sample.setBeachName(beachName);
        sample.setTown(town);
        sample.setState(state);
        sample.setCountry(country);
        sample.setSampleId(sampleId);
        sample.setCoinType(coinType);
        sample.setLocationOnBeach(locationOnBeach);

        // -----------------------------
        // 4. Map Flask response
        // -----------------------------
        if (analysis != null) {
            Object predictedClassObj = analysis.get("predicted_class");
            if (predictedClassObj != null) {
                int predictedClass = ((Number) predictedClassObj).intValue();

                String[] categories = {"Coarse Sand", "Medium Sand", "Fine Sand", "Very Fine Sand"};
                sample.setCategory(categories[predictedClass]);

                // optional: assign default grain sizes based on predicted class
                switch (predictedClass) {
                    case 0 -> { sample.setD10(0.5); sample.setD50(1.0); sample.setD90(2.0); sample.setMeanSize(1.0); }
                    case 1 -> { sample.setD10(0.25); sample.setD50(0.5); sample.setD90(1.0); sample.setMeanSize(0.5); }
                    case 2 -> { sample.setD10(0.125); sample.setD50(0.25); sample.setD90(0.5); sample.setMeanSize(0.25); }
                    case 3 -> { sample.setD10(0.0625); sample.setD50(0.125); sample.setD90(0.25); sample.setMeanSize(0.125); }
                }
            }
        }

        // -----------------------------
        // 5. Save to DB and return
        // -----------------------------
        return service.saveSample(sample);
    }




    // -----------------------------
    // Get all samples
    // -----------------------------
    @GetMapping
    public List<SandSample> getAllSamples() {
        return service.getAllSamples();
    }

    // -----------------------------
    // Reports
    // -----------------------------
    @GetMapping("/reports/csv/{id}")
    public void downloadCSVForSample(@PathVariable Long id, HttpServletResponse response) throws IOException {
        SandSample sample = service.getSampleById(id); // fetch single sample
        service.generateCSVReport(response, sample);    // generate CSV only for this sample
    }


    @GetMapping("/reports/pdf/{id}")
    public void downloadPDFFOrSample(@PathVariable Long id, HttpServletResponse response) throws IOException {
        SandSample sample = service.getSampleById(id);
        service.generatePDFReport(response, sample);
    }

    @GetMapping("/reports/geojson/{id}")
    public Object downloadGeoJSONForSample(@PathVariable Long id) {
        SandSample sample = service.getSampleById(id);
        return service.generateGeoJSON(sample);
    }

}
