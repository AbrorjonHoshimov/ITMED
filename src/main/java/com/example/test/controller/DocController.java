package com.example.test.controller;

import antlr.preprocessor.Hierarchy;
import com.example.test.repository.DocementCSVRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import com.example.test.config.CsvConfig;
import com.example.test.entity.DocumentCSV;
import com.example.test.payload.ApiResponse;
import com.example.test.service.DocCSVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api/csv")
public class DocController {
    @Autowired
    DocCSVService docCSVService;
    @Autowired
    DocementCSVRepository docementCSVRepository;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";

        if (CsvConfig.hasCSVFormat(file)) {
            try {
                docCSVService.save(file);

                message = "Uploaded the file successfully: " + file.getOriginalFilename();

                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/csv/download/")
                        .path(file.getOriginalFilename())
                        .toUriString();

                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(message, fileDownloadUri));
            } catch (Exception e) {
                message = "Could not upload the file: Date pattern 2016-10-14 22:11:20 !";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ApiResponse(message, ""));
            }
        }

        message = "Please upload a csv file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(message, ""));
    }

    @GetMapping("/tutorials")
    public ResponseEntity<List<DocumentCSV>> getAllTutorials() {
        try {
            List<DocumentCSV> allTutorials = docCSVService.getAllTutorials();

            if (allTutorials.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(allTutorials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        InputStreamResource file = new InputStreamResource(docCSVService.load());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }

    @GetMapping("/all")
    public String getAll(Model model) {
        List<DocumentCSV> byOrderByIdAsc = docementCSVRepository.findByOrderByIdAsc();
        List<DocumentCSV> byOrderByIdAscSorted =new ArrayList<>();
        ArrayList<String> versions = new ArrayList<>();

        for (DocumentCSV documentCSV : byOrderByIdAsc) {
            versions.add(documentCSV.getId());
        }

        for (String version : versions) {


        }

        ArrayList<String> results = answer(versions);
        for (String result : results) {
            byOrderByIdAscSorted.add((docementCSVRepository.findById(result)).get());
        }
        if (!byOrderByIdAscSorted.isEmpty()){
            model.addAttribute("documentList", byOrderByIdAscSorted);
        }

        return "Document";
    }

    public static ArrayList<String> answer(ArrayList<String> l) {


        String temp = new String();

        for (int i = 1; i < l.size(); i++) {
            for(int j = i ; j > 0 ; j--){
                if(compareVersion(l.get(j),l.get(j-1))<0){
                    temp = l.get(j);
                    l.set(j,l.get(j-1));
                    l.set(j-1, temp);
                }
            }
        }
        return l;
    }
    public static int compareVersion(String version1, String version2) {
        while(version1.length() > 0 || version2.length() > 0){
            int i1 = version1.indexOf("."), i2 = version2.indexOf(".");
            Double v1 = i1 < 0 ? version1.length() == 0 ? 0 : Double.valueOf(version1) : Double.valueOf(version1.substring(0,i1));
            Double v2 = i2 < 0 ? version2.length() == 0 ? 0 : Double.valueOf(version2) : Double.valueOf(version2.substring(0,i2));
            if(i1 == -1) version1 = "";
            if(i2 == -1) version2 = "";
            version1 = version1.substring(i1+1);
            version2 = version2.substring(i2+1);
            if(v1 < v2) return -1;
            if(v1 > v2) return 1;
            if(i1 == -1 && i2 == -1) break;
        }
        return 0;
    }
}

