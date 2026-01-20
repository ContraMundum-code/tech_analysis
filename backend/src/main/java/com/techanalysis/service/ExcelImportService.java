package com.techanalysis.service;

import com.techanalysis.entity.Paper;
import com.techanalysis.entity.Patent;
import com.techanalysis.entity.Project;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelImportService {

    private final PatentService patentService;
    private final ProjectService projectService;
    private final PaperService paperService;

    public ExcelImportService(PatentService patentService, ProjectService projectService, PaperService paperService) {
        this.patentService = patentService;
        this.projectService = projectService;
        this.paperService = paperService;
    }

    public List<Patent> parsePatentExcel(MultipartFile file) throws Exception {
        List<Patent> patents = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Patent patent = new Patent();
                patent.setPatentNumber(getCellString(row, 0));
                patent.setTitle(getCellString(row, 1));
                patent.setAbstractText(getCellString(row, 2));
                patent.setApplicant(getCellString(row, 3));
                patent.setInventor(getCellString(row, 4));
                patent.setClassificationCode(getCellString(row, 5));
                patent.setCountry(getCellString(row, 6));
                patent.setApplicationDate(getCellDate(row, 7));
                patent.setPublicationDate(getCellDate(row, 8));
                patents.add(patent);
            }
        }
        return patents;
    }

    public List<Project> parseProjectExcel(MultipartFile file) throws Exception {
        List<Project> projects = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Project project = new Project();
                project.setProjectName(getCellString(row, 0));
                project.setPrincipalInvestigator(getCellString(row, 1));
                project.setFundingAmount(getCellDecimal(row, 2));
                project.setInstitution(getCellString(row, 3));
                project.setCountry(getCellString(row, 4));
                project.setStartDate(getCellDate(row, 5));
                project.setEndDate(getCellDate(row, 6));
                projects.add(project);
            }
        }
        return projects;
    }

    public List<Paper> parsePaperExcel(MultipartFile file) throws Exception {
        List<Paper> papers = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Paper paper = new Paper();
                paper.setTitle(getCellString(row, 0));
                paper.setAuthors(getCellString(row, 1));
                paper.setAbstractText(getCellString(row, 2));
                paper.setJournal(getCellString(row, 3));
                paper.setCountry(getCellString(row, 4));
                paper.setCitationCount(getCellInt(row, 5));
                paper.setPublicationYear(getCellInt(row, 6));
                papers.add(paper);
            }
        }
        return papers;
    }

    public int importPatents(List<Patent> patents) {
        return patentService.saveBatch(patents);
    }

    public int importProjects(List<Project> projects) {
        return projectService.saveBatch(projects);
    }

    public int importPapers(List<Paper> papers) {
        return paperService.saveBatch(papers);
    }

    private String getCellString(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> null;
        };
    }

    private LocalDate getCellDate(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }

    private BigDecimal getCellDecimal(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }
        return null;
    }

    private Integer getCellInt(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        return null;
    }
}
