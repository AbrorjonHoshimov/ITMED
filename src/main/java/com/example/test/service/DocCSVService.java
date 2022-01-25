package com.example.test.service;

import com.example.test.config.CsvConfig;
import com.example.test.entity.DocumentCSV;
import com.example.test.repository.DocementCSVRepository;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocCSVService {
    @Autowired
    DocementCSVRepository docementCSVRepository;

    public void save(MultipartFile file) {
        try {
            List<DocumentCSV> tutorials = CsvConfig.csvToTutorials(file.getInputStream());
            docementCSVRepository.saveAll(tutorials);
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }

    public ByteArrayInputStream load() {
        List<DocumentCSV> tutorials = docementCSVRepository.findAll();

        ByteArrayInputStream in = CsvConfig.tutorialsToCSV(tutorials);
        return in;
    }

    public List<DocumentCSV> getAllTutorials() {
        return docementCSVRepository.findAll();
    }

}
