package com.github.zighang_zighang.domain.resume.util;

import com.github.zighang_zighang.domain.resume.exception.ResumeException;
import com.github.zighang_zighang.global.infra.storage.exception.StorageException;
import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.reader.HWPReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;

@Component
public class ResumeTextExtractor {

    public String extractText(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) return "";

        try {
            if (filename.endsWith(".pdf")) {
                return extractPdfText(file);
            } else if (filename.endsWith(".hwp")) {
                return extractHwpText(file);
            } else {
                throw StorageException.INVALID_FILE_EXTENSION.toException();
            }
        } catch (Exception e) {
            throw ResumeException.EXTRACT_FAILED.toException();
        }
    }

    private String extractPdfText(MultipartFile file) {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (Exception e) {
            throw ResumeException.EXTRACT_FAILED.toException();
        }
    }

    private String extractHwpText(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            HWPFile hwpFile = HWPReader.fromInputStream(is);
            StringBuilder sb = new StringBuilder();

            hwpFile.getBodyText().getSectionList().forEach(section ->
                    Arrays.stream(section.getParagraphs()).forEach(p -> {
                        if (p.getText() != null) {
                            p.getText().getCharList().forEach(c -> {
                                char ch = (char) c.getCode();
                                // 제어문자는 건너뜀
                                if (!Character.isISOControl(ch)) {
                                    sb.append(ch);
                                }
                            });
                            sb.append("\n");
                        }
                    })
            );

            return sb.toString().trim();
        } catch (Exception e) {
            throw ResumeException.EXTRACT_FAILED.toException();
        }
    }
}
